import pandas as pd
import numpy as np
import json
import re
import scipy.sparse as sp
from implicit.als import AlternatingLeastSquares
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics.pairwise import cosine_similarity
import os
import sys
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class ProductRecommender:
    def __init__(self):
        self.df_users = None
        self.prod_df = None
        self.als_model = None
        self.tfidf = None
        self.user_map = None
        self.prod_map = None
        self.inter_df = None
        self.median_table = {1:1948812,2:3260085,3:4194701,4:5120080,5:6025515,6:6900700,7:7780592}
        self.load_data()
        self.train_model()

    def load_data(self):
        try:
            # 데이터 파일 경로 설정
            DATA_DIR = os.getenv('DATA_DIR')
            if not DATA_DIR:
                raise ValueError("DATA_DIR environment variable is not set")
            
            USER_CSV = os.path.join(DATA_DIR, 'youth_user_data_5000.csv')
            PROD_JSON = os.path.join(DATA_DIR, 'products_sav.json')
            
            logger.info(f"Loading data from {USER_CSV} and {PROD_JSON}")
            
            # 사용자 데이터 로드
            self.df_users = pd.read_csv(USER_CSV)
            if 'user_id' not in self.df_users.columns:
                self.df_users.insert(0, 'user_id', [f"u{i:04d}" for i in range(1, len(self.df_users)+1)])
            if 'survey_region' not in self.df_users.columns:
                self.df_users['survey_region'] = np.nan
                
            # 필수 컬럼 확인
            required = ['user_id','survey_family_monthly_income','survey_family','survey_age',
                       'survey_army','annual_income','gender','survey_region']
            missing = [c for c in required if c not in self.df_users.columns]
            if missing:
                raise ValueError(f"Missing user columns: {missing}")
                
            # 데이터 전처리
            self.df_users['survey_army'] = self.df_users['survey_army'].fillna(0)
            self.df_users['eligible_by_household_income'] = (
                self.df_users['survey_family_monthly_income'] / self.df_users['survey_family'].map(self.median_table)
            ) <= 2.5
            self.df_users['effective_age'] = self.df_users['survey_age'] - self.df_users['survey_army'] / 12
            self.df_users['net_annual_income'] = self.df_users['annual_income']
            self.df_users['govt_contrib_status'] = self.df_users['net_annual_income'].apply(
                lambda x: 'govt_contrib_applicable' if x <= 60000000 else 'no_contrib' if x <= 75000000 else 'above_threshold'
            )
            self.df_users.set_index('user_id', inplace=True)
            
            # 상품 데이터 로드
            with open(PROD_JSON, 'r', encoding='utf-8') as f:
                raw_products = json.load(f)
                
            # 상품 데이터 전처리
            prod_list = []
            for p in raw_products:
                elig = p.get('eligibility', {})
                regions = elig.get('regions', [])
                age_txt = str(elig.get('age', ''))
                m = re.search(r"(\d+)\D+(\d+)", age_txt)
                min_age, max_age = (int(m.group(1)), int(m.group(2))) if m else (0, 999)
                pi = elig.get('personalIncome')
                max_inc = None
                if isinstance(pi, list):
                    nums = re.findall(r"(\d+)", ' '.join(map(str,pi)))
                    if len(nums) >= 2:
                        max_inc = int(nums[0]) * 1_000_000 + int(nums[1]) * 10_000
                elif isinstance(pi, str):
                    nums = re.findall(r"(\d+)", pi)
                    if nums:
                        max_inc = int(nums[0]) * 10_000
                prod_list.append({
                    'product_code': p['productId'],
                    'productName': p['productName'],
                    'category': p.get('category',''),
                    'regions': regions,
                    'min_age': min_age,
                    'max_age': max_age,
                    'max_personal_income': max_inc,
                    'requires_household_check': 'householdIncome' in elig
                })
            self.prod_df = pd.DataFrame(prod_list)
            self.prod_df['category_text'] = self.prod_df['category'].apply(lambda x: x if isinstance(x, str) else ' '.join(x))
            self.prod_df['text'] = self.prod_df['productName'] + ' ' + self.prod_df['category_text']
            
            logger.info("Data loading completed successfully")
            
        except Exception as e:
            logger.error(f"Error loading data: {e}")
            raise

    def train_model(self):
        try:
            # 사용자-상품 상호작용 데이터 생성
            user_ids = self.df_users.index.tolist()
            prod_codes = self.prod_df['product_code'].tolist()
            rng = np.random.RandomState(42)
            interactions = [(u, rng.choice(prod_codes), rng.randint(1,6)) for u in user_ids]
            interactions += [(rng.choice(user_ids), rng.choice(prod_codes), rng.randint(1,6)) for _ in range(3000)]
            self.inter_df = pd.DataFrame(interactions, columns=['user_id','product_code','rating'])
            
            # 매핑 생성
            self.user_map = {u:i for i,u in enumerate(user_ids)}
            self.prod_map = {p:i for i,p in enumerate(prod_codes)}
            
            # ALS 모델 학습
            rows = self.inter_df['user_id'].map(self.user_map)
            cols = self.inter_df['product_code'].map(self.prod_map)
            data = self.inter_df['rating']
            user_item = sp.csr_matrix((data, (rows, cols)), shape=(len(user_ids), len(prod_codes)))
            self.als_model = AlternatingLeastSquares(factors=20, regularization=0.1, iterations=15)
            self.als_model.fit(user_item.T.tocsr())
            
            # TF-IDF 벡터 생성
            vec = TfidfVectorizer()
            self.tfidf = vec.fit_transform(self.prod_df['text'])
            
            logger.info("Model training completed successfully")
            
        except Exception as e:
            logger.error(f"Error training model: {e}")
            raise

    def filter_soldier_gender(self, df, feat):
        mask = df['productName'].str.contains('장병')
        return df[~mask] if feat['survey_army'] > 0 or feat['gender'] == 'F' else df

    def filter_by_region(self, df, feat):
        return df[df['regions'].apply(lambda regs: (not regs) or feat['survey_region'] in regs)]

    def is_eligible(self, prod, feat):
        return ((prod['min_age'] <= feat['effective_age'] <= prod['max_age']) and
                (not prod['max_personal_income'] or feat['net_annual_income'] <= prod['max_personal_income']) and
                (not prod['requires_household_check'] or feat['eligible_by_household_income']))

    def recommend_for_input(self, input_feat, N=5):
        try:
            feat = pd.Series(input_feat)
            feat['survey_army'] = feat.get('survey_army', 0)
            feat['eligible_by_household_income'] = (
                feat['survey_family_monthly_income'] / self.median_table.get(feat['survey_family'], np.inf)
            ) <= 2.5
            feat['effective_age'] = feat['survey_age'] - feat['survey_army']/12
            feat['net_annual_income'] = feat['annual_income']
            
            # 필터링 적용
            cand = self.filter_soldier_gender(self.prod_df, feat)
            cand = self.filter_by_region(cand, feat)
            eligible = cand[cand.apply(lambda r: self.is_eligible(r, feat), axis=1)]
            if eligible.empty:
                eligible = cand
                
            # 인기도 기반 점수 계산
            pop = self.inter_df['product_code'].value_counts().reindex(self.prod_df['product_code']).fillna(0)
            pop_norm = MinMaxScaler().fit_transform(pop.values.reshape(-1,1)).flatten()
            
            # 추천 결과 생성
            scores = []
            for code in eligible['product_code']:
                idx = self.prod_map[code]
                score = pop_norm[idx]
                name = self.prod_df.loc[self.prod_df['product_code']==code, 'productName'].iloc[0]
                scores.append({'product_code':code, 'productName':name, 'score':round(score,4)})
                
            # 점수 기준 정렬
            scores = sorted(scores, key=lambda x: x['score'], reverse=True)
            return scores[:N]
            
        except Exception as e:
            logger.error(f"Error generating recommendations: {e}")
            return []

def main():
    try:
        # 명령줄 인수에서 사용자 데이터 읽기
        if len(sys.argv) != 2:
            print("Usage: python model.py <user_data_json>")
            sys.exit(1)
            
        user_data = json.loads(sys.argv[1])
        
        # 추천 시스템 초기화 및 실행
        recommender = ProductRecommender()
        recommendations = recommender.recommend_for_input(user_data)
        
        # 결과를 JSON으로 출력
        print(json.dumps(recommendations))
        
    except Exception as e:
        logger.error(f"Error in main: {e}")
        print(json.dumps([]))
        sys.exit(1)

if __name__ == '__main__':
    main()

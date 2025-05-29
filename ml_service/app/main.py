from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import sys
import os
import json
import logging
from app.model import ProductRecommender

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="ML Recommendation Service")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 origin 허용
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용
    allow_headers=["*"],  # 모든 헤더 허용
)

# 모델 인스턴스
recommender = None

class UserData(BaseModel):
    survey_family_monthly_income: float
    survey_family: int
    survey_age: int
    survey_army: Optional[float] = 0
    annual_income: float
    gender: str
    survey_region: Optional[str] = None

class RecommendationResponse(BaseModel):
    product_code: str
    productName: str
    score: float

@app.on_event("startup")
async def startup_event():
    global recommender
    try:
        # 데이터 디렉토리 설정
        data_dir = os.getenv('DATA_DIR', 'data')
        os.makedirs(data_dir, exist_ok=True)
        
        # 모델 초기화
        recommender = ProductRecommender()
        logger.info("ML model initialized successfully")
    except Exception as e:
        logger.error(f"Failed to initialize ML model: {e}")
        raise

@app.post("/recommend", response_model=List[RecommendationResponse])
async def get_recommendations(user_data: UserData):
    try:
        if recommender is None:
            raise HTTPException(status_code=500, detail="ML model is not initialized")
        
        # 사용자 데이터를 딕셔너리로 변환
        user_dict = user_data.dict()
        
        # 추천 결과 생성
        recommendations = recommender.recommend_for_input(user_dict)
        
        return recommendations
    except Exception as e:
        logger.error(f"Error generating recommendations: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health_check():
    return {"status": "healthy", "model_initialized": recommender is not None} 
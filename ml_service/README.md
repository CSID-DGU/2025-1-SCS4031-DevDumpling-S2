# ML Recommendation Service

이 서비스는 FastAPI를 사용하여 구현된 ML 추천 시스템입니다.

## 설치 방법

1. Python 3.8 이상이 필요합니다.

2. 가상환경 생성 및 활성화:
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows
```

3. 의존성 설치:
```bash
pip install -r requirements.txt
```

4. 데이터 파일 준비:
- `data/youth_user_data_5000.csv`
- `data/products_sav.json`
파일들을 `ml_service/data` 디렉토리에 복사합니다.

## 실행 방법

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

## API 엔드포인트

### 1. 추천 요청
- **URL**: `/recommend`
- **Method**: POST
- **Request Body**:
```json
{
    "survey_family_monthly_income": 3000000,
    "survey_family": 1,
    "survey_age": 25,
    "survey_army": 0,
    "annual_income": 30000000,
    "gender": "M",
    "survey_region": "서울"
}
```

### 2. 헬스 체크
- **URL**: `/health`
- **Method**: GET

## API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc 
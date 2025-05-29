FROM python:3.9-slim

WORKDIR /app

# 시스템 패키지 설치 및 캐시 정리
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Python 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir --default-timeout=100 -r requirements.txt \
    && rm -rf /root/.cache/pip/*

# 애플리케이션 코드와 데이터 복사
COPY app/ ./app/
COPY data/ ./data/

# 데이터 디렉토리 생성 및 권한 설정
RUN mkdir -p /app/data && chmod -R 755 /app/data

# 포트 설정
EXPOSE 8000

# 헬스체크를 위한 curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 서비스 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4"] 
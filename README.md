<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=transparent&height=200&text=FINEED&fontSize=120&fontColor=004225&desc=FINance%20Need%20Everyone&descSize=25&descAlignY=75&animation=twinkling&descColor=8B4513" />
  <p style="margin-top: -10px; color: #666666; font-size: 16px;"></p>
</div>

## 목차

1. [팀원 소개](#i-팀원-소개)  
2. [프로젝트 소개](#ii-프로젝트-소개)  
   2-1. [프로젝트 개요](#1-프로젝트-개요)  
   2-2. [선행 사례 및 관련 기술 분석](#2-선행-사례-및-관련-기술-분석)  
   2-3. [주요 서비스 소개](#3-주요-서비스-소개)  
   2-4. [개발 환경 및 기술 스택](#4-개발-환경-및-기술-스택)  
   2-5. [프로젝트 구조](#5-프로젝트-구조)  
3. [시작 가이드](#6-시작-가이드)

## I. 팀원 소개

* 수행 학기: 2025년 1학기
* 프로젝트명: 청년을 위한 금융 소셜 커뮤니티 앱 "FINEED" 
* 팀명: 개발할만두 
    

구분 | 성명 | 학번 | 소속학과 | 연계전공 | 이메일
------|-------|-------|-------|-------|-------
팀장 | 현광수 | 2018112493 | 산업시스템공학과 | 융합소프트웨어 | jklas187@naver.com         
팀원 | 김희진 | 2021113388 | 경영정보학과 | 융합소프트웨어 | blaber_0609@naver.com         
팀원 | 신예성 | 2019112642 | 교육학과 | 융합소프트웨어 | 1020blue@naver.com 
팀원 | 오연진 | 2020110869 | 경제학과 | 융합소프트웨어 | yonjin.oh@gmail.com                
             

* 지도교수 : 융합소프트웨어연계전공 (AI소프트웨어융합학부) 신연순 교수님 
* 산업체 멘토 : 미래에셋증권 김동호 멘토님


### Team
|<img src="https://avatars.githubusercontent.com/u/143872214?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/144208568?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/144078388?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/144078389?v=4" width="150" height="150"/>|
|:-:|:-:|:-:|:-:|
|김희진<br>[@Kim-Hee-Jin](https://github.com/Kim-Hee-Jin) |신예성<br>[@shin-yeseong](https://github.com/shin-yeseong)|오연진<br>[@yonjinoh](https://github.com/yonjinoh)|현광수<br>[@ISEViper](https://github.com/ISEViper)|
|프론트엔드 |프론트엔드 |백엔드, 배포 | 백엔드 |



## II. 프로젝트 소개

### 1. 프로젝트 개요

- 청년들의 재무 독립을 지원하는 금융 커뮤니티 플랫폼 개발


#### 1.1 개발 동기  

- 청년층(18~29세)의 금융 이해력이 성인 전체 평균보다 낮은 상황에서, 체계적인 금융교육 부재로 인한 금융 의사결정 실패 사례 증가
- 금융교육에 대한 정보 부족과 시간 제약으로 인해 교육 기회를 얻지 못하는 청년들을 위한 접근성 높은 플랫폼 필요
- 2030 청년층의 과반이 '인터넷 포털 사이트'를 통해 경제 지식을 습득하는 점을 고려한 커뮤니티 기반 금융 교육 플랫폼 개발 필요


#### 1.2 개발 목표  

- 청년층의 금융 역량 향상과 안정적인 재무 독립을 촉진하는 금융 소셜 커뮤니티 어플리케이션 "FINEED" 개발
- AI 기반 맞춤형 금융 챌린지와 퀴즈를 통한 능동적 학습 환경 제공
-  금융상품 리뷰 및 커뮤니티 기능을 통한 지속적인 사용자 참여 유도 및 금융 습관 형성 지원

#### 1.3 최종결과물  

<div align="center">
  <img src="https://github.com/user-attachments/assets/c3f4e384-6de5-4daa-81e5-01cf02ebdef7" alt="서비스소개" style="max-width: 100%; height: auto;">
</div>

### 2. 선행 사례 및 관련 기술 분석

#### 2.1 선행 사례 분석



본 프로젝트는 **금융 챌린지**, **금융 리뷰**, **금융 기사 요약 및 퀴즈**의 세 가지 주요 기능을 중심으로 설계되었으며, 이를 구현함에 있어 아래와 같은 기존 서비스 사례들을 기능별로 조사하고 분석하였음

----

#### 🔹 금융 챌린지 기능 관련

| 서비스      | 사례              | 핵심 기능                                                   | 한계점                                                             |
|-------------|-------------------|--------------------------------------------------------------|--------------------------------------------------------------------|
| 카카오페이   | 월간 챌린지        | 소비·저축 미션 수행 시 포인트 지급, 사용자 간 랭킹 경쟁             | 보상 중심 단기 참여, 사용자가 원하는 주제 부재 시 참여 저조                |
| 핀크         | 핀크 혜택          | 퀴즈, 쇼핑 등 미션 달성 시 포인트 제공, 앱 내 활용 가능한 재화 지급   | 외부 앱 설치 유도, 비자발적 참여 유도 가능성                              |

**요약:**  
기존 서비스는 포인트 중심의 단기 참여 유도에 그치는 경우가 많으며, **사용자 주도형 목표 설정 및 진행 관리 기능은 부족**.  
본 프로젝트는 **사용자가 직접 금융 목표를 설정하고, 달성 과정을 관리하는 능동적 챌린지 시스템**을 지향.

---

#### 🔹 금융 리뷰 기능 관련

| 서비스        | 사례                 | 핵심 기능                                         | 한계점                                                       |
|---------------|----------------------|--------------------------------------------------|--------------------------------------------------------------|
| 뱅크샐러드     | 금융 쇼핑             | 예적금/보험/대출/카드 등 맞춤형 금융상품 비교 및 추천     | 제휴 중심 노출 가능성, 비교 가능한 상품 다양성 부족                  |
| 네이버페이     | 금융상품 비교          | 조건별 필터를 통한 상품 비교, 파트너사 연계 가입 유도     | 사용자 금융 상태 미반영, 정보 최신성 미흡                           |

**요약:**  
기존 플랫폼은 UI 중심의 상품 비교에 초점을 맞추고 있으며, **실사용자 리뷰 기반의 정보 제공 기능은 미흡**.  
본 프로젝트는 **상품 실사용 경험을 바탕으로 한 리뷰 및 평점 제공 시스템**으로 **신뢰성 있는 상품 선택 환경**을 조성.

---

#### 🔹 금융 기사 요약 및 퀴즈 기능 관련

| 서비스      | 사례                  | 핵심 기능                                                       | 한계점                                                             |
|-------------|-----------------------|------------------------------------------------------------------|--------------------------------------------------------------------|
| 토스         | 피드, 머니라운지       | 카드뉴스 형태 금융 정보, 퀴즈, 커뮤니티 제공                         | 상호작용 기능 부족, 모든 사용자에게 동일한 내용 제공공 미흡                                  |
| 카카오페이   | 증권 토론방            | 종목별 토론 커뮤니티, 매매 인증, 실시간 인기글 확인 가능               | 비전문 정보의 범람, 필터링 부족으로 인한 신뢰도 저하                       |

**요약:**  
기존 서비스는 금융 콘텐츠에 쉽게 접근할 수 있으나, **구조적 학습 요소는 부족**.  
본 프로젝트는 **AI 기반 기사 요약 및 용어 정리**, **사용자 유형별 맞춤 퀴즈 제공**을 통해 **금융 이해도를 체계적으로 높일 수 있는 학습 환경**을 제공.


#### 2.2 본 프로젝트의 차별점

<div align="center">
  <img src="https://raw.githubusercontent.com/CSID-DGU/2025-1-SCS4031-DevDumpling-S2/main/Docs/images/개발할만두_기존사례분석.png" alt="개발할만두_기존사례분석" style="max-width: 100%; height: auto;">
</div>

### 3. 주요 서비스 소개

- 금융 상품 리뷰
    이미지 첨부

- 금융 챌린지 서비스 (공개/비공개)
    이미지 첨부 

- 금융 기사 요약 및 금융 용어 설명
    이미지 첨부 

- 금융 기사 기반 퀴즈
    이미지 첨부

- 커뮤니티 서비스
    이미지 첨부
   
- 마이페이지 
    이미지 첨부  
  
- 상세한 구현 사항은 최종보고서 부록에 기재 



### 4. 개발 환경 및 기술 스택

#### Language & Framework
<div style="display: flex; gap: 5px; flex-wrap: wrap;">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white">
</div>

#### Database
<div style="display: flex; gap: 5px; flex-wrap: wrap;">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
</div>

#### DevOps
<div style="display: flex; gap: 5px; flex-wrap: wrap;">
  <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
</div>

#### Development Tools
<div style="display: flex; gap: 5px; flex-wrap: wrap;">
  <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white">
  <img src="https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=visual-studio-code&logoColor=white">
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">
</div>

#### Collaboration Tools
<div style="display: flex; gap: 5px; flex-wrap: wrap;">
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">
  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
  <img src="https://img.shields.io/badge/Canva-00C4CC?style=for-the-badge&logo=canva&logoColor=white">
</div>

### 5. 프로젝트 구조
#### 5.1 시스템 구조
<div align="center">
  <img src="https://github.com/user-attachments/assets/8563b1bd-dc16-4648-a0a8-0084cf2de549" alt="시스템구조도" style="max-width: 100%; height: auto;">
</div>

#### 5.2 ERD
<div align="center">
  <img src="https://github.com/user-attachments/assets/f351a249-77cb-4a8b-8506-ab7b74f503c6" alt="ERD" style="max-width: 100%; height: auto;">
</div>



## 6. 시작 가이드

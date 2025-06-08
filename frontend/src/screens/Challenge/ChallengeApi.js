// src/screens/Challenge/ChallengeApi.js
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const BASE_URL = 'http://52.78.59.11:8080/api';

// 한글 카테고리명을 영문 코드로 변환하는 함수
export const getCategoryCode = (koreanName) => {
  if (!koreanName) return null;

  // 한글명과 영문 코드 매핑
  const categoryMap = {
    '식비': 'FOOD',
    '카페간식': 'CAFE_SNACK',
    '쇼핑': 'SHOPPING',
    '저축': 'SAVINGS',
    '술유흥': 'ALCOHOL_ENTERTAINMENT',
    '미용': 'BEAUTY',
    '여행': 'TRAVEL',
    '반려동물': 'PET',
    '편의점마트잡화': 'MART_CONVENIENCE',
    '게임': 'GAME_OTT',
    '주거통신': 'HOUSING_COMMUNICATION',
    '교통': 'TRANSPORTATION',
    '의료건강피트니스': 'HEALTH_EXERCISE',
    '도서교육': 'NEW_DISCOUNT'
  };

  // 이미 영문 코드인 경우 그대로 반환
  if (Object.values(categoryMap).includes(koreanName)) {
    return koreanName;
  }

  return categoryMap[koreanName] || null;
};

// 영문 카테고리 코드를 한글명으로 변환하는 함수
export const getCategoryName = (englishCode) => {
  if (!englishCode) return null;

  // 영문 코드와 한글명 매핑
  const categoryMap = {
    'FOOD': '식비',
    'CAFE_SNACK': '카페간식',
    'SHOPPING': '쇼핑',
    'SAVINGS': '저축',
    'ALCOHOL_ENTERTAINMENT': '술유흥',
    'BEAUTY': '미용',
    'TRAVEL': '여행',
    'PET': '반려동물',
    'MART_CONVENIENCE': '편의점마트잡화',
    'GAME_OTT': '게임',
    'HOUSING_COMMUNICATION': '주거통신',
    'TRANSPORTATION': '교통',
    'HEALTH_EXERCISE': '의료건강피트니스',
    'NEW_DISCOUNT': '도서교육'
  };

  // 이미 한글명인 경우 그대로 반환
  if (Object.values(categoryMap).includes(englishCode)) {
    return englishCode;
  }

  return categoryMap[englishCode] || englishCode;
};

// 카테고리 목록 가져오기
export const fetchChallengeCategories = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/challenges/categories`);
    return response.data;
  } catch (error) {
    console.error('카테고리 목록 가져오기 실패:', error);
    return { content: [] };
  }
};

// 카테고리별 챌린지 목록 가져오기
// 백엔드 API: /api/challenges/category/{category}
// 백엔드 서버에서 정의한 파라미터:
// - status(optional): 상태
// - pageable: 페이징 정보(page, size, sort 포함)
export const fetchChallengesByCategory = async (category, status = null, page = 0, size = 10) => {
  try {
    if (!category) {
      console.warn('카테고리가 지정되지 않았습니다.');
      return { content: [] };
    }

    // 백엔드 API 엔드포인트
    const url = `${BASE_URL}/challenges/category/${category}`;

    // 페이징 파라미터 설정
    const params = {
      page: page,
      size: size,
      sort: 'startDate,desc'
    };

    // status 파라미터가 있는 경우에만 추가
    if (status) {
      params.status = status;
    }

    console.log(`카테고리 ${category} 챌린지 요청:`, {
      url,
      params,
    });

    // 현재 사용자 토큰 가져오기
    const token = await AsyncStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    // API 요청 실행
    const response = await axios.get(url, {
      params,
      headers
    });

    console.log(`${category} 카테고리 응답 성공:`, {
      status: response.status,
      totalElements: response.data.totalElements,
      content: response.data.content?.length
    });

    return response.data;
  } catch (error) {
    console.error(`${category} 카테고리 챌린지 목록 가져오기 실패:`, error);

    // 에러 상세 로깅
    if (error.response) {
      console.error('에러 상태 코드:', error.response.status);
      console.error('에러 응답 데이터:', error.response.data);
    }

    // 오류 발생 시 빈 배열 반환
    return { content: [] };
  }
};

// 챌린지 상세 정보 가져오기
export const fetchChallengeDetail = async (challengeId) => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    const response = await axios.get(`${BASE_URL}/challenges/${challengeId}`, { headers });
    return response.data;
  } catch (error) {
    console.error('챌린지 상세 정보 가져오기 실패:', error);
    return { content: [] };
  }
};

// 챌린지 참여하기
export const joinChallenge = async (challengeId, requestData = {}) => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    if (!token) {
      throw new Error('로그인이 필요합니다.');
    }

    console.log(`챌린지 참여 API 호출: ID=${challengeId}`);

    const response = await axios.post(
      `${BASE_URL}/challenges/${challengeId}/join`,
      requestData,
      {
        headers: { Authorization: `Bearer ${token}` }
      }
    );

    console.log('챌린지 참여 성공 응답:', response.data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error('챌린지 참여하기 실패:', error);

    if (error.response) {
      console.error('응답 상태 코드:', error.response.status);
      console.error('응답 데이터:', error.response.data);

      // 이미 참여한 챌린지인 경우
      if (error.response.status === 400 &&
        error.response.data &&
        error.response.data.message &&
        error.response.data.message.includes('이미 참여중')) {
        return { success: true, alreadyJoined: true };
      }
    }

    throw error; // 오류를 전파하여 호출자가 처리하도록 함
  }
};

// 챌린지 생성하기
export const createChallenge = async (payload) => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    const response = await axios.post(`${BASE_URL}/challenges`, payload, { headers });
    return response.data;
  } catch (error) {
    console.error('챌린지 생성 실패:', error.response?.data || error);
    throw error;
  }
};

// 참여 중인 챌린지 목록 가져오기
export const fetchParticipatingChallenges = async () => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    if (!token) {
      return { content: [] };
    }

    // 1. 먼저 AsyncStorage에서 참여 중인 챌린지 ID 가져오기
    const userData = JSON.parse(await AsyncStorage.getItem('userData') || '{}');
    const userId = userData.id;
    const participatingChallenges = JSON.parse(await AsyncStorage.getItem('participatingChallenges') || '[]');
    console.log('로컬에 저장된 참여 챌린지:', participatingChallenges);

    if (!participatingChallenges.length) {
      return { content: [] };
    }

    // 2. 참여 중인 챌린지 ID만 추출 (format: "challengeId_userId")
    const challengeIds = participatingChallenges
      .filter(key => key.includes('_') && key.split('_')[1] === String(userId))
      .map(key => key.split('_')[0]);

    console.log('참여 중인 챌린지 ID만 추출:', challengeIds);

    if (!challengeIds.length) {
      return { content: [] };
    }

    // 3. 각 챌린지 상세 정보 가져오기
    const challengePromises = challengeIds.map(id => fetchChallengeDetail(id));
    const challengeDetails = await Promise.all(challengePromises);

    // 4. 문제가 없는 챌린지만 필터링
    const validChallenges = challengeDetails.filter(challenge => challenge && challenge.id);
    console.log('참여 중인 챌린지 데이터:', validChallenges.length, '개 챌린지');

    return { content: validChallenges };


  } catch (error) {
    console.error('참여 중인 챌린지 목록 가져오기 실패:', error);
    return { content: [] };
  }
};

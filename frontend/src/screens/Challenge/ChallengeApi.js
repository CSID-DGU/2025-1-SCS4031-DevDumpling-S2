// src/screens/Challenge/ChallengeApi.js
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const BASE_URL = 'http://52.78.59.11:8080/api';

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
    
    const response = await axios.post(
      `${BASE_URL}/challenges/${challengeId}/join`,
      requestData,
      {
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    return response.data;
  } catch (error) {
    console.error('챌린지 참여하기 실패:', error);
    return { content: [] };
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
// 아직 백엔드 API가 준비되지 않았으므로, 로그인 유무만 확인하고 빈 배열을 반환합니다.
// 추후 API 엔드포인트가 확정되면 여기서 axios 호출을 추가하세요.
export const fetchParticipatingChallenges = async () => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    if (!token) {
      return { content: [] };
    }

    // TODO: 백엔드에서 참여중인 챌린지 API가 제공되면 아래 코드로 교체
    // const response = await axios.get(`${BASE_URL}/challenges/participating`, {
    //   headers: { Authorization: `Bearer ${token}` }
    // });
    // return response.data;

    return { content: [] };
  } catch (error) {
    console.error('참여 중인 챌린지 목록 가져오기 실패:', error);
    return { content: [] };
  }
};

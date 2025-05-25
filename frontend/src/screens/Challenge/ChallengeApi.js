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
export const fetchChallengesByCategory = async (category, status, page = 0, size = 10) => {
  try {
    // 백엔드 API 엔드포인트에 맞게 수정
    const response = await axios.get(`${BASE_URL}/challenges/category/${category}`, {
      params: {
        status: status,
        page: page,
        size: size
      }
    });
    return response.data;
  } catch (error) {
    console.error(`${category} 카테고리 챌린지 목록 가져오기 실패:`, error);
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

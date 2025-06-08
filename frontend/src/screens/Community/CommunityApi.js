// src/api/community.js
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const BASE_URL = 'http://52.78.59.11:8080/api'; // /api 추가

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  }
});

// 요청 인터셉터 추가
axiosInstance.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 백엔드에서 정의한 유효한 게시판 타입
// public enum BoardType { FREE, INVESTMENT, CHALLENGE, QUIZ }
const VALID_BOARD_TYPES = ['FREE', 'INVESTMENT', 'CHALLENGE', 'QUIZ'];

// 게시글 목록 조회
export const fetchBoardPosts = async (boardType) => {
  try {
    // 게시판 타입 유효성 검사
    if (!boardType || !VALID_BOARD_TYPES.includes(boardType)) {
      console.warn(`유효하지 않은 게시판 타입: ${boardType}. 기본값으로 FREE 사용`);
      boardType = 'FREE'; // 기본값으로 자유 게시판 사용
    }

    // 토큰 가져오기 - 'token'으로 키 이름 수정
    const token = await AsyncStorage.getItem('token');
    console.log('fetchBoardPosts 토큰:', token);

    // 사용자 데이터 가져오기
    const userDataStr = await AsyncStorage.getItem('userData');
    const userData = userDataStr ? JSON.parse(userDataStr) : null;
    console.log('fetchBoardPosts 사용자 데이터:', userData);

    // 요청 구성 - 직접 axios 사용
    try {
      // 일반 axios로 시도
      const url = `${BASE_URL}/boards/${boardType}`;
      const config = {
        headers: {
          Authorization: token ? `Bearer ${token}` : '',
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        timeout: 10000 // 10초 타임아웃
      };

      console.log(`게시글 목록 요청 URL: ${url}`);
      console.log('요청 헤더:', JSON.stringify(config.headers));

      const res = await axios.get(url, config);
      console.log(`게시글 목록 응답 데이터(${boardType}):`, JSON.stringify(res.data));

      return res.data;
    } catch (directError) {
      console.error(`일반 axios 요청 실패, 다른 방법 시도:`, directError);

      // 대체 방법: 전체 URL을 직접 사용
      const fullUrl = `${BASE_URL}/boards/${boardType}`;
      console.log(`대체 방법 시도 - 전체 URL: ${fullUrl}`);

      const res = await axios({
        method: 'get',
        url: fullUrl,
        headers: {
          Authorization: token ? `Bearer ${token}` : '',
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        timeout: 15000
      });

      console.log(`대체 방법 성공 - 응답 데이터:`, JSON.stringify(res.data));
      return res.data;
    }
  } catch (error) {
    console.error(`❌ 게시글 목록 불러오기 실패(${boardType}):`, error);

    // 에러 상세 정보 로깅 추가
    if (error.response) {
      console.error('에러 상태:', error.response.status);
      console.error('에러 데이터:', JSON.stringify(error.response.data));
      console.error('에러 헤더:', JSON.stringify(error.response.headers));
    } else if (error.request) {
      console.error('요청은 전송되었으나 응답이 없음:', error.request);
    } else {
      console.error('요청 설정 중 오류 발생:', error.message);
    }

    // 오류 발생 시 빈 배열 반환
    return { content: [] };
  }
};

// 게시글 작성
export const createBoardPost = async (boardType, postData) => {
  const token = await AsyncStorage.getItem('userToken');
  const res = await axios.post(
    `${BASE_URL}/boards/${boardType}/create`,
    postData,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
};

// 게시글 상세 조회
export const fetchBoardPostDetail = async (boardType, id) => {
  try {
    // 게시판 타입 유효성 검사
    if (!boardType || !VALID_BOARD_TYPES.includes(boardType)) {
      console.warn(`유효하지 않은 게시판 타입: ${boardType}. 기본값으로 FREE 사용`);
      boardType = 'FREE'; // 기본값으로 자유 게시판 사용
    }

    const token = await AsyncStorage.getItem('userToken');
    console.log(`게시글 상세 조회 요청: /boards/${boardType}/${id} (토큰 유무: ${token ? '있음' : '없음'})`);

    const config = {
      headers: {
        Authorization: `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      timeout: 10000 // 10초 타임아웃
    };

    const res = await axios.get(`${BASE_URL}/boards/${boardType}/${id}`, config);
    console.log(`게시글 상세 조회 응답:`, JSON.stringify(res.data));
    return res.data;
  } catch (error) {
    console.error(`❌ 게시글 상세 조회 실패(${boardType}/${id}):`, error);

    // 에러 상세 정보 로깅
    if (error.response) {
      console.error('에러 상태:', error.response.status);
      console.error('에러 데이터:', JSON.stringify(error.response.data));
      console.error('에러 헤더:', JSON.stringify(error.response.headers));
    } else if (error.request) {
      console.error('요청은 전송되었으나 응답이 없음:', error.request);
    } else {
      console.error('요청 설정 중 오류 발생:', error.message);
    }

    throw error;
  }
};

// 게시글 수정
export const updateBoardPost = async (boardType, id, postData) => {
  try {
    // 게시판 타입 유효성 검사
    if (!boardType || !VALID_BOARD_TYPES.includes(boardType)) {
      console.warn(`유효하지 않은 게시판 타입: ${boardType}. 기본값으로 FREE 사용`);
      boardType = 'FREE'; // 기본값으로 자유 게시판 사용
    }

    const token = await AsyncStorage.getItem('userToken');
    console.log(`게시글 수정 요청: /boards/${boardType}/update/${id}`);

    const config = {
      headers: {
        Authorization: `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    };

    const res = await axios.put(`${BASE_URL}/boards/${boardType}/update/${id}`, postData, config);
    console.log('게시글 수정 성공:', JSON.stringify(res.data));
    return res.data;
  } catch (error) {
    console.error(`❌ 게시글 수정 실패(${boardType}/${id}):`, error);

    // 에러 상세 정보 로깅
    if (error.response) {
      console.error('에러 상태:', error.response.status);
      console.error('에러 데이터:', JSON.stringify(error.response.data));
    }

    throw error;
  }
};

// 게시글 삭제
export const deleteBoardPost = async (boardType, id) => {
  try {
    // 게시판 타입 유효성 검사
    if (!boardType || !VALID_BOARD_TYPES.includes(boardType)) {
      console.warn(`유효하지 않은 게시판 타입: ${boardType}. 기본값으로 FREE 사용`);
      boardType = 'FREE'; // 기본값으로 자유 게시판 사용
    }

    const token = await AsyncStorage.getItem('userToken');
    console.log(`게시글 삭제 요청: /boards/${boardType}/delete/${id}`);

    const config = {
      headers: {
        Authorization: `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    };

    const res = await axios.delete(`${BASE_URL}/boards/${boardType}/delete/${id}`, config);
    console.log('게시글 삭제 성공');
    return res.data;
  } catch (error) {
    console.error(`❌ 게시글 삭제 실패(${boardType}/${id}):`, error);

    // 에러 상세 정보 로깅
    if (error.response) {
      console.error('에러 상태:', error.response.status);
      console.error('에러 데이터:', JSON.stringify(error.response.data));
    }

    throw error;
  }
};

// 댓글 삭제
export const deleteComment = async (commentId) => {
  const token = await AsyncStorage.getItem('token');
  const res = await axios.delete(
    `${BASE_URL}/comment/${commentId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
};

// 댓글 수정
export const updateComment = async (commentId, commentData) => {
  try {
    const token = await AsyncStorage.getItem('userToken');
    console.log(`댓글 수정 요청: /comment/${commentId}`);

    const config = {
      headers: {
        Authorization: `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    };

    const res = await axios.put(`${BASE_URL}/comment/${commentId}`, commentData, config);
    console.log('댓글 수정 성공:', JSON.stringify(res.data));
    return res.data;
  } catch (error) {
    console.error(`❌ 댓글 수정 실패(${commentId}):`, error);

    // 에러 상세 정보 로깅
    if (error.response) {
      console.error('에러 상태:', error.response.status);
      console.error('에러 데이터:', JSON.stringify(error.response.data));
    }

    throw error;
  }
};

// 댓글 목록 조회
export async function fetchComments(postId) {
  const res = await axiosInstance.get(`/comments/board/${postId}`);
  return res.data;
}
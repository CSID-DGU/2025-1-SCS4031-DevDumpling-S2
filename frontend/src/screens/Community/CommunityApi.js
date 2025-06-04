// src/api/community.js
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const BASE_URL = 'http://52.78.59.11:8080/api'; // /api 추가

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  // 필요시 headers 등 추가
});

// 게시글 목록 조회
export const fetchBoardPosts = async (boardType) => {
  const res = await axios.get(`${BASE_URL}/boards/${boardType}`);
  return res.data;
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
  const token = await AsyncStorage.getItem('userToken');
  const res = await axios.get(`${BASE_URL}/boards/${boardType}/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
};

// 게시글 수정
export const updateBoardPost = async (boardType, id, postData) => {
  const res = await axios.put(`${BASE_URL}/boards/${boardType}/update/${id}`, postData);
  return res.data;
};

// 게시글 삭제
export const deleteBoardPost = async (boardType, id) => {
  const res = await axios.delete(`${BASE_URL}/boards/${boardType}/delete/${id}`);
  return res.data;
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
  const token = await AsyncStorage.getItem('token');
  const res = await axios.put(
    `${BASE_URL}/comment/${commentId}`,
    commentData,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
};

// 댓글 목록 조회
export async function fetchComments(postId) {
  const res = await axiosInstance.get(`/comments/board/${postId}`);
  return res.data;
}
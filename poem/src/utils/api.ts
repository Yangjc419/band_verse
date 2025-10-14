import axios from 'axios';
import type { ApiResponse } from '../types';

// 创建 axios 实例
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// API 方法
export const poemApi = {
  // 获取诗歌列表
  getPoems: (params?: any): Promise<ApiResponse> => 
    api.get('/poems', { params }),
  
  // 获取诗歌详情
  getPoemById: (id: string): Promise<ApiResponse> => 
    api.get(`/poems/${id}`),
  
  // 创建诗歌
  createPoem: (data: any): Promise<ApiResponse> => 
    api.post('/poems', data),
  
  // 更新诗歌
  updatePoem: (id: string, data: any): Promise<ApiResponse> => 
    api.put(`/poems/${id}`, data),
  
  // 删除诗歌
  deletePoem: (id: string): Promise<ApiResponse> => 
    api.delete(`/poems/${id}`),
  
  // 点赞诗歌
  likePoem: (id: string): Promise<ApiResponse> => 
    api.post(`/poems/${id}/like`),
};

export const userApi = {
  // 获取用户信息
  getUserInfo: (): Promise<ApiResponse> => 
    api.get('/user/profile'),
  
  // 更新用户信息
  updateUserInfo: (data: any): Promise<ApiResponse> => 
    api.put('/user/profile', data),
  
  // 获取用户的诗歌
  getUserPoems: (userId: string): Promise<ApiResponse> => 
    api.get(`/user/${userId}/poems`),
};

export const commentApi = {
  // 获取评论列表
  getComments: (poemId: string): Promise<ApiResponse> => 
    api.get(`/poems/${poemId}/comments`),
  
  // 创建评论
  createComment: (poemId: string, data: any): Promise<ApiResponse> => 
    api.post(`/poems/${poemId}/comments`, data),
  
  // 删除评论
  deleteComment: (commentId: string): Promise<ApiResponse> => 
    api.delete(`/comments/${commentId}`),
};

export default api;
// 诗歌相关类型定义
export interface Poem {
  id: string;
  title: string;
  content: string;
  author: string;
  createTime: string;
  updateTime?: string;
  tags: string[];
  likes: number;
  views: number;
  category: string;
  description?: string;
}

// 用户类型定义
export interface User {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  bio?: string;
  joinDate: string;
  poemCount: number;
  followersCount: number;
  followingCount: number;
}

// 评论类型定义
export interface Comment {
  id: string;
  poemId: string;
  userId: string;
  username: string;
  content: string;
  createTime: string;
  likes: number;
  replies?: Comment[];
}

// API 响应类型
export interface ApiResponse<T = any> {
  success: boolean;
  data: T;
  message: string;
  code: number;
}

// 分页参数
export interface PaginationParams {
  page: number;
  pageSize: number;
  total?: number;
}

// 搜索参数
export interface SearchParams {
  keyword?: string;
  category?: string;
  author?: string;
  tags?: string[];
  sortBy?: 'createTime' | 'likes' | 'views';
  sortOrder?: 'asc' | 'desc';
}
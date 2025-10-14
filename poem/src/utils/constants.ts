// 常量定义

// 诗歌分类
export const POEM_CATEGORIES = [
  { label: '古诗', value: '古诗' },
  { label: '现代诗', value: '现代诗' },
  { label: '词牌', value: '词牌' },
  { label: '散文诗', value: '散文诗' },
  { label: '打油诗', value: '打油诗' },
  { label: '其他', value: '其他' },
];

// 常用标签
export const COMMON_TAGS = [
  '春天', '夏天', '秋天', '冬天',
  '思念', '爱情', '友情', '亲情',
  '山水', '花鸟', '月亮', '星空',
  '人生', '哲理', '励志', '感悟',
  '田园', '边塞', '咏物', '怀古',
];

// 排序选项
export const SORT_OPTIONS = [
  { label: '最新发布', value: 'createTime-desc' },
  { label: '最多点赞', value: 'likes-desc' },
  { label: '最多浏览', value: 'views-desc' },
  { label: '最早发布', value: 'createTime-asc' },
];

// 分页配置
export const PAGINATION_CONFIG = {
  defaultPageSize: 12,
  pageSizeOptions: ['12', '24', '48'],
  showSizeChanger: true,
  showQuickJumper: true,
};

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'poem_token',
  USER_INFO: 'poem_user_info',
  THEME: 'poem_theme',
};

// 消息提示配置
export const MESSAGE_CONFIG = {
  duration: 3,
  maxCount: 3,
};

// 文件上传配置
export const UPLOAD_CONFIG = {
  maxSize: 2 * 1024 * 1024, // 2MB
  acceptTypes: ['image/jpeg', 'image/png', 'image/gif'],
};

export default {
  POEM_CATEGORIES,
  COMMON_TAGS,
  SORT_OPTIONS,
  PAGINATION_CONFIG,
  STORAGE_KEYS,
  MESSAGE_CONFIG,
  UPLOAD_CONFIG,
};
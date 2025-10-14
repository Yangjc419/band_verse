# 诗韵 - 诗歌平台

一个现代化的诗歌创作与分享平台，基于 React + TypeScript + Vite 构建。

## 功能特性

- 🏠 **首页** - 精美的诗歌轮播展示和功能介绍
- 📚 **诗歌列表** - 浏览、搜索和筛选诗歌作品
- 📖 **诗歌详情** - 详细的诗歌展示页面，支持点赞和评论
- ✍️ **诗歌创作** - 富文本编辑器，支持预览和发布
- 👤 **个人中心** - 用户资料管理和作品展示

## 技术栈

- **前端框架**: React 18
- **开发语言**: TypeScript
- **构建工具**: Vite
- **UI 组件库**: Ant Design
- **路由管理**: React Router
- **样式方案**: CSS + Styled Components
- **HTTP 客户端**: Axios

## 快速开始

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

## 项目结构

```
poem/
├── src/
│   ├── components/         # 公共组件
│   ├── pages/              # 页面组件
│   ├── types/              # TypeScript 类型定义
│   ├── App.tsx             # 应用主组件
│   └── main.tsx            # 应用入口
├── package.json
├── tsconfig.json
└── vite.config.ts
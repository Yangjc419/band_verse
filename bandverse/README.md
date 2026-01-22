# Bandverse - 乐队宇宙

社区驱动的音乐发现平台，专注于乐队文化的分享与交流。

## 项目概述

Bandverse 是一个社区驱动的音乐发现平台，核心功能包括：

- **每日推荐**：每天从用户推荐中随机选择一首歌、一句歌词、一个乐队推荐给所有用户
- **用户贡献**：用户可以提交自己喜欢的歌曲和歌词推荐
- **社交互动**：展示推荐人信息，支持点赞、评论等互动功能
- **推荐历史**：查看个人和社区的推荐历史记录

## 技术架构

- **前端**：Android 原生开发（Kotlin + Jetpack Compose）
- **后端**：Supabase（PostgreSQL + 实时API + 认证服务）
- **推荐引擎**：n8n 工作流引擎（每日随机推荐）

## 项目结构

```
bandverse/
├── android/                    # Android 应用
│   ├── app/
│   │   ├── src/main/java/com/bandverse/app/
│   │   │   ├── data/          # 数据层
│   │   │   ├── domain/        # 业务逻辑层
│   │   │   ├── presentation/  # UI 层
│   │   │   └── ui/            # Compose UI 组件
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── supabase/                   # Supabase 配置
│   ├── init.sql               # 数据库初始化脚本
│   ├── rls_policies.sql       # 行级安全策略
│   └── setup_guide.txt        # 设置指南
├── n8n/                       # n8n 工作流配置
│   ├── daily_recommendation_workflow.json
│   └── README.md
└── 设计文档.md                 # 详细设计文档
```

## 快速开始

### 前置要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.5+
- Supabase 账户（https://supabase.com）
- n8n 实例（可自托管或使用 n8n Cloud）

### 1. 设置 Supabase

1. 在 [Supabase Dashboard](https://supabase.com/dashboard) 创建新项目
2. 打开 SQL Editor，执行 `supabase/init.sql` 创建数据库表
3. 执行 `supabase/rls_policies.sql` 配置访问权限
4. 获取项目 URL 和 anon/public key：
   - Settings > API
   - 复制 `Project URL` 和 `anon public` key

### 2. 配置 Android 应用

1. 打开 `android/app/src/main/java/com/bandverse/app/data/local/SupabaseConfig.kt`
2. 替换占位符：
   ```kotlin
   const val SUPABASE_URL = "YOUR_SUPABASE_URL"
   const val SUPABASE_ANON_KEY = "YOUR_SUPABASE_ANON_KEY"
   ```

### 3. 设置 n8n 工作流

1. 登录 n8n 实例
2. 导入 `n8n/daily_recommendation_workflow.json`
3. 配置 Supabase 凭证：
   - 使用 Supabase 的 `service_role` key
   - 详见 `n8n/README.md`
4. 激活工作流，设置每日自动执行

### 4. 构建和运行 Android 应用

1. 在 Android Studio 中打开 `android` 目录
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 按钮（或按 Shift+F10）

## 数据库设计

### 核心表

- `profiles` - 用户档案
- `bands` - 乐队信息
- `albums` - 专辑信息
- `songs` - 歌曲信息
- `user_recommendations` - 用户推荐
- `daily_recommendations` - 每日推荐
- `recommendation_interactions` - 互动记录

详见 `设计文档.md` 第3章。

## n8n 工作流

### 每日推荐工作流

工作流每天自动执行以下步骤：

1. 检查今日是否已有推荐
2. 从已审核的用户推荐中随机选择一条
3. 保存为今日推荐
4. 如果没有用户推荐，使用备用方案（从数据库随机选择歌曲）

详见 `n8n/README.md`。

## 开发指南

### Android 项目架构

采用 MVVM 架构 + Clean Architecture：

- **Data Layer**: Repository、数据模型、Supabase 客户端
- **Domain Layer**: Use Cases、业务逻辑
- **Presentation Layer**: ViewModel、UI Composables

### 添加新功能

1. 在 `data/model` 中定义数据模型
2. 在 `data/repository` 中实现数据访问逻辑
3. 在 `domain/usecase` 中创建用例
4. 在 `presentation` 中实现 ViewModel
5. 在 `ui` 中创建 Compose UI 组件

## 部署指南

### Android 应用发布

1. 生成签名密钥
2. 配置 `build.gradle.kts` 的签名配置
3. 构建 release APK 或 AAB
4. 上传到 Google Play Console

### Supabase 备份

定期备份 Supabase 数据库：

```bash
# 使用 pg_dump 导出数据
pg_dump -h [project-ref].supabase.co -U postgres -d postgres -f backup.sql
```

### n8n 部署

- **自托管**: 使用 Docker 或在服务器上安装 n8n
- **n8n Cloud**: 使用官方云服务（https://n8n.cloud）

## 测试

### 数据库测试

在 Supabase SQL Editor 中测试：

```sql
-- 测试今日推荐
SELECT get_today_recommendation();

-- 测试用户推荐
SELECT * FROM get_user_recommendations('user-id-here');
```

### Android 测试

1. 单元测试：运行 `./gradlew test`
2. UI 测试：运行 `./gradlew connectedAndroidTest`

## 故障排查

### Android 应用问题

- **连接失败**: 检查 Supabase URL 和 API Key 配置
- **数据加载失败**: 检查网络连接和 RLS 策略
- **认证错误**: 确认使用正确的认证方式

### Supabase 问题

- **RLS 策略阻止访问**: 检查策略配置
- **查询慢**: 添加索引或优化查询
- **存储空间不足**: 清理旧数据或升级套餐

### n8n 问题

- **工作流未触发**: 确认工作流已激活
- **Supabase 连接失败**: 检查 service_role key 配置
- **推荐未生成**: 查看执行日志，检查是否有已审核的推荐

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

MIT License

## 联系方式

- 项目主页: https://github.com/yourusername/bandverse
- 问题反馈: https://github.com/yourusername/bandverse/issues

## 致谢

- Supabase - 提供后端基础设施
- n8n - 提供工作流引擎
- JetBrains Compose - 现代 UI 框架

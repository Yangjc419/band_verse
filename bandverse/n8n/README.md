# n8n 工作流配置指南

## 工作流概述

`daily_recommendation_workflow.json` 是用于生成每日推荐的主要工作流，每天自动从用户推荐中随机选择一条作为今日推荐。

## 工作流执行流程

1. **Cron Trigger** - 每天凌晨 2:00 触发
2. **Get Today's Date** - 获取当前日期
3. **Check Existing Recommendation** - 检查今日是否已有推荐
4. **Get Random Recommendation** - 从已审核的用户推荐中随机选择一条
5. **Save Daily Recommendation** - 保存为今日推荐
6. **Verify Recommendation** - 验证推荐是否成功保存
7. **Fallback** - 如果没有用户推荐，则从数据库随机选择歌曲和歌词

## 导入工作流到 n8n

### 方法一：使用 JSON 文件导入

1. 登录你的 n8n 实例
2. 点击右上角的 "+" 按钮创建新工作流
3. 点击右上角的三点菜单，选择 "Import from File" 或 "Import from URL"
4. 上传 `daily_recommendation_workflow.json` 文件
5. 点击 "Import"

### 方法二：复制 JSON 内容

1. 打开 `daily_recommendation_workflow.json` 文件
2. 复制全部 JSON 内容
3. 在 n8n 中点击 "+" 创建新工作流
4. 点击右上角的三点菜单，选择 "Copy Workflow JSON"
5. 粘贴之前复制的 JSON 内容

## 配置 Supabase 凭证

### 1. 获取 Service Role Key

1. 登录 Supabase Dashboard
2. 进入你的项目
3. 点击 Settings > API
4. 复制 `service_role` secret key

### 2. 在 n8n 中创建凭证

1. 在 n8n 工作流编辑器中，点击任意 Supabase 节点
2. 在 "Credentials" 下拉框中选择 "Create New Credential"
3. 选择 "Supabase API"
4. 填写以下信息：
   - **Name**: Supabase Service Role
   - **Project URL**: 你的 Supabase 项目 URL（如 https://xxx.supabase.co）
   - **API Key**: 粘贴之前复制的 service_role key
5. 点击 "Save"

### 3. 应用凭证到所有 Supabase 节点

1. 选择所有 Supabase 节点（可以在工作流中 Ctrl+Click 或 Cmd+Click 多选）
2. 在 Credentials 下拉框中选择刚创建的 "Supabase Service Role"
3. 所有节点都会使用相同的凭证

## 测试工作流

### 手动触发测试

1. 在 n8n 工作流编辑器中，点击 "Execute Workflow" 按钮
2. 检查执行日志，确认所有节点成功执行
3. 查看最后 "Format Success Message" 节点的输出

### 验证数据库

在 Supabase SQL Editor 中执行：

```sql
SELECT 
  dr.recommended_date,
  ur.lyric,
  s.title as song_title,
  b.name as band_name,
  p.username,
  p.avatar_url
FROM daily_recommendations dr
JOIN user_recommendations ur ON dr.user_recommendation_id = ur.id
JOIN songs s ON ur.song_id = s.id
JOIN bands b ON ur.band_id = b.id
JOIN profiles p ON ur.user_id = p.id
ORDER BY dr.recommended_date DESC
LIMIT 5;
```

## 配置定时触发

### 修改触发时间

默认设置为每 24 小时触发一次。要修改触发时间：

1. 点击 "Cron Trigger" 节点
2. 在 "Rule" 部分修改时间设置
3. 使用 Cron 表达式，例如：
   - `0 2 * * *` - 每天凌晨 2:00
   - `0 0 * * *` - 每天午夜
   - `0 */6 * * *` - 每 6 小时

### 启用工作流

1. 点击工作流右上角的开关按钮
2. 工作流状态变为 "Active"
3. 定时器将按照配置自动执行

## 监控和日志

### 查看执行历史

1. 点击左侧菜单 "Executions"
2. 可以查看所有工作流的执行历史
3. 点击某次执行可以查看详细日志

### 设置错误通知（可选）

如果工作流执行失败，可以配置通知：

1. 在工作流中添加 "Email" 或 "Slack" 节点
2. 将 "Fallback" 分支的错误消息连接到通知节点
3. 配置接收通知的邮箱或 Slack 频道

## 故障排查

### 问题：Supabase 连接失败

**解决方案：**
- 确认 Supabase 项目 URL 和 API Key 正确
- 确认使用的是 `service_role` key，不是 `anon` key
- 检查网络连接

### 问题：没有找到用户推荐

**解决方案：**
- 确认 `user_recommendations` 表有状态为 `approved` 的记录
- 检查 RLS 策略是否允许 service_role 访问
- 可以手动插入一条测试推荐

### 问题：工作流执行但没有数据保存

**解决方案：**
- 检查 SQL 查询语法是否正确
- 查看节点执行日志中的错误信息
- 在 Supabase 中手动执行 SQL 语句验证

### 问题：定时器未触发

**解决方案：**
- 确认工作流已激活（Active 状态）
- 检查 Cron 表达式是否正确
- 查看 n8n 服务器时间是否正确

## 高级配置

### 添加推荐去重逻辑

如果希望避免短时间内重复推荐同一乐队或歌曲：

1. 在 "Get Random Recommendation" 节点之前添加一个 SQL 节点
2. 查询最近 7 天已推荐的乐队 ID 和歌曲 ID
3. 在随机查询中添加 `WHERE NOT IN` 条件排除这些 ID

### 添加推荐日志

如果需要记录每次推荐的详细信息：

1. 在工作流末尾添加一个 "Code" 节点
2. 将推荐信息格式化为 JSON
3. 添加一个 Supabase 节点，将日志插入 `recommendation_logs` 表

### 多语言支持

如果需要支持多语言推荐：

1. 在 `user_recommendations` 表中添加 `language` 字段
2. 在随机选择时按语言过滤
3. 可以创建多个工作流，每个语言一个

## 安全建议

1. **不要在前端代码中使用 service_role key**
   - service_role key 应仅用于 n8n 等后端服务
   - Android 应用应使用 anon key

2. **定期轮换 API Key**
   - 在 Supabase Dashboard 中定期生成新的 service_role key
   - 更新 n8n 凭证

3. **限制 n8n 访问权限**
   - 使用防火墙规则限制 n8n 服务器的出站访问
   - 考虑使用 VPC 私有连接

## 相关资源

- [n8n 官方文档](https://docs.n8n.io/)
- [Supabase API 文档](https://supabase.com/docs/guides/api)
- [Cron 表达式参考](https://crontab.guru/)

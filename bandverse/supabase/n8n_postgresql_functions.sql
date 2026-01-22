-- ============================================
-- n8n 工作流所需 PostgreSQL Functions
-- Bandverse 每日推荐系统
-- ============================================
-- 执行说明：
-- 1. 登录 Supabase Dashboard
-- 2. 打开 SQL Editor
-- 3. 复制本文件全部内容
-- 4. 粘贴并执行
-- ============================================

-- ============================================
-- 函数 1: 检查现有推荐
-- 作用：查询指定日期是否已有推荐
-- ============================================
CREATE OR REPLACE FUNCTION get_existing_recommendation(p_date DATE)
RETURNS BOOLEAN AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1
    FROM daily_recommendations
    WHERE recommended_date = p_date
  );
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 2: 获取随机用户推荐
-- 作用：从已审核的用户推荐中随机选择一条，包含关联数据
-- ============================================
CREATE OR REPLACE FUNCTION get_random_recommendation()
RETURNS TABLE (
  id UUID,
  user_id UUID,
  song_id UUID,
  lyric TEXT,
  band_id UUID,
  recommendation_reason TEXT,
  status TEXT,
  username TEXT,
  avatar_url TEXT,
  song_title TEXT,
  band_name TEXT
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    ur.id,
    ur.user_id,
    ur.song_id,
    ur.lyric,
    ur.band_id,
    ur.recommendation_reason,
    ur.status,
    p.username,
    p.avatar_url,
    s.title AS song_title,
    b.name AS band_name
  FROM user_recommendations ur
  JOIN profiles p ON ur.user_id = p.id
  JOIN songs s ON ur.song_id = s.id
  JOIN bands b ON ur.band_id = b.id
  WHERE ur.status = 'approved'
  ORDER BY RANDOM()
  LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 3: 获取随机歌曲（备用方案）
-- 作用：当没有用户推荐时，随机选择一首带歌词的歌曲
-- ============================================
CREATE OR REPLACE FUNCTION get_random_song()
RETURNS TABLE (
  id UUID,
  album_id UUID,
  title TEXT,
  duration INTEGER,
  lyrics TEXT,
  band_id UUID,
  band_name TEXT,
  album_title TEXT
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    s.id,
    s.album_id,
    s.title,
    s.duration,
    s.lyrics,
    a.band_id,
    b.name AS band_name,
    a.title AS album_title
  FROM songs s
  JOIN albums a ON s.album_id = a.id
  JOIN bands b ON a.band_id = b.id
  WHERE s.lyrics IS NOT NULL
    AND LENGTH(s.lyrics) > 100
  ORDER BY RANDOM()
  LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 4: 从歌曲中选择随机歌词
-- 作用：将歌曲歌词按行分割，随机选择一行
-- ============================================
CREATE OR REPLACE FUNCTION get_random_lyric(p_song_id UUID)
RETURNS TEXT AS $$
DECLARE
  v_lyric TEXT;
BEGIN
  SELECT unnest(string_to_array(s.lyrics, E'\n')) INTO v_lyric
  FROM songs s
  WHERE s.id = p_song_id
    AND LENGTH(s.lyrics) > 10
  ORDER BY RANDOM()
  LIMIT 1;

  RETURN v_lyric;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 5: 保存每日推荐
-- 作用：将用户推荐保存为今日推荐
-- ============================================
CREATE OR REPLACE FUNCTION save_daily_recommendation(
  p_date DATE,
  p_user_recommendation_id UUID
)
RETURNS UUID AS $$
DECLARE
  v_id UUID;
BEGIN
  INSERT INTO daily_recommendations (recommended_date, user_recommendation_id, created_at)
  VALUES (p_date, p_user_recommendation_id, NOW())
  RETURNING id INTO v_id;

  RETURN v_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 6: 创建系统推荐
-- 作用：当没有用户推荐时，创建系统用户的推荐
-- ============================================
CREATE OR REPLACE FUNCTION create_system_recommendation(
  p_song_id UUID,
  p_lyric TEXT,
  p_band_id UUID
)
RETURNS UUID AS $$
DECLARE
  v_id UUID;
BEGIN
  INSERT INTO user_recommendations (
    user_id,
    song_id,
    lyric,
    band_id,
    recommendation_reason,
    status,
    created_at
  ) VALUES (
    '00000000-0000-0000-0000-000000000000',
    p_song_id,
    p_lyric,
    p_band_id,
    '系统推荐',
    'approved',
    NOW()
  )
  RETURNING id INTO v_id;

  RETURN v_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 函数 7: 获取每日推荐详情
-- 作用：查询今日推荐的完整信息（包含所有关联表数据）
-- ============================================
CREATE OR REPLACE FUNCTION get_daily_recommendation_detail(p_date DATE)
RETURNS TABLE (
  id UUID,
  recommended_date DATE,
  created_at TIMESTAMPTZ,
  lyric TEXT,
  recommendation_reason TEXT,
  song_title TEXT,
  band_name TEXT,
  username TEXT,
  avatar_url TEXT
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    dr.id,
    dr.recommended_date,
    dr.created_at,
    ur.lyric,
    ur.recommendation_reason,
    s.title AS song_title,
    b.name AS band_name,
    p.username,
    p.avatar_url
  FROM daily_recommendations dr
  JOIN user_recommendations ur ON dr.user_recommendation_id = ur.id
  JOIN songs s ON ur.song_id = s.id
  JOIN bands b ON ur.band_id = b.id
  JOIN profiles p ON ur.user_id = p.id
  WHERE dr.recommended_date = p_date;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 测试函数（可选）
-- 取消注释以下语句来测试函数是否创建成功
-- ============================================

-- 测试函数 1: 检查现有推荐
-- SELECT get_existing_recommendation('2026-01-13');

-- 测试函数 2: 获取随机推荐
-- SELECT * FROM get_random_recommendation();

-- 测试函数 3: 获取随机歌曲
-- SELECT * FROM get_random_song();

-- 测试函数 4: 保存每日推荐（需要有效的推荐 ID）
-- SELECT save_daily_recommendation(
--   '2026-01-13',
--   '550e8400-e29b-41d4-a716-446655440000'::UUID
-- );

-- 测试函数 5: 创建系统推荐（需要有效的歌曲和乐队 ID）
-- SELECT create_system_recommendation(
--   '550e8400-e29b-41d4-a716-446655440000'::UUID,
--   '测试歌词',
--   '660e8400-e29b-41d4-a716-446655440001'::UUID
-- );

-- 测试函数 6: 获取今日推荐详情
-- SELECT * FROM get_daily_recommendation_detail('2026-01-13');

-- ============================================
-- 执行结果检查
-- 如果所有函数都创建成功，应该看到：
-- CREATE FUNCTION
-- ============================================

-- ============================================
-- Row Level Security (RLS) Policies
-- ============================================

-- Enable RLS on all tables
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE bands ENABLE ROW LEVEL SECURITY;
ALTER TABLE albums ENABLE ROW LEVEL SECURITY;
ALTER TABLE songs ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE daily_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE recommendation_interactions ENABLE ROW LEVEL SECURITY;

-- ============================================
-- Profiles Policies
-- ============================================

-- Everyone can view profiles
CREATE POLICY "Public profiles are viewable by everyone"
  ON profiles FOR SELECT
  USING (true);

-- Users can update their own profile
CREATE POLICY "Users can update own profile"
  ON profiles FOR UPDATE
  USING (auth.uid() = id);

-- ============================================
-- Bands Policies
-- ============================================

-- Everyone can view bands
CREATE POLICY "Bands are viewable by everyone"
  ON bands FOR SELECT
  USING (true);

-- Authenticated users can insert bands (optional, based on your requirements)
CREATE POLICY "Authenticated users can insert bands"
  ON bands FOR INSERT
  WITH CHECK (auth.role() = 'authenticated');

-- ============================================
-- Albums Policies
-- ============================================

-- Everyone can view albums
CREATE POLICY "Albums are viewable by everyone"
  ON albums FOR SELECT
  USING (true);

-- ============================================
-- Songs Policies
-- ============================================

-- Everyone can view songs
CREATE POLICY "Songs are viewable by everyone"
  ON songs FOR SELECT
  USING (true);

-- ============================================
-- User Recommendations Policies
-- ============================================

-- Everyone can view approved recommendations
CREATE POLICY "Approved recommendations are viewable by everyone"
  ON user_recommendations FOR SELECT
  USING (status = 'approved');

-- Users can view their own recommendations
CREATE POLICY "Users can view own recommendations"
  ON user_recommendations FOR SELECT
  USING (auth.uid() = user_id);

-- Authenticated users can insert recommendations
CREATE POLICY "Authenticated users can insert recommendations"
  ON user_recommendations FOR INSERT
  WITH CHECK (auth.uid() = user_id);

-- Users can update their own recommendations
CREATE POLICY "Users can update own recommendations"
  ON user_recommendations FOR UPDATE
  USING (auth.uid() = user_id);

-- Users can delete their own recommendations
CREATE POLICY "Users can delete own recommendations"
  ON user_recommendations FOR DELETE
  USING (auth.uid() = user_id);

-- ============================================
-- Daily Recommendations Policies
-- ============================================

-- Everyone can view daily recommendations
CREATE POLICY "Daily recommendations are viewable by everyone"
  ON daily_recommendations FOR SELECT
  USING (true);

-- Service role can insert daily recommendations (for n8n workflow)
CREATE POLICY "Service role can insert daily recommendations"
  ON daily_recommendations FOR INSERT
  WITH CHECK (auth.role() = 'service_role');

-- ============================================
-- Recommendation Interactions Policies
-- ============================================

-- Everyone can view interactions
CREATE POLICY "Interactions are viewable by everyone"
  ON recommendation_interactions FOR SELECT
  USING (true);

-- Authenticated users can insert interactions
CREATE POLICY "Authenticated users can insert interactions"
  ON recommendation_interactions FOR INSERT
  WITH CHECK (auth.uid() = user_id);

-- Users can delete their own interactions
CREATE POLICY "Users can delete own interactions"
  ON recommendation_interactions FOR DELETE
  USING (auth.uid() = user_id);

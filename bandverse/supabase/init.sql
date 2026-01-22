-- ============================================
-- Bandverse Database Schema
-- ============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- Tables
-- ============================================

-- Users/Profiles Table
CREATE TABLE IF NOT EXISTS profiles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  username TEXT UNIQUE NOT NULL,
  email TEXT UNIQUE,
  avatar_url TEXT,
  bio TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Bands Table
CREATE TABLE IF NOT EXISTS bands (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  formation_year INTEGER,
  country TEXT,
  city TEXT,
  bio TEXT,
  image_url TEXT,
  genres JSONB NOT NULL DEFAULT '[]'::jsonb,
  status TEXT DEFAULT 'active',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Albums Table
CREATE TABLE IF NOT EXISTS albums (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  band_id UUID REFERENCES bands(id) ON DELETE CASCADE NOT NULL,
  title TEXT NOT NULL,
  release_year INTEGER,
  cover_url TEXT,
  type TEXT DEFAULT 'album',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Songs Table
CREATE TABLE IF NOT EXISTS songs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  album_id UUID REFERENCES albums(id) ON DELETE CASCADE NOT NULL,
  title TEXT NOT NULL,
  duration INTEGER,
  lyrics TEXT,
  spotify_url TEXT,
  youtube_url TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- User Recommendations Table
CREATE TABLE IF NOT EXISTS user_recommendations (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
  song_id UUID REFERENCES songs(id) ON DELETE CASCADE NOT NULL,
  lyric TEXT NOT NULL,
  band_id UUID REFERENCES bands(id) ON DELETE CASCADE NOT NULL,
  recommendation_reason TEXT,
  status TEXT DEFAULT 'pending',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Daily Recommendations Table
CREATE TABLE IF NOT EXISTS daily_recommendations (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  recommended_date DATE NOT NULL,
  user_recommendation_id UUID REFERENCES user_recommendations(id) ON DELETE CASCADE NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(recommended_date)
);

-- Recommendation Interactions Table
CREATE TABLE IF NOT EXISTS recommendation_interactions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
  recommendation_id UUID REFERENCES daily_recommendations(id) ON DELETE CASCADE NOT NULL,
  interaction_type TEXT NOT NULL,
  content TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, recommendation_id, interaction_type)
);

-- ============================================
-- Indexes
-- ============================================

CREATE INDEX IF NOT EXISTS idx_user_recommendations_user_id ON user_recommendations(user_id);
CREATE INDEX IF NOT EXISTS idx_user_recommendations_song_id ON user_recommendations(song_id);
CREATE INDEX IF NOT EXISTS idx_user_recommendations_status ON user_recommendations(status);
CREATE INDEX IF NOT EXISTS idx_user_recommendations_created_at ON user_recommendations(created_at);
CREATE INDEX IF NOT EXISTS idx_daily_recommendations_date ON daily_recommendations(recommended_date);
CREATE INDEX IF NOT EXISTS idx_songs_album_id ON songs(album_id);
CREATE INDEX IF NOT EXISTS idx_albums_band_id ON albums(band_id);

-- ============================================
-- Functions
-- ============================================

-- Get Today's Recommendation
CREATE OR REPLACE FUNCTION get_today_recommendation()
RETURNS JSONB AS $$
DECLARE
  result JSONB;
BEGIN
  SELECT jsonb_build_object(
    'id', dr.id,
    'recommended_date', dr.recommended_date,
    'lyric', ur.lyric,
    'reason', ur.recommendation_reason,
    'song', (SELECT row_to_json(s) FROM songs s WHERE s.id = ur.song_id),
    'band', (SELECT row_to_json(b) FROM bands b WHERE b.id = ur.band_id),
    'user', (SELECT row_to_json(p) FROM profiles p WHERE p.id = ur.user_id),
    'created_at', dr.created_at
  ) INTO result
  FROM daily_recommendations dr
  JOIN user_recommendations ur ON dr.user_recommendation_id = ur.id
  WHERE dr.recommended_date = CURRENT_DATE;
  
  RETURN COALESCE(result, '{}'::JSONB);
END;
$$ LANGUAGE plpgsql;

-- Get User Recommendations History
CREATE OR REPLACE FUNCTION get_user_recommendations(user_param UUID)
RETURNS TABLE (
  id UUID,
  lyric TEXT,
  recommendation_reason TEXT,
  song JSONB,
  band JSONB,
  created_at TIMESTAMPTZ
) AS $$
BEGIN
  RETURN QUERY
  SELECT 
    ur.id,
    ur.lyric,
    ur.recommendation_reason,
    (SELECT row_to_json(s) FROM songs s WHERE s.id = ur.song_id),
    (SELECT row_to_json(b) FROM bands b WHERE b.id = ur.band_id),
    ur.created_at
  FROM user_recommendations ur
  WHERE ur.user_id = user_param
  ORDER BY ur.created_at DESC;
END;
$$ LANGUAGE plpgsql;

-- Update timestamp trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Triggers
-- ============================================

CREATE TRIGGER update_profiles_updated_at BEFORE UPDATE ON profiles
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_recommendations_updated_at BEFORE UPDATE ON user_recommendations
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Sample Data
-- ============================================

-- Insert sample bands
INSERT INTO bands (name, formation_year, country, city, bio, image_url, genres) VALUES
('The Beatles', 1960, 'UK', 'Liverpool', 'Legendary rock band', 'https://example.com/beatles.jpg', '["rock", "pop"]'::jsonb),
('Pink Floyd', 1965, 'UK', 'London', 'Progressive rock pioneers', 'https://example.com/pinkfloyd.jpg', '["progressive rock", "psychedelic"]'::jsonb),
('Radiohead', 1985, 'UK', 'Abingdon', 'Alternative rock band', 'https://example.com/radiohead.jpg', '["alternative rock", "experimental"]'::jsonb);

-- Insert sample albums
INSERT INTO albums (band_id, title, release_year, type) VALUES
((SELECT id FROM bands WHERE name = 'The Beatles'), 'Abbey Road', 1969, 'album'),
((SELECT id FROM bands WHERE name = 'Pink Floyd'), 'The Dark Side of the Moon', 1973, 'album'),
((SELECT id FROM bands WHERE name = 'Radiohead'), 'OK Computer', 1997, 'album');

-- Insert sample songs with lyrics
INSERT INTO songs (album_id, title, duration, lyrics) VALUES
((SELECT id FROM albums WHERE title = 'Abbey Road'), 'Come Together', 279, 'Here come old flattop, he come grooving up slowly
He got joo-joo eyeball, he one holy roller
He got hair down to his knee
Got to be a joker, he just do what he please

He wear no shoeshine, he got toe-jam football
He got monkey finger, he shoot Coca-Cola
He say I know you, you know me
One thing I can tell you is you got to be free
Come together, right now, over me'),
((SELECT id FROM albums WHERE title = 'The Dark Side of the Moon'), 'Time', 396, 'Ticking away, the moments that make up a dull day
Fritter and waste the hours in an offhand way
Kicking around on a piece of ground in your hometown
Waiting for someone or something to show you the way

Tired of lying in the sunshine, staying home to watch the rain
You are young and life is long, and there is time to kill today
And then one day you find, ten years have got behind you
No one told you when to run, you missed the starting gun'),
((SELECT id FROM albums WHERE title = 'OK Computer'), 'Paranoid Android', 382, 'Please could you stop the noise, I''m trying to get some rest
From all the unborn chicken voices in my head
What''s there? (I may be paranoid, but not an android)
What''s there? (I may be paranoid, but not an android)

When I am king, you will be first against the wall
With your opinion which is of no consequence at all
What''s there? (I may be paranoid, but no android)
What''s there? (I may be paranoid, but no android)');

-- Insert sample profiles
INSERT INTO profiles (username, email, avatar_url, bio) VALUES
('music_lover', 'user1@example.com', 'https://example.com/avatar1.jpg', 'Music is life'),
('band_fan', 'user2@example.com', 'https://example.com/avatar2.jpg', 'Rock & Roll forever'),
('melody_seeker', 'user3@example.com', 'https://example.com/avatar3.jpg', 'Exploring new sounds every day');

-- Insert sample user recommendations
INSERT INTO user_recommendations (user_id, song_id, lyric, band_id, recommendation_reason, status) VALUES
((SELECT id FROM profiles WHERE username = 'music_lover'), 
 (SELECT id FROM songs WHERE title = 'Come Together'), 
 'Come together, right now, over me',
 (SELECT id FROM bands WHERE name = 'The Beatles'),
 'This song captures the essence of unity and togetherness. The bassline is pure magic.',
 'approved'),
((SELECT id FROM profiles WHERE username = 'band_fan'),
 (SELECT id FROM songs WHERE title = 'Time'),
 'And then one day you find, ten years have got behind you',
 (SELECT id FROM bands WHERE name = 'Pink Floyd'),
 'A profound reflection on the passage of time. The clock sounds at the beginning are haunting.',
 'approved');

-- Insert sample daily recommendation
INSERT INTO daily_recommendations (recommended_date, user_recommendation_id) VALUES
(CURRENT_DATE, 
 (SELECT id FROM user_recommendations WHERE lyric = 'Come together, right now, over me'));

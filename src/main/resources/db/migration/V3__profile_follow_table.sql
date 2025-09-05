CREATE TABLE IF NOT EXISTS profile_follows (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  follower_profile_id BIGINT NOT NULL REFERENCES profiles(id),
  followed_profile_id BIGINT NOT NULL REFERENCES profiles(id),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  CONSTRAINT ux_follow_unique UNIQUE (follower_profile_id, followed_profile_id)
);

CREATE INDEX IF NOT EXISTS idx_follow_follower ON profile_follows(follower_profile_id);
CREATE INDEX IF NOT EXISTS idx_follow_followed ON profile_follows(followed_profile_id);

ALTER TABLE profile_stats
  ADD COLUMN IF NOT EXISTS following_count INT DEFAULT 0;

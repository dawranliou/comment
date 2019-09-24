CREATE TABLE IF NOT EXISTS comment(
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  date INTEGER DEFAULT (strftime('%s', 'now')),
  slug VARCHAR(255) NOT NULL,
  parent_comment_id INTEGER,
  text VARCHAR(5000) NOT NULL
);

CREATE TABLE IF NOT EXISTS user(
  id integer primary key,
  email text,
  display_name text,
  password text,
  updated_at integer,
  created_at integer not null default (strftime('%s', 'now')) );

-- :name all-users
-- :command :query
-- :result :many
-- :doc Selects all users
SELECT
  id,
  email,
  display_name,
  password,
  updated_at,
  created_at
FROM
  user;

-- :name get-user-by-id
-- :command :query
-- :result :one
-- :doc Selects the user matching the id
SELECT
  id,
  email,
  display_name,
  password,
  updated_at,
  created_at
FROM
  user
WHERE
  id = :id ::uuid;

-- :name insert-user!
-- :command :insert
-- :result :raw
-- :doc Inserts a single user into account table
INSERT INTO user (
  email,
  display_name,
  password
)
VALUES (
  :email,
  :display_name,
  :password
);

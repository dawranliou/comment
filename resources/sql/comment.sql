-- :name all-comments
-- :command :query
-- :result :many
-- :doc Selects all comments
SELECT
  *
FROM
  comment
ORDER BY date DESC;

-- :name get-comments-by-slug
-- :command :query
-- :result :many
-- :doc Select comments by slug
SELECT
  *
FROM
  comment
WHERE
  slug = :slug
ORDER BY date DESC;

-- :name create-comment
-- :command :insert
-- :result :one
-- :doc Create a comment
INSERT INTO comment (name, slug, text, parent_comment_id)
VALUES (:name, :slug, :text, :parent-comment-id);

-- :name update-comment
-- :command :execute
-- :result :one
-- :doc Update a comment
UPDATE comment
SET name = :name, slug = :slug, text = :text, parent_comment_id = :parent-comment-id
WHERE id = :id;

-- :name delete-comment
-- :command :execute
-- :result :one
-- :doc Delete a comment
DELETE FROM comment WHERE id = :id

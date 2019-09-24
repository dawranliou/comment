(ns comment.db
  (:require
   [hugsql.core :as hugsql :refer [def-db-fns]]
   [hugsql.adapter.next-jdbc :as next-adapter]
   [next.jdbc.connection :as connection]
   [next.jdbc.result-set :as result-set])
  (:import
   (com.zaxxer.hikari HikariDataSource)))

(defn setup-connection-pool [db-spec]
  (connection/->pool HikariDataSource db-spec))

(defn stop-connection-pool [datasource]
  (.close datasource))

(hugsql/set-adapter! (next-adapter/hugsql-adapter-next-jdbc {:builder-fn result-set/as-maps}))

(def-db-fns "sql/user.sql")
(def-db-fns "sql/comment.sql")

(comment
  (require '[comment.config :as config])
  (def ds (setup-connection-pool (:db (config/system-config))))
  (require '[next.jdbc :as jdbc])
  (jdbc/execute! ds ["select * from comment"])
  (all-comments ds)
  (create-comment ds {:name "me" :slug "hi" :text "not bad" :parent-comment-id nil})
  (all-users ds)
  (get-user-by-email ds {:email "aa@email"})
  (require '[buddy.hashers :as hashers])
  (insert-user! ds (-> {:email "aa@email" :password "pass" :display_name "aa"}
                       (update :password hashers/derive))))

(ns comment.db
  (:require
   [hugsql.core :as hugsql :refer [def-db-fns]]
   [hugsql.adapter.next-jdbc :as next-adapter]
   [next.jdbc.connection :as connection]
   [next.jdbc.result-set :as result-set])
  (:import
   (com.zaxxer.hikari HikariDataSource)))

(defn setup-connection-pool [db-spec]
  {:datasource (connection/->pool HikariDataSource db-spec)})

(defn stop-connection-pool [{:keys [datasource]}]
  (.close datasource))

(hugsql/set-adapter! (next-adapter/hugsql-adapter-next-jdbc {:builder-fn result-set/as-maps}))

(def-db-fns "sql/user.sql")

(defn all-users [ds]
  (jdbc/execute! ds ["select * from user"]))

(comment
  (require '[integrant.repl.state])
  (def ds (get-in integrant.repl.state/system [:db :datasource]))
  (all-users ds)
  (insert-user! ds {:email "aa@email" :password "pass" :display_name "aa"}))

(ns comment.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection])
  (:import
   (com.zaxxer.hikari HikariDataSource)))

(defn setup-connection-pool [db-spec]
  {:datasource (connection/->pool HikariDataSource db-spec)})

(defn stop-connection-pool [{:keys [datasource]}]
  (.close datasource))

(ns comment.middleware)

(def db
  {:name    ::db
   :compile (fn [router-data router-opts]
              (fn [handler]
                (fn [request]
                  (handler (assoc request :db (:db router-data))))))})

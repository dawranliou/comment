(ns comment.auth
  (:require
   [buddy.auth.backends :as backends]
   [buddy.auth.backends.httpbasic :as backends-httpbasic]
   [buddy.hashers :as hashers]
   [comment.config :as config]
   [comment.db :as db]
   [comment.jwt :as jwt]))

(defn basic-auth
  [db request {:keys [email password]}]
  (let [user (db/get-user-by-email db email)]
    (if (and user (hashers/check password (:user/password user)))
      (-> user
          (dissoc :user/password)
          (assoc :user/token (jwt/create-token user)))
      false)))

(defn basic-auth-backend
  [db]
  (backends-httpbasic/http-basic-backend {:authfn (partial basic-auth db)}))

(def token-backend
  (backends/jws {:secret (get-in (config/secrets) [:jwt :auth-key]) :options {:alg :hs512}}))

(ns comment.jwt
  (:require
   [buddy.sign.jwt :as jwt]
   [comment.config :as config]))

(defn sign [payload]
  (jwt/sign payload (get-in (config/secrets) [:jwt :auth-key]) {:alg :hs512}))

(defn unsign [payload]
  (jwt/unsign payload (get-in (config/secrets) [:jwt :auth-key]) {:alg :hs512}))

(defn create-token
  "Creates signed jwt-token with user data as payload.
  `valid-seconds` sets the expiration span
  `terse?` include only users :id in payload (fits in URL)"
  [user & {:keys [terse? valid-seconds]
           :or   {terse?        false
                  valid-seconds 7200}}] ;; 2 hours
  (let [fields  (if terse?
                  [:id]
                  [:id :email :username :user-data :permissions])
        payload (-> user
                    (select-keys fields)
                    (assoc :exp (.plusSeconds
                                 (java.time.Instant/now) valid-seconds)))]
    (sign payload)))

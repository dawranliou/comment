(ns user
  (:require
   [integrant.repl :as ig-repl]
   [comment.system :as system]
   [comment.config :as config]))

(ig-repl/set-prep! config/system-config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)

(defn system []
  integrant.repl.state/system)

(comment
  (go)
  (reset)
  (halt))

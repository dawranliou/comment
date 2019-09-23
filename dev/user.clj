(ns user
  (:require
   [integrant.repl :as ig-repl]
   [comment.config :as config]))

(ig-repl/set-prep! config/default-config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)

(ns user
  (:require
   [integrant.repl :as ig-repl]
   [comment.system :as system]))

(ig-repl/set-prep! system/system-config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)

(comment
  (go)
  (halt)
  (reset)

  integrant.repl.state/system)

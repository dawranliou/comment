(ns comment.handler
  (:require
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [ring.middleware.params :as params]))

(def ok (constantly {:status 200 :body "ok"}))

(defn create-app [{:keys [db]}]
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info {:title "my-api"}}
             :handler (swagger/create-swagger-handler)}}]

     ["/comments"
      {:swagger {:tags ["comments"]}}

      [""
       {:get  {:summary "get comments"
               :handler ok}
        :post {:summary "create comments"
               :handler ok}}]

      ["/:slug"
       {:get {:summary    "get all comments for a page"
              :parameters {:path {:slug string?}}
              :handler    ok}}]

      ["/id/:id"
       {:parameters {:path {:id int?}}}
       ["" {:put    {:summary "update a comment"
                     :handler ok}
            :delete {:summary "delete a comment"
                     :handler ok}}]]]]
    {:data
     {:coercion   reitit.coercion.spec/coercion
      :muuntaja   m/instance
      :middleware [;; query-params & form-params
                   params/wrap-params
                   ;; content-negotiation
                   muuntaja/format-negotiate-middleware
                   ;; encoding response body
                   muuntaja/format-response-middleware
                   ;; exception handling
                   exception/exception-middleware
                   ;; decoding request body
                   muuntaja/format-request-middleware
                   ;; coercing response bodys
                   coercion/coerce-response-middleware
                   ;; coercing request parameters
                   coercion/coerce-request-middleware]}})

   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"})
    (ring/create-default-handler))))

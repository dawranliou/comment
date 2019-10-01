(ns comment.handler
  (:require
   [reitit.ring :as ring]
   [reitit.coercion.spec]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.dev.pretty :as pretty]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.dev :as dev]
   [reitit.ring.spec :as spec]
   [spec-tools.spell :as spell]
   [ring.adapter.jetty :as jetty]
   [muuntaja.core :as m]
   [comment.middleware :as mw]))

(defn ok [{:keys [db] :as request}]
  (println "db:"db)
  {:status 200 :body "ok"})

(defn create-app
  [db]
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info {:title "my-api"}}
             :handler (swagger/create-swagger-handler)}}]

     ["/comments"
      {:swagger {:tags ["comments"]}}

      [""
       {:get {:summary "get comments"
              :handler ok}

        :post {:summary    "create comments"
               :parameters {:body {:name              string?
                                   :slug              string?
                                   :text              string?
                                   :parent-comment-id int?}}
               :handler    ok}}]

      ["/:slug"
       {:get {:summary    "get all comments for a page"
              :parameters {:path {:slug string?}}
              :handler    ok}}]

      ["/id/:id"
       {:parameters {:path {:id int?}}}
       ["" {:put {:summary    "update a comment"
                  :parameters {:body {:name              string?
                                      :slug              string?
                                      :text              string?
                                      :parent-comment-id int?}}
                  :handler    ok}

            :delete {:summary "delete a comment"
                     :handler ok}}]]]]
    {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
     ;;:validate                    spec/validate           ;; enable spec validation for route data
     ;;:reitit.spec/wrap            spell/closed            ;; strict top-level validation
     :exception pretty/exception
     :data      {:db         db
                 :coercion   reitit.coercion.spec/coercion
                 :muuntaja   m/instance
                 :middleware [;; swagger feature
                              swagger/swagger-feature
                              ;; query-params & form-params
                              parameters/parameters-middleware
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
                              coercion/coerce-request-middleware
                              ;; multipart
                              multipart/multipart-middleware

                              mw/db]}})

   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"})
    (ring/create-default-handler))))

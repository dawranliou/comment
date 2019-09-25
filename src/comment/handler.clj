(ns comment.handler
  (:require
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [comment.db]))

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
       {:get {:summary "get comments"
              :handler
              (fn [_]
                {:status 200
                 :body   (comment.db/all-comments db)})}

        :post {:summary    "create comments"
               :parameters {:body {:name              string?
                                   :slug              string?
                                   :text              string?
                                   :parent-comment-id int?}}
               :handler
               (fn [{:keys [body-params] :as req}]
                 {:status 201
                  :body   (comment.db/create-comment db body-params)})}}]

      ["/:slug"
       {:get {:summary    "get all comments for a page"
              :parameters {:path {:slug string?}}
              :handler
              (fn [{:keys [parameters]}]
                (let [slug (-> parameters :path (select-keys [:slug]))]
                  {:status 200
                   :body   (comment.db/get-comments-by-slug db slug)}))}}]

      ["/id/:id"
       {:parameters {:path {:id int?}}}
       ["" {:put {:summary    "update a comment"
                  :parameters {:body {:name              string?
                                      :slug              string?
                                      :text              string?
                                      :parent-comment-id int?}}
                  :handler
                  (fn [{:keys [path-params body-params]}]
                    (let [params (merge (select-keys path-params [:id])
                                        (select-keys body-params [:name :slug :text :parent-comment-id]))]
                      {:status 200
                       :body   (comment.db/update-comment db params)}))}

            :delete {:summary "delete a comment"
                     :handler
                     (fn [{:keys [parameters]}]
                       (let [id (-> parameters :path (select-keys [:id]))]
                         {:status 200
                          :body   (comment.db/delete-comment db id)}))}}]]]]
    {:data
     {:coercion   reitit.coercion.spec/coercion
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
                   coercion/coerce-request-middleware]}})

   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path   "/"
      :config {:validatorUrl     nil
               :operationsSorter "alpha"}})
    (ring/create-default-handler))))

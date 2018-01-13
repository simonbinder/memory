(ns memory.server.core
  (:use org.httpkit.server)
  (:require
  [taoensso.sente :as sente]
  [compojure.core :as compojure]
  [ring.middleware.params :refer [wrap-params]]
  [ring.middleware.keyword-params :refer [wrap-keyword-params]]
  [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]))

;;(declare channel-socket)
;;(defmulti event :id)

  (let [{:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]}
        (sente/make-channel-socket! (get-sch-adapter) {})]

    (def ring-ajax-post                ajax-post-fn)
    (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
    (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
    (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
    (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(compojure/defroutes my-app-routes
  (compojure/GET "/" [] {:body "Hello World!"
                           :status 200
                           :headers {"Content-Type" "text/plain"}})
  (compojure/GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (compojure/POST "/chsk" req (ring-ajax-post                req))
)

;;(defn start-router []
;;  (defonce router
;;    (sente/start-chsk-router! (:ch-recv channel-socket) event)))

(def my-app
  (-> my-app-routes
      ;; Add necessary Ring middleware:
      (wrap-keyword-params)
      (wrap-params)))

(defn -main []
  (println "Server started...")
;;  (start-router)
        (run-server #'my-app {:join? false :port 8080}))

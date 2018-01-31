(ns memory.server.core
  (:use org.httpkit.server)
  (:require
   [taoensso.sente :as sente]
   [compojure.core :as compojure]
   [ring.middleware.cors :as cors]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.util.response :as response]
   [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]))

  ;; currently client-id and uid are the same
(defn create-user-id [{:keys [params]}]
  (:client-id params))

(let [packer :edn
        chsk-server
        (sente/make-channel-socket-server!
                   (get-sch-adapter) {:packer packer :user-id-fn create-user-id})

        {:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]}
        chsk-server]

     (def ring-ajax-post                ajax-post-fn)
     (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
     (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
     (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
     (def connected-uids                connected-uids)) ; Watchable, read-only atom

(defn broadcast []
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid [:test-push/hello "Hello Test!"])))

;; Just added for testing
(defn broadcast-2 []
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:test-push/bye "Bye!"])))

(defmulti event :id)

(defmethod event :game/createGame [{:as ev-msg :keys [event uid client-id ?data]}]
  (handle-create-game uid)
)

(defn handle-create-game [uid]
   (game/createGame uid)

)

(defmethod event :default [{:as ev-msg :keys [event]}]
  (println "Unhandled event: " event))

;; ctrl , then shift b
(defmethod event :test/id1 [{:as ev-msg :keys [event uid client-id ?data]}]
  (println "Hello from User: " uid client-id ?data)
  (broadcast)
  (broadcast-2))


(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
    (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
      (println "Disconnected:" uid))


(defmethod event :chsk/ws-ping [_])

(compojure/defroutes routes
    ; (compojure/GET "/status" req (str "Running: " (pr-str @(:connected-uids channel-socket))))
    (compojure/GET "/chsk"  req (ring-ajax-get-or-ws-handshake req))
    (compojure/POST "/chsk" req (ring-ajax-post req)))

(defn start-router []
 (defonce router
   (sente/start-server-chsk-router! ch-chsk event)))

(def my-app
  (-> routes
      ;; Add necessary Ring middleware:
      (wrap-keyword-params)
      (wrap-params)
      (cors/wrap-cors :access-control-allow-origin [#".*"]
                  :access-control-allow-methods [:get :put :post :delete]
                  :access-control-allow-credentials ["true"])))


(defn -main []
  (println "Server starting...")
  (start-router)
  (run-server #'my-app {:join? false :port 8080}))

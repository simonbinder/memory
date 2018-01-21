(ns memory.server.core
  (:use org.httpkit.server)
  (:require
  [taoensso.sente :as sente]
  [compojure.core :as compojure]
  [ring.middleware.cors :as cors]
  [ring.middleware.params :refer [wrap-params]]
  [ring.middleware.keyword-params :refer [wrap-keyword-params]]
  [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]))

(declare channel-socket)
(defmulti event :id)

(defn broadcast []
  (doseq [uid (:any @(:connected-uids channel-socket))]
    ((:send-fn channel-socket) uid [:test-push/hello "Hello!"])))

;; Just added for testing
(defn broadcast-2 []
    (doseq [uid (:any @(:connected-uids channel-socket))]
      ((:send-fn channel-socket) uid [:test-push/bye "Bye!"])))

(defmethod event :default [{:as ev-msg :keys [event]}]
  (println "Unhandled event: " event))

(defmethod event :test/id1 [{:as ev-msg :keys [event uid client-id ?data]}]
  (println "Hello: " uid client-id ?data)
  (broadcast)
  (broadcast-2))

(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
    (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
      (println "Disconnected:" uid))

(defmethod event :chsk/ws-ping [_])

;; currently client-id and uid are the same
(defn create-user-id [{:keys [params]}]
  (:client-id params))

(defn start-websocket []
  (defonce channel-socket
    (sente/make-channel-socket!
            (get-sch-adapter)
            {:user-id-fn create-user-id})))

(compojure/defroutes routes
    ; (compojure/GET "/status" req (str "Running: " (pr-str @(:connected-uids channel-socket))))
    (compojure/GET "/chsk" req ((:ajax-get-or-ws-handshake-fn channel-socket) req))
    (compojure/POST "/chsk" req ((:ajax-post-fn channel-socket) req)))

(defn start-router []
 (defonce router
   (sente/start-chsk-router! (:ch-recv channel-socket) event)))

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
    (start-websocket)
    (start-router)
        (run-server #'my-app {:join? false :port 8080}))

(ns memory.server.core
  (:use org.httpkit.server)
  (:require
   [memory.server.eventrouter :as eventrouter]
   [memory.server.websocket :as websocket]
   [taoensso.sente :as sente]
   [compojure.core :as compojure]
   [ring.middleware.cors :as cors]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.util.response :as response]))



(compojure/defroutes routes
    ; (compojure/GET "/status" req (str "Running: " (pr-str @(:connected-uids channel-socket))))
    (compojure/GET "/chsk"  req (websocket/ring-ajax-get-or-ws-handshake req))
    (compojure/POST "/chsk" req (websocket/ring-ajax-post req)))

(defn start-router []
 (defonce router
   (sente/start-server-chsk-router! websocket/ch-chsk eventrouter/event)))

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

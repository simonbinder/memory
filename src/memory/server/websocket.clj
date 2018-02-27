(ns memory.server.websocket
  (:require
    [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]
    [taoensso.sente :as sente]))

(defn create-user-id [{:keys [params]}]
  (:client-id params))

;; used to establish connection with websockets
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

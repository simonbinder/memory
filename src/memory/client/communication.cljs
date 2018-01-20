(ns memory.client.communication ; .cljs
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   [cljs.core.async :as async :refer (<! >! put! chan)]
   [taoensso.sente  :as sente :refer (cb-success?)]
  ))

(defn get-chsk-url
    "Connect to a configured server instead of the page host"
    [protocol chsk-host chsk-path type]
    (let [protocol (case type :ajax protocol
                              :ws   (if (= protocol "https:") "wss:" "ws:"))]
      (str protocol "//" "localhost:8080" chsk-path)))

(defonce channel-socket
    (with-redefs [sente/get-chsk-url get-chsk-url]
    (sente/make-channel-socket! "/chsk" {:type :auto})))
(defonce chsk (:chsk channel-socket))
(defonce ch-chsk (:ch-recv channel-socket))
(defonce chsk-send! (:send-fn channel-socket))
(defonce chsk-state (:state channel-socket))

(defn send-hello []
  (chsk-send! [:test/id1 {:hello "hello"}]))

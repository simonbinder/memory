(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.gameboard :as gameboard]
      [memory.client.communication :as communication]))

(enable-console-print!)

(println "This text is printed from src/memory/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defonce cards (atom
  { :0 "Card1"
    :1 "Card2"
    :2 "Card3"
    :3 "Card4"
    :4 "Card5"
    :5 "Card6"
    :6 "Card7"
    :7 "Card8"
    :8 "Card9"
    :9 "Card10"
    :10 "Card11"
    :11 "Card12"
    :12 "Card13"
    :13 "Card14"
    :14 "Card15"
    :15 "Card16" }))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and test!"]
   [:input  {:type "button" :value "Click me"
            :on-click
            (fn [e]
              (communication/send-hello))}]
              (gameboard/memory-app cards)

])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.gameboard :as gameboard]
      [memory.client.communication :as communication]))

(enable-console-print!)

(println "This text is printed from src/memory/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defonce counter (atom 0))

(defonce cards (atom (sorted-map)))

(defn add-card [text]
  (let [id (swap! counter inc)]
    (swap! cards assoc id {:id id :title text :turned true})))

(defn turn [id] (swap! cards update-in [id :turned] not))

(defonce init
  (do
    (println "init")
    (add-card "Card 1")
    (add-card "Card 2")
    (add-card "Card 3")
    (add-card "Card 4")
    (add-card "Card 5")
    (add-card "Card 6")
    (add-card "Card 7")
    (add-card "Card 8")
    (add-card "Card 9")
    (add-card "Card 10")
    (add-card "Card 11")
    (add-card "Card 12")
    (add-card "Card 13")
    (add-card "Card 14")
    (add-card "Card 15")
    (add-card "Card 16")))

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

(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.gameboard :as gameboard]
      [memory.client.communication :as communication]))

(enable-console-print!)

(println "This text is printed from src/memory/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload
(defonce cards (atom
  { 1 {:id 1 :title "Card1" :src " " :turned false}
    2 {:id 2 :title "Card2" :src " " :turned false}
    3 {:id 3 :title "Card3" :src " " :turned false}
    4 {:id 4 :title "Card4" :src "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned true}
    5 {:id 5 :title "Card5" :src " " :turned false}
    6 {:id 6 :title "Card6" :src " " :turned false}
    7 {:id 7 :title "Card7" :src " " :turned false}
    8 {:id 8 :title "Card8" :src " " :turned false}
    9 {:id 9 :title "Card9" :src "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned true}
    10 {:id 10 :title "Card10" :src " " :turned false}
    11 {:id 11 :title "Card11" :src " " :turned false}
    12 {:id 12 :title "Card12" :src " " :turned false}
    13 {:id 13 :title "Card13" :src " " :turned false}
    14 {:id 14 :title "Card14" :src " " :turned false}
    15 {:id 15 :title "Card15" :src " " :turned false}
    16 {:id 16 :title "Card16" :src " " :turned false}
  }))

(defonce app-state (atom {:text "Hello world!"}))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and test!"]
   [:input  {:type "button" :value "Click me"
            :on-click
            (fn [e]
              (communication/send-hello))}]
              (gameboard/gameboard cards)

])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

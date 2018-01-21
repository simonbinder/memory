(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.communication :as communication]))

(enable-console-print!)

(println "This text is printed from src/memory/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and test!"]
   [:input  {:type "button" :value "Click me"
            :on-click
            (fn [e]
              (communication/send-hello))}]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
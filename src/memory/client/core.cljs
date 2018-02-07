(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.communication :as communication]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(defn create-game[]
  [:div [:input  {:type "button" :value "Create Game"
  :on-click
  (fn [e]
    (communication/create-game))}]])



(defn join-game []
  (let [game-id (atom nil)] (fn []
  [:div "Join Game"
    [:form
       [:input {:value @game-id
               :type "text"
               :on-change #(reset! game-id (-> % .-target .-value))}]
       [:button {:type "button"
                :name "join"
                :onClick #(communication/join-game @game-id)}
                "Join Game!"]]
     [:div @game-id]]
 )))


(defn game-id-input
  []
  [:div])

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and test!"]
   [:img {:src "../../assets/pinkfloyd.png"}]
   [create-game]
   [join-game]])
(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

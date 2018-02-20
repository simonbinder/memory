(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.model :as model]
      [memory.client.communication :as communication]))

(enable-console-print!)

(defn set-game-id [game-id]
  (swap! model/app-state assoc :game-id (str game-id)))

(defn start-game-reply [reply]
  (print reply)
  (let [game-id (get reply :game-id)]
  (set-game-id game-id)
  (swap! model/app-state assoc :state 1)))

(defn join-game-reply [reply]
  ;(let [deck (get (get reply 2) :deck)]
  (print "join-game-reply" reply)
  ;(print "deck" deck)
  ;(swap! game assoc :deck deck)
  )

(defn join-game [game-id]
  (print game-id)
  (set-game-id game-id)
  (communication/join-game game-id join-game-reply))

;; not implemented yet
(defn handle-click []
  (communication/send-game @model/game))

(defn start-view []
  (let [input-value (atom "")]
  (fn []
    [:div#start-view
    [:input  {:type "button"
              :value "Spiel starten"
              :on-click
                (fn [e]
                  (communication/create-game start-game-reply))}]
    [:input  {:type "button"
              :value "Spiel beitreten"
              :on-click
                (fn [e]
                  (join-game @input-value))}]
    [:input {:type "text"
             :value @input-value
             :on-change #(reset! input-value (-> % .-target .-value))}]])))

(defn waiting-view []
  [:div#waiting-view
    [:p "Schicke die unten angegebene Game-ID an einen Freund. Sobald dieser dem Spiel beitritt kann das Spiel beginnen."]
    [:p (str "Game-ID: "(:game-id @model/app-state))]])

(defn card-item-open []
  (fn [{:keys [title, turned]}]
    [:li
      [:img {:src "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg"}]]))

(defn card-item-closed []
  (fn [{:keys [title, turned]}]
    [:li ]))

(defn card-item [card]
  (fn [{:keys [title, turned]}]
    (if (true? turned)
      [card-item-open card]
      [card-item-closed]
      )))

(defn gameboard []
    (let [game @model/game
          cards (get game :deck)]
      [:div#gameboard
        [:ul#card-list {:style {:width "600px"}}
        (for [card cards]
             ^{:key (:id card)} [card-item card])]]))

(defn main-view []
  [:div
    [:h1 "Memory"]
    ;; reload is not working because atom watched file is not changed, but otherwise circular dependency --> no solution yet
    (case (get @model/app-state :state)
        0 [start-view]
        1 [waiting-view]
        2 [gameboard])])


(reagent/render-component [main-view]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(ns memory.client.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [memory.client.model :as model]
    [memory.client.communication :as communication]
    [memory.client.eventsender :as eventsender]))

(enable-console-print!)

(defn turn-card-if-player-is-allowed-to [id]
  (if (model/check-if-player-is-allowed-to-turn-card id)
    (eventsender/handle-click)))

(defn start-view []
  (let [input-value (atom "")]
  (fn []
    [:div#start-view
      [:input  {:class "button"
                :type "button"
                :value "START GAME"
                :on-click
                  (fn [e]
                    (communication/create-game eventsender/start-game-reply))}]
      [:input  {:class "button"
                :type "button"
                :value "JOIN GAME"
                :on-click
                  (fn [e]
                    (eventsender/join-game @input-value))}]
      [:input {:class "inputfield"
               :type "text"
               :value @input-value
               :on-change #(reset! input-value (-> % .-target .-value))}]])))

(defn waiting-view []
  [:div#waiting-view
    [:p "Send the Game ID below to a friend. As soon as he joins the game, the game can start."]
    [:p  "Game-ID: "]
    [:p (str (:game-id @model/app-state))]])

(defn disconnected-view []
  [:div#waiting-view
    [:p "Your opponent has left the game. Send the ID below to a friend and the game can continue."]
    [:p  "Game-ID: "]
    [:p (str (:game-id @model/app-state))]])

;; hack to get relative paths
(defn replace-path [image-path]
  (if (clojure.string/includes? image-path ".\\resources\\public")
    (clojure.string/replace image-path #".\\resources\\public" "..\\..") ;Windows-path
    (clojure.string/replace image-path #"./resources/public" ""))) ;Mac-path

(defn card-item-resolved [card resolved]
  (fn [{:keys [url]}]
    [:li {:class (str "resolved resolved-cards-player-" resolved)}
      [:img {:src (replace-path url)}]]))

(defn card-item-open [card]
  (fn [{:keys [url]}]
    [:li
      [:img {:src (replace-path url)}]]))

(defn card-item-closed [card]
  (fn [{:keys [id]}]
    [:li
    {:on-click
      (fn [e]
        (turn-card-if-player-is-allowed-to id))}]))

(defn card-item [card]
  (fn [{:keys [resolved, turned, id]}]
    (if (not= resolved 0)
      [card-item-resolved card resolved]
      (if (true? turned)
        [card-item-open card]
        [card-item-closed card]
        ))))

(defn gameboard []
  (let [game @model/game
        cards (get game :deck)
        own-score (:own-score @model/game-count)
        opponent-score (:opponent-score @model/game-count)
        player (:player-number @model/app-state)]
  [:div
    [:div#gameboard
      [:ul#card-list
      (for [card cards]
           ^{:key (:id card)} [card-item card])]]
    [:div#score
      [:p "Your score is: " own-score]
      [:p "The opponent score is: " opponent-score]
      [:p (model/check-if-is-the-players-turn)]]]))

(defn finished-view []
  (let [own-score (:own-score @model/game-count)
       opponent-score (:opponent-score @model/game-count)
       player (:player-number @model/app-state)]
      [:div#finished-view
      [:p "The Game is over!"]
      (cond
        (> own-score opponent-score) [:p "Congratulations! You won with " own-score " uncovered pairs, your opponent only scored " opponent-score]
        (< own-score opponent-score) [:p "You lost! You uncovered " own-score " pairs, your opponent " opponent-score]
        (= own-score opponent-score) [:p "It's a draw! Both players reached a count of: " own-score])
      ]))

(defn main-view []
  [:div
    [:h1#headline "Memory"]
    (if (not= @model/error "")
      [:p#error @model/error])
    (case (:state @model/app-state)
        0 [start-view]
        1 [waiting-view]
        2 [gameboard]
        3 [disconnected-view]
        4 [finished-view])])

(reagent/render-component [main-view]
                          (. js/document (getElementById "app")))

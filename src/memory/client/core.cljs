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
    [:input  {:type "button"
              :value "Spiel starten"
              :on-click
                (fn [e]
                  (communication/create-game eventsender/start-game-reply))}]
    [:input  {:type "button"
              :value "Spiel beitreten"
              :on-click
                (fn [e]
                  (eventsender/join-game @input-value))}]
    [:input {:type "text"
             :value @input-value
             :on-change #(reset! input-value (-> % .-target .-value))}]])))

(defn waiting-view []
  [:div#waiting-view
    [:p "Schicke die unten angegebene Game-ID an einen Freund. Sobald dieser dem Spiel beitritt kann das Spiel beginnen."]
    [:p (str "Game-ID: "(:game-id @model/app-state))]])

(defn disconnected-view []
  [:div#waiting-view
    [:p "Dein Mitspieler hat das Spiel verlassen. Schicke die untenstehende ID an einen Freund und das Spiel kann fortgesetzt werden."]
    [:p (str "Game-ID: "(:game-id @model/app-state))]])

;; hack to get relative paths
(defn replace-path [image-path]
  (if (clojure.string/includes? image-path ".\\resources\\public")
    (clojure.string/replace image-path #".\\resources\\public" "..\\..")
    (clojure.string/replace image-path #"./resources/public" "")))

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
    [:div {:class "score"}
    [:p "Your score is: " own-score]
    [:p "The opponent score is: " opponent-score]
    [:p (model/check-if-is-the-players-turn)]]
    [:div#gameboard  {:class "gameboard"}
      [:ul#card-list
      (for [card cards]
           ^{:key (:id card)} [card-item card])]]]))

(defn finished-view []
  (let [own-score (:own-score @model/game-count)
       opponent-score (:opponent-score @model/game-count)
       player (:player-number @model/app-state)]
      [:div
      [:div {:class "score"}
      [:p "The Game is over!"]
      (cond
        (> own-score opponent-score) [:p "Congratulations! You won with " own-score " uncovered pairs, your opponent only scored " opponent-score]
        (< own-score opponent-score) [:p "You lost! You uncovered " own-score " pairs, your opponent " opponent-score]
        (= own-score opponent-score) [:p "It's a draw! Both players reached a count of: " own-score])
      ]]))

(defn main-view []
  [:div
    [:h1 "Memory"]
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

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

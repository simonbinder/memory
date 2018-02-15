(ns memory.client.core
    (:require
      [reagent.core :as reagent :refer [atom]]
      [memory.client.communication :as communication]))

(enable-console-print!)

(defonce game (atom
      {:active-player 1
       :deck  [ {:id 0 :url "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false :resolved 0}
              {:id 1 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false :resolved 0}
              {:id 2 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false :resolved 0}
              {:id 3 :url "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false :resolved 0}
              {:id 4 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false :resolved 0}
              {:id 5 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false :resolved 0}
              {:id 6 :url "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false :resolved 0}
              {:id 7 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false :resolved 0}
              {:id 8 :url "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false :resolved 0}
              {:id 9 :url "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false :resolved 0}
              {:id 10 :url "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false :resolved 0}
              {:id 11 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false :resolved 0}
              {:id 12 :url "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false :resolved 0}
              {:id 13 :url "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false :resolved 0}
              {:id 15 :url "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false :resolved 0}
              {:id 16 :url "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false :resolved 0}]}))

;; state
;; 0 -> not started (options start new game or join)
;; 1 -> started (waiting for second player)
;; 2 -> started (game can begin)
(defonce app-state (atom {:state 0 :game-id ""}))

(defn set-game-id [reply]
  (let [game-id (get reply :game-id)]
  (swap! app-state assoc :game-id (str game-id))
  (swap! app-state assoc :state 1)))

(defn start-view []
  (let [input-value (atom "nil")]
  [:div#start-view
    [:input  {:type "button"
              :value "Spiel starten"
              :on-click
                (fn [e]
                  (communication/create-game set-game-id))}]
    [:input  {:type "button"
              :value "Spiel beitreten"
              :on-click
                (fn [e]
                  (communication/join-game (:game-id @app-state)))}]
    [:input  {:type "text"
              :value @input-value
              :on-change #(reset! input-value (-> % .-target .-value))}]]))

(defn waiting-view []
  [:div#waiting-view
    [:p "Schicke die unten angegebene Game-ID an einen Freund. Sobald dieser dem Spiel beitritt kann das Spiel beginnen."]
    [:p (str "Game-ID: "(:game-id @app-state))]])

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
    (let [game @game
          cards (get game :deck)]
          (print cards)
      [:div#gameboard
        [:ul#card-list {:style {:width "600px"}}
        (for [card cards]
             ^{:key (:id card)} [card-item card])]]))

(defn main-view []
  [:div
    [:h1 "Memory"]
    (case (get @app-state :state)
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

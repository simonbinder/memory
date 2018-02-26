(ns memory.client.model
  (:require
    [reagent.core :as reagent :refer [atom]]))

(defonce game (atom
          {:active-player 1
           :players {}
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
;; 3 -> player disconnected (waiting for second player)
(defonce app-state (atom {:state 0 :game-id "" :player-number 0 :player-uid "" :turned-cards 0}))

(defonce game-count (atom {:own-score 0 :opponent-score 0}))

(defonce error (atom ""))

(defn count-unresolved-cards [deck player-number]
      ( / (count (filter #(= (% :resolved) player-number) deck)) 2))

(defn calc-game-count []
        (let [deck (get @game :deck)
        player-number (:player-number @app-state)
        opponent-number (if (= player-number 1) 2 1)
        own-score (count-unresolved-cards deck player-number)
        opponent-score (count-unresolved-cards deck opponent-number)] [own-score opponent-score]))

(defn turn-card [id]
  (let [deck  (get @game :deck)
        id-vec (vec (map :id deck))
        index (.indexOf (vec (map :id deck)) id)]
      (swap! game update-in [:deck index] assoc :turned true)
      (swap! app-state update-in [:turned-cards] inc)))

(defn check-if-player-is-allowed-to-turn-card [id]
  (if (and (= (:player-number @app-state) (:active-player @game)) (> 2 (:turned-cards @app-state)))
    (do
      (turn-card id)
      true)
      false))

(defn check-if-is-the-players-turn []
  (if (= (:player-number @app-state) (:active-player @game))
   "It's your turn"
   "It's NOT your turn"))

   (defn clear-error []
     (reset! error ""))

(defn show-error [error-message]
  (reset! error error-message)
  (let [interval (js/setInterval #(clear-error) 5000)]

  (js/clearInterval interval)))

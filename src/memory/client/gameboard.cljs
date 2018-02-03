(ns memory.client.gameboard
    (:require
      [reagent.core :as reagent :refer [atom]]))

(defonce cards (atom (sorted-map)))

(defonce counter (atom 0))

(defn add-card [text]
  (let [id (swap! counter inc)]
    (swap! cards assoc id {:id id :title text :turned true})))

(defn turn [id] (swap! cards update-in [id :turned] not))

(defonce init (do
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

(defn card-item []
  (fn [{:keys [title]}]
    [:li title]))

(defn memory-app []
  (let [filt (atom :all)]
    (fn []
      (let [items (vals @cards)]
        [:div
         [:section#memoryapp
          [:header#header
           [:h1 "Memory"]
           [:div
             [:section#main
              [:ul#card-list {:style {:width "600px"}}
               (for [card items]
                 ^{:key (:id card)} [card-item card])]]]]
             [:footer#footer]]]))))

;;(reagent/render-component [memory-app])
  ;;  (. js/document (getElementById "app")))

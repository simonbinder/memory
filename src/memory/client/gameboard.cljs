(ns memory.client.gameboard
    (:require
      [reagent.core :as reagent :refer [atom]]))



(defn card-item []
  (fn [{:keys [title]}]
    [:li title]))

(defn memory-app [cards]
  (print "cards")
  (print cards)

      (let [items @cards]
      (print "items")
      (print items)
        [:div
         [:section#memoryapp
          [:header#header
           [:h1 "Memory"]
           [:div
             [:section#main
              [:ul#card-list {:style {:width "600px"}}
               (for [card items]
                 ^{:key (:id (val card))} [card-item (val card)])]]]]
             [:footer#footer]]]))

;;(reagent/render-component [memory-app])
  ;;  (. js/document (getElementById "app")))

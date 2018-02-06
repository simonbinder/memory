(ns memory.client.gameboard
    (:require
      [reagent.core :as reagent :refer [atom]]))

(defn card-item []
  (fn [{:keys [title]}]
    [:li title]))

(defn gameboard [cards]
    (let [items @cards]
      [:div
        [:section#memoryapp
          [:div
            [:section#main
              [:ul#card-list {:style {:width "600px"}}
                (for [card items]
                    ^{:key (:id (val card))} [card-item (val card)])]]]]]))

;;(reagent/render-component [memory-app])
  ;;  (. js/document (getElementById "app")))

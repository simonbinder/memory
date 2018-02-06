(ns memory.client.gameboard
    (:require
      [reagent.core :as reagent :refer [atom]]))

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

(defn gameboard [cards]
    (let [items @cards]
      [:div#gameboard
        [:ul#card-list {:style {:width "600px"}}
          (for [card items]
              ^{:key (:id (val card))} [card-item (val card)])]]))

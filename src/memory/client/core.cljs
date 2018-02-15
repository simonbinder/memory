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


(defonce app-state (atom {:text "Hello world!"}))

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
             ^{:key (:id card)} [card-item card])
        ]]))


(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and test!"]
   [:input  {:type "button" :value "Click me"
            :on-click
            (fn [e]
              (communication/send-hello))}]
   [gameboard]
   ])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

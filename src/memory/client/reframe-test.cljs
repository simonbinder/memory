(ns simple.core

    (:require [reagent.core :as reagent]
              [re-frame.core :as rf]
              [clojure.string :as str]))

  ;; A detailed walk-through of this source code is provided in the docs:
  ;; https://github.com/Day8/re-frame/blob/master/docs/CodeWalkthrough.md

  ;; -- Domino 1 - Event Dispatch -----------------------------------------------




  ;; -- Domino 2 - Event Handlers -----------------------------------------------

  (rf/reg-event-db              ;; sets up initial application state
    :initialize                 ;; usage:  (dispatch [:initialize])
    (fn [_ _]                   ;; the two parameters are not important here, so use _
      {:cards (atom
        { 1 {:id 1 :title "Card1" :src "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false}
          2 {:id 2 :title "Card2" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false}
          3 {:id 3 :title "Card3" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false}
          4 {:id 4 :title "Card4" :src "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false}
          5 {:id 5 :title "Card5" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false}
          6 {:id 6 :title "Card6" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false}
          7 {:id 7 :title "Card7" :src "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false}
          8 {:id 8 :title "Card8" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false}
          9 {:id 9 :title "Card9" :src "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false}
          10 {:id 10 :title "Card10" :src "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false}
          11 {:id 11 :title "Card11" :src "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false}
          12 {:id 12 :title "Card12" :src "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false}
          13 {:id 13 :title "Card13" :src "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false}
          14 {:id 14 :title "Card14" :src "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false}
          15 {:id 15 :title "Card15" :src "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false}
          16 {:id 16 :title "Card16" :src "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false}
        })}))    ;; so the application state will initially be a map with two keys


  (rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
    :turn-card             ;; dispatched when the user enters a new colour into the UI text field
    (fn [db [_ id]]
      (let [old-cards @(db :cards)
            old-card-turned (get (get old-cards id) :turned)]
          (assoc db :cards (atom (update-in old-cards [id] assoc :turned (not old-card-turned)))))))  ;; compute and return the new application state

  ;(defn toggle [id] (swap! todos update-in [id :done] not))

  ;; -- Domino 4 - Query  -------------------------------------------------------

  (rf/reg-sub
    :cards
    (fn [db _]     ;; db is current app state. 2nd unused param is query vector
      (:cards db))) ;; return a query computation over the application state


  ;; -- Domino 5 - View Functions ----------------------------------------------

  (defn card-item-open []
   (fn [{:keys [title, turned, src, id]}]
     [:li {:on-click #(rf/dispatch [:turn-card id])}
       [:img {:src src}]]))

  (defn card-item-closed []
   (fn [{:keys [title, turned, id]}]
     [:li {:on-click #(rf/dispatch [:turn-card id])}]))

  (defn card-item [card]
   (fn [{:keys [title, turned]}]
     (if (true? turned)
       [card-item-open card]
       [card-item-closed card]
       )))

  (defn gameboard []
     (let [items @@(rf/subscribe [:cards])]
       [:div#gameboard
         [:ul#card-list {:style {:width "600px"}}
           (for [card items]
               ^{:key (:id (val card))} [card-item (val card)])]]))

  ;; -- Entry Point -------------------------------------------------------------

  (defn ^:export run
    []
    (rf/dispatch-sync [:initialize])     ;; puts a value into application state
    (reagent/render [gameboard]              ;; mount the application's ui into '<div id="app" />'
                    (js/document.getElementById "app")))

(ns memory.server.games
  (:require
    [clojure.java.io :as io]))

(require 'digest)

(def users (atom {}))

(def games (atom {}))

(defn create-game-id [uid]
  (digest/md5 uid))

(defn load-deck-files[]
  (let [directory (clojure.java.io/file  "./resources/public/assets")]
  (let [files
    (for [file (file-seq directory)]
    (when (.isFile file)
      (.getPath file)))]
  (let [files-clean (remove nil? files)]
files-clean))))

(defn create-deck-vector[]
  (let [deck-list
    (for [file (load-deck-files)]
      { :id (str (java.util.UUID/randomUUID))
        :url file
        :turned false
        :resolved 0})]
  (let [deck-vector (into [] deck-list)]
    deck-vector)))

(defn create-deck[]
  (let [deck (into [] (concat (create-deck-vector) (create-deck-vector)))
        deck-shuffled (shuffle deck)]
  deck-shuffled))

; with always one player connected, it returns first player-index where value = nil
(defn get-nil-player-index [game]
  (if-let [nil-player (first (filter #(-> % last nil?) (-> game :players seq)))]
     (first nil-player)
     nil))

(defn add-player-to-game [uid game]
  (if-let [nil-player-index (get-nil-player-index game)]
    (assoc-in game [:players nil-player-index] uid)
    (throw (Exception. "There are already two players participating in this game."))))

(defn create-new-game [player-one-uid]
  {:players {1 player-one-uid 2 nil}
   :active-player 1
   :deck (create-deck)})

(defn add-new-game [uid]
  (let [game-id (create-game-id uid) game (create-new-game uid)]
    (swap! games assoc-in [game-id] game)
    (swap! users assoc uid game-id)
    game-id
   ))

(defn get-game-id-for-uid [uid]
  (get @users uid))

(defn update-users-game-id [uid game-id]
  (swap! users assoc uid game-id))

(defn get-game [id]
    (get @games id))

(defn remove-game [game-id]
    (swap! games dissoc game-id))

(defn update-game [game-id changed-game]
   (swap! games assoc game-id changed-game))

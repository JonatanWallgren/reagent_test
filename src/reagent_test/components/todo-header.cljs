(ns reagent-test.components.todo-header
      (:require [reagent.core :as reagent :refer [atom]]))

(def todo-item (atom ""))

(defn add-todo [todo-state]
  (let [uuid (random-uuid)]
    (swap! reagent-test.core/app-state assoc uuid {:id uuid
                                :title @todo-item
                                :completed false
                                :editing false})))

(defn key-press [e todo-state]
  (when (= (.-key e) "Enter")
    (add-todo todo-state)))

(defn input-change [e]
  (reset! todo-item (.-value (.-target e))))

(defn component [todo-state]
  [:header {:class "header"}
   [:h1 "Todos"]
   [:input {:class "new-todo" 
            :placeholder "What needs to be done?"
            :value @todo-item
            :on-change (fn [e] (input-change e))
            :on-key-press (fn [e] (key-press e todo-state))}]])



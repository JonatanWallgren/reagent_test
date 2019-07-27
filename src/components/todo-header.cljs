(ns reagent-test.components.todo-header
      (:require [reagent.core :as reagent :refer [atom]]))

(def todo-item (atom ""))

(defn add-todo [todo-state]
  (swap! todo-state conj {:id (random-uuid) :title @todo-item :completed false :editing false})
  )

(defn key-press [e todo-state]
  (if (= (.-key e) "Enter")
    (add-todo todo-state)
    nil)
  )

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

(defn tracer []
  (println "Testing the header"))


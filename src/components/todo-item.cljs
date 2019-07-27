(ns reagent-test.components.todo-item
  (:require [reagent.core :as reagent :refer [atom]]))

(def do-edit (atom false))
(def todo-input (atom ""))

(defn select [e id]
  ;(println (.-value (.-target e))))
  (reset! do-edit true)
  (println @do-edit))

(defn edit [e]
  (reset! todo-input (.-value (.-target e)))
  (println @todo-input))

(defn tester [e]
  (println "tester"))

(defn component [item edit-callback cancel-callback edit-item-callback delete-item-callback complete-item-callback]
  (println (item :editing))
  ;(reset! todo-input (item: :title))
  [:li {:key (item :id)
        :class (cond 
                 (item :editing) "editing"
                 (item :completed) "completed"
                 :else nil)}
   
        ; :class (if (item :editing) 
        ;          "editing"
        ;          (if (item: :completed) "completed" nil))}
   [:div {:class "view"}
    [:input {:class "toggle"
             :type "checkbox"
             :value (item :completed)
             :on-click (fn [] (complete-item-callback (item :id)))}]
    [:label {:on-double-click (fn [e] (edit-callback e (item :id)))} (item :title)]
    [:button {:class "destroy"
              :on-click (fn [] (delete-item-callback (item :id)))}]]
   [:input {:class "edit" 
            :value (item :title)
            :on-change (fn [event]
                         (if (item :editing)
                           (edit-item-callback event item)
                           nil))
            :ref (fn [el] 
                   (println el (item :editing))
                   (if (and (item :editing) el)
                     (.focus el) 
                     nil))
            :on-blur (fn [e] 
                       (println "Wooohooo")
                       (cancel-callback e))}]])
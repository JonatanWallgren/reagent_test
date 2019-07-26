(ns reagent-test.components.todo-item
  (:require [reagent.core :as reagent :refer [atom]]))

(def do-edit (atom false))

(defn select [e id]
  ;(println (.-value (.-target e))))
  (reset! do-edit true)
  (println @do-edit))

(defn edit [e]
  (println (.-value (.-target e))))

(defn tester [e]
  (println "tester"))

(defn component [item edit-callback cancel-callback]
  (println (item :editing))
  [:li {:key (item :id)
        :class (if (item :editing) "editing" nil)}
   [:div {:class "view"}
    [:input {:class "toggle"
             :type "checkbox"}]
    [:label {:on-double-click (fn [e] (edit-callback e (item :id)))} (item :title)]
    [:button {:class "destroy"}]]
   [:input {:class "edit" 
            :value "What to do...?"
            :on-change edit
            :ref (fn [el] 
                   (println el (item :editing))
                   (if (and (item :editing) el)
                     (.focus el) 
                     nil))
            :on-blur (fn [e] 
                       (println "Wooohooo")
                       (cancel-callback e))}]])
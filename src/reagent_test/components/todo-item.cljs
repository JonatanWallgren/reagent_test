(ns reagent-test.components.todo-item
  (:require [reagent.core :as reagent :refer [atom]]))

;check destructure item
(defn component [item edit-callback cancel-callback edit-item-callback delete-item-callback complete-item-callback]
  [:li {:key (:id item)
        :class (cond
                 (:editing item) "editing"
                 (:completed item) "completed")}
   [:div {:class "view"}
    [:input {:class "toggle"
             :type "checkbox"
             :checked (:completed item)
             :on-change (fn [] (complete-item-callback (:id item)))}]
    [:label {:on-double-click (fn [e] (edit-callback e (:id item)))} (:title item)]
    [:button {:class "destroy"
              :on-click (fn [] (delete-item-callback (:id item)))}]]
   [:input {:class "edit"
            :value (:title item)
            :on-change (fn [event]
                         (when (:editing item)
                           (edit-item-callback event item)))
            :ref (fn [el]
                   (when (and (:editing item) el)
                     (.focus el)))
            :on-blur (fn [e]
                       (cancel-callback e (:id item)))}]])
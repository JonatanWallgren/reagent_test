(ns reagent-test.components.todo-item
  (:require [reagent.core :as reagent :refer [atom]]))
;check destructure item

(defn component [item edit-callback cancel-callback edit-item-callback delete-item-callback complete-item-callback]
  
  [:li {:key (item :id)
        ;change key value
        ;check else condition
        :class (cond
                 (item :editing) "editing"
                 (item :completed) "completed"
                 :else nil)}
   [:div {:class "view"}
    [:input {:class "toggle"
             :type "checkbox"
             :value (item :completed)
             :on-click (fn [] (complete-item-callback (item :id)))}]
    [:label {:on-double-click (fn [e] (edit-callback e (item :id)))} (item :title)]
    [:button {:class "destroy"
              :on-click (fn [] (delete-item-callback (item :id)))}]]
   ;replace if with when
   [:input {:class "edit"
            :value (item :title)
            :on-change (fn [event]
                         (if (item :editing)
                           (edit-item-callback event item)
                           nil))
            :ref (fn [el]
                   (if (and (item :editing) el)
                     (.focus el)
                     nil))
            :on-blur (fn [e]
                       (cancel-callback e))}]])
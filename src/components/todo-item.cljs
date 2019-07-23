(ns reagent-test.components.todo-item)

(defn component []
  [:li
   [:div
    [:input {:class "toggle"
             :type "checkbox"}]
    [:label "todo-item placeholder"]
    [:button {:class "destroy"}]]
   [:input {:class "edit" :value "What to do...?"}]])
(ns reagent-test.components.todo-footer)

(defn component []
  [:footer {:class "footer"}
   [:span {:class "todo-count"}
    [:strong "42"] "items left"]
   [:ul {:class "filters"}
    [:li
     [:a {:href "#/"} "All"]
     [:a {:href "#active"} "Active"]
     [:a {:href "#completed"} "Completed"]]]
   [:button {:class "Clear Completed"} "Clear completed"]])
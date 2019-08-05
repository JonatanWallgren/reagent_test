(ns reagent-test.components.todo-footer)

(defn component [todo-count filter-callback delete-completed]
  [:footer {:class "footer"}
   [:span {:class "todo-count"}
    [:strong todo-count] (str " item" (if (not= todo-count 1) "s" "") " left")]
   [:ul {:class "filters"}
    [:li
     [:a {:href "#/"
          :on-click (fn [] (filter-callback :reagent-test.core/all))} "All"]
     [:a {:href "#/active"
          :on-click (fn [] (filter-callback :reagent-test.core/active))} "Active"]
     [:a {:href "#/completed"
          :on-click (fn [] (filter-callback :reagent-test.core/completed))} "Completed"]]]
   [:button {:class "clear-completed"
             :on-click delete-completed} "Clear completed"]])
(ns reagent-test.components.todo-header)

(defn component []
  [:header {:class "header"}
   [:h1 "Todos"]
   [:input {:class "new-todo" :placeholder "What needs to be done?"}]])

(defn tracer []
  (println "Testing the header"))


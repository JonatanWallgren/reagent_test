(ns reagent-test.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-test.components.todo-header :as todo-header]
            [reagent-test.components.todo-item :as todo-item]
            [reagent-test.components.todo-footer :as todo-footer]
            [alandipert.storage-atom :refer [local-storage]]))


(enable-console-print!)

(def ::all)
(def ::completed)
(def ::active)

(def current-filter (atom ::all))

(def app-state (local-storage (atom ())
                              :app-state))

(def app-test-spike (atom (hash-map)))

(defn edit-item [e item-id]
    (reset! app-test-spike (assoc-in @app-test-spike [item-id :editing] true)))

(defn todo-complete [item-id]
  (reset! app-test-spike (assoc-in @app-test-spike [item-id :completed] (not (:completed (get @app-test-spike item-id))))))

(defn cancel-edit [e item-id]
    (reset! app-test-spike (assoc-in @app-test-spike [item-id :editing] false)))

(defn delete-item [item-id]
    (reset! app-test-spike (dissoc @app-test-spike item-id)))

(defn change-item [event item]
    (reset! app-test-spike (assoc-in @app-test-spike [(:id item) :title] (.-value (.-target event)))))

(defn set-filter [filter]
  (reset! current-filter filter))

(defn delete-completed-items []
  (println (map #(%) @app-test-spike)))
  ; (let [items-to-delete (map :id (filter #(= (:completed %) true) @app-test-spike))]
  ;   (doseq [id items-to-delete]
  ;     (delete-item id))))

(defn main-section [state]

  [:section {:class "todoapp"}
   [todo-header/component state]
   [:section {:class "main"}
    [:input {:id "toggle-all"
             :class "toggle-all"
             :type "checkbox"}]
    [:label {:htmlFor "toggle-all"} "Mark all as complete"]
    [:ul {:class "todo-list"}
     (doall (for [item @app-test-spike] (case @current-filter
                                          ::all (todo-item/component (second item) edit-item cancel-edit change-item delete-item todo-complete)
                                          ::active (when (not (:completed (second item))) (todo-item/component (second item) edit-item cancel-edit change-item delete-item todo-complete))
                                          ::completed (when (:completed (second item)) (todo-item/component (second item) edit-item cancel-edit change-item delete-item todo-complete)))))]]
   [todo-footer/component (count @app-state) set-filter delete-completed-items]])

(reagent/render-component [main-section app-state]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

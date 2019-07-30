(ns reagent-test.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-test.components.todo-header :as todo-header]
            [reagent-test.components.todo-item :as todo-item]
            [reagent-test.components.todo-footer :as todo-footer]
            [alandipert.storage-atom :refer [local-storage]]))


(enable-console-print!)

; (defroute "/users/:id" {:as params}
;   (js/console.log (str "User: " (:id params))))

; remove map and use keywords
(def filters {:all "all" :completed "completed" :active "active"})

(def current-filter (atom (filters :all)))

;(defonce app-state (atom ()))
(def app-state (local-storage (atom ())
                              :app-state))

; can you introduce a generic update fn?, update this id to this item, or iten would actually have its own id. 
; explore using a map id->item? need to deal with order somehow
; remove local def
; or explore using ->>
(defn edit-item [e item-id]
  (let [edit-state (map
                   (fn [e]
                     (if (= (:id e) item-id)
                       {:id (:id e) :title (:title e) :completed (:completed e) :editing true}
                       e)) @app-state)]
  (reset! app-state edit-state)))

(defn todo-complete [item-id]
  (let [complete-state (doall (map (fn [item] 
                                    (if (= (:id item) item-id)
                                      {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
                                      item)) @app-state))]
  (reset! app-state complete-state)))

; consider just assoc the updated key
; avoid lazy seq
; consider doall with for or mapv (not lazy)
(defn cancel-edit [e]
  (let [cancel-edit-state (map (fn [e] {:id (:id e) :title (:title e) :completed (:completed e) :editing false}) @app-state)]
  (reset! app-state cancel-edit-state)))

(defn delete-item [item-id]
  ; replace local def with let!
  ; lazy remove!
  (let [delete-item-state (remove (fn [item] (= item-id (:id item))) @app-state)]
  (reset! app-state delete-item-state)))

; consider changing vector to map
(defn change-item [event item]
  ; replace local def with let!
  (let [change-state (doall (map
                     (fn [e]
                       (if (= (:id e) (:id item))
                         {:id (:id e) :title (.-value (.-target event)) :completed (:completed e) :editing true}
                         e)) @app-state))]
  (reset! app-state change-state)))

(defn set-filter [filter]
  (reset! current-filter filter))

(defn delete-completed-items []
  ; replace local def with let!
  (let [items-to-delete (map :id (filter #(= (:completed %) true) @app-state))]
  (doseq [id items-to-delete] 
    (delete-item id))))

(defn main-section [state]

  [:section {:class "todoapp"}
   [todo-header/component state]
   [:section {:class "main"}
    [:input {:id "toggle-all"
             :class "toggle-all"
             :type "checkbox"}]
    [:label {:htmlFor "toggle-all"} "Mark all as complete"]
    [:ul {:class "todo-list"}

    ; replace map filter with for, replace cond with case.
    ;  (for [item @app-state
    ;        :when (case ....)]
    ;    (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete))
     (map #(todo-item/component % edit-item cancel-edit change-item delete-item todo-complete)
          (doall (filter (fn [item]
                           (cond
                             (= @current-filter (filters :all)) item
                             (= @current-filter (filters :completed)) (if (item :completed)
                                                                        item
                                                                        nil)
                             (= @current-filter (filters :active)) (if (not (item :completed))
                                                                     item
                                                                     nil))) @app-state)))]]
   [todo-footer/component (count @app-state) set-filter delete-completed-items]])

(reagent/render-component [main-section app-state]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

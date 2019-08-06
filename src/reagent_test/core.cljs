(ns reagent-test.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-test.components.todo-header :as todo-header]
            [reagent-test.components.todo-item :as todo-item]
            [reagent-test.components.todo-footer :as todo-footer]
            [alandipert.storage-atom :refer [local-storage]]))


(enable-console-print!)
; (keyword :all)
; (keyword :completed)
; (keyword :active)
(def ::all)
(def ::completed)
(def ::active)


; (defroute "/users/:id" {:as params}
;   (js/console.log (str "User: " (:id params))))

; remove map and use keywords
(def filters {:all "all" :completed "completed" :active "active"})

(def current-filter (atom ::all))

;(defonce app-state (atom ()))
(def app-state (local-storage (atom ())
                              :app-state))
; (->> maps
;      (filter #(= (:id %) id))
;      first)
; (defn get-new-users
;   [coll]
;   (map #(if (= (:user-id %) "mary") (assoc % :age 8) %) coll))

;(map (fn [x] (update x :name #(if (= "name2" %) % "not 2"))) ds)

(defn update-item [item state]
  (println "the item" item)
  (let [new-state (doall (map #(if        
                                (= (:id %) (:id item)) 
                                 item
                                 %) 
                              state))]
    (reset! app-state new-state)))

; ({:id #uuid "a9367955-44d3-4ffc-9d73-d96b8d8faf9a", :title test 3, :completed false, :editing false} 
;  {:id #uuid "8a7330b2-e418-4156-9524-35212ced6fb5", :title test 2, :completed true, :editing false} 
;  {:id #uuid "3862d583-c8ed-4b90-b74f-539d9f2efa0c", :title test 1, :completed false, :editing false})

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

  (let [test (first (filter 
                     #(when 
                       (= (:id %) item-id) 
                        %) 
                     @app-state))] 
    (do 
      (let 
       [comp (not (:completed test))]
        (update-item (assoc test :completed comp) @app-state)))))
  ; (let [complete-state (doall (map (fn [item]
  ;                                    (case item
  ;                                      (= (:id item) item-id) (not (:completed item)) {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
  ;                                      (= (:id item) item-id) (:completed item) {:id (:id item) :title (:title item) :completed false :editing (:editing item)}
  ;                                      item)) @app-state))
  ; (let [complete-state (doall
  ;                       (map
  ;                        (fn [item]
  ;                            (case item
  ;                              (= (:id item) item-id) (not (:completed item)) {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
  ;                              (= (:id item) item-id) (:completed item) {:id (:id item) :title (:title item) :completed false :editing (:editing item)}
  ;                              item)) @app-state))]
  ;   (do
  ;     (println complete-state)
  ;     (reset! app-state complete-state))))
  ;       (reset! app-state complete-state)])) 
                                    ;  (case item
                                    ;    (= (:id item) item-id) (not (:completed item)) {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
                                    ;    (= (:id item) item-id) (:completed item) {:id (:id item) :title (:title item) :completed false :editing (:editing item)}
                                    ;    item)) @app-state))
                                    ;  (when (and (= (:id item) item-id) (not (:completed item)))
                                    ;    (println "Should happen")
                                    ;    {:id (:id item) :title (:title item) :completed true :editing (:editing item)})
                                    ;  (when (and (= (:id item) item-id) (:completed item))
                                    ;    {:id (:id item) :title (:title item) :completed false :editing (:editing item)})
                                    ;  item) @app-state))]


                                    ;  (if (and (= (:id item) item-id) (not (:completed item)))
                                    ;    {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
                                    ;    {:id (:id item) :title (:title item) :completed false :editing (:editing item)})) @app-state))]
    ;(reset! app-state complete-state)]))
  ;(println @app-state))

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
  (println "main" state)
  [:section {:class "todoapp"}
   [todo-header/component state]
   [:section {:class "main"}
    [:input {:id "toggle-all"
             :class "toggle-all"
             :type "checkbox"}]
    [:label {:htmlFor "toggle-all"} "Mark all as complete"]
    [:ul {:class "todo-list"}
     (doall (for [item @app-state] (case @current-filter
                                              ::all (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete)
                                              ::active (when (not (:completed item)) (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete))
                                              ::completed (when (:completed item) (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete)))))]]
   [todo-footer/component (count @app-state) set-filter delete-completed-items]])
    ;  (for [num [10,20,30]] (case num
    ;                          10 "ten"
    ;                          30 "thirty"
    ;                          "hmm 20?"))]]])

    ;  (for [item @app-state]
    ;        (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete))]]])

    ;  (case @current-filter
    ;    ::all [:div "here be items "])]]])
    ; [:ul {:class "todo-list"} (when (= @current-filter :all)
    ;                             (println "all"))]]])
    ;  (case @current-filter
    ;    :all [:div "here be items "])]]])



                   ;:all (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete))])]]])


    ; replace map filter with for, replace cond with case.
    ;  (for [item @app-state
    ;        :when (case ....)]
    ;    (todo-item/component item edit-item cancel-edit change-item delete-item todo-complete))
  ;    (map #(todo-item/component % edit-item cancel-edit change-item delete-item todo-complete)
  ;         (doall (filter (fn [item]
  ;                          (cond
  ;                            (= @current-filter (filters :all)) item
  ;                            (= @current-filter (filters :completed)) (if (item :completed)
  ;                                                                       item
  ;                                                                       nil)
  ;                            (= @current-filter (filters :active)) (if (not (item :completed))
  ;                                                                    item
  ;                                                                    nil))) @app-state)))]]
  ;  [todo-footer/component (count @app-state) set-filter delete-completed-items]])

(reagent/render-component [main-section app-state]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

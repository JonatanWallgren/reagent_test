(ns reagent-test.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-test.components.todo-header :as todo-header]
            [reagent-test.components.todo-item :as todo-item]
            [reagent-test.components.todo-footer :as todo-footer]))

(enable-console-print!)

(println "Jw This text is printed from src/reagent-test/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

;(defonce app-state (atom {:text "Hello world!"}))

(def test-state (atom '({:id 1 :title "First todo" :completed false :editing false}
                        {:id 2 :title "Second todo" :completed false :editing false}
                        {:id 3 :title "Third todo" :completed false :editing false})))

(def change-test-state (map
                        (fn [e]
                          (if (= (:id e) 2)
                            {:id (:id e) :title (:title e) :completed (:completed e) :editing true}
                            e)) @test-state))
(reset! test-state change-test-state)
;(swap! change-state update-in [:editing] true)
(println "change state" change-test-state)
(println "test state" test-state)


(def filters {:all "all" :completed "completed" :active "active"})

(def current-filter (atom (filters :all)))

(println "Current filter" @current-filter)

(defonce app-state (atom ()))

(reset! app-state ())
(println "1")
(println @app-state)
(def random-id (random-uuid))
;(println random-id)
(swap! app-state conj {:id random-id :title "First todo" :completed false :editing false})
;(swap! app-state conj {:todo-id "1234" :title "First todo" :completed false :editing false})
(println "2")
(println @app-state)
(println "3")
(println (:title (first @app-state)))
(println random-id)
(println (:title (first (filter #(= (:id %) random-id) @app-state))))
(println (:id (first @app-state)))
;(some #(= ))
;(filter #(= (:specie %) :elephant) animals))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change! jw"]])

;(todo-header/tracer)
;(map #(* 5 %) [1 2 3 4 5])

; ;; swap map values
; (def m1 (atom {:a "A" :b "B"}))
; ;; atom

; ;; dereference the atom
; @m1
; ;;=> {:a "A", :b "B"}

; ;; notice that the value swapped in, is part of the returned value
; (swap! m1 assoc :a "Aaay")
; ;;=> {:a "Aaay", :b "B"}

(defn edit-item [e item-id]
  (println "core edit item" item-id)
  (println (.focus (.-lastChild (.-parentNode (.-parentNode (.-target e))))))
  (println "autofocus")
  ;(println (first (filter #(= (:id %) item-id) @app-state)))
  ;(def do-edit (:editing (first (filter #(= (:id %) item-id) @app-state))))
  ;(def do-edit assoc (first (filter #(= (:id %) item-id) @app-state)) :editing true)


  ;(reset! do-edit true)
  ; (def to-change (assoc (first (filter #(= (:id %) item-id) @app-state)) :editing true))
  ; (println "to change" to-change)
  ; (swap! (first (filter #(= (:id %) item-id) @app-state)) to-change)
;(println "do-edit? " (assoc (first (filter #(= (:id %) item-id) @app-state)) :editing true))
  (def edit-state (map
                   (fn [e]
                     (if (= (:id e) item-id)
                       {:id (:id e) :title (:title e) :completed (:completed e) :editing true}
                       e)) @app-state))
  (reset! app-state edit-state)
  (println @app-state))

(defn todo-complete [item-id]
  (def complete-state (doall (map (fn [item] 
                                    (if (= (:id item) item-id)
                                      {:id (:id item) :title (:title item) :completed true :editing (:editing item)}
                                      item)) @app-state)))
  ;(println "todo-complete" complete-state)
  (reset! app-state complete-state)
  )

(defn cancel-edit [e]
  (println "cancel edit")
  (def cancel-edit-state (map (fn [e] {:id (:id e) :title (:title e) :completed (:completed e) :editing false}) @app-state))
  (reset! app-state cancel-edit-state))

(defn delete-item [item-id]
  (def delete-item-state (remove (fn [item] (= item-id (:id item))) @app-state))
  (reset! app-state delete-item-state))

(defn change-item [event item]
  ;(reset! todo-input (.-value (.-target e)))
  (def change-state (doall (map
                     (fn [e]
                       (if (= (:id e) (:id item))
                         {:id (:id e) :title (.-value (.-target event)) :completed (:completed e) :editing true}
                         e)) @app-state)))
  (reset! app-state change-state))

(defn set-filter [filter]
  (println filter)
  (reset! current-filter filter))

(defn delete-completed-items []
  (println "delecomp items")
  ;(filter #(= (:specie %) :elephant) animals)
  (def items-to-delete (map :id (filter #(= (:completed %) true) @app-state)))
  ;;doseq  on vector
; (def my-vector [1 2 3 "a" "b" "c" :a :b :c])
; (doseq [v my-vector] (println v))
  (println items-to-delete)
  (doseq [id items-to-delete] (delete-item id)))

(defn main-section [state]
  ; (println "MEGAFILTERSSSSSSSSSSSSSSSSSS")
  ; (println current-filter (= current-filter (filters :all)))

  [:section {:class "todoapp"}
   [todo-header/component state]
   [:section {:class "main"}
    [:input {:id "toggle-all"
             :class "toggle-all"
             :type "checkbox"}]
    [:label {:htmlFor "toggle-all"} "Mark all as complete"]
    [:ul {:class "todo-list"}
     (map #(todo-item/component % edit-item cancel-edit change-item delete-item todo-complete) 
          (doall (filter (fn [item] (cond
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

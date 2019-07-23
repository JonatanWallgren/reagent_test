(ns reagent-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent-test.components.todo-header :as todo-header]
              [reagent-test.components.todo-item :as todo-item]
              [reagent-test.components.todo-footer :as todo-footer]))

(enable-console-print!)

(println "Jw This text is printed from src/reagent-test/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change! jw"]])

(todo-header/tracer)

(defn main-section []
  [:section {:class "todoapp"}
   [todo-header/component]
   [:section {:class "main"}
    [:input {:id "toggle-all" 
             :class "toggle-all" 
             :type "checkbox"}]
    [:label {:htmlFor "toggle-all"} "Mark all as complete"]
    [:ul {:class "todo-list"}
     [todo-item/component]]]
   [todo-footer/component]])

(reagent/render-component [main-section]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

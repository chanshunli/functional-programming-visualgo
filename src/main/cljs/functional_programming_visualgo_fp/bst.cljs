(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]))


;; 参考D3.js的领域描述来设计你的公共函数库,你的TODO

(defn select-all-path
  "TODO: 查找特定的一些边,然后改变他们的颜色"
  [])

(defn select-all-circle
  "TODO: 查找特定的一些圆圈,然后改变他们的颜色"
  [])

(defn append-path-circle
  "TODO: 在特定的边后面,加上圆圈"
  [])

(defn append-circle-path
  "TODO: 在特定的圆圈后面,加上边"
  [])

(defn update-circle-text
  "TODO: 修改圆圈内部的text内容"
  [text text2])

(defn generate-rand-tree []
  [:div.flex.flex-row.pa3
   [:div.flex.flex-auto]
   [:div
    [:button.f5.ba.bg-white
     {:on-click #(graphviz/d3-graphviz "#graph" "digraph  {a -> d; a -> c; c -> d}")
      :style {:border-radius "1em"
              :height "2em"
              :color "gray"
              :border "2px solid rgba(187, 187, 187, 1)"
              :width "7em"}}
     "生成随机树"]]])

(defn page []
  [:div
   [panel/header {:title "二叉搜索树"}]
   [:div.flex.flex-row
    [:div.bg-black {:style {:width "2em"
                            :height "100vh"}}]
    [:div.flex.flex-auto.justify-center.items-center.mt3.mb3
     {:style {:height "90vh"}
      :id "graph"}]
    [:div.bg-black {:style {:width "2em"
                            :height "100vh"}}]]

   ])

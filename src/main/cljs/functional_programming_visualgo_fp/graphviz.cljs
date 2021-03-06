(ns functional-programming-visualgo-fp.graphviz
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.datas :as datas]))

;; (def viz js/Viz)
(def viz identity)

(comment
  (viz-stri->stri "digraph { a -> b; }"))
(defn viz-stri->stri [stri]
  (->
    (viz stri)
    (clojure.string/replace-first #"width=\"\d+pt\"" "width=\"\50%\"")
    (clojure.string/replace-first #"height=\"\d+pt\"" "height=\"\50%\"")))

(comment
  (viz-stri->ele "digraph { a -> b; }")

  #(let [graph (.querySelector js/document "#graphviz")
         svg (.querySelector graph "svg")]
     (do
       (if svg (.removeChild graph svg) ())
       (.appendChild graph
         (graphviz/viz-stri->ele
           (str "digraph { a -> b; a -> c; c -> " (rand-int 5) ";"
             (rand-int 5) " -> " (rand-int 5) ";  }" ))))))
(defn viz-stri->ele
  [st]
  (.-documentElement
    (.parseFromString
      (js/DOMParser.)
      (viz-stri->stri st)
      "image/svg+xml")))

(comment
  (d3-graphviz "#graph" "digraph  {a -> d; a -> c; c -> d}"))
(defn d3-graphviz [id dot]
  (-> js/d3
    (.select id)
    (.graphviz)
    (.renderDot dot)))

(def dot-src-eg-list
  (list
    "Node1 [id=\"NodeId1\" label=\"N1\" fillcolor=\"#d62728\"]"
    "Node2 [id=\"NodeId2\" label=\"N2\" fillcolor=\"#1f77b4\"]"
    "Node3 [id=\"NodeId3\" label=\"N3\" fillcolor=\"#2ca02c\"]"
    "Node4 [id=\"NodeId4\" label=\"N4\" fillcolor=\"#ff7f0e\"]"
    "Node1 -> Node2 [id=\"EdgeId12\" label=\"E12\"]"
    "Node1 -> Node3 [id=\"EdgeId13\" label=\"E13\"]"
    "Node2 -> Node3 [id=\"EdgeId23\" label=\"E23\"]"
    "Node3 -> Node4 [id=\"EdgeId34\" label=\"E34\"]"))

(def dot-src-eg-list-2
  (list
    "Node1 -> Node2"
    "Node1 -> Node3"
    "Node2 -> Node3"
    "Node3 -> Node4"))

(defn dot-template [label dot-list]
  (str "
  digraph {
    graph [label=\" " label
    " \" labelloc=\"t\", fontsize=\"20.0\" tooltip=\" \"]
    node [style=\"filled\"]
"
    (clojure.string/join  "\n" dot-list)
    "
  }"))

(defonce dot-src (reagent/atom (dot-template "" dot-src-eg-list)))

(comment
  ;; 流程PlayGrounds
  (reset! dot-src (dot-template "" dot-src-eg-list) )


  (render {:dot-src @dot-src :gid "#graph" :interactive-fn delete-interactive})

  ;; 2: 增加一个node: 能够显示动态的切换效果,就像视频一样
  (reset! dot-src (dot-template ""
                    (concat dot-src-eg-list-2
                      (list)
                      (list "Node2 -> Node5")
                      (list "Node5 -> Node6")
                      (list "Node3 -> Node6")
                      ;; ....
                      ;;
                      )
                    ))

  (render {:dot-src @dot-src :gid "#graph" :interactive-fn delete-interactive})

  ())

(defn get-node-title [this]
  (->
    js/d3
    (.select this)
    (.selectAll "title")
    (.text)
    (.trim)))

(defn get-node-text [this]
  (->
    js/d3
    (.select this)
    (.selectAll "text")
    (.text)))

(defn get-node-id [this]
  (->
    js/d3
    (.select this)
    (.attr "id")))

(defn get-node-class [this]
  (->
    js/d3
    (.select this)
    (.attr "class")))

;; TODO: 1. 区别于https://visualgo.net/的设计在于,可以代入自身的数据,不限于数字,还有文本图片等
;; 2. 菜单都放在对象本身上面: 节点和边都是有事件可以操作的,根据当前的模式是什么,对应的点击事件或者长按事件,浮在上面的事件,做事件处理
(defn click-interactive
  "点击边和节点的通用函数: 回调传出来节点名字等信息"
  [op-fn]
  (let [nodes (-> js/d3 ;; 查出来所有的节点和边
                (.selectAll ".node,.edge"))]
    (.on nodes "click"
      (fn []
        (this-as this
          (let [title (get-node-title this)
                text (get-node-text this)
                id (get-node-id this)
                class1 (get-node-class this)
                dot-element (clojure.string/replace title "->" " -> ")]
            (js/console.log
              (str "==== title: " title ","
                "text: " text ","
                "id: " id ","
                "class1: " class1 ","
                "dot-element: " dot-element))
            (op-fn
              {:title title
               :text text
               :id id
               :class1 class1
               :dot-element dot-element})))))))

(declare render)

;; 通用的CURD Boy思想
(defn delete-interactive
  "提供节点或者边的删除的功能,非通用函数"
  []
  (click-interactive
    (fn [{:keys [title text id class1 dot-element]}]
      (let [updated-dot (remove #(>= (.indexOf % dot-element) 0)
                          (clojure.string/split @dot-src "\n"))]
        (js/console.log (str "更新dot: ") updated-dot)
        (reset! dot-src (clojure.string/join "\n" updated-dot))
        (render {:dot-src @dot-src :gid "#graph" :interactive-fn delete-interactive})))))

(defn create-interactive "TODO: 增加新的节点到某个节点下面" [])
(defn update-node-color-interactive  "TODO: 更新节点的颜色" [])
(defn update-node-value-interactive  "TODO: 更新节点的值" [])
(defn update-edge-color-interactive  "TODO: 更新边的颜色" [])
(defn update-edge-value-interactive  "TODO: 更新边的值" [])

(def play-speed 1)

(comment

  (cljs.pprint/pprint dot-src-lines)

  (cljs.pprint/pprint
    (remove #(>= (.indexOf % "Node1 -> Node3") 0) dot-src-lines))

  (cljs.pprint/pprint
    (remove #(>= (.indexOf % "Node1") 0) dot-src-lines))

  (cljs.pprint/pprint dot-src)

  (render {:dot-src @dot-src :gid "#graph" :interactive-fn interactive}))
(defn render
  "通用的render的播放器函数: 传入gid div的id名, interactive-fn交互的函数, dot-src dot的内容"
  [{:keys [gid interactive-fn dot-src]}]
  (->
    js/d3
    (.select gid)
    (.graphviz)
    (.transition
      (fn []
        (-> js/d3
          (.transition)
          (.delay (* play-speed 100))
          (.duration (* play-speed 1000)))))
    (.renderDot dot-src)
    (.on "end" interactive-fn)))

(comment
  (render-list "#graph" @datas/play-list-eg (atom 0)))
(defn render-list
  "递归播放列表(可以用atom,一直修改atom列表来模拟过程)直到列表结束为止"
  [gid rlist dot-index]
  (render
    {:dot-src (get rlist @dot-index)
     :gid gid
     :interactive-fn
     (fn []
       (swap! dot-index inc)
       (if (not= dot-index
             (count rlist))
         (render-list gid rlist dot-index)
         nil))}))

(defn render-atom-update [atom-dot]
  "TODO每次修改atom,就能自动记录下来修改的轨迹,然后播放出来 # 或者是修改atom,响应式的更新到graph图上面"
  )

(defn dot-circle-tmp
  [& {:keys [label datas]}]
  (dot-template label
    (map
      (fn [item]
        (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))
      datas)))

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

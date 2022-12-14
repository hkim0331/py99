(ns py99.charts
  (:require [hiccup.core :refer [html]]))

(defn- ->date-count
  [coll]
  (zipmap (map (comp val first)  coll)
          (map (comp val second) coll)))

(defn- bar-chart
 [coll w h color]
 (let [n (count coll)
       dx (/ w n)]
   (into
    [:svg {:width w :height h :viewbox (str "0 0 " w " " h)}
     [:rect {:x 0 :y 0 :width w :height h :fill "#eee"}]
     [:line {:x1 0 :y1 (- h 10) :x2 w :y2 (- h 10) :stroke "black"}]]
    (for [[x y] (map list (range) coll)]
      [:rect
       {:x (* dx x)
        :y (- h 10 y)
        :width (/ dx 2)
        :height y
        :fill color}]))))

(defn- chart
  ([scale-fn data period width height]
   (chart scale-fn data period width height "red"))
  ([scale-fn data period width height color]
   (let [tmp (->date-count data)
         ys  (for [d period]
               (get tmp d 0))]
     (html (bar-chart (map scale-fn ys) width height color)))))

(defn class-chart
  [answers period width height]
  (chart #(/ % 4.5) answers period width height "orange"))

(defn individual-chart
  [answers period width height]
  (chart #(* % 13) answers period width height))

(defn comment-chart
  [answers period width height]
  (chart #(* % 30) answers period width height "green"))

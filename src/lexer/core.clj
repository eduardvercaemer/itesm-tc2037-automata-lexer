(ns lexer.core
  (:require [lexer.run :refer [run]])
  (:gen-class))

(def begins-with-one
  {:start          :q0
   :transitions
   {:q0            [{:where :one :to :q1 :action :eat}
                    {:where :any :to :halt :action :invalid}]
    :q1            [{:where :end :to :halt :action :show}
                    {:where :any :to :q1 :action :eat}]}})

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (run begins-with-one "10010101010"))

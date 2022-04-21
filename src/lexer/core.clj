(ns lexer.core
  (:require [lexer.run :refer [run]])
  (:gen-class))

(def language
  {:start :stmt
   :transitions
   {;; statements
    :stmt                    [{:where [:ws :newline] :to :stmt}
                              {:where :end :to :halt}
                              {:where :slash :to :cmt-0 :action :eat}
                              {:where :istart :to :asg-token :action :eat}]
    ;; comments begin with two slashes
    :cmt-0                   [{:where :slash :to :cmt-1 :action :eat}]
    :cmt-1                   [{:where :newline :to :stmt :action :out-comment}
                              {:where :end :to :halt :action :out-comment}
                              {:where :any :to :cmt-1 :action :eat}]
    ;; assignments begin with a token
    :asg-token               [{:where :irest :to :asg-token :action :eat}
                              {:where :ws :to :asg-equal :action :out-token}
                              {:where :equal :to :asg-expr :action [:out-token :out-equal]}]
    :asg-equal               [{:where :equal :to :asg-expr :action :out-equal}]
    :asg-expr                [{:where :ws :to :asg-expr}
                              {:where :one :to :asg-end :action :out-one}]
    :asg-end                 [{:where :ws :to :asg-end}
                              {:where :newline :to :stmt}
                              {:where :end :to :halt}]}})

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (run language " abc=  1  \n\n  \n\n \n\n // this is a comment \nbc=1\na =1"))

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
    ;; beginning of expression
    :asg-expr                [{:where :num :to :expr-num :action :eat}
                              {:where :oparen :to :asg-expr :action [:add-paren :out-oparen]}
                              {:where :ws :to :asg-expr}]
    :expr-num                [{:where :num :to :expr-num :action :eat}
                              {:where :newline :to :stmt :action [:check-paren :out-num]}
                              {:where :end :to :halt :action [:check-paren :out-num]}
                              {:where :op :to :asg-expr :action [:out-num :eat :out-op]}
                              {:where :cparen :to :expr-op :action [:del-paren :out-num :out-cparen]}
                              {:where :ws :to :expr-op :action :out-num}]
    :expr-op                 [{:where :ws :to :expr-op}
                              {:where :newline :to :stmt :action :check-paren}
                              {:where :end :to :halt :action :check-paren}
                              {:where :cparen :to :expr-op :action [:del-paren :out-cparen]}
                              {:where :op :to :asg-expr :action [:eat :out-op]}]}})

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (run language " // this is a comment\n\na=5+7\nabc = 5 + (7)\nab = ( 5 )\nn = ( 7 / (2+3))"))

(ns lexer.core
  (:require [lexer.run :refer [run]])
  (:gen-class))

(def language
  {:start :stmt
   :transitions
   {;; statements, are either empty, comments, or assignemnts
    :stmt                    [{:where :end :to :halt}
                              {:where [:ws :newline] :to :stmt}
                              {:where :slash :to :cmt-0 :action :eat}
                              {:where :istart :to :asg-token :action :eat}]
    ;; comments begin with two slashes and end with a newline
    :cmt-0                   [{:where :slash :to :cmt-1 :action :eat}]
    :cmt-1                   [{:where :end :to :halt :action :out-comment}
                              {:where :newline :to :stmt :action :out-comment}
                              {:where :any :to :cmt-1 :action :eat}]
    ;; assignments begin with a token
    :asg-token               [{:where :irest :to :asg-token :action :eat}
                              {:where :ws :to :asg-equal :action :out-token}
                              {:where :equal :to :asg-expr :action [:out-token :out-equal]}]
    ;; then have an assignment operator
    :asg-equal               [{:where :equal :to :asg-expr :action :out-equal}]
    ;; then have an expression
    :asg-expr                [{:where :num :to :expr-num :action :eat}
                              {:where :istart :to :expr-token :action :eat}
                              {:where :oparen :to :asg-expr :action [:add-paren :out-oparen]}
                              {:where :ws :to :asg-expr}]
    ;; expressions have numeric parts
    :expr-num                [{:where :end :to :halt :action [:check-paren :out-num]}
                              {:where :num :to :expr-num :action :eat}
                              {:where :newline :to :stmt :action [:check-paren :out-num]}
                              {:where :slash :to :expr-cmt-0 :action [:out-num :eat]}
                              {:where :op :to :asg-expr :action [:out-num :eat :out-op]}
                              {:where :cparen :to :expr-op :action [:del-paren :out-num :out-cparen]}
                              {:where :ws :to :expr-op :action :out-num}]
    ;; expressions can also have tokens instead of a numeric part
    :expr-token              [{:where :end :to :halt :action [:check-paren :out-token]}
                              {:where :irest :to :expr-token :action :eat}
                              {:where :newline :to :stmt :action [:check-paren :out-token]}
                              {:where :slash :to :expr-cmt-0 :action [:out-num :eat]}
                              {:where :op :to :asg-expr :action [:out-token :eat :out-op]}
                              {:where :cparen :to :expr-op :action [:del-paren :out-token :out-cparen]}
                              {:where :ws :to :expr-op :action :out-token}]
    ;; expressions can end optionally by comments, which can be a little confused by division '/'
    :expr-cmt-0              [{:where :slash :to :cmt-1 :action [:check-paren :eat]}
                              {:where :num :to :expr-num :action [:out-op :eat]}
                              {:where :istart :to :expr-token :action [:out-op :eat]}
                              {:where :oparen :to :asg-expr :action [:out-op :add-paren :out-oparen]}
                              {:where :ws :to :asg-expr :action :out-op}]
    ;; separated by operators
    :expr-op                 [{:where :end :to :halt :action :check-paren}
                              {:where :ws :to :expr-op}
                              {:where :newline :to :stmt :action :check-paren}
                              {:where :cparen :to :expr-op :action [:del-paren :out-cparen]}
                              {:where :slash :to :expr-cmt-0 :action :eat}
                              {:where :op :to :asg-expr :action [:eat :out-op]}]}})

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (let [input "// this is a comment
               a = 5
               
               _678=(a * 2) + (6 / _2 * (3 - 1))
               value = 35 // with comment at the end !
               value = 65 + a // also after tokens !
               value = 9 + (xyz)//or with no spaces !
               value = foo / bar
               value = 5 * (6 / (2) // fails on bad parenthesis !"]
    (run language input)))

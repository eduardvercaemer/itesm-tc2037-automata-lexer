(ns lexer.core
  (:gen-class))

(defn run
  "Run an automata"
  [automata input]
  (let [input (seq input)
        match-symbol (fn [where symbol]
                       (case where
                         :one (= symbol \1)
                         :any true
                         :end (= symbol nil)))
        find-transition (fn [transitions symbol]
                          (first (filter (fn [{where :where}]
                                           (match-symbol where symbol)) transitions)))]
    (loop [curr (:start automata)
           [symbol & rest] input
           state {:token ""}]
      (prn {:curr curr :symbol symbol :state state})
      (let [transitions (get-in automata [:transitions curr])
            transition (find-transition transitions symbol)
            to (:to transition)
            action (:action transition)
            state (case action
                    :eat (update-in state [:token] #(str % symbol))
                    :invalid (do
                               (println "INVALID SYMBOL")
                               state)
                    :show (do
                            (println "token: " (:token state))
                            (assoc-in state [:token] ""))
                    state)]
        (case to
          :halt nil
          (recur to rest state))))))

(def begins-with-one
  {:start          :q0
   :transitions
   {:q0            [{:where :one :to :q1 :action :eat}
                    {:where :any :to :halt :action :invalid}]
    :q1            [{:where :end :to :halt :action :show}
                    {:where :any :to :q1 :action :eat}]}})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run begins-with-one "10010101010"))

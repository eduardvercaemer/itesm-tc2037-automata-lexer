(ns lexer.run)

(defn- run-action
  "Perform different actions"
  [state action symbol]
  (case action
    :eat (update-in state [:token] #(str % symbol))
    :invalid (do
               (println "INVALID SYMBOL")
               state)
    :show (do
            (println "token: " (:token state))
            (assoc-in state [:token] ""))
    state))

(defn- match-symbol
  "Defines the different kinds of symbols"
  [where symbol]
  (case where
    :one (= symbol \1)
    :any true
    :end (= symbol nil)))

(defn- find-transition
  "Find the first valid transition for a symbol"
  [transitions symbol]
  (let [matches? #(match-symbol (:where %) symbol)]
    (or
     (first (filter matches? transitions))
     {:where :any :to :halt :action :invalid})))

(defn run
  "Run an automata
   ---------------
   Given start state and transition map, steps the automata performing the
   transition actions until the machine reaches a :halt state"
  [automata input]
  (let [input (seq input)]
    (loop [curr (:start automata)
           [symbol & rest] input
           state {:token ""}]
      (prn {:curr curr :symbol symbol :state state})
      (let [transitions (get-in automata [:transitions curr])
            transition (find-transition transitions symbol)
            to (:to transition)
            action (:action transition)
            state (run-action state action symbol)]
        (case to
          :halt nil
          (recur to rest state))))))

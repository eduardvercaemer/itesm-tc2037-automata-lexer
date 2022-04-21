(ns lexer.run)

(defn- run-action
  "Perform different actions"
  [state action symbol]
  (case action
    :eat (update-in state [:token] #(str % symbol))
    :invalid (do
               (println "INVALID SYMBOL")
               state)
    :out-comment (do
                   (println "COMMENT:" (:token state))
                   (assoc-in state [:token] ""))
    :out-token (do
                 (println "TOKEN:" (:token state))
                 (assoc-in state [:token] ""))
    :out-equal (do
                 (println "EQUAL")
                 (assoc-in state [:token] ""))
    :out-token-and-equal (do
                           (println "TOKEN:" (:token state))
                           (println "EQUAL")
                           (assoc-in state [:token] ""))
    :out-one (do
               (println "ONE")
               state)
    state))

(defn- match-symbol
  "Defines the different kinds of symbols"
  [where symbol]
  (case where
    :ws (= symbol \ )
    :newline (= symbol \newline)
    :one (= symbol \1)
    :equal (= symbol \=)
    :slash (= symbol \/)
    :num (and (>= (int symbol) (int \0)) (<= (int symbol) (int \9)))
    :lower (and (>= (int symbol) (int \a)) (<= (int symbol) (int \z)))
    :upper (and (>= (int symbol) (int \A)) (<= (int symbol) (int \Z)))
    :under (= symbol \_)
    :alpha (or (match-symbol :lower symbol) (match-symbol :upper symbol))
    :istart (or (match-symbol :alpha symbol) (match-symbol :under symbol))
    :irest (or (match-symbol :istart symbol) (match-symbol :num symbol))
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
      ;;(prn {:curr curr :symbol symbol :state state})
      (let [transitions (get-in automata [:transitions curr])
            transition (find-transition transitions symbol)
            to (:to transition)
            action (:action transition)
            state (run-action state action symbol)]
        (case to
          :halt nil
          (recur to rest state))))))

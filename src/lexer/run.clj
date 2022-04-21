(ns lexer.run)

(defn- run-action
  "Perform different actions"
  [state action symbol]
  ;;(prn {:in "RUN-ACTION" :state state :action action})
  (cond
    (vector? action) (reduce #(run-action %1 %2 symbol) state action)
    (keyword? action)
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
      :out-one (do
                 (println "ONE")
                 state)
      :out-num (do
                 (println "NUM:" (:token state))
                 (assoc-in state [:token] ""))
      :out-op (do
                (println "OP:" (:token state))
                (assoc-in state [:token] ""))
      :add-paren (update-in state [:paren] inc)
      :del-paren (let [paren (:paren state)]
                   (cond
                     (> paren 0) (update-in state [:paren] dec)
                     :else (do
                             (println "INVALID CLOSING PARENTHESIS")
                             (assoc-in state [:invalid] true))))
      :out-oparen (do
                    (println "(")
                    state)
      :out-cparen (do
                    (println ")")
                    state)
      :check-paren (let [paren (:paren state)]
                     (cond
                       (> paren 0) (do
                                     (println "PARENTHESIS NOT CLOSED CORRECTLY")
                                     (assoc-in state [:invalid] true))
                       :else state))
      (do
        (println "INVALID ACTION" action)
        state))
    :else state))

(defn- match-symbol
  "Defines the different kinds of symbols"
  [where symbol]
  (case where
    :ws (= symbol \ )
    :newline (= symbol \newline)
    :one (= symbol \1)
    :equal (= symbol \=)
    :slash (= symbol \/)
    :oparen (= symbol \()
    :cparen (= symbol \))
    :op (contains? #{\+ \* \- \/ \^} symbol)
    :num (and (>= (int symbol) (int \0)) (<= (int symbol) (int \9)))
    :lower (and (>= (int symbol) (int \a)) (<= (int symbol) (int \z)))
    :upper (and (>= (int symbol) (int \A)) (<= (int symbol) (int \Z)))
    :under (= symbol \_)
    :alpha (or (match-symbol :lower symbol) (match-symbol :upper symbol))
    :istart (or (match-symbol :alpha symbol) (match-symbol :under symbol))
    :irest (or (match-symbol :istart symbol) (match-symbol :num symbol))
    :any true
    :end (= symbol nil)))

(defn- transition-matches?
  "Wheter a transition matches a symbol or not"
  [symbol {where :where}]
  (cond
    (vector? where) (first (filter #(match-symbol % symbol) where))
    (keyword? where) (match-symbol where symbol)))

(defn- find-transition
  "Find the first valid transition for a symbol"
  [transitions symbol]
  (or
   (first (filter (partial transition-matches? symbol) transitions))
   {:where :any :to :halt :action :invalid}))

(defn run
  "Run an automata
   ---------------
   Given start state and transition map, steps the automata performing the
   transition actions until the machine reaches a :halt state"
  [automata input]
  (let [input (seq input)]
    (loop [curr (:start automata)
           [symbol & rest] input
           state {:token "" :paren 0}]
      ;;(prn {:curr curr :symbol symbol :state state})
      (let [transitions (get-in automata [:transitions curr])
            transition (find-transition transitions symbol)
            to (:to transition)
            action (:action transition)
            state (run-action state action symbol)
            invalid (:invalid state)]
        (if invalid nil
            (case to
              :halt nil
              (recur to rest state)))))))

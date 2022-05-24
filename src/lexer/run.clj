(ns lexer.run)

(defn- run-action
  "Perform different actions"
  [state action symbol]
  (let [out
        (fn [kind value]
          (->
           state
           (update :tokens #(conj % {:kind kind :value value}))))
        out-t
        (fn [kind]
          (->
           state
           (update :tokens #(conj % {:kind kind
                                     :value (:token state)}))
           (assoc :token "")))
        invalid
        (fn [msg]
          (println "INVALID:" msg)
          (assoc state :invalid true))]
    (cond
      (vector? action) (reduce #(run-action %1 %2 symbol) state action)
      (keyword? action)
      (case action
        :eat (update state :token #(str % symbol))
        :invalid (invalid "")
        :out-comment (out-t "COMMENT")
        :out-token (out-t "VARIABLE")
        :out-equal (out "OP" "=")
        :out-num (out-t "INTEGER")
        :out-float (out-t "FLOAT")
        :out-op (out-t "OP")
        :add-paren (update state :paren inc)
        :del-paren (if (> (:paren state) 0)
                     (update-in state [:paren] dec)
                     (invalid "NO MATCHING OPENNING PARENTHESIS"))
        :out-oparen (out "OP" "(")
        :out-cparen (out "OP" ")")
        :linebreak (out "NEWLINE" "")
        :check-paren (if (> (:paren state) 0)
                       (invalid "NO MATCHING CLOSING PARENTHESIS")
                       state)
        (invalid (str "INVALID ACTION '" action "'")))
      :else state)))

(defn- match-symbol
  "Defines the different kinds of symbols"
  [where symbol]
  (if (nil? symbol)
    (= where :end)
    (case where
      :ws (contains? #{\space \return} symbol)
      :newline (= symbol \newline)
      :one (= symbol \1)
      :equal (= symbol \=)
      :slash (= symbol \/)
      :oparen (= symbol \()
      :cparen (= symbol \))
      :dot (= symbol \.)
      :under (= symbol \_)
      :minus (= symbol \-)
      :e-notation (contains? #{\e \E} symbol)
      :op (contains? #{\+ \* \- \/ \^} symbol)
      :num (and (>= (int symbol) (int \0)) (<= (int symbol) (int \9)))
      :lower (and (>= (int symbol) (int \a)) (<= (int symbol) (int \z)))
      :upper (and (>= (int symbol) (int \A)) (<= (int symbol) (int \Z)))
      :alpha (or (match-symbol :lower symbol) (match-symbol :upper symbol))
      :istart (or (match-symbol :alpha symbol) (match-symbol :under symbol))
      :irest (or (match-symbol :istart symbol) (match-symbol :num symbol))
      :any true
      :end false
      (throw (new Exception "invalid 'where' keyword")))))

(defn- transition-matches?
  "Wheter a transition matches a symbol or not"
  [symbol {where :where}]
  (cond
    (vector? where) (first (filter #(match-symbol % symbol) where))
    (keyword? where) (match-symbol where symbol)
    :else (throw (new Exception "invalid 'where' kind"))))

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
           state {:token "" :paren 0 :tokens []}]
      ;;(prn {:curr curr :symbol symbol :state state})
      (let [transitions (get-in automata [:transitions curr])
            transition (find-transition transitions symbol)
            to (:to transition)
            action (:action transition)
            state (run-action state action symbol)
            invalid (:invalid state)]
        (if invalid (throw (new Exception "invalid state"))
            (case to
              :halt (:tokens state)
              (recur to rest state)))))))

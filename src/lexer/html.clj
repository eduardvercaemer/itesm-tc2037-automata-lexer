(ns lexer.html)

(defn- show-token
  "Converts a single token to its html representation"
  [{kind :kind value :value}]
  (cond
    (= kind "NEWLINE") "<br>"
    :else (str
           "<span class=\"code " kind
           "\">" value
           "</span>")))

(def template
  "<!DOCTYPE html>
   <html>
   <head>
     <title>automata</title>
   </head>
   <body>
     <pre>%s</pre>
     <style type=\"text/css\">
       body {
          background-color: #282c34;
       }
       .code {
          font-family: monospace;
          font-size: 1.2em;
       }
       .COMMENT {
          color: #abb2bf;
       }
       .VARIABLE {
          color: #61afef;
       }
       .OP {
           color: #005757;
       }
       .INTEGER {
           color: #e06c75;
       }
       .FLOAT {
           color: #e06c75;
       }
     </style>
   </body>
   </html>")

;; replace %%% with the html representation of the tokens
(defn htmlize-tokens
  "Build a complete html document from a list of tokens"
  [tokens]
  (->>
   tokens
   (reduce (fn [acc token] (str acc (show-token token))) "")
   (format template)))

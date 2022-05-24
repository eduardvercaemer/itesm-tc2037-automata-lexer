(ns lexer.html)

(defn- show-token
  "Converts a single token to its html representation"
  [{kind :kind value :value}]
  (str
   "<span class=\"code " kind
   "\">" value
   "</span>"))

(def template
  "<!DOCTYPE html>
   <html>
   <head>
     <title>automata</title>
   </head>
   <body>
     <pre>%s</pre>
     <style type=\"text/css\">
       .code {
          font-family: monospace;
          font-size: 1.2em;
       }
       .COMMENT {
          color: #525252;
       }
       .VARIABLE {
          color: #575700;
       }
       .OP {
           color: #ff2424;
       }
       .INTEGER {
           color: #ff4500;
       }
       .FLOAT {
           color: #ff4500;
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

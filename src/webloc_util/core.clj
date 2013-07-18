(ns webloc-util.core
  [:require [net.cgrand.enlive-html :as html]]
  (:gen-class))

;; GENERIC XML select functions

(defn element-that-has-child-with-text
  "Returns selector for element e that has child c where text = t"
  [e c t]
  [e (html/has [c]) (html/has [(html/pred #(= (html/text %) t))])])

(defn select-text
  "Returns seq of strings representing text values of nodes matched by selector 'sel'"
  [node-or-nodes sel]
  (let [child-node (html/select node-or-nodes [sel])]
    (map html/text child-node)))

;; PLIST-specific XML select functions

(defn select-plist-dict-entries-by-key
  "Returns sequence of 'dict' parent nodes where (text of 'dict/key' node) == key-text"
  [plist key-text]
  (html/select plist [:plist (element-that-has-child-with-text :dict :key key-text)]))

(defn select-url
  "Selects value of URL entry from plist (if exists). plist must be an html-snippet or be able to be parsed as an html-snippet (e.g. XML string)"
  [plist]
  (if (seq? plist)
    ; if plist a seq assume has been parsed by html/html-snippet
    (let [dict-node (first (select-plist-dict-entries-by-key plist "URL"))]
      (select-text dict-node :string))
    ; if plist not a seq, assume it's a string and attempt to parse
    (do
      (recur (html/html-snippet plist)))))

;; ENTRY POINT

(defn -main
  "I don't do a whole lot ... yet."
  [name & args]
  (println (str "Hello, " name "!")))

;; TEST SUPPORT

(def sample "<?xml version=\"1.0\" encoding=\"UTF-8 \"?>
<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN \" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd \">
<plist version=\"1.0 \">
<dict>
<key>URL</key>
<string>http://google.com</string>
</dict>
<dict>
<yek>URL</yek>
<string>http://google.com/yek</string>
</dict>
<dict>
<key>LRU</key>
<string>http://google.com/LRU</string>
</dict>
<coocoo>
<key>URL</key>
</string>http://coocoo</string>
</coocoo>
<random>3.14</random>
</plist>")

(defn test1 [] (html/select (html/html-snippet sample) [[:dict (html/has [:key]) (html/has [(html/pred #(= (html/text %) "LRU"))])]]))

(defn test2 [] (select-text (test1) :string))

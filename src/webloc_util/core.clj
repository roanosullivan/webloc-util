(ns webloc-util.core
  [:require
   [net.cgrand.enlive-html :as html]
   [clojure.java.io :as io]
   [me.raynes.fs :as fs]]
  (:gen-class))

;; ------------------------------------
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

;; ------------------------------------
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
      (apply str (select-text dict-node :string)))
    ; if plist not a seq, assume it's a string and attempt to parse
    (do
      (recur (html/html-snippet plist)))))

;; ------------------------------------
;; TRANSFORM

(defn render
  "Maybe something like this exists in net.cgrand.enlive-html, but I could not find it."
  [node-or-nodes]
  (apply str (html/emit* node-or-nodes)))

(defn to-html-anchor
  [url link]
  (let [template (html/html-snippet "<a href=\"HREF\">LINK</a>")]
    (html/transform
     template [:a]
     (html/do->
      (html/set-attr :href url)
      (html/content link)))))

(defn to-html
  [urls links]
  (let [template (html/html-snippet "<ul><li>ANCHOR</li></ul>")
        pairs (map #(vector %1 %2) urls links)]
    (html/transform
     template [:li]
     (html/clone-for
      [pair pairs]
      (let [url (get pair 0)
            link (get pair 1)]
        (html/content (to-html-anchor url link)))))))

(defn to-markdown-link
  [url link]
  (let [template "[LINK](URL)"]
    (-> template
        (clojure.string/replace #"URL" url)
        (clojure.string/replace #"LINK" link))))

(defn to-markdown
  [urls links]
  (let [pairs (map #(vector %1 %2) urls links)]
    (reduce
     #(let [url (get %2 0)
            link (get %2 1)]
        (str %1 " * " (to-markdown-link url link) "\n"))
     "" pairs)))

;; ------------------------------------
;; IO

(defn list-files
  [dirpath])

(defn to-link-names
  [files])

(defn conj-url-from-webloc
  "Adds URL from webloc file to urls list "
  [urls fname]
  (let [raw-xml (slurp fname)
        url (select-url raw-xml)]
    (conj urls url)))

(defn to-urls
  [webloc-files]
  (loop [files webloc-files
         result ()]
    (let [fname (first files)]
      ;(println fname)
      (if-not fname
        result
        (recur (next files)
               (conj-url-from-webloc result fname))))))

;; ------------------------------------
;; ENTRY POINT

(defn -main
  "Read plist from file and output html equivalent."
  [op path & args]
  (let [raw-xml (slurp path)
        fname (.getName (io/file path))
        bname (fs/base-name fname true)
        url (select-url raw-xml)]
    (if (= "html" op)
      (to-html url bname)
      (to-markdown url bname))))

;; ------------------------------------
;; TEST SUPPORT

(def sample-plist "<?xml version=\"1.0\" encoding=\"UTF-8 \"?>
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

(def sample-urls '("http://google.com" "http://yahoo.com"))

(def sample-links '("GOOG" "YAHOO"))

(defn test1 [] (html/select (html/html-snippet sample-plist) [[:dict (html/has [:key]) (html/has [(html/pred #(= (html/text %) "LRU"))])]]))

(defn test2 [] (select-text (test1) :string))

(defn test3 [] (println (render (to-html sample-urls sample-links))))

(defn test4 [] (println (to-markdown sample-urls sample-links)))

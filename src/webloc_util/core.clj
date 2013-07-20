(ns webloc-util.core
  [:require
   [net.cgrand.enlive-html :as html]
   [clojure.java.io :as io]
   [me.raynes.fs :as fs]])


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
;; IO

;; Useful API references for "list-webloc-files"
;;  - http://docs.oracle.com/javase/7/docs/api/java/io/File.html
;;  - http://raynes.github.io/fs/fs.core.html
(defn list-webloc-files
  "Lists .webloc files in directory d. Does not check subfolders (i.e. does _not_ use file-seq)"
  [d]
  (loop [files (.listFiles d)
         result ()]
    (let [f (first files)]
      ;(println f)
      ;(println (if f (fs/extension f) nil))
      (if-not f
        result
        (recur (next files)
               (if (= ".webloc" (fs/extension f))
                 (conj result f)
                 result))))))

(defn to-urls
  "Produces list of URLs from list of .webloc files."
  [webloc-files]
  (loop [files webloc-files
         result ()]
    (let [fname (first files)]
      ;(println fname)
      (if-not fname
        result
        (let [raw-xml (slurp fname)
              url (select-url raw-xml)]
          (recur (next files)
                 (conj result url)))))))

(defn to-basenames
  "Reduces list of files into list of basenames."
  [files]
  (let [add-bname-to-list
        (fn [l f]
          (let [bname (fs/base-name f true)]
            (conj l bname)))]
    (reduce add-bname-to-list () files)))

;; ------------------------------------
;; TRANSFORM

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

(defn render
  "Maybe something like this exists in net.cgrand.enlive-html, but I could not find it."
  [node-or-nodes]
  (apply str (html/emit* node-or-nodes)))

(defn transform-urls
  ;; todo: cleanup this doc
  "Transforms .webloc files from directory dname to specific format based on format flag. .... Dispatches list of urls and corresponding list of names to specific formatter based on format flag. By default transforms urls to string of urls, one per line. Other supported formats include 'html' and 'markdown'."
  ([format dname]
     (let [d (io/file dname)
           webloc-files (list-webloc-files d)
           urls (to-urls webloc-files)
           links (to-basenames webloc-files)]
       (println (str "Reading URLs from .webloc files in '" (.getCanonicalPath d) "'."))
       (transform-urls format urls links)))
  ([format urls names]
     (cond
      (= "html" format) (render (to-html urls names))
      (= "markdown" format) (to-markdown urls names)
      (= "raw" format) (clojure.string/join "\n" urls)
      :else (clojure.string/join "\n" urls))))

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

(def sample-webloc-files '("/Users/roanosullivan/Working/git/gitlab.729x.com/devops/my-me/docs/inspiration/Paulo Bridi.webloc" "/Users/roanosullivan/Working/git/gitlab.729x.com/devops/my-me/docs/inspiration/Kevin Davis.webloc"))

(def sample-dir (io/file "."))

(defn test1 [] (html/select (html/html-snippet sample-plist) [[:dict (html/has [:key]) (html/has [(html/pred #(= (html/text %) "LRU"))])]]))

(defn test2 [] (select-text (test1) :string))

(defn test3 [] (println (render (to-html sample-urls sample-links))))

(defn test4 [] (println (to-markdown sample-urls sample-links)))

(defn test5 [] (to-urls sample-webloc-files))

(defn test6 [] (list-webloc-files sample-dir))

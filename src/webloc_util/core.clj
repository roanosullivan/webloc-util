(ns webloc-util.core
  [:require [net.cgrand.enlive-html :as html]]
  (:gen-class))

(defn has-text [s]
  (html/pred #(= (html/text %) s)))

(defn get-entries-for-key
  "Gets sequence of 'dict' parent elements where text value of child 'key' element = k"
  [plist k]
  (html/select plist [:plist [:dict (html/has [(has-text k)])]]))

(defn get-value
  [dict]
  (let [string-el (html/select dict [:string])]
    (map html/text string-el)))

(defn get-url
  [plist]
  (get-value (first (get-entries-for-key plist "URL"))))

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
<coocoo>
<key>URL</key>
</string>http://coocoo</string>
</coocoo>
</plist>")

(def sample-snippet (html/html-snippet sample))

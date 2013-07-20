(ns webloc-util.app
  [:use [webloc-util.core]]
  (:gen-class))

(defn -main
  "Read urls from .webloc files in specified dir, and prints URLs to standard out in various formats based on 'format' arg (arg0). Supported formats include: (1) 'raw' URLs (one per line), (2) an 'html' unordered list of anchors, or (3) 'markdown' bullet list of links."
  [format dname & args]
  (println (transform-urls format dname)))

(ns stonecutter.test.view.test-helpers
  (:require [midje.sweet :refer :all]
            [net.cgrand.enlive-html :as html]))

(defn create-request [translator err params]
  {:context {:translator translator
             :errors err}
   :params params})

(def no-untranslated-strings
  (let [untranslated-string-regex #"(?!!DOCTYPE|!IEMobile)!\w+"]
    (chatty-checker [response-body] (empty? (re-seq untranslated-string-regex response-body)))))

(defn work-in-progress-removed [enlive-map]
  (empty? (html/select enlive-map [:.clj-wip])))
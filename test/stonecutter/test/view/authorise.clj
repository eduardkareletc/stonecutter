(ns stonecutter.test.view.authorise
  (:require [midje.sweet :refer :all]
            [net.cgrand.enlive-html :as html]
            [stonecutter.test.view.test-helpers :as th]
            [stonecutter.view.authorise :refer [authorise-form]]
            [stonecutter.translation :as t]))

(fact "authorise should return some html"
      (let [page (-> (th/create-request {} nil {})
                     authorise-form
                     html/html-snippet)]
        (html/select page [:body]) =not=> empty?))

(fact "work in progress should be removed from page"
      (let [page (-> (th/create-request {} nil {}) authorise-form html/html-snippet)]
        page => th/work-in-progress-removed))

(fact "there are no missing translations"
      (let [translator (t/translations-fn t/translation-map)
            page (-> (th/create-request translator nil {}) authorise-form)]
        page => th/no-untranslated-strings))
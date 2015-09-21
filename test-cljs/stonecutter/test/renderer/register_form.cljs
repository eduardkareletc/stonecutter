(ns stonecutter.test.renderer.register-form
  (:require [cemerick.cljs.test :as t]
            [stonecutter.renderer.register-form :as r]
            [stonecutter.test.test-utils :as tu]
            [dommy.core :as dommy]
            [clojure.string :as string])
  (:require-macros [cemerick.cljs.test :refer [deftest is testing run-tests are]]
                   [dommy.core :refer [sel1 sel]]
                   [stonecutter.test.macros :refer [load-template]]))

(defn setup-page! [html]
  (dommy/set-html! (sel1 :html) html))

(def index-page-template (load-template "public/index.html"))

(def default-state {:registration-first-name {:value "" :error nil}
                    :registration-last-name  {:value "" :error nil}
                    :registration-email      {:value "" :error nil}
                    :registration-password   {:value "" :error nil :tick nil}})

(defn element-has-text [selector expected-text]
  (let [selected-element (sel1 selector)
        text (dommy/text selected-element)]
    (is (not (string/blank? text)) "Element has no text")
    (is (= expected-text text)
        (str "Expected element to have <" expected-text "> but actually found <" text ">"))))


(defn element-has-no-text [selector]
  (let [selected-element (sel1 selector)
        text (dommy/text selected-element)]
    (is (string/blank? text) "Element is not blank")))

(deftest render!
         (setup-page! index-page-template)

         (testing "adding and removing invalid class from first name, last name and email address fields"
                  (are [?field-key ?form-row-selector]
                       (testing "tabular"
                                (testing "- any error, adds invalid class"
                                         (r/render! (assoc-in default-state [?field-key :error] :ANYTHING))
                                         (tu/test-field-has-class ?form-row-selector r/field-invalid-class))

                                (testing "- no error, removes invalid class"
                                         (r/render! (assoc-in default-state [?field-key :error] :ANYTHING))
                                         (tu/test-field-has-class ?form-row-selector r/field-invalid-class)
                                         (r/render! (assoc-in default-state [?field-key :error] nil))
                                         (tu/test-field-doesnt-have-class ?form-row-selector r/field-invalid-class)))

                       ;?field-key    ?form-row-selector
                       :registration-first-name r/first-name-form-row-element-selector
                       :registration-last-name r/last-name-form-row-element-selector
                       :registration-email r/email-address-form-row-element-selector
                       :registration-password r/password-form-row-element-selector))


         (testing "error messages"
                  (testing "first name"
                           (testing "blank"
                                    (r/render! (assoc-in default-state [:registration-first-name :error] :blank))
                                    (element-has-text r/first-name-validation-element-selector
                                                      (get-in r/translations [:index :register-first-name-blank-validation-message])))
                           (testing "too long"
                                    (r/render! (assoc-in default-state [:registration-first-name :error] :too-long))
                                    (element-has-text r/first-name-validation-element-selector
                                                      (get-in r/translations [:index :register-first-name-too-long-validation-message])))
                           (testing "no error"
                                    (r/render! (assoc-in default-state [:registration-first-name :error] nil))
                                    (element-has-no-text r/first-name-validation-element-selector)))

                  (testing "last name"
                           (testing "blank"
                                    (r/render! (assoc-in default-state [:registration-last-name :error] :blank))
                                    (element-has-text r/last-name-validation-element-selector
                                                      (get-in r/translations [:index :register-last-name-blank-validation-message])))
                           (testing "too long"
                                    (r/render! (assoc-in default-state [:registration-last-name :error] :too-long))
                                    (element-has-text r/last-name-validation-element-selector
                                                      (get-in r/translations [:index :register-last-name-too-long-validation-message])))
                           (testing "no error"
                                    (r/render! (assoc-in default-state [:registration-last-name :error] nil))
                                    (element-has-no-text r/last-name-validation-element-selector)))

                  (testing "email address"
                           (testing "invalid"
                                    (r/render! (assoc-in default-state [:registration-email :error] :invalid))
                                    (element-has-text r/email-address-validation-element-selector
                                                      (get-in r/translations [:index :register-email-address-invalid-validation-message])))
                           (testing "too long"
                                    (r/render! (assoc-in default-state [:registration-email :error] :too-long))
                                    (element-has-text r/email-address-validation-element-selector
                                                      (get-in r/translations [:index :register-email-address-too-long-validation-message])))
                           (testing "no error"
                                    (r/render! (assoc-in default-state [:registration-last-name :error] nil))
                                    (element-has-no-text r/email-address-validation-element-selector)))

                  (testing "password"
                           (testing "blank"
                                    (r/render! (assoc-in default-state [:registration-password :error] :blank))
                                    (element-has-text r/password-validation-element-selector
                                                      (get-in r/translations [:index :register-password-blank-validation-message])))
                           (testing "too short"
                                    (r/render! (assoc-in default-state [:registration-password :error] :too-short))
                                    (element-has-text r/password-validation-element-selector
                                                      (get-in r/translations [:index :register-password-too-short-validation-message])))
                           (testing "too long"
                                    (r/render! (assoc-in default-state [:registration-password :error] :too-long))
                                    (element-has-text r/password-validation-element-selector
                                                      (get-in r/translations [:index :register-password-too-long-validation-message])))
                           (testing "no error"
                                    (r/render! (assoc-in default-state [:registration-password :error] nil))
                                    (element-has-no-text r/password-validation-element-selector))))

         (testing "valid class on password field"
                  (testing "- tick true, adds valid class"
                           (r/render! (assoc-in default-state [:registration-password :tick] true))
                           (tu/test-field-has-class r/password-form-row-element-selector r/field-valid-class))
                  (testing "- tick false, removes valid class"
                           (r/render! (assoc-in default-state [:registration-password :tick] true))
                           (tu/test-field-has-class r/password-form-row-element-selector r/field-valid-class)
                           (r/render! (assoc-in default-state [:registration-password :tick] false))
                           (tu/test-field-doesnt-have-class r/password-form-row-element-selector r/field-valid-class))))

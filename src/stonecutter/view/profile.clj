(ns stonecutter.view.profile
  (:require [traduki.core :as t]
            [net.cgrand.enlive-html :as html]
            [stonecutter.config :as config]
            [stonecutter.routes :as r]
            [stonecutter.view.view-helpers :as vh]))

(defn add-username [request enlive-m]
  (let [email (get-in request [:session :user-login])]
    (html/at enlive-m
             [:.clj--card-email] (html/content email))))

(def library-template (vh/load-template "public/library.html"))

(def application-list-item (first (html/select library-template [:.clj--authorised-app__list-item])))

(def empty-application-list-item (first (html/select library-template [:.clj--authorised-app__list-item--empty])))

(defn application-list-items [authorised-clients]
  (html/at application-list-item
           [:.clj--authorised-app__list-item]
           (html/clone-for [client authorised-clients]
                           [:.clj--client-name] (html/content (:name client))
                           [:.clj--app-item__unshare-link] (html/set-attr :href (str (r/path :show-unshare-profile-card)
                                                                                     "?client_id="
                                                                                     (:client-id client))))))

(defn add-application-list [request enlive-m]
  (let [authorised-clients (get-in request [:context :authorised-clients])]
    (if-not (empty? authorised-clients)
      (html/at enlive-m [:.clj--app__list] (html/content (application-list-items authorised-clients)))
      (html/at enlive-m [:.clj--app__list] (html/content empty-application-list-item)))))

(defn set-sign-out-link [enlive-m]
  (html/at enlive-m
           [:.clj--sign-out__link] (html/set-attr :href (r/path :sign-out))))

(defn set-change-password-link [enlive-m]
  (html/at enlive-m
           [:.clj--change-password__link] (html/set-attr :href (r/path :show-change-password-form))))

(defn set-delete-account-link [enlive-m]
  (html/at enlive-m
           [:.clj--delete-account__link] (html/set-attr :href (r/path :show-delete-account-confirmation))))

(defn hide-admin-span [request enlive-m]
  (let [role (get-in request [:context :role])]
    (html/at enlive-m [:.clj--admin__span]
             (when (= role (:admin config/roles)) identity))))

(defn diplay-email-unconfirmed-message [request enlive-m]
  (if (= false (get-in request [:context :confirmed?]))
    enlive-m
    (vh/remove-element enlive-m [:.clj--unconfirmed-email-message-container])))

(defn set-flash-message [request enlive-m]
  (case (:flash request)
    :password-changed (html/at enlive-m [:.clj--flash-message-text] (html/set-attr :data-l8n "content:flash/password-changed"))
    :email-confirmed (html/at enlive-m [:.clj--flash-message-text] (html/set-attr :data-l8n "content:flash/email-confirmed"))
    :confirmation-email-sent (html/at enlive-m [:.clj--flash-message-text] (html/set-attr :data-l8n "content:flash/confirmation-email-sent"))
    (vh/remove-element enlive-m [:.clj--flash-message-container])))

(defn profile [request]
  (->> (vh/load-template "public/profile.html")
       (set-flash-message request)
       (diplay-email-unconfirmed-message request)
       (add-username request)
       (add-application-list request)
       set-sign-out-link
       set-change-password-link
       set-delete-account-link
       (hide-admin-span request)
       vh/remove-work-in-progress))

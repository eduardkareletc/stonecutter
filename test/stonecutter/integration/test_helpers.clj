(ns stonecutter.integration.test-helpers
  (:require [monger.core :as monger]))


(def test-db-name "stonecutter-test")
(def test-db-uri (format "mongodb://localhost:27017/%s" test-db-name))

(def db-and-conn (atom nil))

(defn get-test-db []
  (:db @db-and-conn))

(defn get-test-db-connection []
  (:conn @db-and-conn))

(defn setup-db []
  (->>
    (monger/connect-via-uri test-db-uri)
    (reset! db-and-conn)))

(defn teardown-db []
  (monger/drop-db (get-test-db-connection) test-db-name)
  (monger/disconnect (get-test-db-connection))
  (reset! db-and-conn nil))

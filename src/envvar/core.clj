(ns envvar.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import  [java.util Properties]))

;;; NB: This is a public function as of version 1.1.2. Converting from a
;;; conventional ENVIRONMENT_VARIABLE to a Clojure keywordized
;;; :environment-variable is a useful part of the envvar interface.
(defn keywordize [s]
  (when (and (string? s) (not (str/blank? s)))
    (-> (str/lower-case s)
        (str/replace "_" "-")
        (str/replace "." "-")
        (keyword))))

(defn- read-system-env-variables []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-system-properties []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-env-file-variables [filename]
  (let [env-file (io/file filename)
        props (Properties.)]
    (when (.exists env-file)
      (.load props (io/input-stream env-file)))
    (->> props
         (map (fn [[k v]] [(keywordize k) v]))
         (into {}))))

(defonce ^:private system-env (read-system-env-variables))
(defonce ^:private system-props (read-system-properties))
(defonce ^:private env-file-variables (atom (read-env-file-variables ".env")))

(defn- merged-env []
  (merge system-env system-props @env-file-variables))

(defonce ^:dynamic env (atom (merged-env)))

(defmacro with-env [bindings & body]
  `(binding [envvar.core/env (atom (assoc @envvar.core/env ~@bindings))]
     ~@body))

(defn reset-file-variables! []
  (reset! env-file-variables {})
  (reset! env (merged-env)))

(defn load-file-variables! [filename]
  (swap! env-file-variables merge (read-env-file-variables filename))
  (reset! env (merged-env)))

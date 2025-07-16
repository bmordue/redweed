(ns rwclj.seed-ttl
  (:require [clojure.string :as str]))

;; Define sample data: a list of people
(defonce people-data
  [{:uri "http://example.com/person/1"
    :vcard/fn "John Doe"
    :vcard/hasEmail "mailto:john.doe@example.com"
    :foaf/age 30}
   {:uri "http://example.com/person/2"
    :vcard/fn "Jane Smith"
    :vcard/hasEmail "mailto:jane.smith@example.com"
    :foaf/knows "http://example.com/person/1"}])

;; Define common prefixes
(def prefixes
  {:rdf "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
   :rdfs "<http://www.w3.org/2000/01/rdf-schema#>"
   :foaf "<http://xmlns.com/foaf/0.1/>"
   :vcard "<http://www.w3.org/2006/vcard/ns#>"
   :ex "<http://example.com/>"})

(defn format-prefix-map [pmap]
  (str/join "\n"
            (for [[prefix uri] pmap]
              (str "@prefix " (name prefix) ": " uri "."))))

(defn format-rdf-term [term]
  (cond
    (keyword? term) (str (namespace term) ":" (name term)) ;; Assumes keywords are qnames like :foaf/name
    (and (string? term) (re-find #"^(https?|mailto):" term)) (str "<" term ">")
    (string? term) (str "\"" (str/replace term #"\"" "\\\"") "\"")
    (number? term) (str term)
    :else (str "\"" (str/replace (str term) #"\"" "\\\"") "\"^^<http://www.w3.org/2001/XMLSchema#string>")))

  (defn resource-to-ttl [resource-map]
    (let [subject (:uri resource-map)]
      (if (nil? subject)
        (throw (IllegalArgumentException. (str "Resource map missing :uri key: " resource-map))))
      (->> (dissoc resource-map :uri) ; Remove :uri, process other key-value pairs as predicates and objects
           (map (fn [[predicate object]]
                  (if (coll? object)
                    (str (format-rdf-term predicate) " " (str/join ", " (map format-rdf-term object)))
                    (str (format-rdf-term predicate) " " (format-rdf-term object)))))
           (str/join " ;\n    ")
           (str (format-rdf-term subject) " \n    ")
           (str "\n.\n"))))


  (defn generate-ttl-string [data]
    (let [prefix-str (format-prefix-map prefixes)
          triple-str (str/join "" (map resource-to-ttl data))]
      (str prefix-str "\n\n" triple-str)))

  (comment
    ;; This is how you might use it later
    (println (generate-ttl-string people-data)))

  (defn -main [& args]
    (println (generate-ttl-string people-data)))

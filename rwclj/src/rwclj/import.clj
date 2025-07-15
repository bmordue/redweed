(ns rwclj.import
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as response]
            [rwclj.vcard :as vcard]
            [rwclj.db :as db]
            [jsonista.core :as json]
            [clojure.string :as str]
            [rwclj.photo :as photo]
            [clojure.java.io :as io])
  (:import [org.apache.jena.rdf.model ModelFactory]))

(defmulti import-resource (fn [dataset type _] type))

(defmethod import-resource :vcard [dataset request]
  (try
    (let [body (slurp (:body request))
          content-type (get-in request [:headers "content-type"] "")]
      (cond
        (str/includes? content-type "text/vcard")
        (let [vcard-text body]
          (if (vcard/validate-vcard vcard-text)
            (let [vcard-data (vcard/parse-vcard vcard-text)
                  person-uri (vcard/generate-person-uri vcard-data)
                  model (ModelFactory/createDefaultModel)
                  _ (vcard/vcard->rdf vcard-data person-uri model)]
              (db/store-rdf-model! dataset model)
              (log/info "Successfully stored vCard RDF for person:" person-uri)
              (-> (response/response
                   (json/write-value-as-string
                    {:status "success"
                     :person-uri person-uri
                     :message "vCard imported successfully"}))
                  (response/content-type "application/json")
                  (response/status 201)))
            (-> (response/response
                 (json/write-value-as-string
                  {:status "error"
                   :message "Invalid vCard format"}))
                (response/content-type "application/json")
                (response/status 400))))
        (str/includes? content-type "application/json")
        (let [json-data (json/read-value body)
              vcard-text (get json-data "vcard")]
          (if (and vcard-text (vcard/validate-vcard vcard-text))
            (let [vcard-data (vcard/parse-vcard vcard-text)
                  person-uri (vcard/generate-person-uri vcard-data)
                  model (ModelFactory/createDefaultModel)
                  _ (vcard/vcard->rdf vcard-data person-uri model)]
              (db/store-rdf-model! dataset model)
              (log/info "Successfully stored vCard RDF for person:" person-uri)
              (-> (response/response
                   (json/write-value-as-string
                    {:status "success"
                     :person-uri person-uri
                     :message "vCard imported successfully"}))
                  (response/content-type "application/json")
                  (response/status 201)))
            (-> (response/response
                 (json/write-value-as-string
                  {:status "error"
                   :message "Invalid or missing vCard data"}))
                (response/content-type "application/json")
                (response/status 400))))
        :else
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Unsupported content type. Use text/vcard or application/json"}))
            (response/content-type "application/json")
            (response/status 415))))
    (catch Exception e
      (log/error "Error processing vCard import:" (.getMessage e))
      (-> (response/response
           (json/write-value-as-string
            {:status "error"
             :message "Internal server error"}))
          (response/content-type "application/json")
          (response/status 500)))))

(defmethod import-resource :photo [dataset request]
  (let [temp-file (-> request :params :file :tempfile)
        original-filename (-> request :params :file :filename)
        file-uri (str "media/photos/" original-filename)]
    (try
      (io/copy temp-file (io/file file-uri))
      (let [metadata (photo/extract-exif-metadata (io/file file-uri))
            rdf-model (photo/create-rdf-model metadata file-uri)]
        (db/store-rdf-model! dataset rdf-model)
        {:status 200 :body {:message "Photo uploaded successfully" :file-uri file-uri}})
      (catch Exception e
        (log/error e "Error processing photo upload")
        {:status 500 :body {:error "Error processing photo upload"}}))))

(defmethod import-resource :default [_ type _]
  (log/warn "No implementation for resource type:" type)
  (response/bad-request {:error (str "Unsupported resource type: " (name type))}))

(defn import-handler [dataset request]
  (let [type (keyword (get-in request [:params :type]))]
    (import-resource dataset type request)))

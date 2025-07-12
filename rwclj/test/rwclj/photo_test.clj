(ns rwclj.photo-test
  (:require [clojure.test :refer :all]
            [rwclj.server :refer [app]]
            [ring.mock.request :as mock]
            [clojure.java.io :as io]
            [rwclj.db :as db]
            [jsonista.core :as json]))

(defn- setup-photo-test-dir
  []
  (let [dir (io/file "media/photos")]
    (when-not (.exists dir)
      (.mkdirs dir))))

#_(deftest ^:integration photo-upload-test
  (testing "Photo upload endpoint"
    (setup-photo-test-dir)
    (let [file-to-upload (io/file "test/resources/test-image.jpg")
          request (-> (mock/request :post "/api/photo/upload")
                      (assoc :headers {"content-type" "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"})
                      (assoc :body (io/input-stream file-to-upload)))
          response (app request)
          body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
      (is (= 200 (:status response)))
      (is (= "Photo uploaded successfully" (:message body)))
      (is (.exists (io/file "media/photos/test-image.jpg")))
      (let [query "PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT ?date WHERE { <media/photos/test-image.jpg> dc:date ?date . }"
            results (db/execute-sparql-select (db/get-dataset) query)]
        (is (not (empty? results)))))))

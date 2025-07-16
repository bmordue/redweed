(ns rwclj.photo-test
  (:require [clojure.test :refer [deftest is testing]]
            [rwclj.server :as server]
            [ring.mock.request :as mock]
            [clojure.java.io :as io]
            [rwclj.db :as db])
  (:import [org.apache.jena.tdb2 TDB2Factory]))


  (deftest ^:kaocha/skip photo-upload-test
    (testing "Photo upload endpoint"
      (let [test-db-dir-str "target/test-jena-photo-db"
            _ (io/delete-file test-db-dir-str true)
            dataset (TDB2Factory/connectDataset test-db-dir-str)
            app (server/make-app dataset)
            file (io/file "test/resources/test-image.jpg")
            request (-> (mock/request :post "/api/photo/upload")
                        (assoc :headers {"content-type" "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"})
                        (assoc :body (str "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n"
                                          "Content-Disposition: form-data; name=\"file\"; filename=\"test-image.jpg\"\r\n"
                                          "Content-Type: image/jpeg\r\n\r\n"
                                          (slurp file)
                                          "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n")))
            response (app request)]
        (is (= 200 (:status response)))
        (is (= "Photo uploaded successfully" (-> response :body :message)))
        (is (.exists (io/file "media/photos/test-image.jpg")))
        (let [query "PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT ?date WHERE { <media/photos/test-image.jpg> dc:date ?date . }"
              results (db/execute-sparql-select dataset query)]
          (is (= "2024-01-01T12:00:00Z" (-> results first :date)))))))

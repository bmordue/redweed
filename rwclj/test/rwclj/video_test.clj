(ns rwclj.video-test
  (:require [clojure.test :refer :all]
            [rwclj.video :as video]
            [rwclj.db :as db]
            [ring.mock.request :as mock]
            [jsonista.core :as json]))

#_(deftest ^:integration process-video-ingest-test
  (testing "should ingest a video and return a 200 response"
    (let [video-url "http://example.com/video.mp4"
          request (-> (mock/request :post "/api/video/ingest")
                      (mock/header "Content-Type" "application/json")
                      (mock/body (json/write-value-as-string {:url video-url})))
          response (video/process-video-ingest request)
          body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
      (is (= 200 (:status response)))
      (is (= "Video ingested successfully" (:message body)))
      (is (= video-url (:video-url body))))))

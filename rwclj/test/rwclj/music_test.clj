(ns rwclj.music-test
  (:require [clojure.test :refer :all]
            [rwclj.music :refer :all]
            [clojure.java.io :as io]))

(deftest process-music-upload-test
  (testing "Processing a music upload"
    (let [request {:params {:file {:tempfile (io/file "test/resources/test.mp3")
                                   :filename "test.mp3"}}}]
      (with-redefs [db/store-rdf-model! (fn [_ _] ())]
        (let [response (process-music-upload request)]
          (is (= 200 (:status response)))
          (is (= "Music uploaded successfully" (-> response :body :message)))
          (is (= "media/music/test.mp3" (-> response :body :file-uri))))))))

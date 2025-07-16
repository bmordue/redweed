(ns rwclj.recipe-test
  (:require [clojure.test :refer :all]
            [rwclj.server :refer :all]
            [rwclj.db :as db]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]))

(def test-hrecipe-html
  "<html>
     <head>
       <title>Chocolate Chip Cookies</title>
     </head>
     <body>
       <div class=\"hrecipe\">
         <h1 class=\"fn\">Chocolate Chip Cookies</h1>
         <p>By <span class=\"author\">John Smith</span></p>
         <p class=\"summary\">The best chocolate chip cookies ever!</p>
         <p>Published: <span class=\"published\">2024-01-01</span></p>
         <p>Yield: <span class=\"yield\">24 cookies</span></p>
         <p>Prep time: <span class=\"preptime\">20 minutes</span></p>
         <p>Cook time: <span class=\"cooktime\">15 minutes</span></p>
         <h2>Ingredients</h2>
         <ul>
           <li class=\"ingredient\">1 cup of flour</li>
           <li class=\"ingredient\">1/2 cup of sugar</li>
           <li class=\"ingredient\">1/4 cup of chocolate chips</li>
         </ul>
         <h2>Instructions</h2>
         <div class=\"instructions\">
           <p>1. Mix flour and sugar.</p>
           <p>2. Add chocolate chips.</p>
           <p>3. Bake at 350 degrees for 15 minutes.</p>
         </div>
       </div>
     </body>
   </html>")

(deftest recipe-import-test
  (testing "Importing a recipe via API"
    (let [request (mock/request :post "/api/recipe/import" test-hrecipe-html)
          response (app request)
          body (json/read-str (:body response) :key-fn keyword)]
      (is (= 201 (:status response)))
      (is (= "Recipe imported successfully" (:message body)))
      (is (= "http://localhost:8080/recipes/Chocolate+Chip+Cookies" (:recipe-uri body)))
      (let [query "PREFIX schema: <http://schema.org/> SELECT ?name WHERE { ?recipe a schema:Recipe ; schema:name ?name . }"
            results (db/execute-sparql-select query)]
        (is (= "Chocolate Chip Cookies" (-> results first :name)))))))

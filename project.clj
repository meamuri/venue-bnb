(defproject dr.meamuri/venue-bnb "0.1.0"
  :description "Venue-bnb algorithms."
  :dependencies [[org.clojure/clojure "1.10.3"]]

  :profiles
  {:dev
   {:source-paths ["env/dev"]}

   :kaocha
   {:dependencies [[lambdaisland/kaocha "1.0.861"]]}}

  :aliases
  {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]})

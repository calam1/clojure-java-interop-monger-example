(ns interop.core
  (:require [monger.core :as mongo]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [clojure.data.json :as json])
  (:gen-class :name com.mongodb.dao.Deals
   :methods [#^{:static false} [saveDeal [java.lang.String] java.lang.String]
             #^{:static false} [getDealsByIds [java.lang.String java.util.Set] java.lang.String]
             #^{:static false} [saveEffectiveDeals [java.util.List] void]]))

(def andQuery {"$and" "queries"})
(def tenantQuery {:id "tenantId"})
(def dealIdQuery {:deal-id {$in ["deal-ids"]}})
(def excludedData {:_id 0})

(defn- write-json-mongodb-objectid [x out]
  (json/write (str x) out))

(extend org.bson.types.ObjectId json/JSONWriter
                {:-write write-json-mongodb-objectid})

(defn- updateDealIdsQuery
  [tenantId dealIds]
  (let [tenantUpdatedQuery (assoc-in tenantQuery [:id] tenantId)
        ids (vec dealIds)
        dealIdUpdatedQuery (assoc-in dealIdQuery [:deal-id] {$in ids})
        partialQuery (vector tenantUpdatedQuery dealIdUpdatedQuery)]
          (assoc-in andQuery ["$and"] partialQuery)))

(defn getDealsByIds
  [tenantId dealIds]
  (let [conn (mongo/connect {:host "127.0.0.1" :port 27017})
        db (mongo/get-db conn "dealsdb")
        collection "deals"
        query (updateDealIdsQuery tenantId dealIds)
        data (mc/find-maps db collection query excludedData)]
            (json/write-str data)))

(defn saveDeal
  [deal]
  (let [conn (mongo/connect {:host "127.0.0.1" :port 27017})
        db (mongo/get-db conn "dealsdb")]
    (let [jsonData (json/read-str deal :key-fn keyword)]
      (json/write-str (mc/insert-and-return db "deals" jsonData)))))

(defn saveEffectiveDeals
  [effectiveDeals]
  (println "argument: " effectiveDeals)
  (let [conn (mongo/connect {:host "127.0.0.1" :port 27017})
        db (mongo/get-db conn "effectivedealsdb")]
    (let [jsonData  (map json/read-str effectiveDeals)
          jsonKeyWord (clojure.walk/keywordize-keys jsonData)]
      (println "test1: " jsonKeyWord)
    (mc/insert-batch db "effectivedeals" jsonKeyWord))))

(defn -saveDeal
  [this deal]
  (saveDeal deal))

(defn -getDealsByIds
  [this tenantId dealIds]
  (getDealsByIds tenantId dealIds))

(defn -saveEffectiveDeals
  [this effectiveDeals]
  (saveEffectiveDeals effectiveDeals))

(defn -main
  [& args])

# Clojure/Java interop code

Proof of concept using clojure to access MongoDB via the monger library:

http://clojuremongodb.info/

This example shows how to deal with using the clojure.data.json/write-str issues with the ObjectId that is specific to MongoDB.  

Also has examples of handling java Sets and Lists.  How to convert them clojure keywords and interact with monger to do batch uploads, and "in" queries.

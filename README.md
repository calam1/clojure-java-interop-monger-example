# Clojure/Java interop code

Proof of concept using clojure to access MongoDB via the monger library:

http://clojuremongodb.info/

This example shows how to deal with using the clojure.data.json/write-str issues with the ObjectId that is specific to MongoDB.  

Also has examples of handling java Sets and Lists.  How to convert them clojure keywords and interact with monger to do batch uploads, and "in" queries.

This github only contains the clojure code.

A couple of simple interactions with the code using java:
```
 public static void getDealsByIds() {
    Deals deals = new Deals();
    String results = deals.getDealsByIds(tenantId, ids);

    JsonFactory f = new MappingJsonFactory();

    try {
      JsonParser jp = f.createJsonParser(results);
      ObjectMapper mapper = new ObjectMapper();

      while (jp.nextToken() != null) {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
          while (jp.nextToken() != JsonToken.END_ARRAY) {
            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
              String deal = jp.readValueAsTree().toString();
              DealEntity dealEntity = mapper.readValue(deal, DealEntity.class);
              System.out.println("DEAL(S) " + deal);
            }
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void saveEffectiveDeals(){
    List<String> effectiveDeals = new ArrayList<>();
    effectiveDeals.add(effective_Deal_First);
    effectiveDeals.add(effective_Deal_Second);
    Deals deals = new Deals();
    deals.saveEffectiveDeals(effectiveDeals);
  }
```

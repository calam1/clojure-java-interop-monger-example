# Clojure/Java interop code

Proof of concept using clojure to access MongoDB via the monger library:

http://clojuremongodb.info/

This example shows how to deal with using the clojure.data.json/write-str issues with the ObjectId that is specific to MongoDB.  

Also has examples of handling java Sets and Lists.  How to convert them clojure keywords and interact with monger to do batch uploads, and "in" queries.

This github only contains the clojure code.

A simple client accessing the compiled clojure "jar"

```
public class MongoORM {
    public static void main(String[] args) {
//    saveDeal();
//    getDealsByIds();
//    saveEffectiveDeals();
        getEffectiveDeals();
    }


    public static void saveDeal() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            DealModel dealModel = objectMapper.readValue(inputDeal, DealModel.class);
            PersistDealRequest persistDealRequest = new PersistDealRequest(dealModel);
            Deal dealToCreate = persistDealRequest.getDeal();
            dealToCreate.setTenantId("78CE7EB3D8AD4468940EE679D7D37307ABC");
            dealToCreate.setDealId(randomUUID().toString());
            DealEntity dealToPersist = DealEntity.converter().apply(dealToCreate);
            String dealToPersistJson = objectMapper.writeValueAsString(dealToPersist);
            Deals deals = new Deals();
            System.out.println("result of save: " + deals.saveDeal(dealToPersistJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getDealsByIds() {
        String tenantId = "78CE7EB3D8AD4468940EE679D7D37307";
        Set<String> ids = new HashSet<>();
        ids.add("086cc9ed-1c8c-4561-adf0-f0e6d9a1728e");
        ids.add("086cc9ed-1c8c-4561-adf0-f0e6d9a1728e123");

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

    public static void saveEffectiveDeals() {
        List<String> effectiveDeals = new ArrayList<>();
        effectiveDeals.add(effective_Deal_First);
        effectiveDeals.add(effective_Deal_Second);
        Deals deals = new Deals();
        deals.saveEffectiveDeals(effectiveDeals);
    }

    public static void getEffectiveDeals() {
        DateTime dateTime = DateTime.now();
        DateTimeZone timeZone = DateTimeZone.UTC;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone( timeZone );

        String formattedDate = formatter.print(dateTime);
        System.out.println(formattedDate);

        Deals deals = new Deals();
        System.out.println("effective deals: " + deals.getEffectiveDeals("78CE7EB3D8AD4468940EE679D7D37307", "456", "2014-06-11"));
    }

    static String inputDeal = "{\n" +
            "  \"brandIds\": [\n" +
            "    \"123\"\n" +
            "  ],\n" +
            "  \"active\": true,\n" +
            "  \"dealType\": \"BOGO\",\n" +
            "  \"name\": \"BG-DEAL-1-1-4\",\n" +
            "  \"schedule\": {\n" +
            "    \"dateSchedule\": {},\n" +
            "    \"daySchedule\": {\n" +
            "      \"effectiveDays\": []\n" +
            "    }\n" +
            "  },\n" +
            "  \"dealModelComponents\": [\n" +
            "    {\n" +
            "      \"qualifiers\": [\n" +
            "        {\n" +
            "          \"attributes\": {\n" +
            "            \"minimumQuantity\": 1,\n" +
            "            \"maximumQuantity\": 1\n" +
            "          },\n" +
            "          \"qualifierType\": \"QuantityQualifier\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"attributes\": {\n" +
            "            \"productCodes\": [\n" +
            "              \"3a\"\n" +
            "            ]\n" +
            "          },\n" +
            "          \"qualifierType\": \"ProductQualifier\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"qualifiers\": [\n" +
            "        {\n" +
            "          \"attributes\": {\n" +
            "            \"minimumQuantity\": 1,\n" +
            "            \"maximumQuantity\": 1\n" +
            "          },\n" +
            "          \"qualifierType\": \"QuantityQualifier\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"attributes\": {\n" +
            "            \"productCodes\": [\n" +
            "              \"3b\"\n" +
            "            ]\n" +
            "          },\n" +
            "          \"qualifierType\": \"ProductQualifier\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"benefit\": {\n" +
            "        \"attributes\": {\n" +
            "          \"percentOff\": 50\n" +
            "        },\n" +
            "        \"benefitType\": \"PercentOffBenefit\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"dealModelRules\": {\n" +
            "    \"maxApplications\": 1,\n" +
            "    \"discountProrated\": false,\n" +
            "    \"discountAppliedToLowestPriced\": true\n" +
            "  },\n" +
            "  \"code\": \"dpEgPwnw\"\n" +
            "}";

    static String effective_Deal_First = "{\"tenant-id\":\"78CE7EB3D8AD4468940EE679D7D37307\",\"brand-id\":\"123\",\"id\":\"78CE7EB3D8AD4468940EE679D7D37307::123::2014-05-24T00:00:00.000Z\",\"effective-date\":\"2014-05-24T00:00:00.000Z\",\"updated-datetime\":\"2014-05-23T16:01:46.092Z\",\"deal-id\":\"1ee7648e-83c6-482e-817c-b3d28c6ab5f2\",\"start-datetime\":\"2014-05-24T05:00:00.000Z\",\"end-datetime\":\"2014-05-31T05:00:00.000Z\"}";
    static String effective_Deal_Second = "{\"tenant-id\":\"78CE7EB3D8AD4468940EE679D7D37307\",\"brand-id\":\"456\",\"id\":\"78CE7EB3D8AD4468940EE679D7D37307::123::2014-05-24T00:00:00.000Z\",\"effective-date\":\"2014-05-24T00:00:00.000Z\",\"updated-datetime\":\"2014-05-23T16:01:46.092Z\",\"deal-id\":\"1ee7648e-83c6-482e-817c-b3d28c6ab5f2\",\"start-datetime\":\"2014-05-24T05:00:00.000Z\",\"end-datetime\":\"2014-05-31T05:00:00.000Z\"}";
}


```

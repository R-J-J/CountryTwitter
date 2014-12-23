package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.*;
import com.utwente.salp2.rafal.geonames.TimeZone;
import com.utwente.salp2.rafal.json.JsonData;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rafal on 15.12.14.
 *
 * This class converts raw JSON data to info about
 * country codes and their probabilities
 */
public class UserCountryInfoMapper
{
   final public static String TWEET_COORDINATES = "$.coordinates.coordinates";
   final public static String TWEET_LANGUAGE = "$.lang";
   final public static String TWEET_LOCATION = "$.user.location";
   final public static String USER_ID = "$.id";
   final public static String USER_TIME_ZONE = "$.time_zone";
   final public static String USER_LOCATION = "$.location";
   final public static String USER_LANGUAGE = "user-$.lang";
   final public static Integer pruneMapWithProbabilities = 0;

   final private Map<String, DataSearcher> searchers;

   public UserCountryInfoMapper(String languageFilePath,
                                String capitalsFilePath,
                                String exceptionFilePath,
                                String groundTrueFilePath,
                                String geoNamesUserName)
           throws Exception
   {
      Language language = new Language(languageFilePath);
      TimeZone timeZone = new TimeZone(capitalsFilePath, exceptionFilePath);
      GeoNames geoNames = new GeoNames(geoNamesUserName);
      Coordinates coordinates = new Coordinates(geoNamesUserName);
      GroundTrue groundTrue = new GroundTrue(groundTrueFilePath);

      searchers = new HashMap<>();
      searchers.put(TWEET_COORDINATES, coordinates);
      searchers.put(TWEET_LANGUAGE, language);
      searchers.put(TWEET_LOCATION, geoNames);
      searchers.put(USER_TIME_ZONE, timeZone);
      searchers.put(USER_LOCATION, geoNames);
      searchers.put(USER_LANGUAGE, language);
      searchers.put(USER_ID, groundTrue);
   }

   public List<UserCountryInfo> match(List<JsonData> userList)
   {
      preprocessCoordinates(userList);

      // Map<dataType, Map<JsonLabel, Map<countryCode, Probability>>>
      Map<String, Map<String, Map<String, Integer>>> dataForEachKey
              = searchers.keySet().stream()
              .collect(Collectors.toMap(
                      (String line) -> line,
                      (String key2) -> getData(key2, userList)));

      return userList.stream()
              .map(json -> mapJsonDataToUCI(json, dataForEachKey))
              .filter(uci -> uci != null)
              .collect(Collectors.toList());
   }

   private UserCountryInfo mapJsonDataToUCI(
           JsonData jsonData,
           Map<String, Map<String, Map<String, Integer>>> dataForEachKey)
   {
      try
      {
         String id = getIdFromJson(jsonData);
         if (id == null)
            return null;
         UserCountryInfo uci = new UserCountryInfo(id);
         System.out.println("Current user: " + id);

         for (String key : searchers.keySet())
         {
            Map<String, Float> countryProbabilities =
                    concatenateInfoAndGetMapWithProbabilities(
                            jsonData,
                            dataForEachKey,
                            key);

            if (pruneMapWithProbabilities > 0)
            {
               countryProbabilities = onlyTopXImportant(
                       pruneMapWithProbabilities,
                       countryProbabilities);
            }

            uci.putData(key, countryProbabilities);
         }

         return uci;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         // No exception should appear here
         throw new RuntimeException();
      }
   }


   public static Map<String, Float> onlyTopXImportant(
           final Integer pruneMapWithProbabilities,
           final Map<String, Float> countryProbabilities)
   {
      if (countryProbabilities.size() <= pruneMapWithProbabilities)
         return countryProbabilities;

      Object[] sortedArray = countryProbabilities.values().stream()
              .sorted((f1, f2) -> Float.compare(f2, f1)) //reverse order of sorting
              .toArray();
      Float minProb = (Float) sortedArray[pruneMapWithProbabilities-1];

      return countryProbabilities.keySet().stream()
              .filter(country -> countryProbabilities.get(country) >= minProb)
              .collect(Collectors.toMap(
                      country -> country,
                      countryProbabilities::get
              ));
   }


   private static Map<String, Float>
   concatenateInfoAndGetMapWithProbabilities(JsonData jsonData, Map<String,
           Map<String, Map<String, Integer>>> dataForEachKey, String key)
           throws Exception
   {
      List<String> values = jsonData.getValues(key);
      Map<String, Map<String, Integer>> allDataForKey = dataForEachKey.get(key);


      Map<String, BigDecimal> dataSum = new HashMap<>();
      for (String label : values)
      {
         Map<String, Integer> dataPartial = allDataForKey.get(label);
         //some labels cannot exist, e.g. not all time zones can be found
         if (dataPartial == null || dataPartial.isEmpty())
            continue;
         concatenateData(dataSum, dataPartial);
      }
      return turnToProbabilities
              (dataSum);
   }


   private static Map<String, Float> turnToProbabilities(
           Map<String, BigDecimal> dataSum)
   {
      BigDecimal denominatorTemp = new BigDecimal(0);
      for (String key : dataSum.keySet())
      {
         denominatorTemp = denominatorTemp.add(dataSum.get(key));
      }
      final BigDecimal denominator = denominatorTemp;

      return dataSum.keySet().stream()
              .collect(Collectors.toMap(
                      key -> key,
                      key -> {
                         double up = dataSum.get(key).doubleValue();
                         double down = denominator.doubleValue();
                         return (float) (up/down);
                      }
              ));
   }


   private static void concatenateData(Map<String, BigDecimal> destination,
                                       Map<String, Integer> source)
   {
      for (String sourceKey : source.keySet())
      {
         if (destination.containsKey(sourceKey))
         { //add values
            BigDecimal pointsInDestination = destination.get(sourceKey);
            Integer pointsInSource = source.get(sourceKey);
            BigDecimal sum = pointsInDestination.add(new BigDecimal(pointsInSource));
            destination.put(sourceKey, sum);
         }
         else
         {  //put
            Integer pointsInSource = source.get(sourceKey);
            destination.put(sourceKey, new BigDecimal(pointsInSource));
         }
      }
   }


   private String getIdFromJson(JsonData jsonData)
   {
      try
      {
         List<String> idList = jsonData.getValues(USER_ID);
         if (idList.size() != 1)
            return null;
         return idList.get(0);
      }
      catch (Exception e)
      {
         // It is something wrong with this Json.
         // Definitely there should be only one id value
         e.printStackTrace();
         return null;
      }
   }


   private void preprocessCoordinates(List<JsonData> userList)
   {
      // This might be vulnerable for coordinates
      // that don't have two values for some reason..
      userList.parallelStream().forEach(jsonData -> {
         try
         {
            List<String> coordinateValues = jsonData.getValues
                    (TWEET_COORDINATES);
            jsonData.removeKey(TWEET_COORDINATES);
            List<String> coordinateValuesCombined = joinCoordinatePairs
                    (coordinateValues);
            jsonData.addKey(TWEET_COORDINATES);
            jsonData.addValue(TWEET_COORDINATES, coordinateValuesCombined);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            throw new RuntimeException();
         }
      });
   }


   private static List<String> joinCoordinatePairs(List<String> coordinateValues)
   {
      List<String> coordinateValuesCombined = new ArrayList<>();
      String temp = "";
      for (String coordinate : coordinateValues)
      {
         if (temp.isEmpty())
         {
            temp = coordinate;
         }
         else
         {
            coordinateValuesCombined.add(
                    temp + Coordinates.COORDINATE_SPLIT_PATTERN + coordinate);
            temp = "";
         }
      }
      return coordinateValuesCombined;
   }


   private Map<String, Map<String, Integer>> getData(
           String key,
           List<JsonData> userList)
   {
      try
      {
         DataSearcher dataSearcher = searchers.get(key);
         Set<String> labels = extract(key, userList);
         return dataSearcher.search(labels);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException("Cannot obtain data for " + key);
      }
   }

   private Set<String> extract(String key, List<JsonData> userList)
   {
      return userList.stream()
              .flatMap(json -> jsonToStream(json, key))
              .collect(Collectors.toSet());
   }

   private Stream<String> jsonToStream(JsonData jsonData, String key)
   {
      try
      {
         return jsonData.getValues(key).stream();
      }
      catch (Exception e)
      {
         // In this place the key must exist in JsonData.
         e.printStackTrace();
         throw new RuntimeException();
      }
   }
}

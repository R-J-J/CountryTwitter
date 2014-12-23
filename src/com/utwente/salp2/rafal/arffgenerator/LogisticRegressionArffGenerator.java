package com.utwente.salp2.rafal.arffgenerator;

import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import com.utwente.salp2.rafal.geonames.UserCountryInfo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by rafal on 18.12.14.
 */
public class LogisticRegressionArffGenerator extends ArffGenerator
{
   //how many top values should be used to create database
   final private static int topX = 2;

   protected String generateHeader()
   {
      String separator = System.getProperty("line.separator");
      return ""+
              "% Data used to get probability of country"+ separator +
              ""+ separator +
              "@RELATION countryTwiter"+ separator +
              ""+ separator +
              "@ATTRIBUTE tweetCoordinates      NUMERIC"+ separator +
              "@ATTRIBUTE tweetLanguage         NUMERIC"+ separator +
              "@ATTRIBUTE tweetLocation         NUMERIC"+ separator +
              "@ATTRIBUTE userTimeZone          NUMERIC"+ separator +
              "@ATTRIBUTE userLocation          NUMERIC"+ separator +
              "@ATTRIBUTE userLanguage          NUMERIC"+ separator +
              "@ATTRIBUTE comesFromThisCountry  NUMERIC"+ separator +
              ""+ separator +
              "@DATA"+ separator;
   }

   protected String generateLine(UserCountryInfo userCountryInfo)
   {
      UserCountryInfo prunedUci = new UserCountryInfo("000");
      for (String key : userCountryInfo.getKeys())
      {
         //Map with all values used to create data lines
         Map<String, Float> originalMap = userCountryInfo.getData(key);
         //Pruned map used to collect countries which data will be created for
         Map<String, Float> prunedMap = UserCountryInfoMapper
                 .onlyTopXImportant(topX, originalMap);

         try
         {
            prunedUci.putData(key, prunedMap);
         }
         catch (Exception e)
         {
            // There won't be such exception. It is clean UserCountryInfo
            e.printStackTrace();
            throw new RuntimeException("Exception that should never appear.");
         }
      }

      Set<String> countries = prunedUci.getKeys().stream()
              .flatMap(key -> prunedUci.getData(key).keySet().stream())
              .collect(Collectors.toSet());

      String dataLines = "";
      String separator = System.getProperty("line.separator");
      for (String country : countries)
      {
         dataLines += createDataLine(country, userCountryInfo) + separator;
      }

      return dataLines;
   }


   private String createDataLine(String country, UserCountryInfo userCountryInfo)
   {
      Float tweetCoordinates = userCountryInfo
              .getData(UserCountryInfoMapper.TWEET_COORDINATES).get(country);
      Float tweetLanguage = userCountryInfo
              .getData(UserCountryInfoMapper.TWEET_LANGUAGE).get(country);
      Float tweetLocation = userCountryInfo
              .getData(UserCountryInfoMapper.TWEET_LOCATION).get(country);
      Float userTimeZone = userCountryInfo
              .getData(UserCountryInfoMapper.USER_TIME_ZONE).get(country);
      Float userLocation = userCountryInfo
              .getData(UserCountryInfoMapper.USER_LOCATION).get(country);
      Float userLanguage = userCountryInfo
              .getData(UserCountryInfoMapper.USER_LANGUAGE).get(country);
      Float comesFromThisCountry = userCountryInfo
              .getData(UserCountryInfoMapper.USER_ID).get(country);


      tweetCoordinates = tweetCoordinates == null ? 0.0f : tweetCoordinates;
      tweetLanguage = tweetLanguage == null ? 0.0f : tweetLanguage;
      tweetLocation = tweetLocation == null ? 0.0f : tweetLocation;
      userTimeZone = userTimeZone == null ? 0.0f : userTimeZone;
      userLocation = userLocation == null ? 0.0f : userLocation;
      userLanguage = userLanguage == null ? 0.0f : userLanguage;
      comesFromThisCountry = comesFromThisCountry == null ? 0.0f : comesFromThisCountry;

      return "" + tweetCoordinates + "," +
              tweetLanguage + "," +
              tweetLocation + "," +
              userTimeZone + "," +
              userLocation + "," +
              userLanguage + "," +
              comesFromThisCountry;
   }
}

package com.utwente.salp2.rafal.arffgenerator;

import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import com.utwente.salp2.rafal.geonames.UserCountryInfo;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by rafal on 19.12.14.
 */
public class Comparator extends ArffGenerator
{
   final private static int topX = 2;

   @Override
   protected String generateLine(UserCountryInfo uci)
   {
      String trueCountryCode = uci.getData(UserCountryInfoMapper.USER_ID)
              .keySet().iterator().next(); //There have to be only one element

      String dataLine = "";
      Set<String> countriesToCompareWith = uci.getKeys().stream()
              .flatMap(key -> UserCountryInfoMapper.onlyTopXImportant(topX,
                      uci.getData(key)).keySet().stream())
              .collect(Collectors.toSet());

      //Remove true country code if exists there
      countriesToCompareWith.remove(trueCountryCode);

      for (String country : countriesToCompareWith)
      {
         dataLine += createDataLine(trueCountryCode, country, uci);
      }

      return dataLine;
   }


   private String createDataLine(String trueCountryCode, String countryToCompareWith,
                                 UserCountryInfo uci)
   {
      // Learn twice with two different orders of countries
      // to prevent results to be order dependant.
      //TODO
      return null;
   }


   @Override
   protected String generateHeader()
   {
      String separator = System.getProperty("line.separator");
      return ""+
              "% Data used to get probability of country"+ separator +
              ""+ separator +
              "@RELATION countryTwitter"+ separator +
              ""+ separator +
              "@ATTRIBUTE oneTweetCoordinates      NUMERIC"+ separator +
              "@ATTRIBUTE oneTweetLanguage         NUMERIC"+ separator +
              "@ATTRIBUTE oneTweetLocation         NUMERIC"+ separator +
              "@ATTRIBUTE oneUserTimeZone          NUMERIC"+ separator +
              "@ATTRIBUTE oneUserLocation          NUMERIC"+ separator +
              "@ATTRIBUTE oneUserLanguage          NUMERIC"+ separator +
              "@ATTRIBUTE TwoTweetCoordinates      NUMERIC"+ separator +
              "@ATTRIBUTE TwoTweetLanguage         NUMERIC"+ separator +
              "@ATTRIBUTE TwoTweetLocation         NUMERIC"+ separator +
              "@ATTRIBUTE TwoUserTimeZone          NUMERIC"+ separator +
              "@ATTRIBUTE TwoUserLocation          NUMERIC"+ separator +
              "@ATTRIBUTE TwoUserLanguage          NUMERIC"+ separator +
              "@ATTRIBUTE comesFromThisCountry     NUMERIC"+ separator +
              ""+ separator +
              "@DATA"+ separator;
   }
}

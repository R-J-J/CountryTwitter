package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.*;

import java.util.*;

/**
 * Created by rafal on 12.12.14.
 *
 * Responsibility of this class is to translate locations to
 * country codes.
 */
public class GeoNames
{
   private final static String USER_NAME = "macdrag";
   private final static int GEONAMES_HISTORY_SIZE = 10;

   private SearchHistory<String, Map<String, Integer>> geoNameHistory;

   public GeoNames()
   {
      geoNameHistory = new SearchHistory<>(GEONAMES_HISTORY_SIZE);
   }

   public Map<String, Integer> searchGeoName (String geoName)
           throws Exception
   {
      return searchGeoName(geoName, false, null);
   }

   public Map<String, Integer> searchGeoName (String geoName,
                                              boolean exactMatch,
                                              FeatureClass featureClass)
           throws Exception
   {
      //TODO history contains but features changed?
      // probably replace web search for time zone with file search
      Map<String, Integer> result = geoNameHistory.search(geoName);
      if (result == null)
         result = searchGeoNameWeb(geoName, exactMatch, featureClass);
      return new HashMap<>(result);
   }

   private Map<String, Integer> searchGeoNameWeb(String geoName,
                                                 boolean exactMatch,
                                                 FeatureClass featureClass)
           throws Exception
   {
      WebService.setUserName(USER_NAME);

      ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
      if (exactMatch)
         searchCriteria.setNameEquals(geoName);
      else
         searchCriteria.setQ(geoName);
      if (featureClass != null)
         searchCriteria.setFeatureClass(featureClass);
      ToponymSearchResult searchResult = WebService.search(searchCriteria);

      Map<String, Integer> sumResults = new HashMap<>();
      for (Toponym toponym : searchResult.getToponyms())
      {
         String countryCode = toponym.getCountryCode();
         Integer count = sumResults.get(countryCode);
         if (count == null)
            sumResults.put(countryCode, 1);
         else
            sumResults.put(countryCode, count + 1);
      }

      geoNameHistory.addToHistory(geoName, sumResults);
      return sumResults;
   }
}

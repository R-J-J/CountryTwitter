package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.WebService;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.Toponym;

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
      Map<String, Integer> result = geoNameHistory.search(geoName);
      if (result == null)
         result = searchGeoNameWeb(geoName);
      return new HashMap<>(result);
   }

   private Map<String, Integer> searchGeoNameWeb (String geoName)
           throws Exception
   {
      WebService.setUserName(USER_NAME);

      ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
      searchCriteria.setQ(geoName);
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

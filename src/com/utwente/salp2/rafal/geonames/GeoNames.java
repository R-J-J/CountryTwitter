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
public class GeoNames implements DataSearcher
{
   private final static int GEONAMES_HISTORY_SIZE = 10;

   private final String userName;
   private SearchHistory<String, Map<String, Integer>> geoNameHistory;

   public GeoNames(final String userName)
   {
      geoNameHistory = new SearchHistory<>(GEONAMES_HISTORY_SIZE);
      this.userName = userName;
   }

   @Override
   public Map<String, Map<String, Integer>> search(Set<String> toSearch)
           throws Exception
   {
      Map<String, Map<String, Integer>> results = new HashMap<>();
      for (String name : toSearch)
      {
         results.put(name, searchGeoNameWeb(name));
      }
      return results;
   }

   public Map<String, Integer> searchGeoName (String geoName)
           throws Exception
   {
      Map<String, Integer> result = geoNameHistory.search(geoName);
      if (result == null)
         result = searchGeoNameWeb(geoName);
      return new HashMap<>(result);
   }


   private Map<String, Integer> searchGeoNameWeb(String geoName)
           throws Exception
   {
      WebService.setUserName(userName);

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

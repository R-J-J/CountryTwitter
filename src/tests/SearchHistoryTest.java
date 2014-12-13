package tests;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchHistoryTest
{
   @Test
   public void testSearchHistory()
   {
      SearchHistory<String, Integer> searchHistory = new SearchHistory<>(3);
      searchHistory.addToHistory("a", 1);
      searchHistory.addToHistory("b", 2);
      searchHistory.addToHistory("c", 3);
      searchHistory.addToHistory("d", 4);

      assertNotNull(searchHistory.search("b"));
      assertNull(searchHistory.search("a"));

      searchHistory.addToHistory("e", 5);

      assertNull(searchHistory.search("c"));
      assertNotNull(searchHistory.search("b"));
      assertNotNull(searchHistory.search("d"));
      assertNotNull(searchHistory.search("e"));
   }
}
package tests;

import com.utwente.salp2.rafal.geonames.TimeZone;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class TimeZoneTest
{

   @Test
   public void testSearchTimeZone()
           throws Exception
   {
      String currentDirectory = System.getProperty("user.dir");
      String exceptionFilePath = currentDirectory +
              "/res/TimeZoneExceptions.txt";
      String countryInfoFilePath = currentDirectory +
              "/res/CountryInfo.csv";
      TimeZone timeZone = new TimeZone(countryInfoFilePath, exceptionFilePath);


      Map<String, Integer> searchResult =
              timeZone.searchTimeZone("Atlantic Time (Canada)");
      assertTrue(searchResult.get("CA") == 1);
      assertTrue(searchResult.size() == 1);


      Set<String> timeZonesSet= new HashSet<>(
              Arrays.asList("Quito", "Eastern Time (US & Canada)"));
      Map<String, Map<String, Integer>> searchResults =
              timeZone.searchTimeZones(timeZonesSet);
      Map<String, Integer> quito = searchResults.get("Quito");
      Map<String, Integer> easternTime =
              searchResults.get("Eastern Time (US & Canada)");

      assertNotNull(quito);
      assertNotNull(easternTime);
      assertTrue(searchResults.size() == 2);

      assertTrue(quito.get("EC") == 1);
      assertTrue(quito.size() == 1);

      assertTrue(easternTime.get("US") == 1);
      assertTrue(easternTime.get("CA") == 1);
      assertTrue(easternTime.size() == 2);
   }
}
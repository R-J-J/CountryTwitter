package tests;

import com.utwente.salp2.rafal.geonames.TimeZone;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
      TimeZone timeZone = new TimeZone(exceptionFilePath);

      Map<String, Integer> searchResult =
              timeZone.searchTimeZone("Eastern Time (US & Canada)");
      assertTrue(searchResult.get("US") == 1);
      assertTrue(searchResult.get("CA") == 1);
      assertTrue(searchResult.size() == 2);

      searchResult =
              timeZone.searchTimeZone("Quito");
      assertTrue(searchResult.get("EC") == 1);
      assertTrue(searchResult.get("AO") == 1);
      assertTrue(searchResult.get("CO") == 1);
      assertTrue(searchResult.get("US") == 2);
      assertTrue(searchResult.get("PE") == 1);
      assertTrue(searchResult.get("PA") == 1);
      assertTrue(searchResult.size() == 6);
   }
}
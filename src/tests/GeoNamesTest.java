package tests;

import com.utwente.salp2.rafal.geonames.GeoNames;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GeoNamesTest
{
   @Test
   public void testSearchGeoName()
           throws Exception
   {
      GeoNames geoNames = new GeoNames();
      Map<String, Integer> result;

      result = geoNames.searchGeoName("Wałdowo Szlacheckie");
      assertTrue(result.get("PL") == 1);
      assertTrue(result.size() == 1);

      result = geoNames.searchGeoName("Grudziądz");
      assertTrue(result.get("PL") == 13);
      assertTrue(result.size() == 1);

      result = geoNames.searchGeoName("Poznan");
      assertTrue(result.get("PL") == 77);
      assertTrue(result.get("US") == 1);
      assertTrue(result.get("AQ") == 1);
      assertTrue(result.get("RU") == 1);
      assertTrue(result.get("UA") == 1);
      assertTrue(result.size() == 5);
   }
}
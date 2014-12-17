package tests;

import com.utwente.salp2.rafal.geonames.Coordinates;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CoordinatesTest
{
   @Test
   public void testSearchCoordinates()
           throws Exception
   {
      Coordinates coordinates = new Coordinates("macdrag");

      String kabul = coordinates.searchCoordinates(34.5, 69.2);
      assertTrue(kabul.equals("AF"));
      String canberra = coordinates.searchCoordinates(-35.3, 149.1);
      assertTrue(canberra.equals("AU"));
      String saopaulo = coordinates.searchCoordinates(-23.6, -46.02);
      assertTrue(saopaulo.equals("BR"));
      saopaulo = coordinates.searchCoordinates(-23.6, -46.02);
      assertTrue(saopaulo.equals("BR"));
   }

   @Test(expected = IOException.class)
   public void testWrongSearchCoordinates()
      throws Exception
   {
      Coordinates coordinates = new Coordinates("macdrag");
      coordinates.searchCoordinates(9999999, 9999999);
   }

   @Test
   public void testSetWithBadElement()
           throws Exception
   {
      Coordinates coordinates = new Coordinates("macdrag");

      Set<String> labels = new HashSet<>();
      String label = "-99.13640766" +
              Coordinates.COORDINATE_SPLIT_PATTERN +
              "19.36169081";
      labels.add(label);
      Map<String, Integer> result = coordinates.search(labels).get(label);
      assertTrue(result.isEmpty());
   }
}
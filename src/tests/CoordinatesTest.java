package tests;

import com.utwente.salp2.rafal.geonames.Coordinates;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CoordinatesTest
{
   @Test
   public void testSearchCoordinates()
           throws Exception
   {
      Coordinates coordinates = new Coordinates();

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
      Coordinates coordinates = new Coordinates();
      coordinates.searchCoordinates(9999999, 9999999);
   }
}
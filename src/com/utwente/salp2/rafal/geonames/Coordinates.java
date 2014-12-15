package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.GeoNamesException;
import org.geonames.WebService;

import java.io.IOException;

/**
 * Created by rafal on 13.12.14.
 *
 * Responsibility of this class is to translate coordinates to
 * country codes.
 */
public class Coordinates
{
   private final static String USER_NAME = "macdrag";
   private final static int COORDINATES_HISTORY_SIZE = 10;

   private SearchHistory<String, String> coordinatesHistory;

   public Coordinates()
   {
      coordinatesHistory = new SearchHistory<>(COORDINATES_HISTORY_SIZE);
   }

   public String searchCoordinates (double latitude, double longitude)
           throws Exception
   {
      String coordinatesAsString = coordinatesAsString(latitude, longitude);
      String result = coordinatesHistory.search(coordinatesAsString);
      if (result == null)
         result = searchCoordinatesWeb(latitude, longitude);
      return result;
   }

   private String searchCoordinatesWeb(double latitude, double longitude)
           throws IOException
   {
      String countryCode;
      try
      {
         WebService.setUserName(USER_NAME);
         countryCode = WebService.countryCode(latitude, longitude);
      }
      catch (GeoNamesException e)
      {
         // This exception appears when coordinates are incorrect
         e.printStackTrace();
         return null;
      }

      String coordinatesAsString = coordinatesAsString(latitude, longitude);
      coordinatesHistory.addToHistory(coordinatesAsString, countryCode);
      return countryCode;
   }

   private String coordinatesAsString(double latitude, double longitude)
   {
      return "" + latitude + longitude;
   }
}

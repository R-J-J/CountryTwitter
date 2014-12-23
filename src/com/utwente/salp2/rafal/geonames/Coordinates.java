package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.GeoNamesException;
import org.geonames.WebService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rafal on 13.12.14.
 *
 * Responsibility of this class is to translate coordinates to
 * country codes.
 */
public class Coordinates implements DataSearcher
{
   public static final String COORDINATE_SPLIT_PATTERN = ";";
   private final static int COORDINATES_HISTORY_SIZE = 10;

   private final String userName;
   private SearchHistory<String, String> coordinatesHistory;

   public Coordinates(final String userName)
   {
      coordinatesHistory = new SearchHistory<>(COORDINATES_HISTORY_SIZE);
      this.userName = userName;
   }

   public String searchCoordinates (double latitude, double longitude)
           throws IOException
   {
      String coordinatesAsString = coordinatesAsString(latitude, longitude);
      String result = coordinatesHistory.search(coordinatesAsString);
      if (result == null)
         result = searchCoordinatesWeb(latitude, longitude);
      return result;
   }

   @Override
   public Map<String, Map<String, Integer>> search(Set<String> toSearch)
   {
      Map<String, Map<String, Integer>> results = new HashMap<>();
      for (String coordinates : toSearch)
      {
         double latitude = getCoordinate(coordinates, 1);
         double longitude = getCoordinate(coordinates, 0);
         Map<String, Integer> tempMap = new HashMap<>();
         try
         {
            String countryCode = searchCoordinates(latitude, longitude);
            if (countryCode == null)
               continue;
            tempMap.put(countryCode, 1);
         }
         catch (IOException e)
         {
            e.printStackTrace();
            throw new RuntimeException();
         }
         results.put(coordinates, tempMap);
      }
      return results;
   }


   private double getCoordinate(String coordinates, int i)
   {
      return Double.parseDouble(
              coordinates.split(COORDINATE_SPLIT_PATTERN)[i]);
   }


   private String searchCoordinatesWeb(double latitude, double longitude)
           throws IOException
   {
      String countryCode;
      try
      {
         WebService.setUserName(userName);
         countryCode = WebService.countryCode(latitude, longitude);
      }
      catch (GeoNamesException e)
      {
         // This exception appears when coordinates are incorrect
         System.err.println("Country for lat: " + latitude +
                 ", and long: " + longitude + " could not be found.");
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

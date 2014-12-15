package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.FeatureClass;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rafal on 14.12.14.
 *
 * Responsibility of this class is to translate time zones to
 * country codes.
 *
 * Expected file structure:
 * Timezone \t comma-separated-country-codes
 *
 * TODO consider searching with set of timeZones due to exception file. There are capitals mostly and GeoNames counts all cities
 */
public class TimeZone
{
   private final static int TIMEZONE_HISTORY_SIZE = 10;
   private SearchHistory<String, Map<String, Integer>> timeZoneFileHistory;
   private GeoNames geoNames;
   private File exceptionFile;

   public TimeZone(String exceptionFilePath)
           throws Exception
   {
      timeZoneFileHistory = new SearchHistory<>(TIMEZONE_HISTORY_SIZE);
      geoNames = new GeoNames();
      exceptionFile = new File(exceptionFilePath);
      if (!exceptionFile.isFile() || !exceptionFile.canRead())
      {
         throw new Exception("Cannot read from given File.");
      }
   }

   public Map<String, Integer> searchTimeZone(String timeZone)
           throws Exception
   {
      Map<String, Integer> result = timeZoneFileHistory.search(timeZone);
      if (result == null)
      {
         result = searchExceptionFile(timeZone);
      }
      if (result.isEmpty())
      {
         //search in the web service
         //TODO searching only capital cities?
         result = geoNames.searchGeoName(timeZone, true, FeatureClass.P);
      }
      if (result.isEmpty())
      {
         System.out.println("Time zone \"" + timeZone + "\" was not found."+
         " Please consider adding it to exception file");
      }
      return new HashMap<>(result);
   }

   private Map<String, Integer> searchExceptionFile(String timeZone)
   {
      Map<String, Integer> result = new HashMap<>();
      //search in exception file
      try (BufferedReader userBufferedReader = new BufferedReader(new
              InputStreamReader(new FileInputStream(exceptionFile))))
      {
         result = userBufferedReader.lines()
                 .filter(line -> line.split("\\t")[0].equals(timeZone))
                 .map(this::takeCountrySetOnly)
                 .flatMap(this::obtainCountries)
                 .collect(Collectors.toMap(str -> str, str -> 1));
                 //TODO check if toMap manages with two same countries
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
         throw new RuntimeException("File with users data was not found " +
                 "when opening input stream.");
      }
      catch (IOException e)
      {
         // If file could not be closed.
         e.printStackTrace();
         // If file stay unclosed, nothing really bad will happen.
      }
      return result;
   }


   private Stream<String> obtainCountries(String setOfCountries)
   {
      String[] countries = setOfCountries.split(",");
      return Stream.of(countries);
   }


   private String takeCountrySetOnly(String line)
   {
      return line.split("\\t")[1];
   }
}

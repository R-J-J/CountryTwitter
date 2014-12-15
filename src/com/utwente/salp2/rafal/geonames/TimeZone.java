package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;
import org.geonames.FeatureClass;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
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
   private File exceptionFile;
   private File capitalFile;

   public TimeZone(String capitalsFilePath, String exceptionFilePath)
           throws Exception
   {
      timeZoneFileHistory = new SearchHistory<>(TIMEZONE_HISTORY_SIZE);

      exceptionFile = new File(exceptionFilePath);
      if (!exceptionFile.isFile() || !exceptionFile.canRead())
         throw new Exception("Cannot read from given File with exception list.");

      capitalFile = new File(capitalsFilePath);
      if (!capitalFile.isFile() || !capitalFile.canRead())
         throw new Exception("Cannot read from given File with exception list.");
   }

   public Map<String, Integer> searchTimeZone(String timeZone)
           throws Exception
   {
      Set<String> tempLanguage = new HashSet<>();
      tempLanguage.add(timeZone);
      Map<String, Map<String, Integer>> results;
      results = searchTimeZones(tempLanguage);
      return results.get(timeZone);
   }

   public Map<String, Map<String, Integer>> searchTimeZones(
           final Set<String> timeZonesSet)
   {
      Map<String, Map<String, Integer>> results;
      Set<String> timeZones = new HashSet<>(timeZonesSet);

      // Get country codes from history from history
      results = timeZones.stream().filter(timeZoneFileHistory::isInHistory)
              .sequential()
              .collect(Collectors.toMap(str -> str,
                      timeZoneFileHistory::search));

      // Remove from the set languages that were already found
      timeZones = removeFoundTimeZones(results, timeZones);
      if (timeZones.isEmpty())
         return results;

      // Search file with capitals
      results.putAll(searchFile(timeZones, capitalFile, 2, 0));

      // Remove from the set languages that were already found
      timeZones = removeFoundTimeZones(results, timeZones);
      if (timeZones.isEmpty())
         return results;

      // Search file with exceptions
      results.putAll(searchFile(timeZones, exceptionFile, 0, 1));

      // Remove from the set languages that were already found
      timeZones = removeFoundTimeZones(results, timeZones);
      if (timeZones.isEmpty())
         return results;

      System.out.println("Following time zones were not found."+
              " Consider adding them to the exception file.");
      timeZones.stream()
              .map(timeZone -> "\t- " + timeZone)
              .forEach(System.out::println);

      return results;
   }


   private static Set<String> removeFoundTimeZones(
           final Map<String, Map<String, Integer>> results,
           final Set<String> timeZones)
   {
      return timeZones.stream()
              .filter(str -> !results.keySet().contains(str))
              .collect(Collectors.toSet());
   }


   private static Map<String, Map<String, Integer>> searchFile(
           final Set<String> timeZone, final File file,
           final int timeZoneColumn, final int countryCodeColumn)
   {
      Map<String, Map<String, Integer>> result = new HashMap<>();
      //search in exception file
      try (BufferedReader userBufferedReader = new BufferedReader(new
              InputStreamReader(new FileInputStream(file))))
      {
         result = userBufferedReader.lines()
                 .filter(line -> contains(timeZone, timeZoneColumn, line))
                 .collect(Collectors.toMap(
                         line -> getTimeZone(timeZoneColumn, line),
                         line -> createMap(countryCodeColumn, line)
                 ));
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
         throw new RuntimeException("File \"" + file.getName() +
                 "\" was not found when opening input stream.");
      }
      catch (IOException e)
      {
         // If file could not be closed.
         e.printStackTrace();
         // If file stay unclosed, nothing really bad will happen.
      }
      return result;
   }


   private static Map<String, Integer> createMap(
           final int countryCodeColumn,
           final String line)
   {
      Map<String, Integer> countryMap = new HashMap<>();
      String countries = getTimeZone(countryCodeColumn, line);
      Arrays.asList(countries.split(",")).forEach
              (country -> countryMap.put(country, 1));
      return countryMap;
   }


   private static String getTimeZone(
           final int timeZoneColumn,
           final String line)
   {
      return line.split("\\t")[timeZoneColumn];
   }


   private static boolean contains(
           final Set<String> timeZone,
           final int timeZoneColumn,
           final String line)
   {
      return timeZone.contains(getTimeZone(timeZoneColumn, line));
   }
}

package com.utwente.salp2.rafal.geonames;

import com.utwente.salp2.rafal.geonames.helpers.SearchHistory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by rafal on 14.12.14.
 *
 * Responsibility of this class is to translate language to
 * country codes.
 *
 * Expected file structure:
 * ISO \t CountryName \t Capital \t population \t comma-separated-languages
 */
public class Language
{
   private final static int LANGUAGE_HISTORY_SIZE = 20;
   private SearchHistory<String, Map<String, Integer>> languageHistory;
   private File languageFile;

   public Language(String languageFilePath)
           throws Exception
   {
      languageHistory = new SearchHistory<>(LANGUAGE_HISTORY_SIZE);
      languageFile = new File(languageFilePath);
      if (!languageFile.isFile() || !languageFile.canRead())
      {
         throw new Exception("Cannot read from given File.");
      }
   }

   public Map<String, Integer> searchLanguage(String language)
   {
      Set<String> tempLanguage = new HashSet<>();
      tempLanguage.add(language);
      Map<String, Map<String, Integer>> results;
      results = searchLanguages(tempLanguage);
      return results.get(language);
   }

   public Map<String, Map<String, Integer>> searchLanguages(final Set<String> languagesSet)
   {
      Map<String, Map<String, Integer>> results;
      Set<String> languages = new HashSet<>(languagesSet);

      // Get country codes from history from history
      results = languages.stream().filter(languageHistory::isInHistory)
              .sequential()
              .collect(Collectors.toMap(str -> str, languageHistory::search));

      // Remove from the set languages that were found in the history
      languages = languages.stream()
              .filter(str -> !results.keySet().contains(str))
              .collect(Collectors.toSet());

      // Search file
      results.putAll(searchFile(languages));

      // Add to history languages that were not found.
      languages.stream().forEachOrdered(lang -> languageHistory.addToHistory
              (lang, results.get(lang)));

      return results;
   }

   private Map<String, Map<String, Integer>> searchFile(Set<String> languages)
   {
      Map<String, Map<String, Integer>> result = new HashMap<>();
      languages.stream().forEach(lang -> result.put(lang, new HashMap<>()));

      try (BufferedReader languagesBufferedReader =
                   new BufferedReader(
                           new InputStreamReader(
                                   new FileInputStream(languageFile))))
      {
         String line;
         while((line = languagesBufferedReader.readLine()) != null)
         {
            Map<String, Integer> languagesOfCountry
                    = getCountryLanguagesAndNoOfSpeakers(line);
            String countryCode = getCountryCode(line);
            combineLanguages(languages, languagesOfCountry, countryCode, result);
         }
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


   private void combineLanguages(Set<String> languagesToLookFor,
                                 Map<String, Integer> givenLanguagesOfCountry,
                                 String countryCode,
                                 Map<String, Map<String, Integer>> resultMap)
   {
      //Two specializations of the same language in the same country
      languagesToLookFor.stream()
              .filter(lang -> givenLanguagesOfCountry.keySet().contains(lang))
              .forEach(languageToLookFor -> {
                 Map<String, Integer> currentMap = resultMap.get
                         (languageToLookFor);
                 Integer newCountOfSpeakers = givenLanguagesOfCountry
                         .get(languageToLookFor);
                 currentMap.put(countryCode, newCountOfSpeakers);
              });
   }


   private Map<String, Integer> getCountryLanguagesAndNoOfSpeakers(String line)
   {
      try
      {
         String[] columns = line.split("\t");
         String languages = columns[4];
         String[] langsArray = languages.split(",");

         Integer population = Integer.parseInt(columns[3]);
         int denominator = sumDownToZero(langsArray.length);

         // Remove additional info about languages
         Map<String, Integer> langMap = new HashMap<>();
         for (int i=0; i < langsArray.length; i++)
         {
            // Get rid of country specialization
            String lang = langsArray[i].split("-")[0];

            int nominator = langsArray.length - i;
            Integer estimatedPopulation =
                    (int) (nominator/(float)denominator * population);
            if (langMap.containsKey(lang))
            {
               //Two specializations of the same language
               Integer currentPopulation = langMap.get(lang);
               langMap.put(lang, currentPopulation + estimatedPopulation);
            }
            else
            {
               langMap.put(lang, estimatedPopulation);
            }
         }

         return langMap;
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
         // E.g. Antarctica has less information
         // Just skip line
         return new HashMap<>();
      }
   }


   private int sumDownToZero(int integer)
   {
      if (integer < 1)
         throw new RuntimeException("Cannot sum down to zero number"+
                 " lower than 1");
      int sum = 0;
      while (integer >= 1)
      {
         sum += integer;
         integer--;
      }
      return sum;
   }


   private String getCountryCode(String line)
   {
      String[] columns = line.split("\\t");
      return columns[0];
   }
}

package com.utwente.salp2.rafal.json;

import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataExtractor;
import com.utwente.salp2.rafal.json.JsonDataUser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rafal on 14.12.14.
 *
 * This class creates List of JsonData for Tweeter. One JsonData object
 * contains information about user and their tweets. Implicitly
 * JsonDataUser and JsonDataTwitter classes are used.
 */
public class JsonDataReader
{
   public static List<JsonData> read(final File usersFile,
                                     final Class<? extends JsonData> jsonDataUserClass,
                                     final File tweetsDirectory,
                                     final Class<? extends JsonData> jsonDataTweetClass)
   {
      return readJsonData(usersFile, jsonDataUserClass).stream()
              .map(json -> combineWithTweets(json, tweetsDirectory, jsonDataTweetClass))
              .collect(Collectors.toList());
   }

   private static List<JsonData> readJsonData(
           File jsonFile,
           Class<? extends JsonData> jsonType)
   {
      List<JsonData> jsonDataList = new ArrayList<>();
      try (BufferedReader userBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile))))
      {
         jsonDataList = userBufferedReader.lines()
                 .map(line -> lineToJsonDataUserStream(line, jsonType))
                 .filter(jsonData -> jsonData != null)
                 .collect(Collectors.toList());
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

      return jsonDataList;
      // There were complaints about uninitialized variable due to IOException,
      // but in fact usersData will be assigned before throwing it.
   }


   private static JsonData lineToJsonDataUserStream(
           String line,
           Class<? extends JsonData> jsonType)
   {
      try
      {
         return new JsonDataExtractor().extractData(new ByteArrayInputStream
                 (line.getBytes(StandardCharsets.UTF_8)), jsonType
                 .newInstance());
      }
      catch (InstantiationException e)
      {
         e.printStackTrace();
         throw new RuntimeException("Could not instantiate "+
                 "<? extends JsonData> class");
      }
      catch (IllegalAccessException e)
      {
         e.printStackTrace();
         throw new RuntimeException();
      }
      catch (Exception e)
      {
         System.err.println("User JSON could not be parsed, so it is ignored.");
         e.printStackTrace();
         return null;
      }
   }


   private static JsonData combineWithTweets(
           JsonData userData,
           File tweetsDirectory,
           Class<? extends JsonData> jsonDataTweetClass)
   {
      try
      {
         List<String> userIds = userData.getValues(JsonDataUser.ID);
         if (userIds.size() != 1)
         {
            throw new Exception("There should be exactly one id value in user" +
                    " data");
         }
         else
         {
            String userIdFileName = userIds.get(0).concat(".json");
            File userIdFile = new File(tweetsDirectory, userIdFileName);
            JsonData combinedData = new JsonData();
            List<JsonData> tweets = readJsonData(userIdFile,
                    jsonDataTweetClass);
            tweets.stream().forEachOrdered(combinedData::combine);
            //Because users and tweets have the same path to language info
            //one of them must be renamed.
            userData.rename(JsonDataUser.LANGUAGE,
                    "user-" + JsonDataUser.LANGUAGE);
            combinedData.combine(userData);
            return combinedData;
         }
      }
      catch (Exception e)
      {
         String separator = System.getProperty("line.separator");
         System.err.println("Wrong JsonData - possibly it is not user JSON" +
                 separator + e.getMessage());
         e.printStackTrace();
         return null;
      }
   }
}

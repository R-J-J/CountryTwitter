package com.utwente.salp2.rafal;

import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataExtractor;
import com.utwente.salp2.rafal.json.JsonDataTwitter;
import com.utwente.salp2.rafal.json.JsonDataUser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rafal on 10.12.14.
 * Main class
 */
public class MainClass
{
   final File tweetsDirectory;
   final File usersFile;
   List<JsonData> userAndTweetsData;


   public static void main(String[] args)
           throws Exception
   {
      if (args.length != 2)
      {
         String separator = System.getProperty("line.separator");
         throw new Exception("Invalid number of arguments. " + separator +
                 "\tFirst argument: path to a directory containing json data " +
                 "about tweets," + separator + "\tSecond argument: path " +
                 " to file with json data about users");
      }

      File tweetsDirectory = new File(args[0]);
      if (!tweetsDirectory.isDirectory())
         throw new Exception("First argument is a path to a directory " +
                 "containing json data about tweets");
      if (!tweetsDirectory.exists())
         throw new Exception("Directory with tweets does not exists.");
      if (!tweetsDirectory.canRead())
         throw new Exception("Cannot read from a directory with tweets");

      File usersFile = new File(args[1]);
      if (!usersFile.isFile())
         throw new Exception("Second argument is a path to a file with " +
                 "json data about users");
      if (!usersFile.exists())
         throw new Exception("File with user data does not exists.");
      if (!usersFile.canRead())
         throw new Exception("Cannot read from a file with user data");

      MainClass mainClass = new MainClass(tweetsDirectory, usersFile);
      mainClass.go();
   }


   public MainClass(File tweetsDirectory, File usersFile)
   {
      this.tweetsDirectory = tweetsDirectory;
      this.usersFile = usersFile;
      userAndTweetsData = new ArrayList<>();
   }


   public void go()
   {
      List<JsonData> userList = readJsonData(usersFile, JsonDataUser.class);
      userList = userList.stream()
              .map(json -> combineWithTweets(json, tweetsDirectory))
              .collect(Collectors.toList());

      for (JsonData jsonData : userList)
         System.out.println(jsonData);
   }

   private static List<JsonData> readJsonData(
           File jsonFile,
           Class<? extends JsonData> jsonType)
   {
      List<JsonData> jsonData = new ArrayList<>();
      try (BufferedReader userBufferedReader = new BufferedReader(new
              InputStreamReader(new FileInputStream(jsonFile))))
      {
         jsonData = userBufferedReader.lines()
                 .map(line -> lineToJsonDataUserStream(line, jsonType)).collect(Collectors.toList());
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

      return jsonData;
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
           File tweetsDirectory)
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
                    JsonDataTwitter.class);
            tweets.stream().forEachOrdered(combinedData::combine);
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



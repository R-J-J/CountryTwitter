package com.utwente.salp2.rafal;

import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataTweets;
import com.utwente.salp2.rafal.json.JsonDataUser;

import java.io.*;
import java.util.List;

/**
 * Created by rafal on 10.12.14.
 * Main class
 */
public class MainClass
{
   final File tweetsDirectory;
   final File usersFile;


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
      File usersFile = new File(args[1]);
      checkFiles(tweetsDirectory, usersFile);

      MainClass mainClass = new MainClass(tweetsDirectory, usersFile);
      mainClass.go();
   }

   private static void checkFiles(File tweetsDirectory, File usersFile)
           throws Exception
   {
      if (!tweetsDirectory.isDirectory())
         throw new Exception("First argument is a path to a directory " +
                 "containing json data about tweets");
      if (!tweetsDirectory.exists())
         throw new Exception("Directory with tweets does not exists.");
      if (!tweetsDirectory.canRead())
         throw new Exception("Cannot read from a directory with tweets");

      if (!usersFile.isFile())
         throw new Exception("Second argument is a path to a file with " +
                 "json data about users");
      if (!usersFile.exists())
         throw new Exception("File with user data does not exists.");
      if (!usersFile.canRead())
         throw new Exception("Cannot read from a file with user data");
   }


   public MainClass(File tweetsDirectory, File usersFile)
   {
      this.tweetsDirectory = tweetsDirectory;
      this.usersFile = usersFile;
   }


   public void go()
           throws Exception
   {
      //TODO print info about algorithm steps
      List<JsonData> userList = JsonDataReader.read(usersFile,
              JsonDataUser.class, tweetsDirectory, JsonDataTweets.class);

      //TODO do something with those file paths
      String currentDirectory = System.getProperty("user.dir");
      UserCountryInfoMapper ucim = new UserCountryInfoMapper(
              currentDirectory + "/res/CountryInfo.csv",
              currentDirectory + "/res/CountryInfo.csv",
              currentDirectory + "/res/TimeZoneExceptions.txt",
              "macdrag");

      List<UserCountryInfo> userCountryInfoList = ucim.match(userList);

      //TODO remove
      userCountryInfoList.forEach(System.out::println);
   }
}



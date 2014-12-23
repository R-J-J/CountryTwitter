package com.utwente.salp2.rafal;

import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import com.utwente.salp2.rafal.geonames.UserCountryInfoSerial;
import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataReader;
import com.utwente.salp2.rafal.json.JsonDataTweets;
import com.utwente.salp2.rafal.json.JsonDataUser;
import com.utwente.salp2.rafal.properties.Prop;

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
      File tweetsDirectory = new File(Prop.tweetFolder);
      File usersFile = new File(Prop.userFile);
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
      List<JsonData> userList = JsonDataReader.read(usersFile, JsonDataUser
              .class, tweetsDirectory, JsonDataTweets.class);

      UserCountryInfoMapper ucim = new UserCountryInfoMapper(
              Prop.countryInfo,
              Prop.countryInfo,
              Prop.timeZoneException,
              Prop.groundTrue,
              Prop.geoNamesUserName);
      List<UserCountryInfo> userCountryInfoList = ucim.match(userList);

      List<UserCountryInfo> old =
              UserCountryInfoSerial
                      .readUciListFromFile(Prop.UciListSerializedFile);

      userCountryInfoList.addAll(old);

      UserCountryInfoSerial.writeUciListToFile(
              userCountryInfoList,
              Prop.UciListSerializedFile);
   }
}



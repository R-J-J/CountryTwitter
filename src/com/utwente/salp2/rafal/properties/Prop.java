package com.utwente.salp2.rafal.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by rafal on 23.12.14.
 */
public class Prop
{
   public static String tweetFolder;
   public static String userFile;
   public static String groundTrue;
   public static String countryInfo;
   public static String timeZoneException;
   public static String geoNamesUserName;
   public static String UciListSerializedFile;
   public static String UciListSerializedFileToClassify;
   public static String resultFolder;


   static
   {
      Properties prop = new Properties();
      String propFileName = "config.properties";

      InputStream inputStream = Prop.class.getClassLoader()
              .getResourceAsStream(propFileName);

      if (inputStream != null) {
         try
         {
            prop.load(inputStream);
         }
         catch (IOException e)
         {
            e.printStackTrace();
            throw new RuntimeException("IOException");
         }
      } else {
         throw new RuntimeException("property file '" + propFileName +
                 "' not found in the classpath");
      }

      tweetFolder = prop.getProperty("tweetFolder");
      userFile = prop.getProperty("userFile");
      groundTrue = prop.getProperty("groundTrue");
      countryInfo = prop.getProperty("countryInfo");
      timeZoneException = prop.getProperty("timeZoneException");
      geoNamesUserName = prop.getProperty("geoNamesUserName");
      UciListSerializedFile = prop.getProperty("UciListSerializedFile");
      UciListSerializedFileToClassify = prop.getProperty("UciListSerializedFileToClassify");
      resultFolder = prop.getProperty("resultFolder");

      // get the property value and print it out
//      Field[] fields = Prop.class.getDeclaredFields();
//
//      Arrays.asList(fields).stream()
//              .filter(field -> Modifier.isStatic(field.getModifiers()))
//              .filter(field -> Modifier.isPublic(field.getModifiers()))
//              .forEach(field -> {
//                 try
//                 {
//                    String fieldName = field.getName();
//                    String stringValue = prop.getProperty(fieldName);
//                    field.setAccessible(true);
//                    field.set(null, stringValue);
//                 }
//                 catch (IllegalAccessException e)
//                 {
//                    e.printStackTrace();
//                    throw new RuntimeException();
//                 }
//              });
   }
}

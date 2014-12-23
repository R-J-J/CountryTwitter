package com.utwente.salp2.rafal.geonames;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafal on 20.12.14.
 */
public class UserCountryInfoSerial
{
   static public void writeUciListToFile(List<UserCountryInfo> uciList,
                                         String filePath)
           throws Exception
   {
      File writeFile = checkAndCreateWriteFile(filePath);

      try (ObjectOutputStream out = new ObjectOutputStream(
              new FileOutputStream(writeFile)))
      {
         for (UserCountryInfo uci : uciList)
            out.writeObject(uci);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new Exception("Could not write list to file");
      }
   }


   static public List<UserCountryInfo> readUciListFromFile(String filePath)
           throws Exception
   {
      File readFile;
      try
      {
         readFile = checkAndCreateReadFile(filePath);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return new ArrayList<>();
      }
      List<UserCountryInfo> uciList = new ArrayList<>();

      try (ObjectInputStream in = new ObjectInputStream(
              new FileInputStream(readFile)))
      {
         UserCountryInfo uci;
         while (true)
         {
               uci = (UserCountryInfo)in.readObject();
               uciList.add(uci);
         }
      }
      catch (EOFException e)
      {
         return uciList;
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new Exception("Could not read list from file");
      }
   }


   private static File checkAndCreateWriteFile(String filePath)
   {
      try
      {
         File file = new File(filePath);
         // if exists then delete file
         if (file.exists() && file.isFile() && !file.delete())
            throw new RuntimeException("Old file could not be deleted"+
                    " or path is wrong.");
         if (!file.createNewFile())
            throw new RuntimeException("File could not be created.");
         return file;
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new RuntimeException("File could not be created.");
      }
   }


   private static File checkAndCreateReadFile(String filePath)
           throws Exception
   {
      File file = new File(filePath);
      if (!file.isFile() || !file.canRead())
      {
         throw new Exception("Could not read list from file");
      }
      return file;
   }
}

package com.utwente.salp2.rafal.arffgenerator;

import com.utwente.salp2.rafal.geonames.UserCountryInfo;

import java.io.*;
import java.util.List;

/**
 * Created by rafal on 18.12.14.
 */
public abstract class ArffGenerator
{
   public File generate(List<UserCountryInfo> userCountryInfoList,
                        String filePath)
   {
      File arffFile = createFile(filePath);

      try (BufferedWriter writer =
                   new BufferedWriter(
                           new OutputStreamWriter(
                                   new FileOutputStream(arffFile))))
      {
         writer.write(generateHeader());
         writer.newLine();

         for (UserCountryInfo uci : userCountryInfoList)
            writer.write(generateLine(uci));

         writer.flush();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
         String separator = System.getProperty("line.separator");
         throw new RuntimeException("Arff file was not created."
                 + separator + "(File path: " + filePath + ")");
      }
      catch (IOException e)
      {
         // If file could not be closed.
         e.printStackTrace();
         // If file stay unclosed, nothing really bad will happen.
      }

      return arffFile;
   }


   private File createFile(String filePath)
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

   protected abstract String generateLine(UserCountryInfo uci);

   protected abstract String generateHeader();
}

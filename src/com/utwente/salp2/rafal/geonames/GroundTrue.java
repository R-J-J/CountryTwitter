package com.utwente.salp2.rafal.geonames;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by rafal on 18.12.14.
 */
public class GroundTrue implements DataSearcher
{
   private File groundTrueFile;

   public GroundTrue(String filePath)
           throws Exception
   {
      groundTrueFile = checkAndCreateFile(filePath);
   }


   private File checkAndCreateFile(String filePath)
           throws Exception
   {
      File file = new File(filePath);
      if (!file.isFile() || !file.canRead())
      {
         throw new Exception("Cannot read from given File.");
      }
      return new File(filePath);
   }


   /**
    *
    * @param toSearch set of user Ids
    * @return map<userID, map<country, probability>>
    * @throws Exception
    */
   @Override
   public Map<String, Map<String, Integer>> search(Set<String> toSearch)
           throws Exception
   {
      Map<String, Map<String, Integer>> results = new HashMap<>();
      try (BufferedReader reader =
                   new BufferedReader(
                           new InputStreamReader(
                                   new FileInputStream(groundTrueFile))))
      {
         results = reader.lines()
                 .filter(line -> toSearch.contains(getColumn(line, 0)))
                 .collect(Collectors.toMap(
                         line -> getColumn(line, 0),
                         this::createResultMap
                 ));
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
      return results;
   }


   private Map<String, Integer> createResultMap(String line)
   {
      Map<String, Integer> result = new HashMap<>();
      result.put(getColumn(line, 2), 1);
      return result;
   }


   private String getColumn(String line, int i)
   {
      return line.split("\\t")[i];
   }
}

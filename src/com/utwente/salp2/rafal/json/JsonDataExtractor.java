package com.utwente.salp2.rafal.json;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by rafal on 11.12.14.
 * <p>
 * Reads JSON file and extracts values that are expected by JsonData
 *
 * TODO should work for everything, i.e. JsonValue instead of String. Implement Json builders.
 */
public class JsonDataExtractor
{
   private class NamesStack
   {
      private List<String> currentPath;


      public NamesStack()
      {
         currentPath = new ArrayList<>();
      }


      public void modifyStack(final String name)
              throws Exception
      {
         previousLevel();
         currentPath.add(name);
      }


      public String getCurrentPath()
      {
         String tempStackAsString = "$";
         for (String name : currentPath)
         {
            tempStackAsString += KEY_SEPARATOR + name;
         }
         return tempStackAsString;
      }


      public void nextLevel()
      {
         currentPath.add("");
      }


      public void previousLevel()
              throws Exception
      {
         int lastElementIndex = currentPath.size() - 1;
         if (lastElementIndex < 0)
         {
            throw new Exception("Negative number of elements in stack.");
         }
         currentPath.remove(lastElementIndex);
      }
   }

   public static final String KEY_SEPARATOR = ".";
   NamesStack namesStack;
   Set<String> pathsToLokFor;


   public JsonData extractData(InputStream dataStream, JsonData jsonData)
           throws Exception
   {
      namesStack = new NamesStack();
      pathsToLokFor = jsonData.getPathsSet();

      try (JsonParser parser = Json.createParser(dataStream))
      {
         while (parser.hasNext())
         {
            switch (parser.next())
            {
               case START_OBJECT:
                  namesStack.nextLevel();
                  break;
               case END_OBJECT:
                  namesStack.previousLevel();
                  break;
               case KEY_NAME:
                  namesStack.modifyStack(parser.getString());
                  break;
               case VALUE_STRING: // TODO should work for all JsonValues
               case VALUE_NUMBER:
                  if (checkIfEqualsToPath())
                     addData(parser, jsonData);
                  break;
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.err.println(e.getMessage());
         throw new Exception("Invalid JSON file.");
      }

      return jsonData;
   }


   private boolean checkIfEqualsToPath()
   {
      // There cannot be two equal paths;
      String currentPath = namesStack.getCurrentPath();
      return pathsToLokFor.stream().anyMatch(currentPath::equals);
   }


   private void addData(JsonParser parser, JsonData jsonData)
           throws Exception
   {
      String currentPath = namesStack.getCurrentPath();
      jsonData.addValue(currentPath, parser.getString());
   }
}


package com.utwente.salp2.rafal.json;

import java.util.*;

/**
 * Created by rafal on 11.12.14.
 * <p>
 * Class that holds collected info from JSON structure.
 * Keys that should be collected from JSON file have to be formatted as
 * "$.fieldName.nextFieldName.andSoOn".
 * All values that match one of the keys will be added to appropriate array
 */
public class JsonData
{
   private Map<String, List<String>> pathsMap = new HashMap<>();


   public JsonData()
   {
   }


   public JsonData(Set<String> keySet)
   {
      try
      {
         addKey(keySet);
      }
      catch (Exception e)
      {
         // There will not be such case because when
         // object is constructed the map is empty.
         e.printStackTrace();
         String separator = System.getProperty("line.separator");
         throw new RuntimeException(e.getMessage() + separator
                 + "Exception appeared in the JsonData constructor.");
      }
   }


   public void addKey(String key) throws Exception
   {
      if (pathsMap.containsKey(key))
      {
         throw new Exception("Key already exists.");
      }
      pathsMap.put(key, new ArrayList<>());
   }


   public void addKey(Set<String> keySet)
           throws Exception
   {
      for (String key : keySet)
      {
         addKey(key);
      }
   }


   public void removeKey(String key)
   {
      pathsMap.remove(key);
   }


   public Set<String> getPathsSet()
   {
      return pathsMap.keySet();
   }


   public void addValue(String key, String value) throws Exception
   {
      List<String> values = new ArrayList<>();
      values.add(value);
      addValue(key, values);
   }

   public void addValue(String key, List<String> values) throws Exception
   {
      List<String> currentValues = pathsMap.get(key);
      if (currentValues == null)
      {
         throw new Exception("Key not found.");
      }
      currentValues.addAll(values);
   }


   public void rename(String oldKey, String newKey)
           throws Exception
   {
      List<String> values = getValues(oldKey);
      pathsMap.remove(oldKey);
      addKey(newKey);
      addValue(newKey, values);
   }


   public void combine(JsonData jsonData)
   {
      for (String key : jsonData.pathsMap.keySet())
      {
         if (pathsMap.containsKey(key))
         {
            List<String> currentValues = pathsMap.get(key);
            List<String> valuesToAdd = jsonData.pathsMap.get(key);
            currentValues.addAll(valuesToAdd);
         }
         else
         {
            List<String> valuesToAdd = jsonData.pathsMap.get(key);
            pathsMap.put(key, valuesToAdd);
         }
      }
   }

   public List<String> getValues(String key)
           throws Exception
   {
      List<String> currentValues = pathsMap.get(key);
      if (currentValues == null)
      {
         throw new Exception("Key not found.");
      }
      return new ArrayList<>(currentValues);
   }


   @Override
   public String toString()
   {
      String separator = System.getProperty("line.separator");
      String text = "";
      for (String key : pathsMap.keySet())
      {
         text += key + separator;
         for (String value : pathsMap.get(key))
         {
            text += value + "; ";
         }
         text += separator;
      }
      text += separator;
      return text;
   }
}

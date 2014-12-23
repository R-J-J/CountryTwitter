package com.utwente.salp2.rafal.geonames;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rafal on 15.12.14.
 *
 * This class holds maps of countries and their probabilities for each label
 */
public class UserCountryInfo implements Serializable
{
   private Map<String, Map<String, Float>> userCountryInfo;
   private String id;

   public UserCountryInfo(String id)
   {
      userCountryInfo = new HashMap<>();
      this.id = id;
   }

   public void putData(String key, Map<String, Float> countryInfo)
           throws Exception
   {
      if (userCountryInfo.containsKey(key))
         throw new Exception("UserCountryData contains already key: " + key);

      userCountryInfo.put(key, countryInfo);
   }

   public Map<String, Float> getData(String key)
   {
      if (!userCountryInfo.containsKey(key))
         return new HashMap<>();

      return userCountryInfo.get(key);
   }

   public Set<String> getKeys()
   {
      return userCountryInfo.keySet();
   }


   public String getId()
   {
      return id;
   }


   @Override
   public String toString()
   {
      String separator = System.getProperty("line.separator");
      String text = "";
      text += "ID: " + getId() + separator;

      for (String key : userCountryInfo.keySet())
      {
         text += key + separator;
         Map<String, Float> countryAndProbabilities =
                 userCountryInfo.get(key);
         for (String countryCode : countryAndProbabilities.keySet())
         {
            Float probability = countryAndProbabilities.get(countryCode);
            text += "\t- " + countryCode + ": " + probability + "%" + separator;
         }
      }
      text += separator;
      return text;
   }
}

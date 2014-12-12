package com.utwente.salp2.rafal.json;

/**
 * Created by rafal on 11.12.14.
 * JsonData with initialized values for Twitter users.
 */
public class JsonDataUser extends JsonData
{
   public JsonDataUser()
   {
      super();
      try
      {
         addKey("$.time_zone");
         addKey("$.location");
         addKey("$.lang");
      }
      catch (Exception e)
      {
         // There will not be such case because when object is constructed the map is empty.
         e.printStackTrace();
         String separator = System.getProperty("line.separator");
         throw new RuntimeException(e.getMessage() + separator + "Exception appeared in the JsonDataUser constructor.");
      }
   }
}

package com.utwente.salp2.rafal.json;

/**
 * Created by rafal on 11.12.14.
 * JsonData with initialized values for Twitter users.
 */
public class JsonDataUser extends JsonData
{
   final public static String ID = "$.id";
   final public static String TIME_ZONE = "$.time_zone";
   final public static String LOCATION = "$.location";
   final public static String LANGUAGE = "$.lang";

   public JsonDataUser()
   {
      super();
      try
      {
         addKey(ID);
         addKey(TIME_ZONE);
         addKey(LOCATION);
         addKey(LANGUAGE);
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

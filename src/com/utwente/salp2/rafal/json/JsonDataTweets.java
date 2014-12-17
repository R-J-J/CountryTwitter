package com.utwente.salp2.rafal.json;

/**
 * Created by rafal on 11.12.14.
 * JsonData with initialized values for Twits.
 */
public class JsonDataTweets extends JsonData
{
   final public static String COORDINATES = "$.coordinates.coordinates";
   final public static String LANGUAGE = "$.lang";
   final public static String LOCATION = "$.user.location";

   public JsonDataTweets()
   {
      super();
      try
      {
         addKey(COORDINATES);
         addKey(LANGUAGE);
         addKey(LOCATION);
      }
      catch (Exception e)
      {
         // There will not be such case because when object is constructed the map is empty.
         e.printStackTrace();
         String separator = System.getProperty("line.separator");
         throw new RuntimeException(e.getMessage() + separator + "Exception appeared in the JsonDataTwitter constructor.");
      }
   }
}

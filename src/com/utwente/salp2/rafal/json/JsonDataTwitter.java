package com.utwente.salp2.rafal.json;

/**
 * Created by rafal on 11.12.14.
 * JsonData with initialized values for Twits.
 */
public class JsonDataTwitter extends JsonData
{
   public JsonDataTwitter()
   {
      super();
      try
      {
         addKey("$.coordinates.coordinates");
         addKey("$.lang");
         addKey("$.user.location");
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

package tests;

import com.utwente.salp2.rafal.json.JsonData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class JsonDataTest
{

   @Test
   public void testCombine()
           throws Exception
   {
      JsonData userData = new JsonData();
      createData(userData, "lang");
      createData(userData, "time_zone");

      JsonData secondUserData = new JsonData();
      createData(secondUserData, "lang");
      createData(secondUserData, "location");

      userData.combine(secondUserData);

      Set<String> keys = userData.getPathsSet();
      assertTrue(keys.contains("lang"));
      assertTrue(keys.contains("time_zone"));
      assertTrue(keys.contains("location"));
      assertTrue(keys.size() == 3);

      List<String> langData = userData.getValues("lang");
      String[] langDataArray = {"a", "b", "a", "b"};
      assertArrayEquals(langData.toArray(), langDataArray);

      List<String> time_zoneData = userData.getValues("time_zone");
      String[] time_zoneDataArray = {"a", "b"};
      assertArrayEquals(time_zoneData.toArray(), time_zoneDataArray);

      List<String> locationData = userData.getValues("location");
      String[] locationDataArray = {"a", "b"};
      assertArrayEquals(locationData.toArray(), locationDataArray);
   }

   private void createData(JsonData userData, String key)
           throws Exception
   {
      userData.addKey(key);

      List<String> data = new ArrayList<>();
      data.add("a");
      data.add("b");

      userData.addValue(key, data);
   }
}
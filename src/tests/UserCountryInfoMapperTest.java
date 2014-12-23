package tests;

import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataTweets;
import com.utwente.salp2.rafal.json.JsonDataUser;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

public class UserCountryInfoMapperTest
{

   @Test
   public void testExtract()
           throws Exception
   {
      List<String> values = new ArrayList<>(Arrays.asList("a", "b", "c"));
      JsonData jsonData1 = new JsonData();
      jsonData1.addKey("zonk");
      jsonData1.addKey("zonk2");
      jsonData1.addValue("zonk", values);

      JsonData jsonData3 = new JsonData();
      jsonData3.addKey("zonk");
      jsonData3.addValue("zonk", values);
      jsonData3.addKey("zonk2");
      jsonData3.addValue("zonk2", "d");

      List<JsonData> userList = Arrays.asList(jsonData1, jsonData1, jsonData3);

      String basePath =
              "/home/rafal/0_Workspaces/IdeaProjects/CountryTwitter/res/";
      UserCountryInfoMapper ucim = new UserCountryInfoMapper(
              basePath + "CountryInfo.csv",
              basePath + "CountryInfo.csv",
              basePath + "TimeZoneExceptions.txt",
              "/home/rafal/Desktop/SALP2 Assignment 2/groundtruth/groundtruth.txt",
              "macdrag");

      Method xx = UserCountryInfoMapper.class.getDeclaredMethod("extract",
              String.class, List.class);
      xx.setAccessible(true);

      Set<String> results = (Set<String>) xx.invoke(ucim, "zonk2", userList);
      assertTrue(results.contains("d"));
      assertTrue(results.size() == 1);

      results = (Set<String>) xx.invoke(ucim, "zonk", userList);
      assertTrue(results.contains("a"));
      assertTrue(results.contains("b"));
      assertTrue(results.contains("c"));
      assertTrue(results.size() == 3);
   }


   @Test
   public void testMatch()
           throws Exception
   {
      String basePath =
              "/home/rafal/0_Workspaces/IdeaProjects/CountryTwitter/src/tests/resources/";
      UserCountryInfoMapper ucim = new UserCountryInfoMapper(
              basePath + "CountryInfo.csv",
              basePath + "CountryInfo.csv",
              basePath + "TimeZoneExceptions.txt",
              "/home/rafal/Desktop/SALP2 Assignment 2/groundtruth/groundtruth.txt",
              "macdrag");

      //JsonData with all keys
      JsonData jsonData = new JsonDataUser();
      jsonData.rename(JsonDataUser.LANGUAGE, "user-$.lang");
      jsonData.combine(new JsonDataTweets());

      jsonData.addValue(JsonDataUser.ID, "12345");
      jsonData.addValue("user-$.lang", "en");
      jsonData.addValue(JsonDataUser.TIME_ZONE, "Amsterdam");

      jsonData.addValue(JsonDataTweets.LANGUAGE,
              Arrays.asList("en", "pl", "pl"));


      List<JsonData> jsonDataList = new ArrayList<>(Arrays.asList(jsonData));
      List<UserCountryInfo> userCountryInfoList = ucim.match(jsonDataList);
      UserCountryInfo userCountryInfo = userCountryInfoList.get(0);

      Map<String, Float> userLanguage =
              userCountryInfo.getData("user-$.lang");
      assertTrue(userLanguage.size() == 2);
      assertEquals(userLanguage.get("NL"), 33.0/333, 0.01);
      assertEquals(userLanguage.get("GB"), 300.0/333, 0.01);

      Map<String, Float> userTimeZone =
              userCountryInfo.getData(JsonDataUser.TIME_ZONE);
      assertTrue(userTimeZone.size() == 1);
      assertEquals(userTimeZone.get("NL"), 1.0, 0.01);

      Map<String, Float> tweetsLanguage =
              userCountryInfo.getData(JsonDataTweets.LANGUAGE);
      assertTrue(tweetsLanguage.size() == 3);
      assertEquals(tweetsLanguage.get("NL"), 33.0/733, 0.01);
      assertEquals(tweetsLanguage.get("GB"), 300.0/733, 0.01);
      assertEquals(tweetsLanguage.get("PL"), 400.0/733, 0.01);
   }
}
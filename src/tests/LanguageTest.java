package tests;

import com.utwente.salp2.rafal.geonames.Language;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LanguageTest
{

   @Test
   public void testSearchLanguages()
           throws Exception
   {
      String currentDirectory = System.getProperty("user.dir");
      String languageFilePath = currentDirectory +
              "/res/CountryInfo.csv";
      Language language = new Language(languageFilePath);
      Map<String, Integer> result;

      result = language.searchLanguage("hy");
      assertNotNull(result);
      assertTrue(result.get("AM") == 2968000);
      assertTrue(result.get("AZ") == 1383918);
      assertTrue(result.get("GE") == 926000);
      assertTrue(result.get("IQ") == 4945267);
      assertTrue(result.get("LB") == 412524);
      assertTrue(result.get("SY") == 4228211);
      assertTrue(result.size() == 6);
   }
}
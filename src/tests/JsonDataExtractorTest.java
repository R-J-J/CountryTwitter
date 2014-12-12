package tests;

import com.utwente.salp2.rafal.json.JsonData;
import com.utwente.salp2.rafal.json.JsonDataExtractor;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class JsonDataExtractorTest
{
   @Test
   public void testSimpleJson()
           throws Exception
   {
      JsonData jsonData = new JsonData();
      String key = "$.a.b";
      jsonData.addKey(key);

      String currentDirectory = System.getProperty("user.dir");
      File file = new File(currentDirectory + "/src/tests/resources/simple.json");
      try (InputStream dataStream = new FileInputStream(file))
      {
         JsonDataExtractor jsonDataExtractor = new JsonDataExtractor();
         jsonDataExtractor.extractData(dataStream, jsonData);
      }

      List<String> values = jsonData.getValues(key);

      assertTrue(values.contains("zonk"));
      assertTrue(values.contains("2"));
      assertTrue(values.size() == 2);
      assertFalse(values.contains("nie"));
      assertFalse(values.contains("toNie"));
   }
}
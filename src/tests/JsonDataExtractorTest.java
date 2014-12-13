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
      String currentDirectory = System.getProperty("user.dir");
      File file = new File(currentDirectory + "/src/tests/resources/simple.json");
      List<String> values = getValuesFromFile(file);

      assertTrue(values.contains("zonk"));
      assertTrue(values.contains("2"));
      assertTrue(values.size() == 2);
      assertFalse(values.contains("nie"));
      assertFalse(values.contains("toNie"));
   }

   @Test
   public void testMultipleJsonInFile()
      throws Exception
   {
      String currentDirectory = System.getProperty("user.dir");
      File file = new File(currentDirectory + "/src/tests/resources/multiple.json");
      List<String> values = getValuesFromFile(file);

      assertTrue(values.contains("zonk"));
      assertTrue(values.contains("zonk2"));
      assertTrue(values.contains("zonk3"));
      assertTrue(values.size() == 3);
   }

   @Test
   public void testValuesArrayJsonInFile()
           throws Exception
   {
      String currentDirectory = System.getProperty("user.dir");
      File file = new File(currentDirectory + "/src/tests/resources/valuesArray.json");
      List<String> values = getValuesFromFile(file);

      assertTrue(values.contains("zonk3"));
      assertTrue(values.size() == 3);
   }

   private List<String> getValuesFromFile(File file)
           throws Exception
   {
      JsonData jsonData = new JsonData();
      String key = "$.a.b";
      jsonData.addKey(key);

      try (InputStream dataStream = new FileInputStream(file))
      {
         JsonDataExtractor jsonDataExtractor = new JsonDataExtractor();
         jsonDataExtractor.extractData(dataStream, jsonData);
      }

      return jsonData.getValues(key);
   }
}
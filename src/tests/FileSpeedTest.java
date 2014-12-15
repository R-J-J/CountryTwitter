package tests;

import org.geonames.ToponymSearchCriteria;
import org.geonames.WebService;

import java.io.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Created by rafal on 12.12.14.
 */
public class FileSpeedTest
{
   public static void main(String[] args)
   {
      FileSpeedTest fileSpeedTest = new FileSpeedTest();

      long startTime = System.currentTimeMillis();
      fileSpeedTest.testRawFile();
      long duration = System.currentTimeMillis() - startTime;
      System.out.println(duration);
      // It took 186 ms
      // But we 9 000 000 lines in general
      // Estimated time for all checks: 2 hours;

      startTime = System.currentTimeMillis();
      fileSpeedTest.testWebService();
      duration = System.currentTimeMillis() - startTime;
      System.out.println(duration);
      // It took 895 ms
      // Estimated time for all checks: 2 minutes;
   }

   private void testRawFile()
   {
      String filePL = "/home/rafal/0_Workspaces/IdeaProjects/"
              +"CountryTwitter/src/tests/resources/PL.txt";
      File usersFile = new File(filePL);

      try (BufferedReader br = new BufferedReader(new
              InputStreamReader(new FileInputStream(usersFile))))
      {
         String line;
         while ((line = br.readLine()) != null)
         {
            for (int i=0; i< 200; i++)
            {
               line.contains("Warsaw");
               line.substring(5, 10);
               line.substring(5, 10);
            }
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private void testWebService()
   {
      WebService.setUserName("macdrag");
      ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
      searchCriteria.setNameEquals("Warsaw");
      try
      {
         for (int i = 0; i < 50; i++)
            WebService.search(searchCriteria);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static boolean isPrime(final int number)
   {
      IntPredicate isDivisible = divisor -> number % divisor == 0;

      return number > 1 && IntStream.range(2, number).noneMatch(isDivisible);
   }
}

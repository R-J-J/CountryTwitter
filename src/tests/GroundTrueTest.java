package tests;

import com.utwente.salp2.rafal.geonames.GroundTrue;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class GroundTrueTest
{

   @Test
   public void testSearch()
           throws Exception
   {
      String path = "/home/rafal/Desktop/SALP2 Assignment 2/groundtruth/groundtruth.txt";
      GroundTrue groundTrue = new GroundTrue(path);

      Set<String> users = new HashSet<>();
      users.add("1585419144");
      users.add("1115784079");
      users.add("1019230242");

      Map<String, Map<String, Integer>> results = groundTrue.search(users);

      Map<String, Integer> id158 = results.get("1585419144");
      assertTrue(id158.size() == 1);
      assertTrue(id158.get("DK") == 1);

      Map<String, Integer> id111 = results.get("1115784079");
      assertTrue(id111.size() == 1);
      assertTrue(id111.get("AU") == 1);

      Map<String, Integer> id101 = results.get("1019230242");
      assertTrue(id101.size() == 1);
      assertTrue(id101.get("US") == 1);
   }
}
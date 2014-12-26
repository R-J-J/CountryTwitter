package com.utwente.salp2.rafal;

import com.utwente.salp2.rafal.classifier.DecissionTreeImpl;
import com.utwente.salp2.rafal.classifier.LogisticRegressionImpl;
import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import com.utwente.salp2.rafal.geonames.UserCountryInfoSerial;
import com.utwente.salp2.rafal.properties.Prop;

import java.io.*;
import java.util.*;

/**
 * Created by rafal on 21.12.14.
 */
public class MainClassification
{
   public static void main(String[] args)
           throws Exception
   {
      List<UserCountryInfo> uciList =
              UserCountryInfoSerial
                      .readUciListFromFile(Prop.UciListSerializedFile);

      List<UserCountryInfo> uciListToClassify =
              UserCountryInfoSerial
                      .readUciListFromFile(Prop.UciListSerializedFileToClassify);


      List<UserCountryInfo> uciListTrain = uciList.subList(0, 299);
      List<UserCountryInfo> uciListTest = uciList.subList(300, 471);



      LogisticRegressionImpl logReg = new LogisticRegressionImpl();
      logReg.train(uciListTrain);
      Map<String, String> resRegTest = logReg.classify(uciListTest);
      preatyPrintConfusionMatrix(resRegTest, uciListTest);
//      System.out.println( "" + logReg.test(uciListTest) + " / " + uciListTest.size());


      Map<String, String> resReg = logReg.classify(uciListToClassify);
      writeToFile(resReg, Prop.resultFolder + "regressionResults.txt");



      DecissionTreeImpl decTree = new DecissionTreeImpl();
      decTree.train(uciListTrain);
      Map<String, Set<String>> resTreeTest = decTree.classify(uciListTest);
      Map<String, String> resTreeTestProcessed = processResTree(resTreeTest);
      preatyPrintConfusionMatrix(resTreeTestProcessed, uciListTest);

//      int[][] confusionMatrix = decTree.test(uciListTest);
//      System.out.println("True-positive: " + confusionMatrix[0][0]);
//      System.out.println("True-negative: " + confusionMatrix[0][1]);
//      System.out.println("False-positive: " + confusionMatrix[1][0]);
//      System.out.println("False-negative: " + confusionMatrix[1][1]);

      Map<String, Set<String>> resTree = decTree.classify(uciListToClassify);
      Map<String, String> resTreeProcessed = processResTree(resTree);
      writeToFile(resTreeProcessed, Prop.resultFolder + "decisionTreeResults.txt");
   }


   private static void preatyPrintConfusionMatrix(
           Map<String, String> estimatedValues,
           List<UserCountryInfo> uciListTest)
   {
      // <True, <Estimated, Value>>
      Map<String, Map<String, Integer>> results = new HashMap<>();

      for (UserCountryInfo uci : uciListTest)
      {
         String id = uci.getId();
         String trueCountry = uci.getData(UserCountryInfoMapper.USER_ID).keySet().iterator().next();
         String estimatedCountry = estimatedValues.get(id);
         putData(results, trueCountry, estimatedCountry);
      }

      Set<String> AllCountryListSet = new HashSet<>();
      AllCountryListSet.addAll(results.keySet());
      for (Map<String, Integer> innerMap : results.values())
         AllCountryListSet.addAll(innerMap.keySet());

      List<String> allCountryListOrdered = new ArrayList<>(AllCountryListSet);
      Collections.sort(allCountryListOrdered);

      for (String country : allCountryListOrdered)
         System.out.print('\t' + country);
      System.out.println();

      for (String country : allCountryListOrdered)
      {
         Map<String, Integer> innerResults = results.get(country);
         if (innerResults == null)
            innerResults = new HashMap<>();

         System.out.print(country);
         for (String country2 : allCountryListOrdered)
         {
            Integer value = innerResults.get(country2);
            if (value == null)
            {
               System.out.print("\t0");
            }
            else
            {
               System.out.print("\t" + value);
            }
         }
         System.out.println();
      }
   }


   private static void putData(
           Map<String, Map<String, Integer>> results,
           String trueCountry,
           String estimatedCountry)
   {
      if(!results.containsKey(trueCountry))
         results.put(trueCountry, new HashMap<>());

      Map<String, Integer> resultsForCountry = results.get(trueCountry);
      if (!resultsForCountry.containsKey(estimatedCountry))
         resultsForCountry.put(estimatedCountry, 0);

      Integer value = resultsForCountry.get(estimatedCountry);
      value = value + 1;
      resultsForCountry.put(estimatedCountry, value);
   }


   private static Map<String, String> processResTree(Map<String, Set<String>> resRegTest)
   {
      Map<String, String> result = new HashMap<>();
      for (String userId : resRegTest.keySet())
      {
         String value;
         Set<String> values = resRegTest.get(userId);
         if (values.size() < 1 || values.size() > 1)
            value = "?";
         else
            value = values.iterator().next();
         result.put(userId, value);
      }
      return result;
   }


   private static void writeToFile(Map<String, String> resReg, String filePath)
   {
      File file = createFile(filePath);

      try (BufferedWriter writer =
                   new BufferedWriter(
                           new OutputStreamWriter(
                                   new FileOutputStream(file))))
      {
         for (String userId : resReg.keySet())
         {
            writer.write(userId + '\t' + resReg.get(userId));
            writer.newLine();
         }
         writer.flush();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
         String separator = System.getProperty("line.separator");
         throw new RuntimeException("File was not created."
                 + separator + "(File path: " + filePath + ")");
      }
      catch (IOException e)
      {
         // If file could not be closed.
         e.printStackTrace();
         // If file stay unclosed, nothing really bad will happen.
      }
   }


   private static File createFile(String filePath)
   {
      try
      {
         File file = new File(filePath);
         // if exists then delete file
         if (file.exists() && file.isFile() && !file.delete())
            throw new RuntimeException("Old file could not be deleted"+
                    " or path is wrong.");
         if (!file.createNewFile())
            throw new RuntimeException("File could not be created.");
         return file;
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new RuntimeException("File could not be created.");
      }
   }
}

package com.utwente.salp2.rafal.classifier;

import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.classifiers.trees.DecisionTree;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.regression.LogisticRegression;
import jsat.regression.RegressionDataSet;

import java.util.*;
import java.util.stream.Collectors;

/**
* Created by rafal on 23.12.14.
*/
public class DecissionTreeImpl
{
   //how many top values should be used to create database
   final private static int topX = 2;

   private DecisionTree decisionTree = null;

   public void train(List<UserCountryInfo> uciList)
   {
      decisionTree = new DecisionTree();
      ClassificationDataSet cds = createDataset(uciList);
      decisionTree.trainC(cds);

//      System.out.println(logisticRegression.getCoefficents());
   }

   public int[][] test(List<UserCountryInfo> uciList)
   {
      int[][] confusionMatrix = new int[2][2];
      if (decisionTree == null)
         return new int[2][2];

      Integer correctlyClassified = 0;
      Map<String, Set<String>>  results = classify(uciList);

      for (UserCountryInfo uci : uciList)
      {
         Set<String> classifiedAs = results.get(uci.getId());
         Set<String> trueVal =
                 uci.getData(UserCountryInfoMapper.USER_ID).keySet();
         Set<String> countriesToCheck = new HashSet<>();
         countriesToCheck.addAll(classifiedAs);
         countriesToCheck.addAll(trueVal);
         for (String country : countriesToCheck)
         {
            boolean tv = trueVal.contains(country);
            boolean ca = classifiedAs.contains(country);
            if (tv && ca)
               confusionMatrix[0][0] += 1;
            else if (tv && !ca)
               confusionMatrix[0][1] += 1;
            else if (!tv && ca)
               confusionMatrix[1][0] += 1;
            else if (!tv && !ca)
               confusionMatrix[1][1] += 1;
         }
      }

      return confusionMatrix;
   }

   public Map<String, Set<String>> classify(List<UserCountryInfo> uciList)
   {
      Map<String, Set<String>> results = new HashMap<>();
      if (decisionTree == null)
         return results;

      for (UserCountryInfo uci : uciList)
      {
         Set<String> countriesToCompare = getCountriesToCompare(uci);
         Set<String> classifiedMap = new HashSet<>();
         for (String countryCode : countriesToCompare)
         {
            Map<String, Float> uciValues = getUciValues(countryCode, uci);
            uciValues.remove(UserCountryInfoMapper.USER_ID);
            DataPoint dp = createDataPoint(uciValues);
            assert dp != null;
            CategoricalResults value = decisionTree.classify(dp);
            if (value.mostLikely() == 1)
            {
               classifiedMap.add(countryCode);
            }
         }
         String id = uci.getId();
         results.put(id, classifiedMap);
      }

      return results;
   }

   private ClassificationDataSet createDataset(List<UserCountryInfo> uciList)
   {
      ClassificationDataSet cds = new ClassificationDataSet(
              6,
              new CategoricalData[0],
              new CategoricalData(2));

      for (UserCountryInfo uci : uciList)
      {
         Set<String> countriesToCompare = getCountriesToCompare(uci);
         for (String countryCode : countriesToCompare)
         {
            Map<String, Float> uciValues = getUciValues(countryCode, uci);
            int value = uciValues.remove(UserCountryInfoMapper.USER_ID)
                    .intValue();
            DataPoint dp = createDataPoint(uciValues);
            assert dp != null;
            cds.addDataPoint(dp, value);
         }
      }

      return cds;
   }


   private DataPoint createDataPoint(Map<String, Float> uciValues)
   {
      List<Double> valuesList = uciValues.keySet().stream()
              .sorted()
              .map(uciValues::get)
              .map(Float::doubleValue)
              .collect(Collectors.toList());
      Double[] valuesArrayTemp = valuesList.toArray(new Double[valuesList.size()]);

      double[] valuesArray = new double[uciValues.size()];
      for (int i=0; i < valuesArrayTemp.length; i++)
      {
         valuesArray[i] = valuesArrayTemp[i];
      }

      Vec vec = DenseVector.toDenseVec(valuesArray);
      return new DataPoint(vec);
   }


   private Map<String, Float> getUciValues(String countryCode, UserCountryInfo uci)
   {
      Map<String, Float> result = new HashMap<>();
      for (String key : uci.getKeys())
      {
         Float value = uci.getData(key).get(countryCode);
         if (value == null)
            value = 0f;
         result.put(key, value);
      }
      return result;
   }


   private Set<String> getCountriesToCompare(UserCountryInfo userCountryInfo)
   {
      UserCountryInfo prunedUci = new UserCountryInfo("000");
      for (String key : userCountryInfo.getKeys())
      {
         //Map with all values used to create data lines
         Map<String, Float> originalMap = userCountryInfo.getData(key);
         //Pruned map used to collect countries which data will be created for
         Map<String, Float> prunedMap = UserCountryInfoMapper
                 .onlyTopXImportant(topX, originalMap);

         try
         {
            prunedUci.putData(key, prunedMap);
         }
         catch (Exception e)
         {
            // There won't be such exception. It is clean UserCountryInfo
            e.printStackTrace();
            throw new RuntimeException("Exception that should never appear.");
         }
      }

      return prunedUci.getKeys().stream()
              .flatMap(key -> prunedUci.getData(key).keySet().stream())
              .collect(Collectors.toSet());
   }
}

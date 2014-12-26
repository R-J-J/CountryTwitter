package com.utwente.salp2.rafal.classifier;

import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.geonames.UserCountryInfoMapper;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.regression.LogisticRegression;
import jsat.regression.RegressionDataSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rafal on 21.12.14.
 */
public class LogisticRegressionImpl
{
   //how many top values should be used to create database
   final private static int topX = 2;

   private LogisticRegression logisticRegression = null;

   public void train(List<UserCountryInfo> uciList)
   {
      logisticRegression = new LogisticRegression();
      RegressionDataSet rds = createDataset(uciList);
      logisticRegression.train(rds);

      System.out.println(logisticRegression.getCoefficents());
   }

   public Integer test(List<UserCountryInfo> uciList)
   {
      if (logisticRegression == null)
         return 0;

      Integer correctlyClassified = 0;

      Map<String, String> results = classify(uciList);
      for (UserCountryInfo uci : uciList)
      {
         String classifiedAs = results.get(uci.getId());
         Map<String, Float> trueVal =
                 uci.getData(UserCountryInfoMapper.USER_ID);
         if (trueVal.keySet().contains(classifiedAs))
         {
            correctlyClassified += 1;
         }
      }

      return correctlyClassified;
   }

   public Map<String, String> classify(List<UserCountryInfo> uciList)
   {
      Map<String, String> results = new HashMap<>();
      if (logisticRegression == null)
         return results;

      for (UserCountryInfo uci : uciList)
      {
         Set<String> countriesToCompare = getCountriesToCompare(uci);
         double maxVal = -1.0;
         String bestCountryCode = "";
         for (String countryCode : countriesToCompare)
         {
            Map<String, Float> uciValues = getUciValues(countryCode, uci);
            uciValues.remove(UserCountryInfoMapper.USER_ID);
            DataPoint dp = createDataPoint(uciValues);
            assert dp != null;
            // value is probability
            double value = logisticRegression.regress(dp);
            if (value > maxVal)
            {
               maxVal = value;
               bestCountryCode = countryCode;
            }
         }
         String id = uci.getId();
         results.put(id, bestCountryCode);
      }

      return results;
   }

   private RegressionDataSet createDataset(List<UserCountryInfo> uciList)
   {
      Set<String> keysNoId = new HashSet<>(uciList.iterator().next().getKeys());
      keysNoId.remove(UserCountryInfoMapper.USER_ID);
      int noOfVariables = keysNoId.size();


      RegressionDataSet rds = new RegressionDataSet(noOfVariables, new CategoricalData[0]);

      for (UserCountryInfo uci : uciList)
      {
         Set<String> countriesToCompare = getCountriesToCompare(uci);
         for (String countryCode : countriesToCompare)
         {
            Map<String, Float> uciValues = getUciValues(countryCode, uci);
            Double value = uciValues.remove(UserCountryInfoMapper.USER_ID)
                    .doubleValue();
            DataPoint dp = createDataPoint(uciValues);
            assert dp != null;
            rds.addDataPoint(dp, value);
         }
      }

      return rds;
   }


   private DataPoint createDataPoint(Map<String, Float> uciValues)
   {
      List<Double> valuesList = uciValues.keySet().stream()
              .sorted()
              .map(uciValues::get)
              .map(Float::doubleValue)
              .collect(Collectors.toList());
      Double[] valuesArrayTemp = valuesList.toArray(new Double[valuesList.size()]);

      double[] valuesArray = new double[valuesList.size()];
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

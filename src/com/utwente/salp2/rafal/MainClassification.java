package com.utwente.salp2.rafal;

import com.utwente.salp2.rafal.arffgenerator.LogisticRegressionArffGenerator;
import com.utwente.salp2.rafal.classifier.DecissionTreeImpl;
import com.utwente.salp2.rafal.classifier.LogisticRegressionImpl;
import com.utwente.salp2.rafal.geonames.UserCountryInfo;
import com.utwente.salp2.rafal.geonames.UserCountryInfoSerial;
import com.utwente.salp2.rafal.properties.Prop;

import java.util.List;

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

      List<UserCountryInfo> uciListTrain = uciList.subList(0, 299);
      List<UserCountryInfo> uciListTest = uciList.subList(300, 471);

      LogisticRegressionImpl logReg = new LogisticRegressionImpl();
      logReg.train(uciListTrain);
      System.out.println( "" + logReg.test(uciListTest) + " / " + uciListTest.size());

      DecissionTreeImpl decTree = new DecissionTreeImpl();
      decTree.train(uciListTrain);
      int[][] confusionMatrix = decTree.test(uciListTest);
      System.out.println("True-positive: " + confusionMatrix[0][0]);
      System.out.println("True-negative: " + confusionMatrix[0][1]);
      System.out.println("False-positive: " + confusionMatrix[1][0]);
      System.out.println("False-negative: " + confusionMatrix[1][1]);

//      String arffFilePath = "/home/rafal/Desktop/out.arff";
//      LogisticRegressionArffGenerator lrag =
//              new LogisticRegressionArffGenerator();
//      lrag.generate(uciList, arffFilePath);
   }
}

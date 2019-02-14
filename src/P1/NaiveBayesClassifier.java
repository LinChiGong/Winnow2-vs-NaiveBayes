/**
 * This class implements a Naive Bayes classifier that works on dataset with
 * all Boolean attributes. The model should first be trained on a training set
 * and then it can make predictions on the test set
 * 
 * @author Winston Lin
 */
package P1;

import java.io.*;
import java.util.*;

public class NaiveBayesClassifier 
{
    int numClass; // Number of classes in the dataset
    ArrayList<int[]> trainSet = new ArrayList<int[]>(); // Training set
    ArrayList<int[]> testSet = new ArrayList<int[]>();  // Test set
    double[] pClasses; // Stores the prior for each class
    // Stores the likelihood for each attribute given class
    ArrayList<double[]> pAttributes = new ArrayList<double[]>();
    double m = 1;     // Used for smoothing
    double p = 0.001; // Used for smoothing
    int[] prediction; // Stores the predictions
    double accuracy;  // Classification accuracy
    String fileName = ""; // Name of the dataset
    
    public NaiveBayesClassifier(Preprocessor ppr)
    {
        this.numClass = ppr.numClass;
        this.trainSet = ppr.trainSet;
        this.testSet = ppr.testSet;
        pClasses = new double[numClass];
        prediction = new int[ppr.testSet.size()];
        this.fileName = ppr.fileName;
    }
    
/**
 * This method trains the model on the training set
 */
    public void fit()
    {// Stores the probabilities for each class
        if (numClass == 2)
        {
            pClasses[0] = countClass(0);
            pAttributes.add(countAttribute(0));
            pClasses[1] = countClass(1);
            pAttributes.add(countAttribute(1));
        }
        else
        {
            for (int i = 0; i < numClass; i++)
            {
                pClasses[i] = countClass(i + 1);
                pAttributes.add(countAttribute(i + 1));
            }
        }
    }

/**
 * This method calculates prior for each class
 * 
 * @param focusClass is the class being focused
 * @return prior of the focused class
 */
    public double countClass(int focusClass)
    {
        int count = 0;
        for (int i = 0; i < trainSet.size(); i++)
        {
            if (trainSet.get(i)[trainSet.get(0).length - 1] == focusClass)
            {
                count++;
            }
        }
        return (double) count / trainSet.size();
    }

/**
 * This method calculates the likelihood for each attribute given class
 * 
 * @param focusClass is the class being focused
 * @return likelihood of all attributes given the focused class
 */
    public double[] countAttribute(int focusClass)
    {
        double[] pAttributesFocusClass = new double[trainSet.get(0).length 
                                                    - 1];
        for (int i = 0; i < pAttributesFocusClass.length; i++)
        {
            int classCount = 0;
            int attributeCount = 0;
            for (int j = 0; j < trainSet.size(); j++)
            {
                if (trainSet.get(j)[trainSet.get(0).length - 1] == focusClass)
                {
                    classCount++;
                    if (trainSet.get(j)[i] == 1)
                    {
                        attributeCount++;
                    }
                }
            }
            pAttributesFocusClass[i] = (attributeCount + m * p)/(classCount 
                    + m); // m-estimate smoothing
        }
        return pAttributesFocusClass;
    }

/**
 * This method prints the model for demonstration purpose
 */
    public void printParams()
    {
        System.out.println("The Naive Bayes model is trained.");
        System.out.println("m = " + m);
        System.out.println("p = " + p);
        System.out.println();

        for (int i = 0; i < numClass; i++)
        {
            if (numClass == 2)
            {
                System.out.println("Prior and list of likelihoods for class " 
                        + i + "(rounded to five decimal places):");
            }
            else
            {
                System.out.println("Prior and list of likelihoods for class " 
                        + (i + 1) + "(rounded to five decimal places):");
            }
            System.out.println(Math.round(pClasses[i] * 100000.0) / 100000.0);
            System.out.print("[ ");
            for (int j = 0; j < pAttributes.get(0).length; j++)
            {
                System.out.print(Math.round(pAttributes.get(i)[j] * 100000.0) 
                        / 100000.0 + " ");
            }
            System.out.println("]");
            System.out.println();
        }
    }

/**
 * This method makes predictions on the test set and calculates prediction
 * accuracy
 */
    public void predict()
    {
        int correctPrediction = 0;       
        // Predict the class with the highest score (posterior)
        for (int i = 0; i < testSet.size(); i++)
        {
            double maxScore = 0;
            int maxScoreClass = 0;
            for (int j = 0; j < numClass; j++)
            {
                double posterior = pClasses[j];
                for (int k = 0; k < testSet.get(0).length - 1; k++)
                {
                    if (testSet.get(i)[k] == 1)
                    {
                        posterior *= pAttributes.get(j)[k];
                    }
                    else
                    {
                        posterior *= 1.0 - pAttributes.get(j)[k];
                    }
                }
                if (posterior > maxScore)
                {
                    maxScore = posterior;
                    if (numClass == 2)
                    {
                        maxScoreClass = j;
                    }
                    else
                    {
                        maxScoreClass = j + 1;
                    }
                }
            }
            prediction[i] = maxScoreClass;
        }
        
        for (int i = 0; i < prediction.length; i++)
        {
            if (testSet.get(i)[testSet.get(0).length - 1] == prediction[i])
            {
                correctPrediction++;
            }
        }
        accuracy = Math.round(correctPrediction * 10000.0 / prediction.length)
                / 100.0;
    }

/**
 * This method prints the prediction results for demonstration purpose
 */
    public void printResult()
    {
        System.out.println("Prediction accuracy: " + accuracy + "%"); 
        System.out.println();
        System.out.println("\tActual Class\tPredicted Class");
        System.out.println("\t------------\t---------------");
        for (int i = 0; i < prediction.length; i++)
        {
            System.out.println("\t     " + testSet.get(i)[testSet.get(0).length 
                                      - 1] +  "      \t     " + prediction[i]);
        }
    }

/**
 * This method writes the outputs specific for this project to files
 * 
 * @throws IOException
 */
    public void writeToFile() throws IOException
    {
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
                fileName + "-NaiveBayes-output.txt", true)));
        if (fileName.equals("Soybean"))
        {
            fout.println("Dataset: soybean-small.data");
            fout.println("Class information:");
            fout.println("Class 1 = D1");
            fout.println("Class 2 = D2");
            fout.println("Class 3 = D3");
            fout.println("Class 4 = D4");
        }
        else if (fileName.equals("Vote"))
        {
            fout.println("Dataset: house-votes-84.data");
            fout.println("Class information:");
            fout.println("Class 1 = Republican");
            fout.println("Class 0 = Democrat");
        }
        else if (fileName.equals("Breast-Cancer"))
        {
            fout.println("Dataset: breast-cancer-wisconsin.data");
            fout.println("Class information:");
            fout.println("Class 1 = Malignant");
            fout.println("Class 0 = Benign");
        }
        else if (fileName.equals("Iris"))
        {
            fout.println("Dataset: iris.data");
            fout.println("Class information:");
            fout.println("Class 1 = Iris Setosa");
            fout.println("Class 2 = Iris Versicolour");
            fout.println("Class 3 = Iris Virginica");
        }
        else if (fileName.equals("Glass"))
        {
            fout.println("Dataset: glass.data");
            fout.println("Class information:");
            fout.println("Class 1 = Building Windows Float Processed");
            fout.println("Class 2 = Building Windows Non-float Processed");
            fout.println("Class 3 = Vehicle Windows Float Processed");
            fout.println("Class 4 = Containers");
            fout.println("Class 5 = Tableware");
            fout.println("Class 6 = Headlamps");
        }
        fout.println();
        fout.println("----------------------------------------");
        fout.println("Parameters of the trained Naive Bayes model");
        fout.println("----------------------------------------");
        fout.println("m = " + m);
        fout.println("p = " + p);
        fout.println();
        for (int i = 0; i < numClass; i++)
        {
            if (numClass == 2)
            {
                fout.println("Prior and list of likelihoods for class " 
                        + i + "(rounded to five decimal places):");
            }
            else
            {
                fout.println("Prior and list of likelihoods for class " 
                        + (i + 1) + "(rounded to five decimal places):");
            }
            fout.println(Math.round(pClasses[i] * 100000.0) / 100000.0);
            fout.print("[ ");
            for (int j = 0; j < pAttributes.get(0).length; j++)
            {
                fout.print(Math.round(pAttributes.get(i)[j] * 100000.0) 
                        / 100000.0 + " ");
            }
            fout.println("]");
            fout.println();
        }
        fout.println("----------------------");
        fout.println("Classification Results");
        fout.println("----------------------");
        fout.println("Prediction accuracy: " + accuracy + "%"); 
        fout.println();
        fout.println("\tActual Class\tPredicted Class");
        fout.println("\t------------\t---------------");
        for (int i = 0; i < prediction.length; i++)
        {
            fout.println("\t     " + testSet.get(i)[testSet.get(0).length 
                                      - 1] +  "      \t     " + prediction[i]);
        }
        fout.close();
    }
}

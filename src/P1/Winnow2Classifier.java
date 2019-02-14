/**
 * This class implements a Winnow-2 classifier that works on dataset with
 * all Boolean attributes. The model should first be trained on a training set
 * and then it can make predictions on the test set
 * 
 * @author Winston Lin
 */
package P1;

import java.io.*;
import java.util.*;

public class Winnow2Classifier 
{
    int numClass; // Number of classes in the dataset
    ArrayList<int[]> trainSet = new ArrayList<int[]>(); // Training set
    ArrayList<int[]> testSet = new ArrayList<int[]>();  // Test set
    double theta;     // Threshold
    double alpha = 2; // Promotion/demotion rate
    // Stores weights for each class
    ArrayList<double[]> weightsList = new ArrayList<double[]>();
    int[] prediction; // Stores the predictions
    double accuracy;  // Classification accuracy
    String fileName = ""; // Name of the dataset
    
    public Winnow2Classifier(Preprocessor ppr)
    {
        this.numClass = ppr.numClass;
        this.trainSet = ppr.trainSet;
        this.testSet = ppr.testSet;
        prediction = new int[ppr.testSet.size()];
        this.fileName = ppr.fileName;
    }
  
/**
 * This method trains the model on the training set
 * 
 * @param iteration is the number of repeated training
 */
    public void fit(int iteration)
    {
        if (numClass == 2)
        {
            weightsList.add(fitOneClass(1, iteration));
        }
        else
        {// Stores the weights for each class when there are multiple classes
            for (int i = 0; i < numClass; i++)
            {
                weightsList.add(fitOneClass(i + 1, iteration));
            }
        }
    }
    
/**
 * This method trains on one class at a time
 * 
 * @param focusClass is the class being focused
 * @param iteration is the number of repeated training
 * @return weights for the focused class
 */
    public double[] fitOneClass(int focusClass, int iteration)
    {
        // Set threshold to half of the number of attributes
        theta = (trainSet.get(0).length - 1) / 2; 
        double[] weights = new double[trainSet.get(0).length - 1];
        Arrays.fill(weights, 1);
        for (int k = 0; k < iteration; k++)
        {
            for (int i = 0; i < trainSet.size(); i++)
            {
                int actualClass = (trainSet.get(i)[trainSet.get(i).length - 1]
                     == focusClass) ? 1 : 0; // All other classes are negatives
                double dotProduct = 0;
                for (int j = 0; j < trainSet.get(0).length - 1; j++)
                {
                    dotProduct += weights[j] * trainSet.get(i)[j];
                }
                int predictClass = (dotProduct > theta) ? 1 : 0;
                if (predictClass == 0 && actualClass == 1)
                {// Promotion
                    for (int p = 0; p < weights.length; p++)
                    {
                        if (trainSet.get(i)[p] == 1)
                        {
                            weights[p] *= alpha;
                        }
                    }
                }
                else if (predictClass == 1 && actualClass == 0)
                {// Demotion
                    for (int d = 0; d < weights.length; d++)
                    {
                        if (trainSet.get(i)[d] == 1)
                        {
                            weights[d] /= alpha;
                        }
                    }
                }
            }
        }
        return weights;
    }
    
/**
 * This method prints the model for demonstration purpose
 */
    public void printParams()
    {
        System.out.println("The Winnow-2 model is trained.");
        System.out.println("Theta = " + theta);
        System.out.println("Alpha = " + alpha);
        System.out.println();
        if (numClass == 2)
        {
            System.out.println("The following is the list of weights:");
            System.out.print("[ ");
            for (int i = 0; i < weightsList.get(0).length; i++)
            {
                System.out.print(weightsList.get(0)[i] + " ");
            }
            System.out.println("]");
            System.out.println();
        }
        else
        {
            for (int i = 0; i < numClass; i++)
            {
                System.out.println("List of weights for class " + (i + 1) 
                        + ":");
                System.out.print("[ ");
                for (int j = 0; j < weightsList.get(0).length; j++)
                {
                    System.out.print(weightsList.get(i)[j] + " ");
                }
                System.out.println("]");
                System.out.println();
            }
        }
    }
    
/**
 * This method makes predictions on the test set and calculates prediction
 * accuracy
 */ 
    public void predict()
    {
        int correctPrediction = 0;
        if (numClass == 2)
        {
            for (int i = 0; i < testSet.size(); i++)
            {
                double dotProduct = 0;
                for (int j = 0; j < testSet.get(0).length - 1; j++)
                {
                    dotProduct += weightsList.get(0)[j] * testSet.get(i)[j];
                }
                int predictClass = (dotProduct > theta) ? 1 : 0;
                prediction[i] = predictClass;
            }
        }
        else
        {/* For multiple classes, predict the class with the highest score (dot
            product) */
            for (int i = 0; i < testSet.size(); i++)
            {
                double maxScore = 0;
                int maxScoreClass = 0;
                for (int j = 0; j < numClass; j++)
                {
                    double dotProduct = 0;
                    for (int k = 0; k < testSet.get(0).length - 1; k++)
                    {
                        dotProduct += weightsList.get(j)[k] 
                                * testSet.get(i)[k];
                    }
                    if (dotProduct > maxScore)
                    {
                        maxScore = dotProduct;
                        maxScoreClass = j + 1;
                    }
                }
                prediction[i] = maxScoreClass;
            }
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
    public void writeToFile(int iteration) throws IOException
    {
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
               fileName + "-Winnow2-iter" + iteration + "-output.txt", true)));
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
        fout.println("Parameters of the trained Winnow-2 model");
        fout.println("----------------------------------------");
        fout.println("Theta = " + theta);
        fout.println("Alpha = " + alpha);
        fout.println();
        if (numClass == 2)
        {
            fout.println("The following is the list of weights:");
            fout.print("[ ");
            for (int i = 0; i < weightsList.get(0).length; i++)
            {
                fout.print(weightsList.get(0)[i] + " ");
            }
            fout.println("]");
            fout.println();
        }
        else
        {
            for (int i = 0; i < numClass; i++)
            {
                fout.println("List of weights for class " + (i + 1) 
                        + ":");
                fout.print("[ ");
                for (int j = 0; j < weightsList.get(0).length; j++)
                {
                    fout.print(weightsList.get(i)[j] + " ");
                }
                fout.println("]");
                fout.println();
            }
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

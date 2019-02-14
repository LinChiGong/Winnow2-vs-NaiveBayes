/**
 * This class would accept an input file, process it, build a Winnow-2 model &
 * a Naive Bayes model, and make predictions using the two models
 * 
 * @author Winston Lin
 */
package P1;

import java.io.*;
import java.util.*;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        String inputFileName;
        
        // Prompt for the dataset to be used
        Scanner scan = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter input file name: ");
        inputFileName = scan.nextLine().trim();
        System.out.println();
        
        // Preprocess the dataset and split it into train and test set
        Preprocessor ppr = new Preprocessor(inputFileName);
        ppr.dummify();
        ppr.trainTestSplit();        
        
        // Build the Winnow-2 model and train it with the train set
        Winnow2Classifier w2Model = new Winnow2Classifier(ppr);
        w2Model.fit(1);
        w2Model.printParams();
        
        System.out.print("Press 'Enter' to proceed to make predictions on the"
                + " test set:"); // For demonstration purpose
        String temp = scan.nextLine().trim();
        System.out.println();
        
        // Use the trained model to make predictions on the test set
        w2Model.predict();
        w2Model.printResult();
        
        System.out.println();
        System.out.print("Press 'Enter' to proceed to create and train the"
                + " Naive Bayes model:"); // For demonstration purpose
        temp = scan.nextLine().trim();
        System.out.println();
        
        // Build the Naive Bayes model and train it with the train set
        NaiveBayesClassifier nbModel = new NaiveBayesClassifier(ppr);
        nbModel.fit();
        nbModel.printParams();
        
        System.out.print("Press 'Enter' to proceed to make predictions on the"
                + " test set:"); // For demonstration purpose
        temp = scan.nextLine().trim();
        System.out.println();
        
        // Use the trained model to make predictions on the test set
        nbModel.predict();
        nbModel.printResult();

        scan.close();
    }
}

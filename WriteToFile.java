/**
 * This class builds 3 models - regular Winnow-2, Winnow-2 with repeated
 * training, and Naive Bayes - to solve 5 classification problems specific to
 * this project. Each model and its prediction results are outputed to a file.
 * A final report that includes all classification accuracy for all models is
 * created
 * 
 * @author Winston Lin
 */
package P1;

import java.io.*;

public class WriteToFile 
{
    public static void main(String[] args) throws IOException
    {
        // 5 datasets used in this project
        String[] datasets = {"soybean-small.data", "house-votes-84.data", 
                "breast-cancer-wisconsin.data", "iris.data", "glass.data"};
        // Writes to the final report
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(
                "Compare-all-results.txt", true)));
        fout.println("\t\tWinnow-2-iter1\tWinnow-2-iter50\tNaive-Bayes");
        for (String dataset : datasets)
        {
            // Preprocess each dataset and split it into train and test set
            Preprocessor ppr = new Preprocessor(dataset);
            ppr.dummify();
            ppr.trainTestSplit();        
            
            // Build a Winnow-2 model and train it through the train set once
            Winnow2Classifier w2Model1 = new Winnow2Classifier(ppr);
            w2Model1.fit(1);            
            w2Model1.predict();
            w2Model1.writeToFile(1);
            
            // Second Winnow-2 model that is trained with 50 iterations
            Winnow2Classifier w2Model2 = new Winnow2Classifier(ppr);
            w2Model2.fit(50);            
            w2Model2.predict();
            w2Model2.writeToFile(50);
            
            // Build the Naive Bayes model and train it with the train set
            NaiveBayesClassifier nbModel = new NaiveBayesClassifier(ppr);
            nbModel.fit();
            nbModel.predict();
            nbModel.writeToFile();
            
            // Write to the final report
            if (dataset.equals("breast-cancer-wisconsin.data"))
            {
                fout.println(ppr.fileName + "\t" + w2Model1.accuracy + "%\t\t" 
                       + w2Model2.accuracy + "%\t\t" + nbModel.accuracy + "%");  
            }
            else
            {
                fout.println(ppr.fileName + "\t\t" + w2Model1.accuracy 
                        + "%\t\t" + w2Model2.accuracy + "%\t\t" 
                        + nbModel.accuracy + "%");
            }
        }
        fout.close();
    }
}

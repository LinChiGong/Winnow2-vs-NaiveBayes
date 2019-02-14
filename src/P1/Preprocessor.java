/**
 * This class pre-processes a dataset. Categorical and continuous attributes 
 * are transformed to Boolean attributes. Missing values are filled with 
 * majority vote within class or median within class
 * 
 * @author Winston Lin
 */
package P1;

import java.io.*;
import java.util.*;

public class Preprocessor 
{
    String filePath = ""; // Path of the dataset
    // Stores dataset in a 2D array
    ArrayList<String[]> rows = new ArrayList<String[]>(); 
    ArrayList<int[]> dumDataset = new ArrayList<int[]>(); // Dummified dataset
    int numClass = 0; // Number of classes in the dataset
    ArrayList<int[]> trainSet = new ArrayList<int[]>(); // Training set
    ArrayList<int[]> testSet = new ArrayList<int[]>();  // Test set
    String fileName = ""; // Name of the dataset
    
    public Preprocessor(String filePath) throws FileNotFoundException
    {
        this.filePath = filePath;
        File file = new File(filePath);
        Scanner input = new Scanner(file);
        while (input.hasNextLine())
        {
            String row = input.nextLine();
            String[] rowArray = row.split(",");
            rows.add(rowArray);
        }
        input.close();
    }

/**
 * This method processes the 5 datasets used in this project
 */
    public void dummify()
    {
        if (filePath.equals("soybean-small.data"))
        {
            dumSoybean();
            numClass = 4;
            fileName = "Soybean";
        }
        else if (filePath.equals("house-votes-84.data"))
        {
            dumVote();
            numClass = 2;
            fileName = "Vote";
        }
        else if (filePath.equals("breast-cancer-wisconsin.data"))
        {
            dumBreastCancer();
            numClass = 2;
            fileName = "Breast-Cancer";
        }
        else if (filePath.equals("iris.data"))
        {
            dumIris();
            numClass = 3;
            fileName = "Iris";
        }
        else if (filePath.equals("glass.data"))
        {
            dumGlass();
            numClass = 6;
            fileName = "Glass";
        }
    }
    
/**
 * This method splits the dataset into a training set and a test set
 */
    public void trainTestSplit()
    {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < dumDataset.size(); i++)
        {
            indices.add(i);
        }
        Collections.shuffle(indices);
        for (int j = 0; j < indices.size(); j++)
        {// 2/3 of the dataset constitute the training set, others are test set
            if (j < indices.size() / 3)
            {
                testSet.add(dumDataset.get(indices.get(j)));
            }
            else
            {
                trainSet.add(dumDataset.get(indices.get(j)));
            }
        }
    }

/**
 * This method processes the "Soybean" dataset
 */
    public void dumSoybean()
    {
        for (int i = 0; i < rows.size(); i++)
        {
            int[] dumRow = new int[(rows.get(i).length - 1) * 7 + 1];
            for (int j = 0; j < rows.get(i).length - 1; j++)
            {
                dumRow[j * 7 + Integer.parseInt(rows.get(i)[j])] = 1;
            }
            if (rows.get(i)[rows.get(i).length - 1].equals("D1"))
            {
                dumRow[dumRow.length - 1] = 1;
            }
            else if (rows.get(i)[rows.get(i).length - 1].equals("D2"))
            {
                dumRow[dumRow.length - 1] = 2;
            }
            else if (rows.get(i)[rows.get(i).length - 1].equals("D3"))
            {
                dumRow[dumRow.length - 1] = 3;
            }
            else
            {
                dumRow[dumRow.length - 1] = 4;
            }
            dumDataset.add(dumRow);
        }
    }
  
/**
 * This method processes the "Vote" dataset
 */
    public void dumVote()
    {
        for (int i = 0; i < rows.size(); i++)
        {
            int[] dumRow = new int[rows.get(i).length];
            for (int j = 1; j < rows.get(i).length; j++)
            {// Start from the second column because the first column is class
                if (rows.get(i)[j].equals("y"))
                {
                    dumRow[j - 1] = 1;
                }
                else if (rows.get(i)[j].equals("n"))
                {
                    dumRow[j - 1] = 0;
                }
                else
                {/* Fill in the missing values with the majority vote of the 
                    same class */
                    int classYes = 0;
                    int classNo = 0;
                    for (int k = 0; k < rows.size(); k++)
                    {
                        if (rows.get(k)[0].equals(rows.get(i)[0]))
                        {
                            if (rows.get(k)[j].equals("y"))
                            {
                                classYes++;
                            }
                            else if (rows.get(k)[j].equals("n"))
                            {
                                classNo++;
                            }
                        }
                    }
                    if (classYes > classNo)
                    {
                        dumRow[j - 1] = 1;
                    }
                }
            }
            if (rows.get(i)[0].equals("democrat"))
            {
                dumRow[dumRow.length - 1] = 0;
            }
            else
            {
                dumRow[dumRow.length - 1] = 1;
            }
            dumDataset.add(dumRow);
        }       
    }
    
/**
 * This method processes the "Breast Cancer" dataset
 */
    public void dumBreastCancer()
    {
        for (int i = 0; i < rows.size(); i++)
        {
            int[] dumRow = new int[(rows.get(i).length - 2) * 10 + 1];
            for (int j = 1; j < rows.get(i).length - 1; j++)
            {
                if (rows.get(i)[j].equals("?"))
                {// Fill in the missing values with the class median
                    int classMedian = classMedian(j, 
                            rows.get(i)[rows.get(i).length - 1]);
                    dumRow[(j - 1) * 10 + classMedian - 1] = 1;
                }
                else
                {
                    dumRow[(j - 1) * 10 + Integer.parseInt(rows.get(i)[j]) - 1]
                            = 1;
                }
            }
            if (rows.get(i)[rows.get(i).length - 1].equals("2"))
            {
                dumRow[dumRow.length - 1] = 0; // 0 is benign
            }
            else
            {
                dumRow[dumRow.length - 1] = 1; // 1 is malignant
            }
            dumDataset.add(dumRow);
        }
    }
    
/**
 * This method processes the "Iris" dataset
 */
    public void dumIris()
    {
        for (int i = 0; i < rows.size(); i++)
        {
            /* Discretize each attribute: 1st attribute 4 categories, 2nd
               attribute 3 categories, ..., and leave last column for class */
            int[] dumRow = new int[4 + 3 + 6 + 3 + 1];
            for (int j = 0; j < rows.get(i).length - 1; j++)
            {
                if (j == 0)
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 4;
                    dumRow[category] = 1;
                }
                else if (j == 1)
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 2;
                    dumRow[4 + category] = 1;
                }
                else if (j == 2)
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 1;
                    dumRow[7 + category] = 1;
                }
                else
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]);
                    dumRow[13 + category] = 1;
                }
            }
            if (rows.get(i)[rows.get(i).length - 1].equals("Iris-setosa"))
            {
                dumRow[dumRow.length - 1] = 1;
            }
            else if (rows.get(i)[rows.get(i).length - 1].equals(
                    "Iris-versicolor"))
            {
                dumRow[dumRow.length - 1] = 2;
            }
            else
            {
                dumRow[dumRow.length - 1] = 3;
            }
            dumDataset.add(dumRow);
        }
    }
    
/**
 * This method processes the "Glass" dataset
 */
    public void dumGlass()
    {
        for (int i = 0; i < rows.size(); i++)
        {
            /* Discretize each attribute: 1st attribute 3 categories, 2nd
               attribute 8 categories, ..., and leave last column for class */
            int[] dumRow = new int[3 + 8 + 5 + 4 + 7 + 7 + 12 + 4 + 6 + 1];
            for (int j = 1; j < rows.get(i).length - 1; j++) // Drop the Id
            {
                if (j == 1) // RI
                {
                    int category = (int) ((Double.parseDouble(rows.get(i)[j]) 
                            - 1.51) * 100);
                    dumRow[category] = 1;
                }
                else if (j == 2) // Na
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 10;
                    dumRow[3 + category] = 1;
                }
                else if (j == 3) // Mg
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]);
                    dumRow[11 + category] = 1;
                }
                else if (j == 4) // Al
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]);
                    dumRow[16 + category] = 1;
                }
                else if (j == 5) // Si
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 69;
                    dumRow[20 + category] = 1;
                }
                else if (j == 6) // K
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]);
                    dumRow[27 + category] = 1;
                }
                else if (j == 7) // Ca
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]) 
                            - 5;
                    dumRow[34 + category] = 1;
                }
                else if (j == 8) // Ba
                {
                    int category = (int) Double.parseDouble(rows.get(i)[j]);
                    dumRow[46 + category] = 1;
                }
                else if (j == 9) // Fe
                {
                    int category = (int) (Double.parseDouble(rows.get(i)[j]) 
                            * 10);
                    dumRow[50 + category] = 1;
                }
            }
            if (Integer.parseInt(rows.get(i)[rows.get(i).length - 1]) < 4)
            {// Class 1, 2, 3 stay as class 1, 2, 3
                dumRow[dumRow.length - 1] = Integer.parseInt(
                        rows.get(i)[rows.get(i).length - 1]);
            }
            else
            {// Class 5, 6, 7 are reassigned as class 4, 5, 6
                dumRow[dumRow.length - 1] = Integer.parseInt(
                        rows.get(i)[rows.get(i).length - 1]) - 1;
            }
            dumDataset.add(dumRow);
        }
    }

/**
 * This method calculates the median value of an attribute using data entries
 * that belong to the same class 
 *  
 * @param col is the index of the attribute
 * @param Class is the class of the data entries
 * @return the class median
 */
    public int classMedian(int col, String Class)
    {
        int median;
        ArrayList<Integer> classMember = new ArrayList<Integer>();
        for (int i = 0; i < rows.size(); i++)
        {
            if (rows.get(i)[rows.get(i).length - 1].equals(Class) 
                    && !rows.get(i)[col].equals("?"))
            {
                classMember.add(Integer.parseInt(rows.get(i)[col]));
            }
        }
        median = classMember.get(classMember.size() / 2);
        return median;
    }    
}

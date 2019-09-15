# Winnow2-vs-NaiveBayes

## Abstract

In this project, two algorithms – Winnow-2 and Naïve Bayes – are experimented to solve classification problems for 5 datasets obtained from the UCI Machine Learning Repository [1]. Both algorithms have very good performance on 4 out of the 5 datasets yet both fail to perform well on one specific dataset. In general, the Naïve Bayes algorithm performs better than the Winnow-2 algorithm. 

## Introduction

The main problems in this project are classification problems based on 5 datasets – “Breast Cancer”, “Glass”, “Iris”, “Soybean”, and “Vote”. Of the 5 datasets, “Vote” and “Breast Cancer” have 2 classes, whereas others have multiple classes. Also, “Vote” is the only dataset whose attributes are all Boolean attributes. Other datasets have either categorical, continuous, or mixed attributes.

Winnow-2 is a supervised, online learning algorithm that only works well with Boolean attributes and 2-class classification problems. Therefore, we need to “dummify” each non-Boolean attribute to multiple Boolean attributes and treat a multi-class classification problem as multiple 2-class classification problems. Since Winnow-2 only combines the results of multiple 2-class classification problems instead of solving the problem directly, I would expect Winnow-2 to perform worse on multi-class classification problems.

On the other hand, Naïve Bayes is a supervised learning algorithm that is powerful yet easy to implement. Even though Naïve Bayes works well with all kinds of attributes, in this project, I still train it with dummified datasets for the purpose of comparison. I expect Naïve Bayes to perform better than Winnow-2 on multi-class classification problems. At the same time, due to the conditional independence assumption of Naïve Bayes, if there are a lot of dependencies among the attributes, Naïve Bayes might not perform well.

## Methods

- Data processing: For categorical attributes, each category is assigned as a separate attribute. For continuous attributes, I first create groups for each attribute. For example, if a continuous attribute has [max, min] = [2, 5], I would make 3 groups as follows: [2-3, 3-4, 4-5]. Then, each group is assigned as a separate attribute.

Missing values are filled with class vote or class median. If the attribute is Boolean or nominal categorical, the missing value is filled with the majority vote value of the data points that belong to the same class. If the attribute is ordinal categorical or continuous, the missing value is filled with the median value of the data points that belong to the same class.

- Winnow-2 classifier: 
> The default for alpha (promotion/demotion rate) is 2. Theta (threshold) is set to equal to half of the number of attributes [2]. For a 2-class classification problem, only one classifier is trained. Predictions are based on whether the dot product of weights and each test sample exceeds the threshold or not. For a multi-class classification problem, one classifier is trained for each class. When training on one class, data points of that class are seen as positive samples while data points of all other classes are regarded negatives. As a result, each class has its own weights. When making predictions on the test samples, a dot product is calculated for each class. The class with the largest dot product is the predicted class.

> Note that I implement an “iteration” argument for the training function [3]. The regular Winnow-2 classifier only has one iteration: it adjusts its weights as it goes through all the entries in the training set once. A second iteration allows the model to go through the training set again. In this project, I want to compare the results of regular Winnow-2 models and Winnow-2 models with 50 iterations.

- Naïve Bayes classifier: During the training process, the model simply counts occurrences and stores the prior of each class and the likelihood of each attribute given class. During the testing process, a posterior is calculated and recorded for each class. The class with the largest posterior value is the predicted class. m-estimate smoothing is implemented where m (pseudo-sample size) = 1 and p (weight) = 0.001 [4].

- Summary statistics: Classification accuracy (number of correct predictions / total number of test samples) of each model on each dataset is calculated.

Results

•	Sample outputs:




























Output of the Winnow-2 model on “Vote” dataset is on the left. Output of the Naïve Bayes model on “Vote” dataset is on the right. Class descriptions are shown in the first section. Parameters including weights, priors, and likelihoods are shown in the next section. Side-by-side classification results and classification accuracies are shown in the last section.

•	Summary statistics


Record all classification accuracies. First column is the regular Winnow-2 algorithm. Second column is the Winnow-2 algorithm with 50 iterations. Third column is the Naïve Bayes algorithm. Rows are the 5 datasets. 

## Discussion

As shown above, both Winnow-2 and Naïve Bayes perform quite well on all datasets except for the “Glass” dataset (Surprisingly, Winnow-2 handles other multi-class classification problems quite well!). I think there are 3 possible reasons which may explain the underperformance on “Glass”. First of all, the dataset may not be very representative. Note that there are 6 classes, 214 entries, and 9 attributes in the “Glass” dataset. This means that after split to training and test sets, on average, there are only 23 training samples for each class. Considering the number of attributes, the training samples may be too few to represent the whole class. Obtaining more data may help create better training sets. 

Second, all attributes in “Glass” are continuous. Recall that since Winnow-2 is designed for Boolean attributes, I have to categorize continuous attributes and then dummify them into Boolean attributes. A lot of information may be lost during this transformation process, and therefore affecting the performance of the algorithms. If we implement a Gaussian Naïve Bayes model which is able to use the original “Glass” dataset instead of the transformed dataset, we may achieve better results.

Third, “Glass” classification problem may not be linearly separable in the first place. It is worth noting that both Winnow-2 and Naïve Bayes are only capable of learning linearly separable problems. If “Glass” is just not linearly separable, then we should implement other algorithms that are more suitable to this task.

Notice that Winnow-2 with 50 iterations (2nd column) generally performs better than regular Winnow-2 (1st column). I think this is due to the fact that all the datasets we have in this project have only hundreds of entries. Multiple iterations during training gives the models more time to adjust their weights. However, this may not always be beneficial. Notice that for the “Iris” dataset, the repeated training makes things worse. I believe this is a sign of overfitting. When the model is already robust enough yet we keep feeding the same set of training samples to it, eventually, we are only catching the noise. It is important to select the “right” number of iterations per problem.

Also notice that Naïve Bayes generally performs better than both Winnow-2 algorithms. This is not surprising since Naïve Bayes is commonly considered more robust than Winnow-2. However, for the “Vote” dataset, Naïve Bayes does lose to Winnow-2. Since Naïve Bayes works well under the strong assumption that all attributes are conditionally independent, I would guess that one possible reason the Naïve Bayes model underperforms in this case may be that attributes in “Vote” are relatively dependent. If that is the case, implementing a Tree Augmented Naïve Bayes would generate better results.

## Summary

In this project, we compare and contrast how regular Winnow-2, Winnow-2 with repeated training, and Naïve Bayes algorithms performs on 5 classification problems. Although Naïve Bayes performs better than regular Winnow-2 in 4 out of 5 problems, there is case where Winnow-2 does beat Naïve Bayes in terms of classification accuracy. Moreover, the performance of Winnow-2 can be enhanced by allowing the model to go through the training set repeatedly during the training process. One thing to keep in mind is that the repeated training may cause overfitting, and therefore we have to be very careful when choosing the number of iterations.

For multi-class classification problems, even though Winnow-2 performs unexpectedly well, Naïve Bayes still has better performance. For problems that are based on datasets with relatively dependent attributes, Winnow-2 may be the better choice. For problems that are not linearly separable, both Winnow-2 and Naïve Bayes are not ideal, and we should look for alternative algorithms.

## References
1.	Dua, D. and Karra Taniskidou, E. (2017). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml]. Irvine, CA: University of California, School of Information and Computer Science. 
2.	Littlestone, N. (1988). Learning quickly when irrelevant attributes abound: A new linear-threshold algorithm. Machine Learning,2(4), 285-318. doi:10.1007/bf00116827 
3.	Zhang, T. (2001). Regularized winnow methods. Advances in Neural Information Processing Systems, 13, 703–709. 
4.	Jiang, L., Wang, D., & Cai, Z. (2007). Scaling Up the Accuracy of Bayesian Network Classifiers by M-Estimate. Advanced Intelligent Computing Theories and Applications. With Aspects of Artificial Intelligence Lecture Notes in Computer Science, 475-484. doi:10.1007/978-3-540-74205-0_52


a. Name: Hong Guo: hg2376 ; Liang Wu: lw2589

b. Project1.jar README.txt Transcript.pdf 
   Source code: BingTest.java Result.java Rocchio.java

c. Type: 
        java -jar Project1.jar <Bing account key> <Precision> <query>

d. First, we use the original query to get results from bing api. We parsed
the XML and combined the title and description of the result. Then we preprocess the records by
getting rid of symbol, extra space. Then we split the record to ArrayList of
Strings and then get rid of stopwords. We passed the preprocessed
documents to Rocchio Algorithm, pick two terms with highest weight. Finally, we
add those two terms to expanded query. 

e. Basically, we tried different versions of Rocchio Algorithms: First, we
tried the weight calculation with tf.idf format that we discussed in class.
Then we tried ltc version of Rocchio: let Term Frequency as: 1 + log(tf), let
Document frequency as log(N/df) and then we normalized using L2 Norm. Finally,
we tried the natural version of Rocchio as: let Term Frequency as: tf, let
Document frequency as 1. Comparing with those three versions, we find that ltc
version has the best result. In order to optimize our result, we used
word proximity techniques, adding weight to nearby words. This method
significantly improve all query results. We also put more weight on the same order
as the result returned by Bing, which also further improved the result. So our conclusion is: tf*log(N/df)
version of Rocchio algorithm combined with word or term proximity.     
 
f: My Bing Key: Zog+fM4vOmoXFmoKDtNVGamO1PxHC1Tvx5Ks4VkrrFs

g: We tried and experimented different algorithms, and tried a lot of test
cases. Actually, there is no definite conclusion that which version of Rocchio
algorithms wins in all cases. However, we think the process of experimenting and implementing all those versions is also an important
learning and thinking experience. Besides, the combined algorithms (Rocchio
with word proximity) accheived very good result!   

package project1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Rocchio {

    // preprocess the input string, get rid of non-letter char and stop words 
    public static ArrayList<ArrayList<String>> preprocess(ArrayList<String> list) {
        // get rid of non-letter char
        ArrayList<String> preprocess = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            StringBuilder s = new StringBuilder();
            if (Character.isLetter(list.get(i).charAt(0))) {
            	char ch = list.get(i).charAt(0);
            	ch = Character.toLowerCase(ch);
                s.append(ch);
            }
//            s.append(list.get(i).charAt(0));
            for (int j = 1; j < list.get(i).length(); j++) {
                char temp1 = list.get(i).charAt(j);
                if (temp1 >= 'a' && temp1 <= 'z' || temp1 >= 'A' && temp1 <= 'Z' ||Character.isDigit(temp1)|| temp1 == ' ') {
                    char temp = Character.toLowerCase(list.get(i).charAt(j));
                    s.append(temp);
                }
            }
            preprocess.add(s.toString());
            
        }
        
        // get rid of stop words
        HashSet<String> stopwords = new HashSet<String>(Arrays.asList("a", "an", "and", "are", "as", "at", "be", 
                "by", "for", "from", "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", "to", "was",
                "were", "will","lions", "with")); 
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < preprocess.size(); i++) {
            String[] array = preprocess.get(i).split("\\s+");
            ArrayList<String> item = new ArrayList<String>(Arrays.asList(array));
            ArrayList<String> removeWords = new ArrayList<String>();
            for (int j = 0; j < item.size(); j++) {
                // remove stop word or empty string 
                if (stopwords.contains(item.get(j)) || item.get(j)=="" || item.get(j) == null) {
                    removeWords.add(item.get(j));
                }                
            }
            item.removeAll(removeWords);
            res.add(item);
        }
        
        
        
        return res;
    }
    
    // store number of each distinct words for each document --- tf (need to get rid of empty string)
    public static ArrayList<HashMap<String, Double>> computetf(ArrayList<ArrayList<String>> list, ArrayList<String> query) {
        // store number of each distinct words for each document --- tf
        ArrayList<HashMap<String, Double>> resSet = new ArrayList<HashMap<String,Double>>();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Double> map = new HashMap<String, Double>();
            for (int j = 0; j < list.get(i).size(); j++) {
                if (!map.containsKey(list.get(i).get(j))) {
                    map.put(list.get(i).get(j), 1.0/i);
                }
                else {
                    map.put(list.get(i).get(j), map.get(list.get(i).get(j))+1.0/i);
                }
                if (query.get(0)==(list.get(i).get(j))) {
                    if (j>0) {
                        map.put(list.get(i).get(j-1), map.get(list.get(i).get(j-1))+1.0/i);
                    }
                    if (j < list.get(i).size()-1) {
                        if (!map.containsKey(list.get(i).get(j+1))) {
                            map.put(list.get(i).get(j+1), 1.0/i);
                        }
                        else {
                            map.put(list.get(i).get(j+1), map.get(list.get(i).get(j+1))+1.0/i);
                        }
                    }
                }
            }
            resSet.add(map);
        }
        return resSet;
    }
    
    // the map for storing the document frequency
    public static HashMap<String, Integer> computeidf(ArrayList<ArrayList<String>> list) {
        // the map for storing the document frequency
        HashMap<String, Integer> mapD = new HashMap<String, Integer>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                if (!mapD.containsKey(list.get(i).get(j))) {
                    mapD.put(list.get(i).get(j), 0);
                } 
            }
        }
        /*String remove = "";
        for (Entry<String, Integer> entry : mapD.entrySet()) {
            if (entry.getKey() == null || entry.getKey().length() ==0) {
                remove = entry.getKey();
            }
        }
        
        mapD.remove(remove);*/
        
        //calculate idf 
        for (Entry<String, Integer> entry : mapD.entrySet()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(entry.getKey())) {
                    mapD.put(entry.getKey(), mapD.get(entry.getKey())+1);
                }
            }
        }
        return mapD;
    }
    
    public static ArrayList<ArrayList<Double>> computeVect(ArrayList<ArrayList<String>> list, 
            HashMap<String, Integer> mapD, ArrayList<HashMap<String, Double>> resSet) {
        // result 
        ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
        // calculate the weight of term 
        for (int i = 0; i < list.size(); i++) {
            // item has same number element as mapD -- total number of distinct words
            ArrayList<Double> item = new ArrayList<Double>();
            for (Entry<String, Integer> entry : mapD.entrySet())
            {
                double tf = 0;
                if (resSet.get(i).containsKey(entry.getKey())) {
                    // get the tf from the hashmap of ith record
                    tf = resSet.get(i).get(entry.getKey());
                }
                item.add(tf*Math.log(list.size()/entry.getValue()));
            }
            res.add(item);
        }
        
        return res;
    }
        

    
   /* // compute the vector model of those documents 
    public static ArrayList<ArrayList<Double>> computeVec(ArrayList<ArrayList<String>> list) {
        // result 
        ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
        // the map for storing the document frequency
        HashMap<String, Integer> mapD = new HashMap<String, Integer>();
        // store number of each distinct words for each document --- ti
        ArrayList<HashMap<String, Integer>> resSet = new ArrayList<HashMap<String,Integer>>();
        
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            for (int j = 0; j < list.get(i).size(); j++) {
                if (!map.containsKey(list.get(i).get(j))) {
                    map.put(list.get(i).get(j), 1);
                }
                else {
                    map.put(list.get(i).get(j), map.get(list.get(i).get(j))+1);
                }
                // create mapD to store document frequency
                if (!mapD.containsKey(list.get(i).get(j))) {
                    mapD.put(list.get(i).get(j), 0);
                } 
            }
            resSet.add(map);
        }
        
        //calculate idf 
        for (Entry<String, Integer> entry : mapD.entrySet()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(entry.getKey())) {
                    mapD.put(entry.getKey(), mapD.get(entry.getKey())+1);
                }
            }
        }
        
        // calculate the weight of term 
        for (int i = 0; i < list.size(); i++) {
            // item has same number element as mapD -- total number of distinct words
            ArrayList<Double> item = new ArrayList<Double>();
            for (Entry<String, Integer> entry : mapD.entrySet())
            {
                int tf = 0;
                if (resSet.get(i).containsKey(entry)) {
                    // get the tf from the hashmap of ith record
                    tf = resSet.get(i).get(entry.getKey());
                }
                item.add(tf*Math.log(list.size()/entry.getValue()));
            }
            res.add(item);
        }
        
        return res;
        
    }*/
    
    public static ArrayList<String> rocchioAlg(ArrayList<Double> queryVec, ArrayList<Boolean> relevance, 
            ArrayList<ArrayList<Double>> docVec, HashMap<String, Integer> mapD, ArrayList<String> query) { 
        double alpha = 1;
        double beta = 0.7;
        double gamma = 0.3;
        
        int r = 0;
        int nr = 0;
        for (int i = 0; i < relevance.size(); i++) {
            if (relevance.get(i) ==  true) {
                r++;
            }
            else {
                nr++;
            }
        }
        
        ArrayList<String> wordArray = new ArrayList<String>();
        for (Entry<String, Integer> entry : mapD.entrySet()) {
            wordArray.add(entry.getKey());
        }
        
        ArrayList<Double> newQueryVec = new ArrayList<Double>();
        for (int i = 0; i < queryVec.size(); i++) {
            double temp = alpha*queryVec.get(i);
            for (int j = 0 ; j < relevance.size(); j++) {
                if (relevance.get(j)) {
                    temp += beta * docVec.get(j).get(i)/r;
                }
                else {
                    temp -= gamma * docVec.get(j).get(i)/nr;
                }
            }
            newQueryVec.add(temp);
        }
        
        int maxInd1 = -1;
        int maxInd2 = -2;
        double max1 = 0;
        double max2 = 0;
        for (int i = 0; i < newQueryVec.size(); i++) {
            if (newQueryVec.get(i) > max1 && newQueryVec.get(i) > max2 && !query.contains(wordArray.get(i))) {
                max2 = max1;
                max1 = newQueryVec.get(i);
                maxInd2 = maxInd1;
                maxInd1 = i;
            } 
            else if (newQueryVec.get(i) > max2 && !query.contains(wordArray.get(i))) {
                max2 = newQueryVec.get(i);
                maxInd2 = i;
            }
        }
        
        query.add(wordArray.get(maxInd1));
        //System.out.print(maxInd1);
        query.add(wordArray.get(maxInd2));
        //System.out.print(maxInd2);
        return query;
    }
    
    public static void main(String args[]) {
        String s1 = new String("Elon Musk - Wikipedia, the free encyclopedia Summary : Elon Reeve Musk (born June 28, 1971) is a South Africa-born, Canadian-American entrepreneur, engineer, inventor and investor. He is the CEO and CTO of ");
        
//        String s2 = new String("Safe & durable baby gates will keep your baby safe. Choose from a large assortment of baby gates including extra wide gates to suit your needs. Shop today.");
        
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add(s1);
//        list1.add(s2);
        ArrayList<ArrayList<String>> list = Rocchio.preprocess(list1);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                System.out.print("|"+list.get(i).get(j)+"|");
            }
        }
        //ArrayList<HashMap<String,Integer>> resSet = Rocchio.computetf(list);
        /*for (int i = 0; i < resSet.size(); i++) {
            for (Entry<String, Integer> entry : resSet.get(i).entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue() + " ");
            }
        }*/
        
        //HashMap<String, Integer> mapD = Rocchio.computeidf(list);
        /*for (Entry<String, Integer> entry : mapD.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue() + " ");
        }
        */
       /* ArrayList<ArrayList<Double>> result = Rocchio.computeVect(list, mapD, resSet);
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).size(); j++) {
                System.out.println(result.get(i).get(j));
            }
        }*/
        
        // result 
        /*ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
        // calculate the weight of term 
        for (int i = 0; i < list.size(); i++) {
            // item has same number element as mapD -- total number of distinct words
            ArrayList<Double> item = new ArrayList<Double>();
            for (Entry<String, Integer> entry : mapD.entrySet())
            {
                int tf = 0;
                if (resSet.get(i).containsKey(entry.getKey())) {
                    // get the tf from the hashmap of ith record
                    tf = resSet.get(i).get(entry.getKey());
                }
                System.out.println(tf);
                item.add(tf*Math.log(list.size()/entry.getValue()));
            }
            res.add(item);
        }*/
        
    }

}

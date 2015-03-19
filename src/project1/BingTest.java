package project1;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;






//Download and add this library to the build path.
import org.apache.commons.codec.binary.Base64;


public class BingTest {
  
  public static Result queryExpansion(ArrayList<String> query, double target, String key) throws IOException{
      String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query.get(0);
      for (int i = 1; i < query.size(); i++) {
          bingUrl += "+" + query.get(i);
      }
      bingUrl += "%27&$top=10&$format=Atom";
      
      //Provide your account key here. 
      //String accountKey = "Zog+fM4vOmoXFmoKDtNVGamO1PxHC1Tvx5Ks4VkrrFs";
      String accountKey = key;
      
      
      
      //Scanner in = new Scanner(System.in);
      //int target = in.nextInt();
      //in.close();
      
      byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
      String accountKeyEnc = new String(accountKeyBytes);

      URL url = new URL(bingUrl);
      URLConnection urlConnection =  url.openConnection();
      urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
              
      InputStream inputStream = (InputStream) urlConnection.getContent(); 
      
      byte[] contentRaw = new byte[urlConnection.getContentLength()];
      inputStream.read(contentRaw);
      String content = new String(contentRaw);

      //The content string is the xml/json output from Bing.
      //System.out.println(content);
      
      int r = 0;
      int nr = 0;
      
      // put all document in an array
      ArrayList<String> documents = new ArrayList<String>();
      // array to record if the document is relevant
      ArrayList<Boolean> relevance = new ArrayList<Boolean>();
      String oldQuery = "";
      try {
          
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          InputSource is = new InputSource(new StringReader(content));
          Document doc = dBuilder.parse(is);
       
          doc.getDocumentElement().normalize();
       
          System.out.println("Parameters:");
          System.out.println("Client key = " + accountKey);
         
          
          for (int i = 0; i < query.size(); i++) {
              oldQuery+=" "+query.get(i);
          }
          System.out.println("Query      = " + oldQuery);
          System.out.println("Precision  = " + target);
          System.out.println("URL: " + bingUrl);
          System.out.println("Total no of results : 10");
          System.out.println("Bing Search Results:");
          NodeList nList = doc.getElementsByTagName("m:properties");
          System.out.println("=======================");
          
          for (int temp = 0; temp < nList.getLength(); temp++) {
       
              Node nNode = nList.item(temp);
       
              //System.out.println("\nCurrent Element :" + nNode.getNodeName());
       
              if (nNode.getNodeType() == Node.ELEMENT_NODE) {
       
                  Element eElement = (Element) nNode;
                  System.out.println("Result " + (temp+1));
                  System.out.println("[");
                  System.out.println("URL: " + eElement.getElementsByTagName("d:Url").item(0).getTextContent());
                  System.out.println("Title: " + eElement.getElementsByTagName("d:Title").item(0).getTextContent());
                  System.out.println("Summary : " + eElement.getElementsByTagName("d:Description").item(0).getTextContent());
                  System.out.println("]");
                  String document = eElement.getElementsByTagName("d:Title").item(0).getTextContent() +
                         " " + eElement.getElementsByTagName("d:Description").item(0).getTextContent();
                  documents.add(document);
                  Scanner userInput = new Scanner(System.in);
                  System.out.println("Relevant (Y/N)?");
                  String s = userInput.next();
                  if (s.equals("Y") || s.equals("y")) {
                      r++;
                      relevance.add(true);
                  }
                  else {
                      nr++;
                      relevance.add(false);
                  }
              }               
          
          }
      } 
      catch (Exception e) {
          e.printStackTrace();
      }
      
       // pre-process the document
      ArrayList<ArrayList<String>> list = Rocchio.preprocess(documents);
      // store number of each distinct words for each document --- tf
      ArrayList<HashMap<String,Double>> resSet = Rocchio.computetf(list,query);
      // the map for storing the document frequency
      HashMap<String, Integer> mapD = Rocchio.computeidf(list);
      // vector model 
      ArrayList<ArrayList<Double>> docVec = Rocchio.computeVect(list, mapD, resSet);
      
      ArrayList<Double> queryVec = new ArrayList<Double>();
      for (Entry<String, Integer> entry : mapD.entrySet()) {
          if (query.contains(entry.getKey())) {
              queryVec.add((double) 1);
          }
          else {
              queryVec.add((double) 0);
          }
      }
      
      query = Rocchio.rocchioAlg(queryVec, relevance, docVec, mapD, query);
      
      
      
/*        for (int i = 0; i < newQuery.size();i++) {
          System.out.println("'"+newQuery.get(i)+"'");
      }*/
      double precision = 0;
      precision = (double) r/(r+nr);
      System.out.println("=================================");
      System.out.println("FEEDBACK SUMMARY");
      System.out.println("Query " + oldQuery);        
      System.out.println("Precision " + precision); 
      Result result = new Result();
      if (precision < target) {
          System.out.println("Still below the desired precision of " + target);
          System.out.println("Indexing results ....");
          System.out.println("Indexing results ....");
          String newquery = query.get(query.size()-1)+ " " + query.get(query.size()-2);
          System.out.println("Augmenting by " + newquery);
          result.setPrecision(precision);
          result.setQuery(query.get(query.size()-1), query.get(query.size()-2));
      }
      else {
          result.setPrecision(precision);
          System.out.println("Desired precision reached, done");
      }
      return result;
  }
  

  public static void main(String[] args) throws IOException {
      String targetString = args[1];
      double target = Double.parseDouble(targetString);
      int num = args.length;
      ArrayList<String> query = new ArrayList<String>();
      for (int i = 2; i < num; i++) {
      String initQuery = args[i].toLowerCase();
      query.add(initQuery);
      }
      String key = args[0];
      double precision = 0;
      while (precision < target) {
          Result result = BingTest.queryExpansion(query, target, key);
          precision = result.getPrecision();
      } 
      
  }
     

}

package project1;

public class Result {
    double precision;
    String[] expandedQuery = new String[2];
    public void setPrecision(double d) {
        precision = d;
    }
    
    public void setQuery(String s1, String s2) {
        expandedQuery[0] = s1;
        expandedQuery[1] = s2;
    }
    
    public double getPrecision() {
        return precision;
    }
    
    public String[] getString() {
        return expandedQuery;
    }
}

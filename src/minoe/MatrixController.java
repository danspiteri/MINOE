
package minoe;

import java.io.IOException;

/*
 * MatrixController.java
 * 
 * Description:
 * Given an array of table data, this class will determine the co-occurence
 * of terms in an given documents.  For instance, if a link is specified
 * between the terms "ocean" and "crab", then all documents that contain
 * these two terms will be counted and returned as part of an array.
 *
 * To calculate filled cells / number of search operations:
 *
 * y = ((x^2 - x) / 2) + x
 *
 * Where
 * x = number of terms
 * y = number of operations
 *
 * For 10 terms there would be ((10^2 - 10) / 2) + 10 = 55 search operations.
 * For 11 terms there would be ((11^2 - 11) / 2) + 11 = 66 search operations.
 * For 50 terms there would be ((50^2 - 50) / 2) + 50 = 1,275 search operations.
 * For 100 terms there would be ((100^2 - 100) / 2) + 100 = 5050 search operations.
 *
 * @author Dan Spiteri
 */
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.apache.lucene.queryParser.ParseException;

public class MatrixController {

    public SearchFiles searchfiles; // Lucene
    private String[] terms; // User specified terms
    public float termsProcessed = 0;
    public StatusFrame statusFramePointer;
    public Hashtable<String, Integer> documentList;  // a list of all documents in the table
    public Hashtable<String, Hashtable<String, Integer>> termDocumentList;
    private Globals globals;


    // Contructor
    public MatrixController(Globals globals, MetaDataController mdc){
        this.searchfiles = globals.getSearchFiles();
        this.documentList = new Hashtable<String, Integer>();
        this.globals = globals;
    }
        // Contructor
    public MatrixController(Globals globals, String[] terms, MetaDataController mdc){
        this.terms = terms;
        this.searchfiles = globals.getSearchFiles();
        this.documentList = new Hashtable<String, Integer>();
        this.globals = globals;
    }
   
    /**
     * Returns a two-dimensional array of the co-occurence table data.
     * Index starts at 0 for columns and rows.  
     * @param filter
     * @return 
     */
    public int[][] generateTable(SearchCriteria criteria){
        int size = this.terms.length;
        int[][] tableResults = new int[size][size];
        for (int row=0; row<this.terms.length; row++){
            for(int col=0; col<this.terms.length; col++){
                String a = this.terms[row];
                String b = this.terms[col];
                // Compare data for different terms.
                if (col >= row){
                    int coCount = this.countTerms(a, b, criteria);
                    // there was a problem
                    if(coCount == -1){
                        return null;
                    }
                    tableResults[row][col] = coCount;
                }
            }
            
            this.termsProcessed++;
            
            // Update the progress bar.
            if (this.statusFramePointer != null){
                //Thread.yield();
                float v = (1 - (size - this.termsProcessed) / size) * 100;
                int x = (int) v;
                this.statusFramePointer.setProgressBar(x + "%", x);
            }
        }
        return tableResults;
    }

    public int countTerms(String termA, String termB, SearchCriteria criteria){
        Hashtable<String, Integer> results = getCooccurenceDocNames(termA, termB, criteria);
        if(results == null){
            JOptionPane.showMessageDialog(null, "Error:  search failed on: " + termA + ", " + termB);
        }
        Enumeration<String> e = results.keys();
        int count=0;
        while(e.hasMoreElements()){
            String key = e.nextElement();
            int val = results.get(key);
            count += val;
            // add to the document list (keep track of all document counts)
            if(this.documentList.containsKey(key)){
                int currentvalue = this.documentList.get(key);
                currentvalue += val;
                this.documentList.put(key, currentvalue);
            } else{
                this.documentList.put(key, val);
            }

        }
        return count;
    }

    /**
     * Returns a hashtable of document names by terms and their counts.
     * @param terms
     * @param criteria
     */
    public Hashtable<String, String[]> createTermDocumentMatrix(String terms[], SearchCriteria criteria){
        Hashtable<String, Hashtable<String, Integer>> tempTermDocumentMatrix =
                new Hashtable<String, Hashtable<String, Integer>>();

        MetaDataController mdc = this.globals.getMetaDataController();
        // All documents relating to the filter criteria.
        List<String> list = null;
        if(criteria == null){
            list = mdc.getDocuments();
        } else{
            list = mdc.getDocumentsBySearchCriteria(criteria);
        }


        // Get the counts for each term and each document.
        for (String term: terms){
            // file name => counts
            // Returns a table of file names and the count for each file.
            Hashtable<String, Integer> results = getCooccurenceDocNames(term, term, criteria);
            if(results == null){
                JOptionPane.showMessageDialog(null, "Error:  search failed on: " + term);
            }

            Enumeration<String> e = results.keys(); // file names

            // associate the documents with terms and their counts
            while(e.hasMoreElements()){
                String fileName = e.nextElement();
                int val = results.get(fileName);  // term frequency

                Hashtable rowTable;

                if(tempTermDocumentMatrix.containsKey(fileName)){
                    rowTable = tempTermDocumentMatrix.get(fileName);
                } else {
                    rowTable = new Hashtable<String, Integer>();
                }

                rowTable.put(term, val);
                tempTermDocumentMatrix.put(fileName, rowTable);
            }
        }

        Hashtable<String, String[]> termDocumentMatrix =
                new Hashtable<String, String[]>();

        int i=0;

        termDocumentMatrix.put("header", terms);

        // convert table into sparse matrix
        for(Object o : list.toArray()){
            String fileName = o.toString();

            // new array to store the row information
            String[] rowData  = new String[terms.length];

            int j=0;

            for (String term: terms){
                
                if(tempTermDocumentMatrix.containsKey(fileName)){
                    Hashtable rowTable = tempTermDocumentMatrix.get(fileName);
                    if(rowTable.containsKey(term)){
                       rowData[j] = rowTable.get(term).toString(); // value
                    } else {
                       rowData[j] = "0";
                    }
                } else {
                    rowData[j] = "0";
                }

                j++;
            }
            termDocumentMatrix.put(fileName, rowData);
            i++;
        }
        return termDocumentMatrix;
    }
    
    /**
     * Returns an array of document names.  Each document 
     * is a co-occurence between the two terms.
     * A return value of null means there was a problem with the query or Lucene.
     * @param termA
     * @param termB
     * @param filter 
     * @return String[] Documents that contain both terms.
     */
    public Hashtable<String, Integer> getCooccurenceDocNames(String termA, String termB, SearchCriteria criteria){
        try {
            Hashtable<String, Integer> results = this.searchfiles.returnResults(termA, termB, criteria);
            return results;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
        }
        return null;
    }
    
    public void setTerms(String[] terms){
        this.terms = terms;
    }

    public Hashtable<String, Integer> getDocumentList(){
        return this.documentList;
    }
}//end class

package minoe;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.openide.util.Exceptions;

   /**
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
   */

public class SearchFiles {

    private MetaDataController metadata;

    String index = "indexes";
    String field = "contents"; // the field that Lucene searches, i.e. the document contents.
    RAMDirectory ramDir;
    Directory directory;
    IndexReader reader;

    public SearchFiles(MetaDataController mdc) {
        this.metadata = mdc;
        try {
            // try loading index into memory
            directory = FSDirectory.getDirectory(index);
            ramDir = new RAMDirectory(directory);
            reader = IndexReader.open(ramDir);
        } catch (Exception ex) {
            System.out.println("Error loading index: " + ex.toString());
        } catch (java.lang.OutOfMemoryError err){
            // if there isn't memory available, then just read index from disk.
            try {
                directory = FSDirectory.getDirectory(index);
                reader = IndexReader.open(directory);
            } catch (IOException ex) {
                System.out.println("Error loading index: " + err.toString());
            }
        }

    }

    /**
     * Accepts two strings to search within a distance of each other.
     * Strings are divided into query components and matched with each component.
     * 
     * Ex:
     * termA:  "fishing licence"
     * termB:  "lobster trap" crab*
     *
     * Results in:
     * Comparing 
     * "fishing license" and "lobster trap" within distance x
     * and
     * "fishing license" and crab* within distance x
     *
     * Multiphrase query = "foo ba*".  Not supported.
     *
     * Returns all of the documents that match these distance comparisons.
     *
     * @param termA
     * @param termB
     * @param criteria
     * @return
     * @throws org.apache.lucene.index.CorruptIndexException
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    public Hashtable<String, Integer> returnResults(String termA, String termB, SearchCriteria criteria) throws CorruptIndexException, IOException, ParseException{

//      System.out.println(termA + " - " + termB);

      int slop = criteria.getSlop();
      boolean inOrder = false;
      
      Analyzer analyzer = new StandardAnalyzer();

      if (termA == null || termB == null) {
          return null;
      }

      QueryParser queryParser = new QueryParser(field, analyzer);

      Vector<Spans> spansVec = new Vector<Spans>();

      // If both termA and termB are the same we want to run a different type of search...
      if(termA.equalsIgnoreCase(termB)){
          // Divide the term into query components.
          // Can use termA or termB since they're both the same.
          Query query = queryParser.parse(termA);
          ArrayList<SpanQuery> termList = new ArrayList<SpanQuery>();
          this.buildClauses(query, termList, reader);

          for (int i = 0; i < termList.size(); i++) {
              SpanQuery singleSpan = termList.get(i);
              Spans spans;
              if(singleSpan instanceof SpanTermQuery){
                  SpanTermQuery stq = (SpanTermQuery) singleSpan;
                 // Now run the search
                 spans = stq.getSpans(reader);
              }else{
                  SpanNearQuery snq = (SpanNearQuery)singleSpan;
                  // Now run the search
                  spans = snq.getSpans(reader);
              }

              // store the results in a vector of spans
              spansVec.add(spans);
          }
      } else {
          // ...termA and termB are different.
          // Divide the first term into query components.
          Query queryA = queryParser.parse(termA);
          ArrayList<SpanQuery> termAList = new ArrayList<SpanQuery>();
          this.buildClauses(queryA, termAList, reader);

          // Divide the second term into query components.
          Query queryB = queryParser.parse(termB);
          ArrayList<SpanQuery> termBList = new ArrayList<SpanQuery>();
          this.buildClauses(queryB, termBList, reader);

          // Search each query type from each term.
          int termalistsize = termAList.size();
          int termblistsize = termBList.size();
          for (int i = 0; i < termalistsize; i++) {
               SpanQuery aSpan = termAList.get(i);
               for (int j = 0; j < termblistsize; j++) {
                   SpanQuery bSpan = termBList.get(j);

                   // Now run the search
                   SpanNearQuery snq = new SpanNearQuery(new SpanQuery[]{aSpan, bSpan}, slop, inOrder);
                   Spans spans = snq.getSpans(reader);

                   // store the results in a vector of spans
                   spansVec.add(spans);
               }
          }
      }

      // Filename => matches.
      Hashtable<String, Integer> counts = new Hashtable<String, Integer>();

      // Get the term counts (span counts) for all documents.
      for (Spans spans : spansVec) {
          while(spans.next()){
              int id = spans.doc();
              Document doc = reader.document(id);
              String docname = doc.get("file name");
              if(counts.containsKey(docname)){
                 int count = counts.get(docname).intValue();
                 count++;
                 counts.put(docname, count);
              } else{
                 counts.put(docname, 1);
              }
          }
      }

      // The documents matching the search criteria.
      List<String> docList = new ArrayList<String>();

      // If user specified certain documents to search in.
      List<String> criterialist = criteria.getDocumentList();

      // Build a list of documents that the search is limited to.
      if(criterialist.size() > 0){
          // document search
          docList = criterialist;
      } else{
          // metadata search
          docList = this.metadata.getDocumentsBySearchCriteria(criteria);
      }
      // Now filter the documents based upon the criteria.
      Hashtable<String, Integer> results = new Hashtable<String, Integer>();
      Enumeration<String> e = counts.keys();
      while(e.hasMoreElements()){
          String doc = e.nextElement();
          // document search
          if(docList.size() > 0 && docList.contains(doc)){
              // More cleanup before we add to our final output list -
              // Spans keep track of a beginning and end position it appears,
              // so we must divide the total count by 2.
              int finalcount = counts.get(doc);
              if(finalcount > 1){
                  // Note:  This isn't true now?  Keep testing.
                  //finalcount = finalcount / 2;
              }
              results.put(doc, finalcount);
          }
      }

      return results;
    }

    /**
     * Separates a query into spanqueries.
     * @param query
     * @param termList
     * @param reader
     * @return
     */
    public ArrayList buildClauses(Query query, ArrayList<SpanQuery> termList, IndexReader reader){
        try {
            if(query instanceof BooleanQuery){
              // this is a boolean query OR this is a boolean query
                BooleanQuery bq = (BooleanQuery) query;
                BooleanClause[] bclauses = bq.getClauses();
                for (int i = 0; i < bclauses.length; i++){
                    Query childQuery = bclauses[i].getQuery();
                    // Rewrite this clause e.g one* becomes (one OR onerous)
                    childQuery.rewrite(reader);
                    buildClauses(childQuery, termList, reader);
                }
            } else if(query instanceof PhraseQuery){
               // phrasequery ex: "this is a phrase query"
               // convert phrase queries to SpanNearQuery because
               // phrase queries will do the phrase out of order.
               PhraseQuery pq = (PhraseQuery)query;
               Term[] termArr = pq.getTerms();
               SpanTermQuery[] thisSpan = new SpanTermQuery[termArr.length];
               for (int i = 0; i < termArr.length; i++) {
                   Term term = termArr[i];
                   SpanTermQuery termSpan = new SpanTermQuery(term);
                   thisSpan[i] = termSpan;
               }
               SpanNearQuery snq = new SpanNearQuery(thisSpan, 0, true);
               termList.add(snq);
            } else if(query instanceof TermQuery){
               // add to queryList
               TermQuery tq = (TermQuery) query;
               Term term = tq.getTerm();
               SpanQuery stq = new SpanTermQuery(term);
               termList.add(stq);
            } else if(query instanceof WildcardQuery){
                // wildcard query can be like:  "?ild*"
                WildcardQuery wq = (WildcardQuery) query;
                // Rewrite this clause e.g one* becomes (one OR onerous)
                Query q = wq.rewrite(reader);
                buildClauses(q, termList, reader);
            } else if(query instanceof PrefixQuery){
                // prefix query ex:  "fish*"
                PrefixQuery pq = (PrefixQuery) query;
                Query q = pq.rewrite(reader);
                buildClauses(q, termList, reader);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unknown query type: " + ex.toString());
        }
        return termList;
    }

    /**
     * Returns the document names and search scores for a given search string.
     * @param searchString
     * @param criteria
     * @return
     * @throws org.apache.lucene.index.CorruptIndexException
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    public Hashtable<String, Float> returnResults(String searchString, SearchCriteria criteria) throws CorruptIndexException, IOException, ParseException{
      IndexSearcher searcher = new IndexSearcher(reader);
      Analyzer analyzer = new StandardAnalyzer();

      if (searchString == null) {
          return null;
      }

      QueryParser parser = new QueryParser(field, analyzer);
      Query query = parser.parse(searchString);

      // Search the collection
      CustomHitCollector collector = new CustomHitCollector(searcher, CustomHitCollector.ID_TYPE);
      searcher.search(query, collector);

      // Output each file that the term was found in.
      Hashtable<String, Float> counts = collector.getDocumentsList();

      List<String> docList = new ArrayList<String>();
      List<String> criterialist = criteria.getDocumentList();

      // Build a list of documents that the search is limited to.
      if(criterialist.size() > 0){
          // document search
          docList = criterialist;
      } else{
          // metadata search
          docList = this.metadata.getDocumentsBySearchCriteria(criteria);
      }

      // Now filter the documents based upon the criteria.
      Hashtable<String, Float> results = new Hashtable<String, Float>();
      Enumeration<String> e = counts.keys();
      while(e.hasMoreElements()){
          String doc = e.nextElement();
          // document search
          if(docList.size() > 0 && docList.contains(doc)){
              results.put(doc, counts.get(doc));
          }
      }

      return results;
    }

    /**
     * Returns the absolute path that the file was indexed with.
     * Used for opening the contents of the file.
     * @param inFileName
     * @return
     * @throws org.apache.lucene.index.CorruptIndexException
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    public String getPath(String inFileName) throws CorruptIndexException, IOException{

        IndexSearcher searcher = new IndexSearcher(reader);

        String retVal = null;

        Term t = new Term("file name", inFileName);
        Query query = new TermQuery(t);
        CustomHitCollector collector = new CustomHitCollector(searcher, CustomHitCollector.PATH_TYPE);
        searcher.search(query, collector);

        Hashtable<String, Float> results = collector.getDocumentsList();
        Enumeration<String> keys = results.keys();
        while(keys.hasMoreElements()){
            String path = keys.nextElement();
            return path;
        }

        return retVal;
    }

    /**
     * Returns all of the file names in the index.
     * @return
     */
    public Vector<String> getAllFileNames() throws CorruptIndexException, IOException{
        Vector<String> files = null;

        int numdocs = this.reader.numDocs();

        files = new Vector<String>();

        for (int i = 0; i < numdocs; i++) {
           Document doc = this.reader.document(i);
           String thisFileName = doc.get("file name");
           files.add(thisFileName);
        }

        return files;
    }

    class CustomHitCollector extends HitCollector{

        private IndexSearcher searcher;
        // list of documents and search scores
        private Hashtable<String, Float> documentsList = new Hashtable<String, Float>();
        // count of hits per document
        private Hashtable<String, Integer> documentHits = new Hashtable<String, Integer>();
        public static final String PATH_TYPE = "path";
        public static final String ID_TYPE = "id";
        private String type = ID_TYPE; // default

        public CustomHitCollector(IndexSearcher searcher, String type){
            this.searcher = searcher;
            this.type = type;
        }

        @Override
        public void collect(int doc, float score) {
            Document document;
            try {
                document = searcher.doc(doc);
                String fileName = null;
                if(this.type.equalsIgnoreCase(PATH_TYPE)){
                   fileName = document.get("path");
                   String separator = "\\" + java.io.File.separator;
                   fileName = fileName.replaceAll("::", separator);
                } else{
                   fileName = document.get("file name");
                }
                // track score per document
                this.documentsList.put(fileName, score);
                // track hits per document.
                if(this.documentHits.containsKey(fileName)){
                   int count = this.documentHits.get(fileName);
                   count++;
                   this.documentHits.put(fileName, count);
                } else{
                    this.documentHits.put(fileName, 1);
                }
            } catch (CorruptIndexException ex) {
            } catch (IOException ex) {
            }
        }

        public Hashtable<String, Float> getDocumentsList(){
            return this.documentsList;
        }
        public Hashtable<String, Integer> getDocumentHits(){
            return this.documentHits;
        }
    }//end class CustomHitCollector

}//end class

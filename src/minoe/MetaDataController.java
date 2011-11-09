package minoe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Controls the meta data information for the document collection.
 * Loads this information upon program execution and can add information
 * to the meta data file during program runtime.
 *
 * Performs an integrity check on the meta data.  Warns on invalid column titles,
 * errors on duplicate or missing file names, and duplicate document titles.
 * Names and titles must be unique.
 *
 * @author Daniel Spiteri
 */
public class MetaDataController {
    
    public Vector<String> fileID;                           // file names
    public Hashtable<String, String> documentTitles;        // file name => document title
    public Hashtable<String, String> documentDescription;   // file name => document description
    public Hashtable<String, String> fileAgencies;          // agencies for each file, pipe-delimited.
    public Hashtable<String, String> retrievalDates;
    public Hashtable<String, String> retrievalSources;
    public Hashtable<String, String> retrievedBys;
    public Hashtable<String, String> documentYears;
    public Hashtable<String, String> documentLocations;
    public Hashtable<String, String> documentTypes;
    public Hashtable<String, String> documentLabels;
    public Hashtable<String, String> collectionNames;       // the collection that this document belongs to

    public Vector<String> collections; // all of the unique collection names

    public Hashtable<String, String> agencyInformation; // abbreviation => description

    public String[] columnTitles = {"File ID", "Document Title", "Document Description",
                                    "Agencies (pipe-separated)", "Retrieval Date", "Retrieval Source",
                                    "Retrieved By", "Collection Name", "Year",
                                    "Location", "Type", "Labels"};
    public String[] agencyColumnTitles = {"Agency ID", "Description"};

    Hashtable<String, Hashtable> columnData; // column name => data

    public MetaDataController(){
        this.columnData = new Hashtable<String, Hashtable>();
    }

    /**
     * Returns a List of String[] arrays containing the file data.
     * @param inFile
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public List<String[]> getFileData(File inFile) throws FileNotFoundException, IOException{
        CSVReader reader = new CSVReader(new FileReader(inFile));
        String[] data;
        List<String[]> entries = new ArrayList<String[]>();
        while((data = reader.readNext()) != null){
            // ignore columns beyond the specified column titles
            String[] values = new String[columnTitles.length];
            for (int i = 0; i < columnTitles.length; i++) {
                values[i] = data[i];
            }
            entries.add(values);
        }
        reader.close();
        return entries;
    }

        /**
     * Returns a List of String[] arrays containing the file data.
     * @param inFile
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public List<String[]> getAgencyFileData(File inFile) throws FileNotFoundException, IOException{
        CSVReader reader = new CSVReader(new FileReader(inFile));
        String[] data;
        List<String[]> entries = new ArrayList<String[]>();
        while((data = reader.readNext()) != null){
            // ignore columns beyond the specified column titles
            String[] values = new String[this.agencyColumnTitles.length];
            for (int i = 0; i < this.agencyColumnTitles.length; i++) {
                values[i] = data[i];
            }
            entries.add(values);
        }
        reader.close();
        return entries;
    }

    /**
     * Examines a meta data file for accuracy; correct column names and so forth.
     * Warns on invalid column titles,
     * errors on duplicate or missing file names and duplicate document titles.
     * Names and titles must be unique.
     * @return Warning message.  Null if no warnings.
     */
    public String integrityCheck(File file) throws FileNotFoundException, IOException{
        CSVReader reader = new CSVReader(new FileReader(file));
        String [] line_data; // array of values from the line

        int line_num=1;
        StringBuffer sb = new StringBuffer();
        Vector<String> uniqueFileNames = new Vector<String>();
        Vector<String> uniqueTitles = new Vector<String>();
        int dupenames = 0;
        int dupetitles = 0;
        boolean blanks_found = false;
        while ((line_data = reader.readNext()) != null) {
           if(line_num == 1){
               // check column titles
               for (int i = 0; i < columnTitles.length; i++) {
                   if(line_data.length < i){
                      sb.append("Missing " + this.columnTitles[i] + " column.");
                      sb.append("\n");
                   } else if(line_data[i].equals(this.columnTitles[i]) == false){
//                      sb.append("Expected: " + this.columnTitles[i] + ", Found: " + line_data[i]);
//                      sb.append("\n");
                   }
               }
           } else {
               // check for missing/invalid data
               String f = line_data[0];
               if(uniqueFileNames.contains(f)){
                   dupenames++;
               } else {
                   uniqueFileNames.add(f);
               }
               String t = line_data[1];
               if(uniqueTitles.contains(t)){
                   dupetitles++;
               } else {
                   uniqueTitles.add(t);
               }
               String year = line_data[9];
               String loc = line_data[10];
               String type = line_data[11];
               if(year == null || year.length() == 0){
                  blanks_found = true;
               }
           }
           line_num++;
        }

        if(dupenames > 0 || dupetitles > 0){
           sb.append(dupenames + " duplicate file names found, " + dupetitles + " duplicate titles found.");
        }
        if(blanks_found == true){
           sb.append("\nYear must be specified.");
        }

        return sb.toString();
    }

    /**
     * Does an integrity check on a table of data.
     * @param data
     * @return
     */
    public String integrityCheck(Vector<Vector> data, boolean offset) {
        StringBuffer sb = new StringBuffer();
        Vector<String> uniqueFileNames = new Vector<String>();
        Vector<String> uniqueTitles = new Vector<String>();
        int dupenames = 0;
        int dupetitles = 0;
        boolean blanks_found = false;

        int filenamecol = 0;
        int titlecol = 1;
        if(offset == true){
            filenamecol++;
            titlecol++;
        }

        for (Vector<String> lineData : data) {
           String f = lineData.get(filenamecol);
           if(uniqueFileNames.contains(f)){
               dupenames++;
           } else {
               uniqueFileNames.add(f);
           }
           String t = lineData.get(titlecol);
           if(uniqueTitles.contains(t)){
               dupetitles++;
           } else {
               uniqueTitles.add(t);
           }
           String year = lineData.get(9);
           if(year == null || year.length() == 0){
              blanks_found = true;
           }
        }

        if(dupenames > 0 || dupetitles > 0){
           sb.append("\n" + dupenames + " duplicate file names found, " + dupetitles + " duplicate titles found.");
        }
        if(blanks_found == true){
           sb.append("\nYear must be specified.");
        }

        return sb.toString();
    }

    /**
     * Returns a boolean table containing position of duplicate elements (for certain columns).
     * @param data
     * @param offset
     * @return
     */
    public Vector<Vector> getIntegrityDuplicates(Vector<Vector> data, boolean offset){
        Vector<String> uniqueFileNames = new Vector<String>();
        Vector<String> uniqueTitles = new Vector<String>();

        int rows = data.size();
        int cols = data.get(0).size();
        Vector<Vector> duplicateTable = new Vector<Vector>(rows);

        //initialize table to false
        for (int i = 0; i < rows; i++) {
            Vector<Boolean> rowValues = new Vector<Boolean>(cols);
            for (int j = 0; j < cols; j++) {
                rowValues.add(new Boolean(false));
            }
            duplicateTable.add(rowValues);
        }

        int filenamecol = 0;
        int titlecol = 1;
        if(offset == true){
            filenamecol++;
            titlecol++;
        }

        // keep track of duplicate cells
        for (int row = 0; row < data.size(); row++) {
            Vector<String> lineData = data.get(row);
            String f = lineData.get(filenamecol);
            if(uniqueFileNames.contains(f)){
                Vector<Boolean> rowVec = duplicateTable.get(row);
                Boolean flag = new Boolean(true);
                rowVec.set(1, flag);
            } else {
                uniqueFileNames.add(f);
            }
            String t = lineData.get(titlecol);
            if(uniqueTitles.contains(t)){
                Vector<Boolean> rowVec = duplicateTable.get(row);
                Boolean flag = new Boolean(true);
                rowVec.set(2, flag);
            } else {
                uniqueTitles.add(t);
            }

        }
        return duplicateTable;

    }

    /**
     * Loads all of the file meta data.
     * Line 1:  Header.
     * Line 2 and up contains data, comma-separated (can be enclosed in quotes).
     * Format:
     *   Column A:  File ID
     *   Column B:  Document Title
     *   Column C:  Document Description
     *   Column D:  Agencies as a pipe-separated list
     *   Column E:  Retrieval date
     *   Column F:  Retrieval source
     *   Column G:  Retrieved by
     *   Column H:  Collection that this file belongs to
     *   Column I:  Year
     *   Column J:  Location
     *   Column K:  Document Type
     *   Column L:  Additional Labels (pipe-separated)
     * @param file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @return Warnings.
     */
    public String loadDataFile(File file) throws FileNotFoundException, IOException{

        // Check for errors before loading
        String warnings = this.integrityCheck(file);

        int len = (int) file.length();
       
        this.fileID                = new Vector<String>(len);
        this.documentTitles        = new Hashtable<String, String>(len);
        this.documentDescription   = new Hashtable<String, String>(len);
        this.fileAgencies          = new Hashtable<String, String>(len);
        this.retrievalDates        = new Hashtable<String, String>(len);
        this.retrievalSources      = new Hashtable<String, String>(len);
        this.retrievedBys          = new Hashtable<String, String>(len);
        this.documentYears         = new Hashtable<String, String>(len);
        this.documentLocations     = new Hashtable<String, String>(len);
        this.documentTypes         = new Hashtable<String, String>(len);
        this.documentLabels        = new Hashtable<String, String>(len);
        this.collectionNames       = new Hashtable<String, String>(len);
        this.collections           = new Vector<String>();

       CSVReader reader = new CSVReader(new FileReader(file));
       String [] lineData; // array of values from the current line in the file

       int lineNum=1;
       boolean errorEncountered = false;
       while ((lineData = reader.readNext()) != null && errorEncountered == false) {
           if(lineData.length > 0 && lineNum > 1){
               try{
                   String fN          = lineData[0].replace("\"", "");
                   String fileName    = fN.trim();

                   this.fileID.add(fileName);
                   this.documentTitles.put(fileName,       lineData[1].replace("\"", ""));
                   this.documentDescription.put(fileName,  lineData[2].replace("\"", ""));
                   this.fileAgencies.put(fileName,         lineData[3].replace("\"", ""));
                   this.retrievalDates.put(fileName,       lineData[4].replace("\"", ""));
                   this.retrievalSources.put(fileName,     lineData[5].replace("\"", ""));
                   this.retrievedBys.put(fileName,         lineData[6].replace("\"", ""));
                   String collectionName =                 lineData[7].replace("\"", "");
                   this.collectionNames.put(fileName,      collectionName);
                   this.documentYears.put(fileName,        lineData[8].replace("\"", ""));
                   this.documentLocations.put(fileName,    lineData[9].replace("\"", ""));
                   this.documentTypes.put(fileName,        lineData[10].replace("\"", ""));
                   if(lineData.length > 11){
                       this.documentLabels.put(fileName,       lineData[11].replace("\"", ""));
                   }
                   if(!this.collections.contains(collectionName)){
                       this.collections.add(collectionName);
                   }
               } catch(Exception ex){
                   errorEncountered = true;
                   JOptionPane.showMessageDialog(null, "Error loading meta data file information.  " +
                           "Check " + file.getName() + " for invalid formatting on line " + lineNum + ".");
               }
           }
           
           lineNum++;
       }
       reader.close();


       if(warnings != null && warnings.length() > 0){
           return warnings;
       }

       return null;
    
    }//end method loadData
    

    /**
     * Loads in a list of all the agencies and their acronyms.
     * Acronym saved as upper case.
     * @param file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void loadAgenciesFile(File file) throws FileNotFoundException, IOException{
       int len = (int) file.length();
       this.agencyInformation = new Hashtable<String, String>(len);

       CSVReader reader = new CSVReader(new FileReader(file));
       String [] lineData; // array of values from the current line in the file
       boolean errorEncountered = false;
       while ((lineData = reader.readNext()) != null && errorEncountered == false) {
           String acronym     = lineData[0].replace("\"", "").trim().toUpperCase();  // DOE, CDF, etc...
           String description = lineData[1].replace("\"", "").trim();

           this.agencyInformation.put(acronym, description);
       }
       reader.close();
    }

    /**
     * Returns the description of an agency given its location and acronym.
     * @param agency
     * @return
     */
    public String getAgencyDescription(String agency){
        String a = agency.toUpperCase();
        if(this.agencyInformation != null){
            if(this.agencyInformation.containsKey(a)){
                return this.agencyInformation.get(a);
            }
        }
        return "";
    }

    /**
     * Returns an array of the agencies for a particular file, given the file name.
     * @param fileName
     * @return
     */
    public String[] getFileAgencies(String fileName){
        String[] agencyList = null;
        if(this.fileAgencies != null){
            if(this.fileAgencies.containsKey(fileName)){
                String pipedList =  this.fileAgencies.get(fileName);
                agencyList = pipedList.split("\\|");
            }
        }
        return agencyList;
    }

    public String descriptionToFileName(String desc){
        if(this.documentDescription.containsValue(desc)){
            return null;
        }
        return null;
    }

    public String getFileTitle(String fileName){
        if(this.documentTitles.containsKey(fileName)){
            String title = this.documentTitles.get(fileName);
            return title;
        } else{
            return fileName;
        }
    }

    public String getFileDescription(String fileName){
        if(this.documentDescription.containsKey(fileName)){
            String desc = this.documentDescription.get(fileName);
            return desc;
        } else{
            return fileName;
        }
    }

    /**
     * Returns information based upon the document name.
     * Pass in a hash table from this class when calling this method.
     * @param fileName
     * @param ht
     * @return
     */
    public String getInfoFromFileName(String fileName, Hashtable ht){
        return (String) ht.get(fileName);
    }

    /**
     * Loads the user's meta data file into memory (globals).
     * @param sourceFile
     * @param newCollectionName
     * @param clearDatabase
     * @throws java.io.IOException
     */
    public void addUserMetaData(File sourceFile, String newCollectionName, boolean clearDatabase) throws IOException{
       // clear out the existing meta data if specified
       if(clearDatabase){
           int len = (int) sourceFile.length();
           this.fileID                = new Vector<String>(len);
           this.documentTitles        = new Hashtable<String, String>(len);
           this.documentDescription   = new Hashtable<String, String>(len);
           this.fileAgencies          = new Hashtable<String, String>(len);
           this.retrievalDates        = new Hashtable<String, String>(len);
           this.retrievalSources      = new Hashtable<String, String>(len);
           this.retrievedBys          = new Hashtable<String, String>(len);
           this.documentYears         = new Hashtable<String, String>(len);
           this.documentLocations     = new Hashtable<String, String>(len);
           this.documentTypes         = new Hashtable<String, String>(len);
           this.documentLabels        = new Hashtable<String, String>(len);
           this.collectionNames       = new Hashtable<String, String>(len);
           this.collections           = new Vector<String>();
       }
       int lineNum=1;
       boolean errorEncountered = false;
       Vector<String> skippedFiles = new Vector<String>();

       CSVReader reader = new CSVReader(new FileReader(sourceFile));
       String [] lineData; // array of values from the line
       
       while ((lineData = reader.readNext()) != null && errorEncountered == false) {
           if(lineData.length > 0 && lineNum > 1){
               try{
                   String fN          = lineData[0].replace("\"", "");
                   String fileName    = fN.trim();

                   // Check if this file id already exists, if not then load.
                   if(!this.fileID.contains(fileName)){
                       this.fileID.add(fileName);
                       this.documentTitles.put(fileName,       lineData[1].replace("\"", ""));
                       this.documentDescription.put(fileName,  lineData[2].replace("\"", ""));
                       this.fileAgencies.put(fileName,         lineData[3].replace("\"", ""));
                       this.retrievalDates.put(fileName,       lineData[4].replace("\"", ""));
                       this.retrievalSources.put(fileName,     lineData[5].replace("\"", ""));
                       this.retrievedBys.put(fileName,         lineData[6].replace("\"", ""));
                       this.collectionNames.put(fileName,      newCollectionName);
                       this.documentYears.put(fileName,        lineData[7].replace("\"", ""));
                       this.documentLocations.put(fileName,    lineData[8].replace("\"", ""));
                       this.documentTypes.put(fileName,        lineData[9].replace("\"", ""));
                       if(lineData.length < 11){
                           this.documentLabels.put(fileName,"");
                       } else{
                           this.documentLabels.put(fileName,        lineData[10].replace("\"", ""));
                       }

                       if(!this.collections.contains(newCollectionName)){
                           this.collections.add(newCollectionName);
                       }
                   } else{
                       skippedFiles.add(fileName);
                   }
               } catch(Exception ex){
                   errorEncountered = true;
                   JOptionPane.showMessageDialog(null, "Error saving user meta data file information.  " +
                           "Check " + sourceFile.getName() + " for invalid formatting on line " + lineNum + ".");
               }
           }
           lineNum++;
       }
       reader.close();
       
       if(skippedFiles.size() > 0){
           String msg = "Duplicate meta data information provided for " + skippedFiles.size() + " files, ";
           msg += "use the meta data editor to correct.";
           JOptionPane.showMessageDialog(null, msg);
       }
    }

    /**
     * Saves the meta data information in memory to the meta data file.
     * @param sourceFile
     * @param destFile 
     * @param newCollectionName
     * @throws IOException
     */
    public void saveMetaDataFile(File destFile) throws IOException{
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter(destFile));
            // Header Row
            for (String title : this.columnTitles) {
                outputStream.write(csvPrep(title));
            }
            outputStream.newLine();
            // data
            for (String fileName : this.fileID) {
                outputStream.write(csvPrep(fileName));
                outputStream.write(csvPrep(this.documentTitles.get(fileName)));
                outputStream.write(csvPrep(this.documentDescription.get(fileName)));
                outputStream.write(csvPrep(this.fileAgencies.get(fileName)));
                outputStream.write(csvPrep(this.retrievalDates.get(fileName)));
                outputStream.write(csvPrep(this.retrievalSources.get(fileName)));
                outputStream.write(csvPrep(this.retrievedBys.get(fileName)));
                outputStream.write(csvPrep(this.collectionNames.get(fileName)));
                outputStream.write(csvPrep(this.documentYears.get(fileName)));
                outputStream.write(csvPrep(this.documentLocations.get(fileName)));
                outputStream.write(csvPrep(this.documentTypes.get(fileName)));
                outputStream.write(csvPrep(this.documentLabels.get(fileName)));
                outputStream.newLine();
            }
        } catch(IOException ex){
            JOptionPane.showMessageDialog(null, "Error saving meta data.");
        }
        finally {
          outputStream.close();
        }
    }
    
    /**
     * Writes the information passed in the data vector to the file.
     * Overwrites any existing information.
     * First row in the vector should be the header.
     * @param data
     * @throws java.io.IOException
     */
    public boolean writeToMetaDataFile(Vector<Vector> data, File destFile) throws IOException{
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter(destFile));
            for (Vector<String> v : data) {
                for (String s : v) {
                    outputStream.write(csvPrep(s));
                }
                outputStream.newLine();
            }
        } catch(IOException ex){
            JOptionPane.showMessageDialog(null, "Error saving meta data: " + ex.toString());
            return false;
        }
        finally {
          outputStream.close();
          return true;
        }
    }

        /**
     * Writes the information passed in the data vector to the file.
     * Overwrites any existing information.
     * First row in the vector should be the header.
     * @param data
     * @throws java.io.IOException
     */
    public boolean writeToAgencyFile(Vector<Vector> data, File destFile) throws IOException{
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter(destFile));
            for (Vector<String> v : data) {
                for (String s : v) {
                    outputStream.write(csvPrep(s));
                }
                outputStream.newLine();
            }
        } catch(IOException ex){
            JOptionPane.showMessageDialog(null, "Error saving agency data: " + ex.toString());
            return false;
        }
        finally {
          outputStream.close();
          return true;
        }
    }

    /**
     * Wraps a string in quotes and appends a comma.
     * @param s
     * @return
     */
    public String csvPrep(String s){
        return '"' + s + '"' + ',';
    }

    /**
     * Returns a sorted list of all the distinct years
     * in the documentYears field.  For use in the filter GUI.
     * @return
     */
    public List<String> getDistinctDocumentYears(){
        List<String> list = new ArrayList<String>();
        Enumeration e = this.documentYears.keys();
        while(e.hasMoreElements()){
            String key = e.nextElement().toString();
            String value = this.documentYears.get(key);
            if(!list.contains(value)){
               list.add(value);
            }
        }
        // Sort and return.
        Collections.sort(list);
        return list;
    }

    /**
     *
     * @param attribute
     * @param value
     * @return
     */
    public List<String> getDocumentsByAttribute(String attribute, String value){
        List<String> list = null;
        Hashtable<String,String> docTable = null;

        if(attribute.equalsIgnoreCase("year")){
            docTable = this.documentYears;
        } else if(attribute.equalsIgnoreCase("location")){
            docTable = this.documentLocations;
        }else if(attribute.equalsIgnoreCase("type")){
            docTable = this.documentTypes;
        }else if(attribute.equalsIgnoreCase("other")){
            docTable = this.documentLabels;
        }

        if(value != null){
            list = new ArrayList<String>();
            // Get a list of documents with a certain attribute.
            Enumeration e = docTable.keys();
            while(e.hasMoreElements()){
                String key = e.nextElement().toString(); // file name
                String val = docTable.get(key); // year, location, type, label.
                // other labels are separated by pipe
                if(attribute.equalsIgnoreCase("other")){
                   String[] labelsToFind = value.split("\\|");
                   String[] labelsToSearch = val.split("\\|");
                    for (String label : labelsToFind) {
                        for (String s : labelsToSearch) {
                            if(label.equalsIgnoreCase(s)){
                               list.add(key);
                            }
                        }
                    }
                }else if(value.equalsIgnoreCase(val)){
                    list.add(key);
                }
            }
        }

        return list;
    }
    
    /**
     * Returns a sorted list of all the distinct document locations
     * in the documentLocations field.  If a year is specified, only
     * documents pertaining to that year are returned.
     * For use in the filter GUI.
     * @param year
     * @return
     */
    public List<String> getDistinctDocumentLocations(String year){
        List<String> docList = null;
        if(year != null){
            docList = getDocumentsByAttribute("year", year);
        }

        List<String> list = new ArrayList<String>(); // return list

        Enumeration e = this.documentLocations.keys();
        while(e.hasMoreElements()){
            String key = e.nextElement().toString(); // file name
            String value = this.documentLocations.get(key); // location
            // If user didn't specify a year, then don't check the
            // docList before adding to the distinct list.
            if(docList == null){
               if(!list.contains(value)){
                    list.add(value);
                }
            } else if(docList.contains(key)){
                if(!list.contains(value)){
                   list.add(value);
                }
            }
        }
        // Sort and return.
        Collections.sort(list);
        return list;
    }

    /**
     * Returns a sorted list of all the distinct document types
     * in the documentTypes field.  If a year and/or location are specified,
     * then documents pertaining to that year and/or location are returned.
     * For use in the filter GUI.
     * @param year
     * @param location
     * @return
     */
    public List<String> getDistinctDocumentTypes(String year, String location){
        List<String> yearList = null;
        List<String> locationList = null;
        if(year != null){
            yearList = getDocumentsByAttribute("year", year);
        }
        if(location != null){
            locationList = getDocumentsByAttribute("location", location);
        }

        List<String> list = new ArrayList<String>(); // return list

        Enumeration e = this.documentTypes.keys();
        while(e.hasMoreElements()){
            String key = e.nextElement().toString(); // file name
            String value = this.documentTypes.get(key); // location
            // If user didn't specify a year and location, then don't check
            // before adding to the distinct list.
            if(yearList == null && locationList == null){
               if(!list.contains(value)){
                    list.add(value);
                }
            } else if(yearList != null && locationList == null){
                if(yearList.contains(key)){
                    if(!list.contains(value)){
                       list.add(value);
                    }
                }
            } else if(yearList != null && locationList != null){
                if(yearList.contains(key) && locationList.contains(key)){
                    if(!list.contains(value)){
                       list.add(value);
                    }
                }
            }
        }

        // Sort and return.
        Collections.sort(list);
        return list;
    }

    /**
     * Returns a sorted list of all the distinct document other labels
     * in the documentLabels field.  If a year and/or location and/or type are specified,
     * then documents pertaining to that year and/or location and/or type are returned.
     * For use in the FilterPanel GUI.
     * @param year
     * @param location
     * @param docType
     * @return
     */
    public List<String> getDistinctOtherLabels(String year, String location, String docType){
        List<String> yearList = null;
        List<String> locationList = null;
        List<String> docTypeList = null;
        if(year != null){
            yearList = getDocumentsByAttribute("year", year);
        }
        if(location != null){
            locationList = getDocumentsByAttribute("location", location);
        }
        if(docType != null){
            docTypeList = getDocumentsByAttribute("type", docType);
        }

        List<String> list = new ArrayList<String>(); // return list

        Enumeration e = this.documentLabels.keys();
        while(e.hasMoreElements()){
            String key = e.nextElement().toString(); // file name
            String labels = this.documentLabels.get(key);
            String[] labelValues = labels.split("\\|");
            // If user didn't specify a year and location and type, then don't check
            // before adding to the distinct list.
            if(yearList == null && locationList == null && docTypeList == null){
               for (String label : labelValues) {
                   if(!list.contains(label) && !label.isEmpty()){
                       list.add(label);
                   }
               }
            } else if(yearList != null && locationList == null && docTypeList == null){
                if(yearList.contains(key)){
                    for (String label : labelValues) {
                       if(!list.contains(label) && !label.isEmpty()){
                           list.add(label);
                       }
                    }
                }
            } else if(yearList != null && locationList != null && docTypeList == null){
                if(yearList.contains(key) && locationList.contains(key)){
                    for (String label : labelValues) {
                       if(!list.contains(label) && !label.isEmpty()){
                           list.add(label);
                       }
                    }
                }
            } else if(yearList != null && locationList != null && docTypeList != null){
                if(yearList.contains(key) && locationList.contains(key) && docTypeList.contains(key)){
                    for (String label : labelValues) {
                       if(!list.contains(label) && !label.isEmpty()){
                           list.add(label);
                       }
                    }
                }
            }
        }

        // Sort and return.
        Collections.sort(list);
        return list;
    }

    /**
     * Returns a list of documents given the criteria passed in.
     * @param criteria
     * @return
     */
    public List<String> getDocumentsBySearchCriteria(SearchCriteria criteria){
          List<String> docList = new ArrayList<String>();  // list to return

          Vector<String> years = criteria.getYears();
          List<String> thisList = new ArrayList<String>();
          for (String year : years) {
              List<String> l = getDocumentsByAttribute("year", year);
              thisList.addAll(l);
          }
          docList.addAll(thisList);

          thisList = new ArrayList<String>();
          Vector<String> types = criteria.getTypes();
          for (String type : types) {
              List<String> l = getDocumentsByAttribute("type", type);
              thisList.addAll(l);
          }
          if(thisList.size() > 0){
              docList.retainAll(thisList);
          }

          thisList = new ArrayList<String>();
          Vector<String> locations = criteria.getLocations();
          for (String location : locations) {
              List<String> l = getDocumentsByAttribute("location", location);
              thisList.addAll(l);
          }
          if(thisList.size() > 0){
              docList.retainAll(thisList);
          }

          thisList = new ArrayList<String>();
          Vector<String> labels = criteria.getLabels();
          for (String label : labels) {
              List<String> l = getDocumentsByAttribute("other", label);
              thisList.addAll(l);
          }
          if(thisList.size() > 0){
              docList.retainAll(thisList);
          }
          return docList;
    }

    /**
     * Returns all of the documents in the meta data file.
     * @return
     */
    public List<String> getDocuments(){
        List<String> docList = new ArrayList<String>();
        for (String fileName : this.fileID) {
            docList.add(fileName);
        }
        return docList;
    }

    /**
     * Returns a table of document file names and titles.  If a document list
     * is passed in, then the files in that list are used.  Otherwise, all
     * files are used.
     * @param doclist
     * @return
     */
    public Hashtable<String, String> getTitlesAndDocuments(List<String> doclist){
        Hashtable<String, String> result = new Hashtable<String, String>();
        List<String> docs = null;
        if(doclist != null){
            docs = doclist;
        } else {
            docs = getDocuments();
        }
        for (String doc : docs) {
            String desc = this.documentTitles.get(doc);
            result.put(desc, doc);
        }
        return result;
    }

}

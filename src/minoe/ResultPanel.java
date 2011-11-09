package minoe;

import java.io.FileNotFoundException;
import minoe.ResultsFrame.CustomListTableModel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import minoe.ResultsFrame.CustomTableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.openide.util.Exceptions;


/**
 * Displays the results table for each jurisdiction.
 * Called by ResultsFrame and resides in a tabbed pane with other
 * ResultPanel components.
 * @author  Daniel Spiteri
 */
public class ResultPanel extends javax.swing.JPanel {

    public CustomTableModel tableModel; // data stored in the table
    public CustomListTableModel listTableModel; // for the list view
    public Hashtable<String, Integer> documentList;  // a list of all documents in the table
    public Hashtable<String, String[]> termDocumentMatrix;
    
    public float[][] linkagesModel;  // user-specified linkages on screen 2
    
    public JTable table;
    float linkagescount;
    float gaps;
    float gValue;
    float JaccardValue;
    float QAP;
    float pGValue;
    float pJValue;
    float pQValue;

    public SearchCriteria criteria;

    public ResultsFrame resultsFrame; // pointer for referencing ResultsFrame vars.
    
    /** Creates new form resultPanel */
    public ResultPanel(ResultsFrame rf) {
        this.resultsFrame = rf;
        initComponents();
        hidePLabels();
    }
    
    public ResultPanel(CustomTableModel dtm, JTable table, ResultsFrame rf) {
        initComponents();
        this.tableModel = dtm;
        this.table = table;
        this.resultsFrame = rf;
        hidePLabels();
    }

    public void setSearchCriteria(SearchCriteria sc){
        this.criteria = sc;
    }

    public SearchCriteria getSearchCriteria(){
        return this.criteria;
    }

    public void setDocumentList(Hashtable<String, Integer> doclist){
        this.documentList = doclist;
    }

    public Hashtable<String, Integer> getDocumentList(){
        return this.documentList;
    }

    /**
     * Hides all the labels with p values.
     */
    public void hidePLabels(){
        this.pGLabel.setVisible(false);
        this.pJLabel.setVisible(false);
        this.pQLabel.setVisible(false);
    }

    /**
     * Exports the table to an excel spreadsheet.
     * Colors the cells as gaps and linkages.
     */
    public void saveAsExcel() {
        int LIST_TYPE = 1;
        int DEFAULT = 0;
        int outtype = DEFAULT;
        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(this);
        String basefilename = fc.getSelectedFile().getAbsolutePath();
        String filename = basefilename;

        File f = new File(basefilename);
        if(f.exists() == false){
            filename = basefilename + ".xls";
        }

        String[][] tabledata = null;
        float[][] linkages_data = null;
        if(this.tableModel == null){
           tabledata = this.getTableContents(this.listTableModel);
           outtype = LIST_TYPE;
           linkages_data = this.resultsFrame.listLinkages;
        }else{
           tabledata = this.getTableContents(this.tableModel);
           linkages_data = this.linkagesModel;
        }

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("---");
        
        // first create header row
        if(outtype != LIST_TYPE){
            for (int r = 0; r < tabledata.length; r++) {
                String[] strings = tabledata[r];
                for (int c = 0; c < 1; c++) {
                    cell = row.createCell(r+1);
                    cell.setCellValue(strings[c]);
                }
            }
        }

        // fill in the rest of the table
        for (int i = 0; i < tabledata.length; i++) {
            String[] strings = tabledata[i];
            if(strings == null || strings.length == 0){
                continue;
            }
            if(outtype == LIST_TYPE){
                row = sheet.createRow(i);
            } else {
                row = sheet.createRow(i+1);
            }
            for (int j = 0; j < strings.length; j++) {

                if(strings[j] == null){
                    continue;
                }

                CellStyle style = wb.createCellStyle();
                cell = row.createCell(j);

                // Make sure cell contains number values before converting to int.
                String pattern = "[^0-9]";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(strings[j]);

                if(m.find()){
                    cell.setCellValue(strings[j]);
                } else {
                    int cell_value = Integer.parseInt(strings[j]);
                    if(cell_value == 0 && linkages_data[i][j] > 0){
                        // gaps
                        style.setFillForegroundColor(IndexedColors.RED.getIndex());
                        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    } else if(cell_value > 0  && linkages_data[i][j] > 0) {
                        // linkages
                        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
                        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    }
                    if(i == j && outtype != LIST_TYPE){
                        // same terms
                        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    }
                    cell.setCellValue(cell_value);
                }
                
                cell.setCellStyle(style);
            }
        }

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filename);
            wb.write(fileOut);
            fileOut.close();
            JOptionPane.showMessageDialog(this,
                    "Results saved to " + filename,
                    "File Saved",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } 
    }

    /**
     * Saves the data in the table to file.
     */
    public void saveData(){
        FileWriter fstream = null;
        try {
            int LIST_TYPE = 1;
            int DEFAULT = 0;
            int outtype = DEFAULT;
            JFileChooser fc = new JFileChooser();
            fc.showSaveDialog(this);
            String basefilename = fc.getSelectedFile().getAbsolutePath();
            String filename = basefilename;

            String[][] tabledata = null;
            if(this.tableModel == null){
               tabledata = this.getTableContents(this.listTableModel);
               outtype = LIST_TYPE;
            }else{
               tabledata = this.getTableContents(this.tableModel);
            }
            String outString = this.buildCSV(tabledata, outtype);
            File f = new File(basefilename);
            if(f.exists() == false){
                filename = basefilename + ".csv";
            }
            fstream = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fstream);

            bw.write(outString);
            bw.close();
            JOptionPane.showMessageDialog(this,
                    "Results saved to " + filename,
                    "File Saved",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex){

            }
        }
    }

    // Save the term document matrix to file
    public void saveTDM(){
        FileWriter fstream = null;
        try {
            int DEFAULT = 0;
            int outtype = DEFAULT;
            JFileChooser fc = new JFileChooser();
            fc.showSaveDialog(this);
            String basefilename = fc.getSelectedFile().getAbsolutePath();
            String outfilename = basefilename;


            StringBuffer rows = new StringBuffer();
            StringBuffer headers = new StringBuffer();

            headers.append("---,");

            String[] headerValues = this.termDocumentMatrix.get("header");
            for(String t : headerValues){
                headers.append(t + ",");
            }
            headers.append("\n");

            Enumeration<String> e = this.termDocumentMatrix.keys();
            while(e.hasMoreElements()){
                String fileName = e.nextElement();

                if(fileName.equalsIgnoreCase("header")){
                    continue;
                }

                rows.append(fileName + ',');

                String[] rowData = this.termDocumentMatrix.get(fileName);

                for(String cell : rowData){
                    rows.append(cell + ',');
                }
                
                rows.append("\n");
            }

            String outString = headers.toString() + rows.toString();
            File f = new File(basefilename);
            if(f.exists() == false){
                outfilename = basefilename + ".csv";
            }
            fstream = new FileWriter(outfilename);
            BufferedWriter bw = new BufferedWriter(fstream);

            bw.write(outString);
            bw.close();
            JOptionPane.showMessageDialog(this,
                    "Results saved to " + outfilename,
                    "File Saved",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex){

            }
        }
    }

    public void runCalculations(CustomTableModel model, float[][] linkages, boolean updateglobals){
            this.resultsFrame.sf.setLabel("Calculating : ");
            this.resultsFrame.sf.setProgressBar("", 0);
            calculateGValue(model, linkages, updateglobals);
            calculateJaccardValue(model, linkages, updateglobals);
            calculateQAP(model, linkages, updateglobals);
            calculatePValues(model, linkages, updateglobals);
    }

    /**
     * Calculates the G value of the coefficient table and the results.
     * The G value is defined as the number of gaps / number of links.
     * A result of 1 means that each piece of the ecosystem is represented
     * in law, whereas a value between 0 and 1 is a percentage.
     * @param tableModel
     * @param linkages
     * @param updateglobals
     * @return
     */
    public float calculateGValue(CustomTableModel tableModel, float[][] linkages, boolean updateglobals){
        float numberOfGaps = 0;
        float numberOfLinkages = 0;
        float gval = 0;
        for (int i = 0; i < linkages.length; i++) {
            float[] links = linkages[i];
            for (int j = 0; j < links.length; j++) {
                 float linkValue = links[j];
                 if (linkValue > 0){
                    numberOfLinkages++;
                    if (tableModel.isEditable(i, j)){
                        Object o = tableModel.getValueAt(i, j);
                        int cellvalue = (Integer) o;
                        if (cellvalue == 0 && linkValue > 0){
                            numberOfGaps++;
                        }
                    }
                }
            }
        }
//        gval = numberOfGaps / numberOfLinkages;
        gval = 1 - (numberOfGaps / numberOfLinkages);
        // Round to two decimal places
        gval *= 100;
        gval = (float)Math.round(gval)/100;

        if(updateglobals == true){
            this.gValue = gval;
            this.gaps = numberOfGaps;
            this.linkagescount = numberOfLinkages;
            setGLabel();
        }

        return gval;
    }

    /**
     * Calculates the Jaccard's Coefficient value of two tables.
     * Jaccard's Coefficient is defined as the intersection of two tables,
     * A and B, divided by the union of the two tables so that
     * J(A,B) = |A intersect B| / |A union B|.
     * The algorithm is quite simple in that if A and B are 1, then add 1
     * to the numerator and denominator.  If A and B are zero, then skip.
     * Otherwise, just add one to the denominator.
     * @param tableModel
     * @param linkages
     */
    public float calculateJaccardValue(CustomTableModel tableModel, float[][] linkages, boolean updateglobals){
        float numerator = 0;
        float denominator = 0;
        float answer = 0;
        for (int i = 0; i < linkages.length; i++) {
            float[] links = linkages[i];
            for (int j = 0; j < links.length; j++) {
                // Don't evaluate the cells with the same term in the row and col header.
                if (j != i+1){
                     float linkValue = links[j]; // from the linkages table
                     if (tableModel.isEditable(i, j)){
                        Object o = tableModel.getValueAt(i, j);  // from the results table
                        int cellvalue = (Integer) o;
                        if (cellvalue > 0 && linkValue > 0){
                            numerator += 1;
                            denominator += 1;
                        } else if (cellvalue > 0 || linkValue > 0){
                            denominator += 1;
                        }
                     }
                }
            }//end for
        }//end for
        if (denominator > 0 && numerator > 0){
            answer = numerator / denominator;
        }
        // Round to two decimal places
        answer *= 100;
        answer = (float)Math.round(answer)/100;

        if(updateglobals == true){
            this.JaccardValue = answer;
//            String answerText = "Jaccard's Coefficient: " + answer + " (" + numerator + ", " + denominator + ").";
            String answerText = "Jaccard's Coefficient: " + answer + ".";
            this.setJaccardLabel(answerText);
        }

        return answer;

    }

    /**
     * Quadratic Assignment Procedure / Mantel's Test.
     * QAP is defined as:
     * Tr(A'B) / SQRT(Tr(A'A)Tr(B'B))
     * Where function Tr() is the transposing function (diagonal values)
     * and A' or B' is the inverse table relation (swapping columns with rows).
     *
     * @param tableModel
     * @param linkages
     */
    public float calculateQAP(CustomTableModel tableModel, float[][] linkages, boolean updateglobals){
       float[][] a = symmetrisizeArray(linkages);
       float[][] b = symmetrisizeTable(tableModel);

       float[][] aPrime = primeTable(a);
       float[][] bPrime = primeTable(b);

       float resultNumer = Tr(multiplyMatrices(aPrime, b));
       float resultDenom = (float) Math.sqrt(Tr(multiplyMatrices(aPrime, a)) * Tr(multiplyMatrices(bPrime, b)));

       float tr1 = Tr(multiplyMatrices(aPrime, a));
       float tr2 = Tr(multiplyMatrices(bPrime, b));

       float qap = resultNumer / resultDenom;

       // Round to two decimal places
//       qap *= 100;
//       qap = (float)Math.round(qap)/100;

       if(updateglobals == true){
          this.QAP = qap;
//          String answerText = "QAP: " + qap + " (" + resultNumer + ", " + resultDenom + "). " + tr1 + " - " + tr2;
          String answerText = "QAP: " + qap + ".";
          this.setQAPLabel(answerText);
       }

       return qap;

    }


    /**
     * Calculates the GValue, Jaccard Value and QAP using random permutations
     * of the co-occurence table.
     *
     * @param tableModel
     * @param linkages
     * @param updateglobals
     * @return
     */
    public void calculatePValues(CustomTableModel tableModel, float[][] linkages, boolean updateglobals){
        // Store the count of the occurence of each G value.
        Hashtable<String, Integer> gValueHistogram = new Hashtable();
        // Each Jaccard value calculated.
        Hashtable<String, Integer> jValueHistogram = new Hashtable();
        // Each QAP value.
        Hashtable<String, Integer> qValueHistogram = new Hashtable();

        // Populate the histogram.
        for (int i = 0; i < 10000; i++) {
            CustomTableModel randomModel = randomizeTable(tableModel);

            float g = calculateGValue(randomModel, linkages, false);
            String sG = String.valueOf(g);
            float j = calculateJaccardValue(randomModel, linkages, false);
            String sJ = String.valueOf(j);
            float q = calculateQAP(randomModel, linkages, false);
            String sQ = String.valueOf(q);

            if(gValueHistogram.containsKey(sG)){
                Integer ng = (Integer) gValueHistogram.get(sG);
                int n = ng.intValue();
                n++;
                gValueHistogram.put(sG, n);
            } else{
                gValueHistogram.put(sG, 1);
            }

            if(jValueHistogram.containsKey(sJ)){
                Integer ng = (Integer) jValueHistogram.get(sJ);
                int n = ng.intValue();
                n++;
                jValueHistogram.put(sJ, n);
            } else{
                jValueHistogram.put(sJ, 1);
            }

            if(qValueHistogram.containsKey(sQ)){
                Integer ng = (Integer) qValueHistogram.get(sQ);
                int n = ng.intValue();
                n++;
                qValueHistogram.put(sQ, n);
            } else{
                qValueHistogram.put(sQ, 1);
            }

            float ii = (float) i; // have to convert to float to divide by 10,000.
            double progressdecimal = (ii / 10000) * 100;
            int progress = (int) progressdecimal;
            // Show the progress bar every 10%
            if(progress % 10 == 0){
              this.resultsFrame.sf.setProgressBar(String.valueOf(progress) + "%", progress);
            }
        }

        // Sum the total frequency of G values that are greater
        // than original G.  Divide by 10,000.
        int gcount = countP(gValueHistogram, this.gValue);
        float gP = deriveP((float)gcount);

        int jcount = countP(jValueHistogram, this.JaccardValue);
        float jP = deriveP((float)jcount);

        int qapcount = countP(qValueHistogram, this.QAP);
        float qapP = deriveP((float)qapcount);

        if(updateglobals == true){
            this.pGValue = gP;
            this.pJValue = jP;
            this.pQValue = qapP;
            setPLabels();
        }


    }

    /**
     * Returns the number of items in the hashtable greater than the rootValue.
     * @param histogram
     * @param rootValue
     * @return
     */
    public int countP(Hashtable<String, Integer> histogram, float rootValue){
        Enumeration e = histogram.keys();
        int count = 0;
        while(e.hasMoreElements()){
            String fl = (String) e.nextElement();
            float value = Float.valueOf(fl);
            if (value > rootValue){
                Integer ng = (Integer) histogram.get(fl);
                int n = ng.intValue();
                count += n;
            }
        }
        return count;
    }

    /**
     * Returns the p value given an integer.
     * @param count
     * @return
     */
    public float deriveP(float count){
        float p1 = count / 10000;
        float p2 = 1 - p1;
        // Round to 3 decimal places.
        //float p3 = Math.round(p2 * 1000);
        //float p = p3 / 1000;
        return p2;
    }

    /**
     * Transpose.  Returns the sum of the diagonals of a two-dimensional array.
     * @param matrix float[][]
     * @return float
     */
    public float Tr(float[][] matrix){
        float returnval = 0;
        int numberOfRows = matrix.length;
        int numberOfCols = matrix[0].length;
        for (int row = 0; row < numberOfRows; row++) {
           for (int col = 0; col < numberOfCols; col++) {
                if (row == col){
                    returnval += matrix[row][col];
                }
           }
       }
        return returnval;
    }

    /**
     * Multiplies two matrices.
     *
     * Background on Matrix Multiplication:
     * To multiply two matrices (two-dimensional arrays), we take row 1 from
     * table A and multiply column 1 from table B.  If table A is 2x3 (rows x columns),
     * and table B is 3x2, then the answer table will be 2x2.
     * Ex:
     * [1 0 -2] [0   3]   [ 0 -5]
     * [0 3 -1] [-2 -1] = [-6 -7]
     *          [0   4]
     * @param aArr
     * @param bArr
     * @return float[][]
     */
    public float[][] multiplyMatrices(float[][] aArr, float[][] bArr){
        if (aArr.length != bArr.length){
            return null;
        }
        float[][] returnArr = new float[aArr.length][aArr.length];
        // Start with the first row in matrix A, multiply by each column in B.
        // Move to the next row in matrix A, multiply by each column in B...etc.
        for (int row = 0; row < aArr.length; row++) {
           for (int colB = 0; colB < bArr.length; colB++) {
           float sum = 0;
               for (int colA = 0; colA < aArr.length; colA++) {
                   sum += aArr[row][colA] * bArr[colA][colB];
               }

           returnArr[row][colB] = sum;
           }
       }
        return returnArr;
    }

    /**
     * Converts a table/array into its prime equivalent.  A prime table is
     * a table that has been symmetrically rotated along the diagonal so that
     * [row,col] is transposed with [col,row].
     * @param arr
     * @return
     */
    public float[][] primeTable(float[][] arr){
        float[][] returnArr = new float[arr.length][arr.length];
        for (int row = 0; row < arr.length; row++) {
           for (int col = 0; col < arr.length; col++) {
               returnArr[row][col] = arr[col][row];
               returnArr[col][row] = arr[row][col];
           }
       }
        return returnArr;
    }

    /**
     * Since the tables we build are only filled where column >= row,
     * we need to fill in the empty values in the table with it's
     * symmetric equivalent.  Diagonals are treated as zero.
     * @param tableModel CustomTableModel
     * @return float[][]
     */
    public float[][] symmetrisizeTable(CustomTableModel tableModel){
       Vector v = tableModel.getDataVector();
       Object[] tableData = new Object[v.size()];

       // Copy
       for (int i = 0; i < v.size(); i++) {
           Vector rV = (Vector) v.elementAt(i);
           Object[] oA = new Object[rV.size()];
           rV.copyInto(oA);
           tableData[i] = oA;
       }

       // Remove the first column in the table data.
       // Offset by one because the first column is terms, not values.
       for (int i = 0; i < tableData.length; i++) {
            Object[] rowObj = (Object[]) tableData[i];
            Object[] newObj = new Object[rowObj.length - 1];
            System.arraycopy(rowObj, 1, newObj, 0, rowObj.length - 1);
            tableData[i] = newObj;
       }

       int numOfRows = tableData.length;
       int numOfCols = ((Object[]) tableData[0]).length;
       float[][] returnArr = new float[numOfRows][numOfCols];

       // Take the value from (row, col) and insert into (col, row).
       // Populate the return array at the same time.
       for (int row = 0; row < numOfRows; row++) {
           for (int col = 0; col < numOfCols; col++) {
               if (row > col){
                   Object[] fromRow = (Object[]) tableData[col];
                   Object o = fromRow[row];  // row is actually the column here
                   Integer cellVal = (Integer) o;
                   float fromValue = cellVal.floatValue();
                   returnArr[row][col] = fromValue;
               } else if (row == col){
                   // Treat the diagonals as zero.
                   // This is where term = term.
                   returnArr[row][col] = 0;
               } else{
                   // Straight copy
                   Object[] fromRow = (Object[]) tableData[row];
                   Object o = fromRow[col];
                   Integer cellVal = (Integer) o;
                   float copyValue = cellVal.floatValue();
                   returnArr[row][col] = copyValue;
               }
           }
       }
       return returnArr;
    }

    /**
     * Similar to symmetrisizeTable, but accepts a float array.
     * @param arr float[][] Ecosystem linkages.
     * @return float[][] Symmetric table.
     */
    public float[][] symmetrisizeArray(float[][] arr){
        int rows = arr.length;
        int columns = arr[0].length;
       // Remove the first column.
       // Offset by one because the first column is terms, not values.
        float[][] returnArr = new float[rows][columns - 1];
        for (int i = 0; i < rows; i++) {
            float[] fs = new float[columns - 1];
            float[] temp = arr[i];
            System.arraycopy(temp, 1, fs, 0, columns - 1);
            returnArr[i] = fs;
        }
        for (int row = 0; row < rows; row++) {
           for (int col = 0; col < columns; col++) {
               if (row > col){
                   // Take the value from (row, col) and insert into (col, row).
                   returnArr[row][col] = returnArr[col][row];
               }
           }
       }
       return returnArr;
    }

    /**
     *
     * @param tableModel
     * @return
     */
    public CustomTableModel randomizeTable(CustomTableModel tableModel){

       Vector<Vector> v = tableModel.getDataVector();
       Vector<Vector> tableData = new Vector<Vector>(v.size());

       Vector rowOne = (Vector) v.elementAt(0);

       int numOfRows = v.size();
       int numOfCols = rowOne.size();

       Vector<String> colIDs = new Vector();

       // Deep copy so that we don't destroy original table data.
        for (int i = 0; i < v.size(); i++) {
            Vector rowVector = (Vector) v.elementAt(i);
            Vector newVector = new Vector();
            for (int j = 0; j < rowVector.size(); j++) {
                newVector.add(j, rowVector.elementAt(j));
            }
            tableData.add(i, newVector);
        }


       // Grab column id's.
       colIDs.add("");
       for (int i = 0; i < v.size(); i++) {
           Vector<String> rV = (Vector) v.elementAt(i);
           colIDs.add(i+1, rV.elementAt(0));
       }


       CustomTableModel newModel = new CustomTableModel(numOfRows, numOfCols, 0);
       newModel.setDataVector(tableData, colIDs);

       // For determining the size of the list that holds the table's values.
       //int numOfEditables = (numOfRows * (numOfCols - 1)) - ((numOfCols - 1) * 2);

       // Stores the values of the table to be randomized.
       List<Integer> list = new ArrayList<Integer>();

       // Grab each value from the table and populate in the list.
       for (int row = 0; row < numOfRows; row++) {
            for (int col = 1; col < numOfCols; col++) {
                if(col > row + 1){
                   Vector rowData = (Vector) tableData.elementAt(row);
                   Object obj = rowData.elementAt(col);
                   Integer n = (Integer) obj;
                   list.add(n);
                }
            }
       }

       //if (list.size() != numOfEditables){
           //System.out.println("Warning:  mis-matched number of results in randomize routine.");
       //}

       // Shuffle the numbers randomly.
       Collections.shuffle(list);

        // Populate our new table with the randomized results.
       for (int row = 0; row < numOfRows; row++) {
            for (int col = 1; col < numOfCols; col++) {
                if(col > row + 1){
                   Integer n = list.remove(0);
                   newModel.setValueAt(n, row, col);
                }
            }
       }

       if (list.size() > 0){
           //System.out.println("Warning: unused values in randomized list.");
       }

       return newModel;
    }

    public void setGLabel(){
        String labelText = "M value:  " + this.gValue + " (" + this.gaps + " gaps / " + this.linkagescount + " linkages).";
        this.gLabel.setText(labelText);
    }

    public void setJaccardLabel(String txt){
        this.jaccardLabel.setText(txt);
    }

    public void setQAPLabel(String txt){
        this.qapLabel.setText(txt);
    }

    public void setPLabels(){
        this.pGLabel.setText("P value (G):  " + this.pGValue + ".");
        this.pJLabel.setText("P value (J):  " + this.pJValue + ".");
        this.pQLabel.setText("P value (QAP):  " + this.pQValue + ".");
    }

    public void setTableModel(CustomTableModel dtm){
        this.tableModel = dtm;
    }

    public void setListTableModel(CustomListTableModel lm){
        this.listTableModel = lm;
    }

    public void setTable(JTable t){
        this.table = t;
    }

    /**
     * When user selects checkboxes for selecting various cells.
     * @param selectType
     */
    public void setSelectedCells(String selectType){
        CustomTableModel model = (CustomTableModel) this.getTable().getModel();
        if(selectType.equalsIgnoreCase("All")){
            model.selectAllCells();
        } else if(selectType.equalsIgnoreCase("None")){
            model.unSelectAllCells();
        } else if(selectType.equalsIgnoreCase("Diagonals")){
            model.selectDiagonals();
        } else if(selectType.equalsIgnoreCase("Linkages")){
            model.selectLinkages();
        } else if(selectType.equalsIgnoreCase("Non Linkages")){
            model.selectNonLinkages();
        }
        this.getTable().repaint();
    }

    public JTable getTable(){
        return this.table;
    }

    public Vector<Vector> getSelectedTerms(){
        if(this.listTableModel != null){
            // this panel is "list view" - selected cells feature disabled.
            return null;
        } else {
            // this panel is normal view
            CustomTableModel model = (CustomTableModel) this.getTable().getModel();
            return model.getSelectedTermsFromCells();
        }
    }

    /**
     * Returns the table data in a two-dimensional String array.
     * @param defaultTableModel
     * @return
     */
    public String[][] getTableData(DefaultTableModel defaultTableModel){
        String[][] tableData =
                new String[defaultTableModel.getRowCount()][defaultTableModel.getColumnCount()];

        for (int row=0; row < defaultTableModel.getRowCount(); row++){
            for (int col=1; col < defaultTableModel.getColumnCount(); col++){
                // Retrieve cell value
                Object cellValue = defaultTableModel.getValueAt(row, col);
                if (cellValue == null){
                    return null;
                } else {
                    String cellData = (String) defaultTableModel.getValueAt(row, col);
                    tableData[row][col] = cellData;
                }
            }
        }

        return tableData;
    }

    public String[][] getTableContents(DefaultTableModel defaultTableModel){
        String[][] tableData =
                new String[defaultTableModel.getRowCount()][defaultTableModel.getColumnCount()];

        for (int row=0; row < defaultTableModel.getRowCount(); row++){
            for (int col=0; col < defaultTableModel.getColumnCount(); col++){
                    Object o = defaultTableModel.getValueAt(row, col);
                    if (o instanceof Integer){
                        Integer num = (Integer) o;
                        tableData[row][col] = num.toString();
                    }else {
                       String cellData = (String) defaultTableModel.getValueAt(row, col);
                       tableData[row][col] = cellData;
                    }
            }
        }
        return tableData;
    }

    public javax.swing.JScrollPane getScrollPane(){
        return this.scrollPane;
    }

    /**
     * Build a Comma-delimited string.
     * @param data
     * @return
     */
    public String buildCSV(String[][] data, int type){

        String outstring = "";
        String header = "---,";

        for (int i = 0; i < data.length; i++) {
            String[] strings = data[i];
            header += strings[0] + ",";
            for (int j = 0; j < strings.length; j++) {
                outstring += strings[j] + ",";
            }
            outstring += "\n";
        }
        if(type == 1){
           header = "Terms, Count";
        }
        return header + "\n" + outstring;
    }

    /**
     * Resizes the columns to fit their contents.
     * @param table
     */
    public void autoFitColumns(JTable table){
        // Adjust column widths
        TableColumn column = null;
        for (int j = 0; j < table.getColumnCount(); j++) {
            column = table.getColumnModel().getColumn(j);
            JTableHeader header = table.getTableHeader();

            int rowCount = table.getRowCount();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)header.getDefaultRenderer()
                       .getTableCellRendererComponent(table, column.getIdentifier()
                               , false, false, -1, col).getPreferredSize().getWidth();
               for(int row = 0; row<rowCount; row++){
                   int preferedWidth = (int)table.getCellRenderer(row, col).getTableCellRendererComponent(table,
                           table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                   width = Math.max(width, preferedWidth);
               }
               header.setResizingColumn(column); // this line is very important
               column.setWidth(width+table.getIntercellSpacing().width);

        }
        revalidate();
    }

    /**
     * Resize row heights automatically to the height of the contents.
     * @param table
     */
    public void autoFitRows(JTable table){
        // Get the current default height for all rows
        int height = table.getRowHeight();
        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            // Determine highest cell in the row
            for (int c=0; c<table.getColumnCount(); c++) {
                TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
                Component comp = table.prepareRenderer(renderer, rowIndex, c);
                int h = comp.getPreferredSize().height + 2 * table.getRowMargin();
                height = Math.max(height, h);
            }
            table.setRowHeight(rowIndex, height);
        }
        revalidate();
    }

    /**
     * Resizes the columns to a smaller width.
     * @param table
     */
    public void compactColumns(JTable table){
        // Adjust column widths
        TableColumn column = null;
        for (int j = 0; j < table.getColumnCount(); j++) {
            column = table.getColumnModel().getColumn(j);
            column.setWidth(40);
        }
        revalidate();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        labelPanel = new javax.swing.JPanel();
        gLabel = new javax.swing.JLabel();
        jaccardLabel = new javax.swing.JLabel();
        qapLabel = new javax.swing.JLabel();
        pGLabel = new javax.swing.JLabel();
        pJLabel = new javax.swing.JLabel();
        pQLabel = new javax.swing.JLabel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        scrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        scrollPanel.setLayout(new javax.swing.BoxLayout(scrollPanel, javax.swing.BoxLayout.LINE_AXIS));

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new java.awt.Dimension(100, 100));
        scrollPanel.add(scrollPane);

        add(scrollPanel);

        labelPanel.setMaximumSize(new java.awt.Dimension(32767, 50));
        labelPanel.setPreferredSize(new java.awt.Dimension(100, 50));
        labelPanel.setLayout(new java.awt.GridLayout(2, 3));

        gLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        gLabel.setText("M value:");
        labelPanel.add(gLabel);

        jaccardLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        jaccardLabel.setText("Jaccard's Coefficient:");
        labelPanel.add(jaccardLabel);

        qapLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        qapLabel.setText("QAP:");
        labelPanel.add(qapLabel);

        pGLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        pGLabel.setText("P value (M):");
        labelPanel.add(pGLabel);

        pJLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        pJLabel.setText("P value (J):");
        labelPanel.add(pJLabel);

        pQLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        pQLabel.setText("P value (QAP):");
        labelPanel.add(pQLabel);

        add(labelPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gLabel;
    private javax.swing.JLabel jaccardLabel;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JLabel pGLabel;
    private javax.swing.JLabel pJLabel;
    private javax.swing.JLabel pQLabel;
    private javax.swing.JLabel qapLabel;
    public javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel scrollPanel;
    // End of variables declaration//GEN-END:variables

    
}//end class

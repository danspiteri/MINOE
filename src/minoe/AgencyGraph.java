package minoe;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.apache.lucene.index.CorruptIndexException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * Renders a 3d bar graph of agencies.  The graph is clickable and
 * options can be viewed by right-clicking the chart.
 *
 * @author Dan Spiteri
 */
public class AgencyGraph {

    Globals globals;
    String termA = "";
    String termB = "";
    String title = "";

    public AgencyGraph(Globals globals, Hashtable<String, Integer> fileNames, SearchCriteria criteria){
        this.globals = globals;
        this.createGraph(fileNames, criteria);
    }

    public AgencyGraph(Globals globals, String termA, String termB, SearchCriteria criteria){
        this.globals = globals;
        this.termA = termA;
        this.termB = termB;

        // Get the file names for linking to the parent documents.
        MatrixController mc = new MatrixController(this.globals, this.globals.getMetaDataController());
        Hashtable<String, Integer> fileNames = mc.getCooccurenceDocNames(termA, termB, criteria);
        
        this.createGraph(fileNames, criteria);
    }

    /**
     *
     * @param fileNames
     * @param criteria
     */
    public void createGraph(Hashtable<String, Integer> fileNames, SearchCriteria criteria){

        int doc_count = 0; // # of unique documents
        int hit_count = 0; // # of co-occurence hits
       
        // The number of hits per agency.  agency => co-occurence
        Hashtable<String, Integer> agencyCountTable = new Hashtable();
        // Descriptions of the agencies, used for the tool tip.
        Hashtable<String, String> agencyDescriptionsTable = new Hashtable();
        // Documents per agency, for viewing the files. agency => vector(sections);
        Hashtable<String, Vector> agencyByDocuments = new Hashtable();

        // Count the number of agencies per co-occurence.
        for (String fileName : fileNames.keySet()) {
            int thiscount = fileNames.get(fileName);
            hit_count += thiscount;
            doc_count++;
            // The agencies for this document.
            String[] agencies = globals.getMetaDataController().getFileAgencies(fileName);
            // count the occurences of each agency
            for (String agency : agencies) {
                if(agencyCountTable.containsKey(agency)){
                    // Agency has been found already, so add to count.
                    int agencycount = agencyCountTable.get(agency);
                    agencycount += thiscount;
                    agencyCountTable.put(agency, agencycount);
                }else{
                    // First time counting this agency, save description and store count.
                    String description = globals.getMetaDataController().getAgencyDescription(agency);
                    agencyDescriptionsTable.put(agency, description);
                    agencyCountTable.put(agency, thiscount);
                }

                // Build list of documents that belong to this agency.
                if(agencyByDocuments.containsKey(agency)){
                    Vector agencyDocs = (Vector) agencyByDocuments.get(agency);
                    agencyDocs.add(fileName);
                }else{
                    Vector agencyDocs = new Vector();
                    agencyDocs.add(fileName);
                    agencyByDocuments.put(agency, agencyDocs);
                }
            }
        }
        
        // Now we have each agency that corresponds to the documents and the number of
        // documents that the agency is found in, so we can graph.

        // Create the dataset that goes in the chart.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] keys = (String[]) agencyCountTable.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String a : keys) {
            int value = agencyCountTable.get(a);
            String category = a;
            //value, category, x axis label
            dataset.addValue(value, category, "Agencies");
        }

        String chartTitle = criteria.toString();
        if(!termA.equals("") && !termB.equals("")){
          chartTitle +=  " (" + termA + " - " + termB + ")";
        }
        JFreeChart chart = ChartFactory.createBarChart3D(
                chartTitle, /* title */
                null, /* Category X Label  */
                "# of Hits per Agency", /* Value (y) Axis Label  */
                dataset, /* dataset for the chart  */
                PlotOrientation.VERTICAL, /* horizontal or vertical  */
                true, /* show legend?  */
                true, /* generate tool tips?  */
                false /* generate urls?  */
                );

        // subtitle
        TextTitle subtitle = new TextTitle(hit_count + " hit(s) across " + doc_count + " document(s).");
        chart.addSubtitle(subtitle);

        // chart customisation
        chart.setBackgroundPaint(new Color(235,235,235));  // color of the entire chart window

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setForegroundAlpha(1);
        
        plot.setBackgroundPaint(Color.white); // background color of the chart area
        plot.setRangeGridlinePaint(new Color(225,225,225));

        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // tooltip object for when you hover your cursor over a bar chart
        MyToolTip mytooltip = new MyToolTip();
        mytooltip.setDescriptions(agencyDescriptionsTable);

        CategoryPlot myPlot = chart.getCategoryPlot();
        CategoryItemRenderer cir = myPlot.getRenderer();
        int items = myPlot.getDataset().getRowKeys().size();
        for(int i=0; i<=items; i++){
            cir.setSeriesToolTipGenerator(i, mytooltip);
        }

        // create the window that the chart appears in
        ChartPanel chartPanel = new ChartPanel(chart);
        // register mouse listener for displaying pop-up window of files
        MyChartListener mcl = new MyChartListener(agencyByDocuments, globals, this.termA, this.termB);
        chartPanel.addChartMouseListener(mcl);
        ChartWindow chartWindow = new ChartWindow();
        chartWindow.setContentPane(chartPanel);
        chartWindow.setTitle("Agency Graph: " + chartTitle);

        // display the window
        this.globals.parentOwner.add(chartWindow);
        chartWindow.pack();
        chartWindow.setVisible(true);

    }
}


/* CUSTOM CLASSES */


/**
 * Custom class for displaying tool tips in the chart.
 * @author Work
 */
class MyToolTip implements CategoryToolTipGenerator{

    Hashtable agencyDescriptions;

    public void setDescriptions(Hashtable h){
        this.agencyDescriptions = h;
    }

    @Override
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
        Number value = dataset.getValue(row, column).intValue();
        String r = (String) dataset.getRowKey(row);
        String d = (String) agencyDescriptions.get(r);
        return r + ": " + d + " (" + value + ")";
    }

}

/**
 * Handles the clicking functionality of the chart.
 * Displays a window of laws pertaining to a column of data.
 */
class MyChartListener implements ChartMouseListener{

    Globals globals;
    Hashtable<String, Vector> agencyByDocs;
    String termA;
    String termB;

    public MyChartListener(Hashtable<String, Vector> abd, Globals g, String termA, String termB){
        this.globals = g;
        this.agencyByDocs = abd;
        this.termA = termA;
        this.termB = termB;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        ChartEntity ent = event.getEntity();
        if(ent instanceof CategoryItemEntity && event.getTrigger().getClickCount() > 1){
            CategoryItemEntity cie = (CategoryItemEntity) ent; // jfreechart functionality
            String rowKey = (String) cie.getRowKey(); // The agency name.  US-DOI, CA-DFG, etc.
            Hashtable<String, Integer> fileList = new Hashtable<String, Integer>();
            // If this agency has more than 0 documents.
            if(this.agencyByDocs.containsKey(rowKey)){
                // Grab all of the document names that correlate with this agency.
                Vector<String> v = this.agencyByDocs.get(rowKey);
                int i=0;
                for (String docName : v) {
                    i++;
                    fileList.put(docName, i);
                }
                // Display document list window
                DocumentListWindow dlw = new DocumentListWindow(fileList, globals, null, termA, termB);
                dlw.setTitle("Viewing laws for agency " + rowKey + ": " + termA + " - " + termB);

            } else{
                if(rowKey == null || rowKey.equals("")){
                   JOptionPane.showMessageDialog(null, "Agency information not found, no document list generated.");
                }else{
                   JOptionPane.showMessageDialog(null, "No document list found for "+rowKey+".");
                }
            }
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        // do nothing
    }

}






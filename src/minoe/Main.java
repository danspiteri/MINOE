/**
 *
 * MINOE
 *
 * Main.java
 *
 * This application was developed and is maintained by Daniel J. Spiteri,
 * supervised by Dr. Julie Ekstrom and provided by the Packard Foundation
 * through Stanford University in Palo Alto, California.  This program was
 * created as part of an EBM Tool initiative to offer an automated scientific
 * method for analyzing textual law documents as they relate to ecosystems.
 *
 * Technical Notes:
 *   Developed with NetBeans IDE 6.5.
 *
 *   Makes use of several open-source libraries:
 *      Lucene - Apache API for searching and indexing text documents.
 *      Visual Library - Java/NetBeans API for handling GUI widgets.
 *      JFreeChart - Java API for drawing bar graphs, pie charts, etc.
 *      OpenCSV - Java API for reading CSV files.
 *
 * The code is documented as much as possible, given the amount of time that I
 * was budgeted to work on the program.  If there are any programming questions feel free
 * to contact danspiteri@gmail.com.
 *
 */


package minoe;


import javax.swing.UIManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;


public class Main {
    
    static Workspace workspace;             // the MDI (gui interface controller/handler)
    
    public Globals globals;                 // information needed in the entire program
    public MetaDataController metaData;     // handles the loading and manipulation of the meta data files
    private File metaDirectory;             // location of the meta data file
    private File agencyDirectory;           // location of the file containing the agency information

    /** 
     * @throws java.io.IOException
     * @throws java.lang.Exception 
     */
    public Main(String[] args) throws IOException, Exception {

        /* Create splash screen. */
        SplashWindow splash = new SplashWindow();

        if(args == null || args.length == 0 || !args[0].equalsIgnoreCase("Main")){
            splash.setStatusMessage("Warning:  Less memory available when running directly.");
            Thread.sleep(2000);
            /*
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Warning:  Less memory is available when running the application directly.  " +
                    "Please run the program from MINOE.bat or MINOE.sh.");
             */
        }

        this.globals = new Globals();

        /* Load config file.*/
        boolean loadNormal = true; // if something happened with the config settings.
        ConfigFileLoader cfl = null;
        try{
            cfl = new ConfigFileLoader(new File("tool.config"));
        }
        catch (Exception e) {
            loadNormal = false;
            splash.setStatusMessage("Config file not found, certain features will be unavailable.");
            Thread.sleep(2000);
        }

        if (loadNormal){
            this.metaDirectory          = cfl.getMetaDir();
            this.agencyDirectory        = cfl.getAgencyDir();
            globals.setConfigurationInfo(cfl);
        }

        /* Load meta data.*/
        if (loadNormal){
            splash.setStatusMessage("Loading meta data...");
            try {
                metaData = new MetaDataController();
                String warnings = metaData.loadDataFile(metaDirectory);
                if(warnings != null && warnings.length() > 0){
                    JOptionPane.showMessageDialog(null, "Formatting errors found in meta data file. " +
                            "Use the meta data editor to correct: \n" + warnings);
                    globals.setMetaDataErrorFlag(true);
                }
                metaData.loadAgenciesFile(agencyDirectory);     // load agency meta data
                splash.setStatusMessage("Meta Data loaded.");
                Thread.sleep(500);
            } catch (FileNotFoundException fe) {
                splash.setStatusMessage("Meta data file not found, bypassing...");
                Thread.sleep(1000);
            }
            try{
                splash.setStatusMessage("Loading index (may take a moment)...");
                globals.setMetaDataController(metaData);        // reference for later use and load index in memory
                Thread.sleep(500);
            } catch (Exception ex){
                splash.setStatusMessage("Problem loading index, bypassing...");
                Thread.sleep(1000);
            }
        }

        splash.setStatusMessage("Starting GUI...");

        try{
            buildGUI();
        } catch(Exception e){
            splash.setStatusMessage("Unable to create GUI: " + e.toString());
            JOptionPane.showMessageDialog(null, "Unable to create GUI: " + e.toString());
            Thread.sleep(1000);
        } finally{
            splash.setVisible(false);
        }

        /* Some de-bugging code for testing search times and term counts. */
        boolean debug = false;
        if(debug){
            SearchFiles sf = this.globals.getSearchFiles();
            long starttime = System.currentTimeMillis();
            SearchCriteria testCriteria = new SearchCriteria();
            testCriteria.addYear("2006");
            testCriteria.setSlop(1000000);
            Hashtable<String, Integer> tbl = sf.returnResults("lobster", "trap", testCriteria);
            long thistime = System.currentTimeMillis();
            System.out.println("Time: " + (thistime - starttime));
            System.out.println("---------");
            for (String s : tbl.keySet()) {
                System.out.println(s);
                System.out.println(sf.getPath(s));
            }
            System.out.println("Size: " + tbl.size());
            System.exit(1);
        }
            
    } 

    public void buildGUI(){
        boolean packFrame = false;
        try {
            workspace = new Workspace(this.globals);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: IO Exception -" + ex.toString());
        }
        workspace.setGlobals(this.globals);
        globals.setWorkspace(workspace);

        String baseLocation = this.globals.getBaseLocation();
        File iconFile = new File(baseLocation + "/resources/graphics/smallearth.png");
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(iconFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        workspace.setIconImage(icon);

        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            workspace.pack();
        } else {
            workspace.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = workspace.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        workspace.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        workspace.setVisible(true);
        workspace.setTitle("MINOE");
    }

    public static void main(String[] args) throws IOException{
        try {
            try {
                // Give the program and appearance of the operating system.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e) {
            }
            new Main(args);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
  }
    
    
} // end class Main



package minoe;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * Various pieces of information needed globally.
 *  
 */
public class Globals {
    
    private MetaDataController mdl;
    private ConfigFileLoader cfl;
    public File indexFolder = new File("indexes");
    public String baseLocation = System.getProperty("user.dir") + System.getProperty("file.separator");
    
    public CustomDesktopPane parentOwner;  // parent container
    public Workspace workspace;
    private SearchFiles searchFiles;

    private boolean meta_error_flag = false; // are there errors in the meta data?


    public Globals(){
        
    }

    /**
     * Creates an array of FilterPanels for use in the GUI.
     * Create a new filter panel for each collection.
     * @return
     */
    public FilterPanel createFilterPanel(){
        FilterPanel panel = new FilterPanel(null, null, this);
        return panel;
    }

    public void setMetaDataController(MetaDataController mdl){
        this.mdl = mdl;
        try{
            this.searchFiles = null;
            this.searchFiles = new SearchFiles(mdl);
        } catch (Exception ex){
            System.out.println("Error in globals loading index: " + ex.toString());
        }
    }
    
    public void setConfigurationInfo(ConfigFileLoader cfl){
        this.cfl = cfl;
    }
    
    public void setParentOwner(CustomDesktopPane parent){
        this.parentOwner = parent;
    }

    public void setWorkspace(Workspace w){
        this.workspace = w;
    }
    public Workspace getWorkspace(){
        return this.workspace;
    }
    
    public void setMetaDataErrorFlag(boolean b){
        this.meta_error_flag = b;
    }

    public String getBaseLocation(){
        return this.baseLocation;
    }

    public SearchFiles getSearchFiles(){
        return this.searchFiles;
    }

    public ConfigFileLoader getConfigurationInfo(){
        return this.cfl;
    }
    
    public MetaDataController getMetaDataController(){
        return this.mdl;
    }

    public boolean hasMetaDataErrors(){
        return this.meta_error_flag;
    }
    

    public File getIndexFolder(){
        return this.indexFolder;
    }

    /**
     * Returns the contents of a file as a string.
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    public String openFile(String fileName) throws IOException {
        File file = new File(fileName);

        InputStreamReader inputStream = null;
        int size = Math.min((int) file.length(), Integer.MAX_VALUE);
        char[] cbuf = new char[(int) file.length()];
        String contents = "";
        try{
            inputStream = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            inputStream.read(cbuf, 0, size);
            contents = String.valueOf(cbuf);
        } catch(IOException ex){
            javax.swing.JOptionPane.showMessageDialog(parentOwner, "Error opening file: " + ex.toString());
        } catch (java.lang.OutOfMemoryError err){
            javax.swing.JOptionPane.showMessageDialog(parentOwner, "Error opening file: "+fileName+". File is too large. ");
        } catch(Exception ex){
            javax.swing.JOptionPane.showMessageDialog(parentOwner, "Error opening file: "+fileName+". " + ex.toString());
        } finally {
            inputStream.close();
        }

        return contents;
    }

    public void setGlobalWaitCursor(){
        this.workspace.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    public void setGlobalDefaultCursor(){
        this.workspace.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
        
}

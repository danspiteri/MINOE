

package minoe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Dan Spiteri
 * 
 * This class reads tool.config.
 */
public class ConfigFileLoader {
    
    private File metadir;
    private File pcrdir;
    private File agencydir;
    private File parentagencydir;
    private File tagdir;
    
    public ConfigFileLoader(){
        
    }
    
    public ConfigFileLoader(File configFile) throws FileNotFoundException, IOException{
        BufferedReader in = new BufferedReader(new FileReader(configFile));
        String line = in.readLine().trim();
        
        while(line != null){
            // Comments start with #.
            if (line.length() > 0 && line.charAt(0) != '#'){
                String[] lineData = line.split("=");
                String key = lineData[0].trim();
                String value = lineData[1].trim();
                if (key.equalsIgnoreCase("metadir")) {
                    setMetaDir(value);
                }
                if (key.equalsIgnoreCase("pcrdir")) {
                    setPCRDir(value);
                }
                if (key.equalsIgnoreCase("agencydir")) {
                    setAgencyDir(value);
                }
                if (key.equalsIgnoreCase("parentagencydir")) {
                    setParentAgencyDir(value);
                }
                if (key.equalsIgnoreCase("tagdir")) {
                    setTagDir(value);
                }
            }
            line = in.readLine();
        }//end while
        in.close();
    }//end method readConfigFile
    
    
    
    public void setMetaDir(String fileName){
        this.metadir = new File(fileName);
    }
    public void setPCRDir(String fileName){
        this.pcrdir = new File(fileName);
    }
    public void setAgencyDir(String fileName){
        this.agencydir = new File(fileName);
    }
    public void setParentAgencyDir(String fileName){
        this.parentagencydir = new File(fileName);
    }
    public void setTagDir(String fileName){
        this.tagdir = new File(fileName);
    }

    public File getMetaDir(){
        return this.metadir;
    }
    public File getPCRDir(){
        return this.pcrdir;
    }
    public File getAgencyDir(){
        return this.agencydir;
    }
    public File getParentAgencyDir(){
        return this.parentagencydir;
    }
    public File getTagDir(){
        return this.tagdir;
    }
}

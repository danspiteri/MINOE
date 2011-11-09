package minoe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;


/**
 * This class controls the adding and removal of documents from the collection.
 *
 * @author Daniel Spiteri
 */
public class CollectionController {

    public StatusFrame statusFrame;
    public File rootUserFolder;                    // the folder the user specified in the import wizard
    public Vector<String> tags;                    // list of folder names
    public Vector<String> fileNames;               // list of file names
    public Vector<String> absFilePaths;            // list of absolute file paths
    public Hashtable<String, String> colFilePaths; // list of relative file paths after docs are
                                                   // copied into collection.  "file name" => "relative file name"
    public Hashtable<String, Integer> tagCounts;   // number of documents per tag. document => count
    public Hashtable<String, Vector> docTagsMap;   // document => tags
    private Vector<String> folderStack;            // relative parent folder names (used as a stack)

    public File collectionFolder = new File("collection");  // Folder name of all the documents in the collection.
    public File userInfoFileCSV = new File("fileinfo.csv"); // Either one of these can contain import meta information -
    public File userInfoFileTXT = new File("fileinfo.txt"); // the file would sit in the user's folder all by itself.
    public File localInfoFile; // pointer to the user's fileinfo file.

    private boolean copyErrors = false; // did errors occur during the copy process?
    
    private boolean debug = false;

    public String collectionName; // user-specified collection name

    public CollectionController(){
        this.tags         = new Vector<String>();
        this.fileNames    = new Vector<String>();
        this.absFilePaths = new Vector<String>();
        this.docTagsMap   = new Hashtable<String, Vector>();
        this.tagCounts    = new Hashtable<String, Integer>();
        this.folderStack  = new Vector<String>();
    }


    /**
     * Adds documents to the index.
     * @param indexFolder The folder containing the index files.
     * @param clearDatabase Clears the datbase.
     * @param overwrite Existing files of the same name are copied over.
     * @param statusFrame
     */
    public void importDocuments(File indexFolder, boolean clearDatabase, boolean overwrite, StatusFrame statusFrame){
        if (indexFolder.exists() && indexFolder.canRead()) {
            try {

                statusFrame.setProgressBar("Copying documents into collection...", 0);

                // 1.  Copy all of the user's documents into the collection folder.

                // Erase contents of collection folder.
                if(clearDatabase){
                    deleteDirectory(this.collectionFolder);
                }

                if(this.collectionFolder.exists() == false){
                    this.collectionFolder.mkdir();
                }

                Vector<String> paths = new Vector<String>();
                paths.add(this.collectionFolder.getName());

                this.colFilePaths = new Hashtable<String, String>();              // populated in...
                copyFilesIntoCollection(this.rootUserFolder, paths, statusFrame, overwrite); // ...this method

                if(this.copyErrors == true){
                    return;
                }

                // 2.  Add files to the index.
                statusFrame.setProgressBar("Indexing file contents...", 0);

                // First we mark for deletion any documents in the collection that already exist.
                if(clearDatabase != true){
                    IndexReader deleteReader = IndexReader.open(indexFolder);
                    Enumeration<String> fileKeys = this.colFilePaths.keys();
                    while(fileKeys.hasMoreElements()){
                        String fileName = fileKeys.nextElement();
                        // delete documents with this file name
                        deleteReader.deleteDocuments(new Term("file name", fileName));
                    }
                    deleteReader.close();
                }

                // Note:  This doesn't remove the documents from the collections folder -
                // it just creates a new index, or modifies the existing index.
                FSDirectory fsDir = FSDirectory.getDirectory(indexFolder);

                IndexWriter writer = new IndexWriter(fsDir,
                        new StandardAnalyzer(),
                        clearDatabase,
                        IndexWriter.MaxFieldLength.UNLIMITED);


                // Adjust the points at which documents merge together during indexing.
                writer.setMaxMergeDocs(1000);
                writer.setMergeFactor(1000);

                // Add the documents that we just copied into our collection into the index,
                // rather than indexing from whatever folder the user specified.
                Enumeration<String> keys = this.colFilePaths.keys();
                float count = 0;
                float numofdocs = this.colFilePaths.size();
                long starttime = System.currentTimeMillis();
                int incsize = Math.max(1, (this.colFilePaths.size() / 100)); // the number of documents to process before
                                                                             // updating the status frame text (1%).

                writer.setRAMBufferSizeMB(16); // default is 16 - should be fine.

                // Create the index in memory.
                // Will write to the disk when the buffer is flushed or when while is done.
                while(keys.hasMoreElements()){
                    count++;
                    String fileName = keys.nextElement();
                    String pathToFile = this.colFilePaths.get(fileName);
                    File file = new File(pathToFile);
                    if(!debug){
                        try{
                            Document doc = FileDocument.Document(file, pathToFile);
                            writer.addDocument(doc);
                        } catch(Exception ex){
                            JOptionPane.showMessageDialog(null, ex.toString());
                            writer.rollback();
                            return;
                        } catch(java.lang.OutOfMemoryError err){
                            JOptionPane.showMessageDialog(null, "Memory limit reached.  Try using a smaller set of documents. ");
                            writer.rollback();
                            return;
                        }

                    }
                    float p = (count / numofdocs) * 100;
                    int percent = (int) p;
                    // display time passed
                    if((count % incsize ) == 0 && percent > 0){
                        long thistime = System.currentTimeMillis();
                        int seconds = (int) ((thistime - starttime) / 1000);
                        String time = seconds + " seconds passed";
                        if(seconds > 60){
                           int minutes = seconds / 60;
                           time = minutes + " minutes passed";
                        }
                        statusFrame.setProgressBar("Indexing ("+(int)(count)+"/"+(int) numofdocs+", "+ time +")", percent);
                    }
                }
                statusFrame.setProgressBar("Finalizing...", 100);
                writer.optimize();
                writer.close();
            } catch (CorruptIndexException ex) {
                JOptionPane.showMessageDialog(null, "Error: The index is corrupt.");
            } catch (LockObtainFailedException ex) {
                JOptionPane.showMessageDialog(null, "Error: Failed to obtain file lock.  Try restarting the program or deleting write.lock.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error IO Exception. ");
            }
        } else{
            JOptionPane.showMessageDialog(null, "Error: The indexes folder does not exist or is unreadable." +
                    "If you are running the application from a CD this feature will be unavailable.");
        }
    }

    /**
     * Deletes a directory and all of it's contents.
     * @param path
     */
    public void deleteDirectory(File path){
        if( path.exists() ) {
          File[] files = path.listFiles();
          for(int i=0; i<files.length; i++) {
             if(files[i].isDirectory()) {
               deleteDirectory(files[i]);
             }
             else {
               files[i].delete();
             }
          }
        }
        path.delete();
    }

    /**
     * Copies the files from the user's folder into the collection.
     * @param fromFolder
     * @param paths
     * @param statusFrame
     */
    public void copyFilesIntoCollection(File fromFolder, Vector<String> paths, StatusFrame statusFrame, boolean overwrite){
        if(this.copyErrors == true){
            return;
        }
        if(fromFolder == null){
            JOptionPane.showMessageDialog(null, "User folder not specified.");
            return;
        }

        paths.add(fromFolder.getName() + File.separator);
        String path = "";
        for (int i = 0; i < paths.size(); i++) {
            String p = paths.elementAt(i);
            path += p + File.separator;
        }
        File outfolder = new File(path);
        if(!debug){
            outfolder.mkdir();
        }

        File[] files = fromFolder.listFiles();
        for (File file : files) {
            if(file.isDirectory()){
                copyFilesIntoCollection(file, paths, statusFrame, overwrite); // recursive
            } else{
                File toFile = new File(path + file.getName());
                try {
                    if((debug) && isValidFileType(file)){
                        // debugging
                        System.out.println("Copy: " + file.getAbsolutePath() + " to " + toFile.getPath());
                    } else if(isValidFileType(file)){
                        boolean file_exists = toFile.exists();
                        if(file_exists == false || (file_exists && overwrite)){
                            // copy and re-index document
                            copyFile(file, toFile);
                            statusFrame.setProgressBar("Copying " + file.getName(), 0);
                            this.colFilePaths.put(toFile.getName(), toFile.getPath());
                        } else {
                            // skip file
                            statusFrame.setProgressBar("Skipping " + file.getName(), 0);
                        }
                        
                    }
                } catch (IOException ex) {
                    this.copyErrors = true;
                    JOptionPane.showMessageDialog(null, "Error: Cannot copy " + file.getName() + " into the collection - " + ex.toString() + ".  Import routine cancelled.");
                }
            }
        }
        paths.remove(paths.size() - 1); // pop folder off the stack
    }

    /**
     * Checks the file that the user is importing as valid.  We only want to 
     * import the file into the collection if this returns true.
     * @param file
     * @return
     */
    public boolean isValidFileType(File file){
        // only support .txt files at the moment.
        if(file.getName().endsWith(".txt") &&
                !file.getName().equalsIgnoreCase(this.userInfoFileCSV.getName()) &&
                !file.getName().equalsIgnoreCase(this.userInfoFileTXT.getName())){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Copies a file.
     * @param sourceFile
     * @param destFile
     * @throws java.io.IOException
     */
    public void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
          destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
          source = new FileInputStream(sourceFile).getChannel();
          destination = new FileOutputStream(destFile).getChannel();
          destination.transferFrom(source, 0, source.size());
        }
        finally {
          if(source != null) {
           source.close();
          }
          if(destination != null) {
           destination.close();
          }
        }
    }

    /**
     * Lists every document in the folder and tags each file with folder names.
     * Checks for meta data file.  Saves all of the file names and paths to be imported
     * in this collection controller.
     * @param docFolder
     */
    public void buildListOfDocuments(File docFolder){
        if(this.rootUserFolder == null){
            this.rootUserFolder = docFolder;
        }
        if(containsFiles(docFolder)){
            String thisFolderName = docFolder.getName();
            if(tags.contains(thisFolderName) == false){
                tags.add(thisFolderName);       // add this folder to the global "tag" list
            }
            if(folderStack.contains(thisFolderName) == false){
                folderStack.add(thisFolderName);  // add to the local tag list
            }
            if(this.statusFrame != null){
                statusFrame.setLabel("Processing " + thisFolderName + "...");
                statusFrame.setProgressBar("", 0);
            }
            File[] files = docFolder.listFiles();
            float count=0;
            for (File file : files) {
                if(file.isDirectory()){
                    buildListOfDocuments(file); // recursive
                } else {
                    // only support .txt files at the moment.
                    if(isValidFileType(file)){
                        fileNames.add(file.getName());
                        absFilePaths.add(file.getAbsolutePath());
                        docTagsMap.put(file.getName(), folderStack); // all the tags for this file
                        count++;
                        for (String t : folderStack) {
                            if(this.tagCounts.containsKey(t)){
                               int tc = this.tagCounts.get(t);
                               tc ++;
                               this.tagCounts.put(t, tc);
                            } else{
                               this.tagCounts.put(t, 1);
                            }
                        }
                        if(this.statusFrame != null){
                            statusFrame.setLabel("Processing " + thisFolderName + "...");
                            float progress = (count / files.length) * 100;
                            int p = (int) progress;
                            if(p > 0 && p % 10 == 0){
                               statusFrame.setProgressBar("" + p, p);
                            }
                        }
                   } else if(file.getName().equalsIgnoreCase(this.userInfoFileCSV.getName()) || 
                            file.getName().equalsIgnoreCase(this.userInfoFileTXT.getName())){
                      // meta file found, save this information.
                       setLocalInfoFile(file);
                   }

                }
            }
            // Count the number of documents in each folder.
            // Sub-folders add to the value of parent folders as well.
            /*
            for (String t : folderStack) {
                if(this.tagCounts.containsKey(t)){
                   int c = this.tagCounts.get(t);
                   c += count;
                   this.tagCounts.put(t, c);
                } else{
                    int c = (int) count;
                    this.tagCounts.put(t, c);
                }
            }
             */

            // "pop" the last tag off, indicating a return to the parent folder.
            folderStack.remove(folderStack.size() - 1);
        }
    }

    /**
     * Set a pointer to the local meta data file that the user has so that
     * when we import the meta data can be brought into the main meta data file.
     * @param file
     */
    public void setLocalInfoFile(File file){
       this.localInfoFile = file;
    }

    /**
     * Returns the tags/folder names that have documents.
     * @return
     */
    public Vector getNonZeroTags(){
        Vector<String> tagvec = new Vector<String>();
        for (String tag : this.tags) {
            if(this.tagCounts.containsKey(tag)){
                int c = this.tagCounts.get(tag);
                if(c > 0){
                    tagvec.add(tag);
                }
            }
        }
        return tagvec;
    }

    /**
     * Does the folder contain any files/documents?
     * @param docFolder
     * @return
     */
    public boolean containsFiles(File docFolder){
        if(docFolder.isDirectory()){
            File[] files = docFolder.listFiles();
            if(files.length > 0){
                return true;
            } else{
                return false;
            }
        }
        return false;
    }

    /**
     * Link to a status frame dialog window.
     * @param sf
     */
    public void setStatusFramePointer(StatusFrame sf){
        this.statusFrame = sf;
    }

    public void setCollectionName(String name){
        this.collectionName = name;
    }

}

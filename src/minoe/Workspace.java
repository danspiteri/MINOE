package minoe;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import javax.swing.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.border.Border;
import org.openide.util.Exceptions;

/**
 * This class is the main GUI work area that holds all of the application's
 * windows and frames.
 * @author  Daniel Spiteri
 */
public class Workspace extends javax.swing.JFrame {
    
    JFileChooser jFileChooser1 = new JFileChooser();
    BufferedImage image = null;
    
    private CustomDesktopPane desktopPane;
    
    public Globals globals;

    String baseLocation;

    ImageIcon visualmenuicon;
    ImageIcon greenbuttonicon;
    ImageIcon exitbuttonicon;
    ImageIcon importbuttonicon;
    ImageIcon metabuttonicon;
    ImageIcon agencybuttonicon;
    ImageIcon helpbuttonicon;
    ImageIcon aboutbuttonicon;
    ImageIcon searchbuttonicon;
    ImageIcon printbuttonicon;

    MyButton aboutButton;
    MyButton agencyButton;
    MyButton exitButton;
    MyButton gapButton;
    MyButton graphButton;
    MyButton helpButton;
    MyButton importButton;
    MyButton metaButton;
    MyButton searchButton;
    MyButton printButton;

    /** 
     * @param globals 
     * @throws java.io.IOException
     */
    
    public Workspace(Globals globals) throws IOException {

      this.globals = globals;
      desktopPane = new CustomDesktopPane();
      desktopPane.setBackground(new java.awt.Color(0, 102, 153));
      desktopPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
      desktopPane.setGlobals(this.globals);
      setContentPane(desktopPane);

      baseLocation = this.globals.getBaseLocation();

      visualmenuicon = new ImageIcon(baseLocation + "/resources/graphics/graph.png", "Graph");
      greenbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/greenbutton.png", "Green Circle");
      exitbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/redx.png", "Red X");
      importbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/greenarrow.png", "Import");
      metabuttonicon = new ImageIcon(baseLocation + "/resources/graphics/pencil.png", "Pencil");
      agencybuttonicon = new ImageIcon(baseLocation + "/resources/graphics/greenpencil.png", "Green Pencil");
      helpbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/book.png", "Help");
      aboutbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/smallearth.png", "About");
      searchbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/glass.png", "Search");
      printbuttonicon = new ImageIcon(baseLocation + "/resources/graphics/printer.png", "Print");
      
      globals.setParentOwner(desktopPane);
      initComponents();  

      gapButton = new MyButton("Gap Analysis", greenbuttonicon);
      gapButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayGapModule();
            }
      });

      graphButton = new MyButton("Visualization", visualmenuicon);
      graphButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayGraphModule();
            }
      });

      importButton = new MyButton("Import Documents", importbuttonicon);
      importButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFileChooser();
            }
      });

      metaButton = new MyButton("Meta Data", metabuttonicon);
      metaButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayMetaEditor();
            }
      });

      agencyButton = new MyButton("Agencies", agencybuttonicon);
      agencyButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAgencyEditor();
            }
      });

      searchButton = new MyButton("Search", searchbuttonicon);
      searchButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displaySearchWindow();
            }
      });

      helpButton = new MyButton("Help", helpbuttonicon);
      helpButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayHelpWindow();
            }
      });

      aboutButton = new MyButton("About", aboutbuttonicon);
      aboutButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayAboutWindow();
            }
      });

      exitButton = new MyButton("Exit", exitbuttonicon);
      exitButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmExit();
            }
      });

      printButton = new MyButton("Print", printbuttonicon);
      printButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPrintDialog();
            }
      });

      this.menuBar1.add(gapButton);
      this.menuBar1.add(graphButton);
      this.menuBar1.add(importButton);
      this.menuBar1.add(metaButton);
      this.menuBar1.add(agencyButton);
      this.menuBar1.add(searchButton);
      this.menuBar1.add(printButton);
      this.menuBar1.add(helpButton);
      this.menuBar1.add(aboutButton);
      this.menuBar1.add(exitButton);

      this.setExtendedState(JFrame.MAXIMIZED_BOTH);

      // If there are meta data errors, disable buttons.
      if(globals.hasMetaDataErrors()){
          disableSearchButtons();
      } else {
          Welcome welcomeFrame = new Welcome();
          welcomeFrame.setOwner(desktopPane);
          desktopPane.add(welcomeFrame);
          welcomeFrame.pack();
          welcomeFrame.setVisible(true);
          welcomeFrame.requestFocusInWindow();
      }

    }

    /**
     * Disables the gaps, graph and search button.
     * Changes the text of the meta data editor button to show errors.
     */
    public void disableSearchButtons(){
        gapButton.setEnabled(false);
        graphButton.setEnabled(false);
        searchButton.setEnabled(false);
        metaButton.setText("Meta Data (errors)");
        metaButton.setForeground(java.awt.Color.RED);
    }

    public void enableSearchButtons(){
        gapButton.setEnabled(true);
        graphButton.setEnabled(true);
        searchButton.setEnabled(true);
        metaButton.setText("Meta Data");
        metaButton.setForeground(java.awt.Color.BLACK);
    }

    public void displayGapModule(){
        try {
            //GapsWizard gapsWizard = new GapsWizard(desktopPane);
            GapsWizardChooser gapsWizardChooser = new GapsWizardChooser(desktopPane);
            gapsWizardChooser.setVisible(true);
            desktopPane.add(gapsWizardChooser);
            gapsWizardChooser.setSelected(true);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void displayGraphModule(){
       try {
            DrawFrame drawFrame = new DrawFrame();
            drawFrame.setParentOwner(this.desktopPane);

            drawFrame.setVisible(true);
            desktopPane.add(drawFrame);
            drawFrame.pack();
            drawFrame.setMaximum(true); // Default to max screen size.
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void displayHelpWindow(){
        // Load the help file
        try{
           HelpWindow.main(null);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.toString(), "Error opening help documentation " + e.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void displayMetaEditor(){
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        MetaDataEditor editor = new MetaDataEditor(this.globals.getMetaDataController(), 
                this.globals.getConfigurationInfo().getMetaDir(),
                this.globals);
        editor.setVisible(true);
        desktopPane.add(editor);
        editor.show();
        try {
            editor.setSelected(true);
        } catch (PropertyVetoException ex) {

        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void displayAgencyEditor(){
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        AgencyEditor editor = new AgencyEditor(this.globals.getMetaDataController(),
                this.globals.getConfigurationInfo().getAgencyDir(),
                this.globals);
        editor.setVisible(true);
        desktopPane.add(editor);
        editor.show();
        try {
            editor.setSelected(true);
        } catch (PropertyVetoException ex) {

        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void displayAboutWindow(){
        AboutFrame af = new AboutFrame();
        af.setVisible(true);
        desktopPane.add(af);
        af.pack();
    }

    public void displaySearchWindow(){
        SearchWindow sw = new SearchWindow(this.globals);
        desktopPane.add(sw);
        sw.pack();
        sw.setVisible(true);
    }

    public void showFileChooser(){
       JFileChooser chooser = new JFileChooser();
        try{
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnvalue = chooser.showDialog(this, "Import");
            if (returnvalue == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if(file.isDirectory() == false){
                    JOptionPane.showMessageDialog(this, "Please choose a folder/directory." , "Error Opening Folder", JOptionPane.ERROR_MESSAGE);
                    showFileChooser();
                } else{
                    // Proceed to import wizard
                    ImportWizard importWizard = new ImportWizard(file, this.globals);
                    this.add(importWizard);
                    importWizard.setSelected(true);
                    importWizard.show();
                }
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.toString(), "Error opening file/folder: " + e.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showPrintDialog(){
        //Custom button text
        Object[] options = {"Print All",
                        "Print Active",
                        "Cancel"};
        int c = JOptionPane.showOptionDialog(desktopPane,
                "Print all windows or the active window?",
                "Print",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        if(c == 1){
            // Print Active Window
            if(this.desktopPane.getSelectedFrame() != null) {
                PrintUtilities.printComponent(this.desktopPane.getSelectedFrame());
            } else {
                JOptionPane.showMessageDialog(this, "No active window detected. Please select a window and try again.", "No active window.", JOptionPane.ERROR_MESSAGE);
            }
        } else if (c == 0){
            // Print all windows
            PrintUtilities.printComponent(this);
        }
    }

    public void setGlobals(Globals globals){
        this.globals = globals;
    }

    public Globals getGlobals(){
        return this.globals;
    }


    public void confirmExit(){
        int c = JOptionPane.showConfirmDialog(desktopPane, "Press OK to exit the program.", "Quit?", JOptionPane.OK_CANCEL_OPTION);
        if(c == 0){
            System.exit(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        menuBar1.setFont(new java.awt.Font("Arial", 0, 12));
        menuBar1.setMinimumSize(new java.awt.Dimension(100, 40));
        menuBar1.setPreferredSize(new java.awt.Dimension(140, 40));
        setJMenuBar(menuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 683, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 607, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmExit();
    }//GEN-LAST:event_formWindowClosing
                                      
    


    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    new EBMMDI().setVisible(true);
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//
//                
//            }
//        });
//        
//
//        
//    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar menuBar1;
    // End of variables declaration//GEN-END:variables

}


class MyButton extends JButton implements MouseListener{

    Border noBorder = BorderFactory.createEmptyBorder(10,10,10,10);

    public MyButton(String t, ImageIcon icon){
        super(t, icon);
        this.setFont(new Font("Arial", Font.PLAIN, 11));
        this.setContentAreaFilled(false);
        this.addMouseListener(this);
        this.setBorder(noBorder);

    }
    @Override
    public void mouseEntered(MouseEvent e){
        this.setContentAreaFilled(true);

    }
    @Override
    public void mouseExited(MouseEvent e){
        this.setContentAreaFilled(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}


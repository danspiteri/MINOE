/*
 * StatusFrame.java
 *
 * Created on July 22, 2008, 1:21 AM
 */

package minoe;

import java.awt.*;
import javax.swing.JPanel;

public class StatusFrame extends javax.swing.JFrame {

    private String _msg = "";
    private ResultsFrame resultsFramePointer; // for cancelling
    private DrawFrame drawFramePointer; // for cancelling draw operation
    private ImportWizard importWizard;  // for cancelling import documents
    
    /**
     * Creates new StatusFrame
     * @param rf 
     */
    @SuppressWarnings("static-access")
    public StatusFrame(ResultsFrame rf) {
        this.resultsFramePointer = rf;
        initComponents();
        setCenter();
     
    }

    public StatusFrame(DrawFrame d){
        this.drawFramePointer = d;
        initComponents();
        setCenter();
    }

    public StatusFrame(ImportWizard wizard){
        this.importWizard = wizard;
        initComponents();
        setCenter();
    }

    /**
     * If true is passed in, sets scrollbar to indeterminate.
     * @param b Indeterminate?
     */
    public StatusFrame (boolean b){
        initComponents();
        setCenter();
        this.progressBar.setIndeterminate(b);
        this.cancelButton.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new MyPanel();
        progressBar = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(StatusFrame.class, "StatusFrame.title")); // NOI18N
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(300, 100));
        setResizable(false);

        panel.setMinimumSize(new java.awt.Dimension(300, 100));
        panel.setPreferredSize(new java.awt.Dimension(300, 75));

        progressBar.setBackground(new java.awt.Color(255, 255, 255));
        progressBar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        progressBar.setForeground(new java.awt.Color(0, 0, 0));
        progressBar.setMinimumSize(new java.awt.Dimension(250, 25));
        progressBar.setPreferredSize(new java.awt.Dimension(250, 25));
        panel.add(progressBar);

        cancelButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cancelButton.setText(org.openide.util.NbBundle.getMessage(StatusFrame.class, "StatusFrame.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        panel.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if(this.resultsFramePointer != null){
           this.resultsFramePointer.cancel();
        }
        if(this.drawFramePointer != null){
           this.drawFramePointer.cancel();
        }
        if(this.importWizard != null){
            this.importWizard.cancel();
        }
        System.gc();
        this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    public void setCenter(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 300;
        int h = 100;
        this.setSize(w, h);
        this.setPreferredSize(new Dimension(w, h));

        progressBar.setStringPainted(true);

        // attempt to center the splash on the page
        this.setLocation((screenSize.width - w) / 2, (screenSize.height - h) / 2);
        this.setVisible(true);
    }

    /**
     * Sets the text and value (0-100) of the progress bar.
     * @param s
     * @param value
     */
   public void setProgressBar(String s, int value){
        this.progressBar.setString(this._msg + s);
        this.progressBar.setValue(value);
        
        Rectangle progressRect = this.panel.getBounds();
        progressRect.x = 0;
        progressRect.y = 0;
        repaint();
        /*
        try{
          this.panel.paintImmediately(progressRect);
        }
        catch(Exception e){
            System.out.println("Set Progress Bar Exception.");
        }
         */

    }
   
   public void setLabel(String s){
       this._msg = s;    
   }
          
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel panel;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

    @SuppressWarnings("static-access")
    public void run() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));   
    }
    
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //StatusFrame();
            }
        });
    }

    class MyPanel extends JPanel{
        // Custom panel used to eliminate flicker from repaint method.
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

    }
    
}

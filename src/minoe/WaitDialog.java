/*
 * WaitDialog.java
 *
 * Created on June 10, 2008, 11:50 PM
 */

package minoe;

/**
 *
 * @author  Dan Spiteri
 */
public class WaitDialog extends javax.swing.JDialog {
    
    /** Creates new form WaitDialog */
    @SuppressWarnings("static-access")
    public WaitDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setLocation((screenSize.width - this.WIDTH) / 2, (screenSize.height - this.HEIGHT) / 2);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jLabel1.setFont(new java.awt.Font("Bitstream Vera Sans", 0, 12));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(WaitDialog.class, "WaitDialog.jLabel1.text")); // NOI18N
        getContentPane().add(jLabel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WaitDialog dialog = new WaitDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
}

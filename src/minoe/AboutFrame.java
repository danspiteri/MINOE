/*
 * AboutFrame.java
 *
 * Created on July 18, 2007, 1:51 AM
 */

package minoe;

/**
 *
 * @author Daniel Spiteri
 */
public class AboutFrame extends javax.swing.JInternalFrame {
    
    /** Creates new form AboutFrame */
    public AboutFrame() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("About");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/minoe/smallearth.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(355, 2000));
        setMinimumSize(new java.awt.Dimension(100, 100));
        setPreferredSize(new java.awt.Dimension(600, 450));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(800, 400));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 350));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/minoe/splash.png"))); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(335, 369));
        jPanel3.add(jLabel1);

        jPanel1.add(jPanel3);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 300));
        jPanel2.setMinimumSize(new java.awt.Dimension(335, 150));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 300));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(50, 50));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(335, 250));

        jTextArea1.setBackground(new java.awt.Color(224, 223, 227));
        jTextArea1.setColumns(40);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(15);
        jTextArea1.setText("MINOE v1.10\n11/08/2011\n\nThis application was developed at Stanford University and is supported by the Ecosystem-Based Management Tools Initiative Fund from the David and Lucile Packard Foundation, which is administered through Duke University.  \n\n- Prof.  Kincho Law, Principal Investigator  \n- Julia Ekstrom, Postdoctoral Scholar  \n- Gloria Lau, Consulting Professor \n- Dan Spiteri, Programmer  \n- Jack Cheng, Ph.D. candidate \n\nContact:\njaekstrom@gmail.com\n\nProgramming questions:\ndanspiteri@gmail.com");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jTextArea1.setMinimumSize(new java.awt.Dimension(164, 350));
        jScrollPane1.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane1);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanel4.setMinimumSize(new java.awt.Dimension(59, 20));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        getContentPane().add(jPanel4);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
}

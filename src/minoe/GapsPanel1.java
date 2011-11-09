

package minoe;

import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Displays a GUI for entering terms.  Keeps track of the terms in a list.
 * @author  Dan Spiteri
 */
public class GapsPanel1 extends javax.swing.JPanel {
    
    // Keep reference to parent container.
    private GapsContainer container;
    
    private DefaultListModel defaultListModel;

    /** Creates new form GapsPanel1 */
    public GapsPanel1(GapsContainer container) {
        this.container = container;
        defaultListModel = new DefaultListModel();
        initComponents();
        termList.addListSelectionListener(new MyListSelectionListener());
        int size = defaultListModel.getSize();
        if(size == 0){
            removeFromListButton.setEnabled(false);
            //nextButton.setEnabled(false);
        }
        
    }

   public String[] getListData(){
        Enumeration e = defaultListModel.elements();
        String[] listdata = new String[defaultListModel.getSize()];
        int i=0;
        while(e.hasMoreElements()){
            listdata[i] = e.nextElement().toString();
            i++;
        }
        return listdata;
   }

   public void enableRemoveButton(boolean remove){
       removeFromListButton.setEnabled(remove);
   }

   public void setListData(String[] termList){
       for (String term : termList) {
            defaultListModel.addElement(term);
       }
   }

   public void addTerm(){
        String newTerm = termTextField.getText();
        if(!(newTerm.length() > 0)){
            return;
        }
        // if there is an item selected, switch to edit mode
        if(termList.getSelectedIndex() >= 0){
            defaultListModel.set(termList.getSelectedIndex(), newTerm);
            addButton.setText("Add");
            termList.clearSelection();
        } else {
            defaultListModel.addElement(newTerm);
        }

        termTextField.setText("");
        this.countLabel.setText("Count: " + defaultListModel.getSize());

        // Enable the next button if there is at least one item.
        int size = defaultListModel.getSize();
        if(size > 0){
            removeFromListButton.setEnabled(true);
            container.enableNext(true);
        }
   }
      
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        containerPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        termTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        termList = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        removeFromListButton = new javax.swing.JButton();
        countLabel = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(500, 600));
        setPreferredSize(new java.awt.Dimension(425, 300));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setMaximumSize(new java.awt.Dimension(32767, 25));
        titlePanel.setMinimumSize(new java.awt.Dimension(0, 0));
        titlePanel.setPreferredSize(new java.awt.Dimension(10, 40));
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.jLabel3.text")); // NOI18N
        titlePanel.add(jLabel3);

        add(titlePanel);

        jSeparator1.setForeground(new java.awt.Color(51, 51, 51));
        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 2));
        jSeparator1.setPreferredSize(new java.awt.Dimension(420, 2));
        add(jSeparator1);

        containerPanel.setMaximumSize(new java.awt.Dimension(3000, 3000));
        containerPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        containerPanel.setLayout(new javax.swing.BoxLayout(containerPanel, javax.swing.BoxLayout.Y_AXIS));

        topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.topPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        topPanel.setMaximumSize(new java.awt.Dimension(3000, 60));
        topPanel.setMinimumSize(new java.awt.Dimension(190, 60));
        topPanel.setPreferredSize(new java.awt.Dimension(100, 60));
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.LINE_AXIS));

        termTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        termTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                termTextFieldKeyReleased(evt);
            }
        });
        topPanel.add(termTextField);

        addButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        addButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        topPanel.add(addButton);

        containerPanel.add(topPanel);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.bottomPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        bottomPanel.setMaximumSize(new java.awt.Dimension(3000, 600));
        bottomPanel.setPreferredSize(new java.awt.Dimension(100, 325));
        bottomPanel.setLayout(new javax.swing.BoxLayout(bottomPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 200));

        termList.setModel(defaultListModel);
        termList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        termList.setVisibleRowCount(5);
        jScrollPane1.setViewportView(termList);

        bottomPanel.add(jScrollPane1);

        jPanel4.setFont(new java.awt.Font("Arial", 0, 11));
        jPanel4.setMaximumSize(new java.awt.Dimension(100, 69));
        jPanel4.setMinimumSize(new java.awt.Dimension(100, 69));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 69));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        moveUpButton.setFont(new java.awt.Font("Arial", 0, 11));
        moveUpButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.moveUpButton.text")); // NOI18N
        moveUpButton.setMaximumSize(new java.awt.Dimension(100, 25));
        moveUpButton.setPreferredSize(new java.awt.Dimension(90, 23));
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });
        jPanel4.add(moveUpButton);

        moveDownButton.setFont(new java.awt.Font("Arial", 0, 11));
        moveDownButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.moveDownButton.text")); // NOI18N
        moveDownButton.setMaximumSize(new java.awt.Dimension(100, 25));
        moveDownButton.setPreferredSize(new java.awt.Dimension(89, 23));
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });
        jPanel4.add(moveDownButton);

        removeFromListButton.setFont(new java.awt.Font("Arial", 0, 11));
        removeFromListButton.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.removeFromListButton.text")); // NOI18N
        removeFromListButton.setMaximumSize(new java.awt.Dimension(100, 25));
        removeFromListButton.setPreferredSize(new java.awt.Dimension(90, 23));
        removeFromListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromListButtonActionPerformed(evt);
            }
        });
        jPanel4.add(removeFromListButton);

        bottomPanel.add(jPanel4);

        containerPanel.add(bottomPanel);

        countLabel.setText(org.openide.util.NbBundle.getMessage(GapsPanel1.class, "GapsPanel1.countLabel.text")); // NOI18N
        containerPanel.add(countLabel);

        add(containerPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void removeFromListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromListButtonActionPerformed

        int index = termList.getSelectedIndex();
        defaultListModel.remove(index);
        
        int size = defaultListModel.getSize();
        
        // Disable the next button if the list is empty.
        if(size == 0){
            this.enableRemoveButton(false);
            container.enableNext(false);
        } else {
            termList.setSelectedIndex(0);
        }
        this.countLabel.setText("Count: " + defaultListModel.getSize());
    }//GEN-LAST:event_removeFromListButtonActionPerformed

    private void termTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_termTextFieldKeyReleased
        int buttonPressed = evt.getKeyCode();
        // 10 = enter button.
        if (buttonPressed == 10){
            addTerm();
        }
    }//GEN-LAST:event_termTextFieldKeyReleased

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        int index = termList.getSelectedIndex();
        if (index > 0){
            Object upObject = defaultListModel.getElementAt(index);
            Object swapObject = defaultListModel.getElementAt(index-1);
            defaultListModel.removeElementAt(index);
            defaultListModel.insertElementAt(swapObject, index);
            defaultListModel.removeElementAt(index-1);
            defaultListModel.insertElementAt(upObject, index-1);
            termList.setSelectedIndex(index-1);
        }
    }//GEN-LAST:event_moveUpButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        int index = termList.getSelectedIndex();
        if (index < defaultListModel.getSize()-1){
            Object downObject = defaultListModel.getElementAt(index);
            Object swapObject = defaultListModel.getElementAt(index+1);
            defaultListModel.removeElementAt(index);
            defaultListModel.insertElementAt(swapObject, index);
            defaultListModel.removeElementAt(index+1);
            defaultListModel.insertElementAt(downObject, index+1);
            termList.setSelectedIndex(index+1);
        }
    }//GEN-LAST:event_moveDownButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addTerm();
}//GEN-LAST:event_addButtonActionPerformed
    

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JLabel countLabel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton removeFromListButton;
    private javax.swing.JList termList;
    private javax.swing.JTextField termTextField;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    class MyListSelectionListener implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList list = (JList) e.getSource();
            if(list != null){
                int index = list.getSelectedIndex();
                if(index >= 0){
                    termTextField.setText(defaultListModel.elementAt(index).toString());
                    addButton.setText("Edit");
                }
            }
//            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
//            if(!lsm.isSelectionEmpty()){
//                int index = lsm.getMinSelectionIndex();
//                termTextField.setText(defaultListModel.elementAt(index).toString());
//            }
        }

    }
}

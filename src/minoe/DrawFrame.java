package minoe;

/**
 * DrawFrame.java
 *
 * Visualization class.  Renders terms and connectors in the scene, offering
 * a visual representation of the relationships between terms.
 *
 * @author Daniel Spiteri
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;

import javax.swing.JPopupMenu;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.*;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.action.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.model.ObjectState;
import org.openide.util.Exceptions;


public class DrawFrame extends javax.swing.JInternalFrame {
    
    CustomDesktopPane parentOwner;
    
    private MyScene scene;
    private JComponent myView;
    private LayerWidget mainLayer;
    private LayerWidget interractionLayer;
    private LayerWidget connectionLayer;
    
    public Color DEFAULT_NODE_COLOR = Color.CYAN;
    public Color DEFAULT_TERM_COLOR = Color.BLACK;
    public Color DEFAULT_LINK_COLOR = Color.BLACK;
    public Color DEFAULT_GAP_COLOR = Color.RED;
    public Color DEFAULT_SCENE_COLOR = Color.WHITE;
    public int DEFAULT_LINE_ALPHA = 100; // 0-255, where 0 is invisible.

    private float max_results_length = 0; // for determining font sizes
    private float max_connection_size = 0;

    private Vector<Vector> gapsTable; // list of TermWidget vectors marked as gaps
    private Vector<ConnectionWidget> gapConnections; // list of connection widgets marked as gaps
    private Vector<ConnectionWidget> otherConnections; // neither gaps nor links
    private Hashtable<ConnectionWidget, Integer> connectionValues;

    private String[] termList;
    private float[][] linkages;

    private DefaultListModel listModel;
    
    public int defaultBorderSize = 2;
    public float maxLineThickness = 15;
    private final double defaultzoomlevel = 1.0;
    private int defaultFontSize = 10;
    private int maxFontSize = 40;

    private boolean animateJitter = false;
    private boolean cancel = false;

    private StatusFrame sf;
    private StatusFrameWorker statusFrameWorker;

    private FilterPanel filterPanel;

    WidgetAction action;
    Map m1 = new HashMap();
    ObjectScene objectScene;

    
    /** Creates new form DrawFrame */
    public DrawFrame() {
        listModel = new DefaultListModel();
        initComponents();
        this.gapsTable = new Vector<Vector>();
        this.gapConnections = new Vector<ConnectionWidget>();
        this.otherConnections = new Vector<ConnectionWidget>();
        this.connectionValues = new Hashtable<ConnectionWidget, Integer>();


        this.termColorPanel.setBackground(this.DEFAULT_TERM_COLOR);
//        this.nodeColorPanel.setBackground(this.DEFAULT_NODE_COLOR);
        this.linkColorPanel.setBackground(DEFAULT_LINK_COLOR);
        this.gapColorPanel.setBackground(DEFAULT_GAP_COLOR);
        this.sceneColorPanel.setBackground(DEFAULT_SCENE_COLOR);

        

        scene = new MyScene(this);
        myView = scene.createView();
        
        /* Add new widget when user clicks.*/
        //scene.getActions().addAction(ActionFactory.createSelectAction(new CreateProvider()));
               
        /* Enable zooming (one line of code). */
        scene.getActions().addAction(ActionFactory.createCenteredZoomAction(1.02));
        scene.setZoomFactor (this.defaultzoomlevel);
       
        scrollPane.setViewportView(myView);

        JComponent satelliteView = scene.createSatelliteView();
        leftPanel.add(satelliteView);

        mainLayer = new LayerWidget(scene);
        scene.addChild(mainLayer);

        interractionLayer = new LayerWidget(scene);
        scene.addChild(interractionLayer);
        
        connectionLayer = new LayerWidget(scene);
        scene.addChild(connectionLayer);
        
        objectScene = new ObjectScene();

        scene.setBackground(DEFAULT_SCENE_COLOR);

        updateZoomLabel();

        MouseWheelListener mwl = new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateZoomLabel();
            }
        };
        myView.addMouseWheelListener(mwl);
        scene.validate();

    }

    public void setFilterPanel(FilterPanel panel){
        this.filterPanel = panel;
        this.filterContainerPanel.add(panel);
    }


    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        leftPanel = new javax.swing.JPanel();
        addTermButton = new javax.swing.JButton();
        graphButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        gapJList = new javax.swing.JList();
        jSeparator5 = new javax.swing.JSeparator();
        statusLabel = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        viewerLabel = new javax.swing.JLabel();
        filterContainerPanel = new javax.swing.JPanel();
        colorsPanel = new javax.swing.JPanel();
        changeTermColorButton = new javax.swing.JButton();
        termColorPanel = new javax.swing.JPanel();
        changeLinkColorButton = new javax.swing.JButton();
        linkColorPanel = new javax.swing.JPanel();
        changeGapColorButton = new javax.swing.JButton();
        gapColorPanel = new javax.swing.JPanel();
        changeSceneColorButton = new javax.swing.JButton();
        sceneColorPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        zoomPanel = new javax.swing.JPanel();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        zoomLabel = new javax.swing.JLabel();
        connectingLinesPanel = new javax.swing.JPanel();
        showGapsCheckBox = new javax.swing.JCheckBox();
        showLinksCheckBox = new javax.swing.JCheckBox();
        showOtherCheckBox = new javax.swing.JCheckBox();
        centerPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        savePictureMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        closeMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JSeparator();
        fitGraphMenuItem = new javax.swing.JMenuItem();
        gapCentricMenuitem = new javax.swing.JMenuItem();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Visualization Module"); // NOI18N
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/minoe/33.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(3000, 3000));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        tabbedPane.setFont(new java.awt.Font("Arial", 0, 11));
        tabbedPane.setMaximumSize(new java.awt.Dimension(175, 3000));
        tabbedPane.setMinimumSize(new java.awt.Dimension(175, 253));
        tabbedPane.setPreferredSize(new java.awt.Dimension(175, 500));

        leftPanel.setFont(new java.awt.Font("Arial", 0, 11));
        leftPanel.setPreferredSize(new java.awt.Dimension(150, 100));
        leftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        addTermButton.setFont(new java.awt.Font("Arial", 0, 12));
        addTermButton.setForeground(new java.awt.Color(51, 51, 255));
        addTermButton.setText("Add Term");
        addTermButton.setPreferredSize(new java.awt.Dimension(100, 30));
        addTermButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTermButtonActionPerformed(evt);
            }
        });
        leftPanel.add(addTermButton);

        graphButton.setFont(new java.awt.Font("Arial", 0, 12));
        graphButton.setForeground(new java.awt.Color(51, 204, 0));
        graphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/minoe/33.png"))); // NOI18N
        graphButton.setText("Graph");
        graphButton.setPreferredSize(new java.awt.Dimension(100, 30));
        graphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphButtonActionPerformed(evt);
            }
        });
        leftPanel.add(graphButton);

        clearButton.setFont(new java.awt.Font("Arial", 0, 12));
        clearButton.setForeground(new java.awt.Color(204, 0, 0));
        clearButton.setText("Clear All");
        clearButton.setPreferredSize(new java.awt.Dimension(100, 27));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        leftPanel.add(clearButton);

        jSeparator4.setAlignmentY(1.0F);
        jSeparator4.setMinimumSize(new java.awt.Dimension(100, 1));
        jSeparator4.setPreferredSize(new java.awt.Dimension(115, 3));
        leftPanel.add(jSeparator4);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel1.setText("Gaps List");
        leftPanel.add(jLabel1);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(125, 130));

        gapJList.setFont(new java.awt.Font("Arial", 0, 11));
        gapJList.setModel(listModel);
        jScrollPane1.setViewportView(gapJList);

        jPanel4.add(jScrollPane1);

        leftPanel.add(jPanel4);

        jSeparator5.setAlignmentY(1.0F);
        jSeparator5.setMinimumSize(new java.awt.Dimension(100, 1));
        jSeparator5.setPreferredSize(new java.awt.Dimension(115, 3));
        leftPanel.add(jSeparator5);

        statusLabel.setFont(new java.awt.Font("Arial", 0, 11));
        statusLabel.setText("Status:");
        leftPanel.add(statusLabel);

        jSeparator7.setAlignmentY(1.0F);
        jSeparator7.setMinimumSize(new java.awt.Dimension(100, 1));
        jSeparator7.setPreferredSize(new java.awt.Dimension(115, 3));
        leftPanel.add(jSeparator7);

        viewerLabel.setFont(new java.awt.Font("Arial", 0, 11));
        viewerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        viewerLabel.setText("Viewer");
        viewerLabel.setAlignmentY(0.0F);
        leftPanel.add(viewerLabel);

        tabbedPane.addTab("Controls", leftPanel);

        filterContainerPanel.setMaximumSize(new java.awt.Dimension(3000, 225));
        filterContainerPanel.setMinimumSize(new java.awt.Dimension(150, 300));
        filterContainerPanel.setPreferredSize(new java.awt.Dimension(150, 300));
        filterContainerPanel.setLayout(new javax.swing.BoxLayout(filterContainerPanel, javax.swing.BoxLayout.Y_AXIS));
        tabbedPane.addTab("Filters", filterContainerPanel);

        changeTermColorButton.setFont(new java.awt.Font("Arial", 0, 12));
        changeTermColorButton.setText("Term Color");
        changeTermColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeTermColorButtonActionPerformed(evt);
            }
        });
        colorsPanel.add(changeTermColorButton);

        termColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        termColorPanel.setMaximumSize(new java.awt.Dimension(125, 25));
        termColorPanel.setMinimumSize(new java.awt.Dimension(100, 25));
        termColorPanel.setPreferredSize(new java.awt.Dimension(125, 25));
        termColorPanel.setLayout(null);
        colorsPanel.add(termColorPanel);

        changeLinkColorButton.setFont(new java.awt.Font("Arial", 0, 12));
        changeLinkColorButton.setText("Link Color");
        changeLinkColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLinkColorButtonActionPerformed(evt);
            }
        });
        colorsPanel.add(changeLinkColorButton);

        linkColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        linkColorPanel.setMaximumSize(new java.awt.Dimension(125, 25));
        linkColorPanel.setMinimumSize(new java.awt.Dimension(100, 25));
        linkColorPanel.setPreferredSize(new java.awt.Dimension(125, 25));
        linkColorPanel.setLayout(null);
        colorsPanel.add(linkColorPanel);

        changeGapColorButton.setFont(new java.awt.Font("Arial", 0, 12));
        changeGapColorButton.setText("Gap Color");
        changeGapColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeGapColorButtonActionPerformed(evt);
            }
        });
        colorsPanel.add(changeGapColorButton);

        gapColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gapColorPanel.setMaximumSize(new java.awt.Dimension(125, 25));
        gapColorPanel.setMinimumSize(new java.awt.Dimension(100, 25));
        gapColorPanel.setPreferredSize(new java.awt.Dimension(125, 25));
        gapColorPanel.setLayout(null);
        colorsPanel.add(gapColorPanel);

        changeSceneColorButton.setFont(new java.awt.Font("Arial", 0, 12));
        changeSceneColorButton.setText("Background Color");
        changeSceneColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeSceneColorButtonActionPerformed(evt);
            }
        });
        colorsPanel.add(changeSceneColorButton);

        sceneColorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sceneColorPanel.setMaximumSize(new java.awt.Dimension(125, 25));
        sceneColorPanel.setMinimumSize(new java.awt.Dimension(100, 25));
        sceneColorPanel.setPreferredSize(new java.awt.Dimension(125, 25));
        sceneColorPanel.setLayout(null);
        colorsPanel.add(sceneColorPanel);

        tabbedPane.addTab("Colors", colorsPanel);

        getContentPane().add(tabbedPane);

        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));

        topPanel.setMaximumSize(new java.awt.Dimension(32767, 50));
        topPanel.setMinimumSize(new java.awt.Dimension(500, 50));
        topPanel.setPreferredSize(new java.awt.Dimension(500, 50));
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.X_AXIS));

        zoomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Zoom (or mousewheel)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        zoomPanel.setMaximumSize(new java.awt.Dimension(250, 50));
        zoomPanel.setMinimumSize(new java.awt.Dimension(250, 50));
        zoomPanel.setPreferredSize(new java.awt.Dimension(250, 50));
        zoomPanel.setLayout(new javax.swing.BoxLayout(zoomPanel, javax.swing.BoxLayout.X_AXIS));

        zoomInButton.setFont(new java.awt.Font("Arial", 0, 10));
        zoomInButton.setText("+");
        zoomInButton.setMaximumSize(new java.awt.Dimension(50, 20));
        zoomInButton.setMinimumSize(new java.awt.Dimension(50, 20));
        zoomInButton.setPreferredSize(new java.awt.Dimension(50, 20));
        zoomInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInButtonActionPerformed(evt);
            }
        });
        zoomPanel.add(zoomInButton);

        zoomOutButton.setFont(new java.awt.Font("Arial", 0, 10));
        zoomOutButton.setText("-");
        zoomOutButton.setMaximumSize(new java.awt.Dimension(50, 20));
        zoomOutButton.setMinimumSize(new java.awt.Dimension(50, 20));
        zoomOutButton.setPreferredSize(new java.awt.Dimension(50, 20));
        zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutButtonActionPerformed(evt);
            }
        });
        zoomPanel.add(zoomOutButton);

        zoomLabel.setFont(new java.awt.Font("Arial", 0, 10));
        zoomLabel.setText(" Zoom Factor:");
        zoomPanel.add(zoomLabel);

        topPanel.add(zoomPanel);

        connectingLinesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Connecting Lines", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        connectingLinesPanel.setMaximumSize(new java.awt.Dimension(250, 50));
        connectingLinesPanel.setMinimumSize(new java.awt.Dimension(250, 50));
        connectingLinesPanel.setPreferredSize(new java.awt.Dimension(250, 50));
        connectingLinesPanel.setLayout(new javax.swing.BoxLayout(connectingLinesPanel, javax.swing.BoxLayout.X_AXIS));

        showGapsCheckBox.setFont(new java.awt.Font("Arial", 0, 10));
        showGapsCheckBox.setSelected(true);
        showGapsCheckBox.setText("Show Gaps");
        showGapsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGapsCheckBoxActionPerformed(evt);
            }
        });
        connectingLinesPanel.add(showGapsCheckBox);

        showLinksCheckBox.setFont(new java.awt.Font("Arial", 0, 10));
        showLinksCheckBox.setSelected(true);
        showLinksCheckBox.setText("Show Links");
        showLinksCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLinksCheckBoxActionPerformed(evt);
            }
        });
        connectingLinesPanel.add(showLinksCheckBox);

        showOtherCheckBox.setFont(new java.awt.Font("Arial", 0, 10));
        showOtherCheckBox.setSelected(true);
        showOtherCheckBox.setText("Show Other");
        showOtherCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOtherCheckBoxActionPerformed(evt);
            }
        });
        connectingLinesPanel.add(showOtherCheckBox);

        topPanel.add(connectingLinesPanel);

        rightPanel.add(topPanel);

        centerPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 2, true));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setBorder(null);
        scrollPane.setMaximumSize(new java.awt.Dimension(3000, 3000));
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        centerPanel.add(scrollPane);

        rightPanel.add(centerPanel);

        getContentPane().add(rightPanel);

        fileMenu.setText("File");

        savePictureMenuItem.setText("Save Graph As Image..");
        savePictureMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePictureMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(savePictureMenuItem);
        fileMenu.add(jSeparator1);

        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);

        jMenuBar1.add(fileMenu);

        viewMenu.setText("View");
        viewMenu.add(jSeparator2);

        fitGraphMenuItem.setText("Fit Graph to Screen");
        fitGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitGraphMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(fitGraphMenuItem);

        gapCentricMenuitem.setText("Arrange Gap Centric");
        gapCentricMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gapCentricMenuitemActionPerformed(evt);
            }
        });
        viewMenu.add(gapCentricMenuitem);

        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /* **************************/
    /* ACTION PERFORMED METHODS */
    /* **************************/
    
    /* Add term button */
    private void addTermButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTermButtonActionPerformed
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float w = this.centerPanel.getWidth();
        float h = this.centerPanel.getHeight();        
        
        int width = (int) (w / this.scene.getZoomFactor());
        int height = (int) (h / this.scene.getZoomFactor());
        
        // attempt to center the widget upon creation
        this.createTermWidget(
                "Double Click to Rename Me", 
                (width) / 2, 
                (height) / 2);

        this.refit();
    }//GEN-LAST:event_addTermButtonActionPerformed

    /**
     * Finds the cooccurence given the term widgets on the page and renders them.
     * @param evt
     */
    private void graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphButtonActionPerformed
        this.resetCancel();
        this.connectTermsWorker();
}//GEN-LAST:event_graphButtonActionPerformed
    /* Zoom in. */
    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
            scene.setZoomFactor (scene.getZoomFactor() * 1.1);
            updateZoomLabel();
            scene.validate();
    }//GEN-LAST:event_zoomInButtonActionPerformed

    /* Zoom out. */
    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
            scene.setZoomFactor (scene.getZoomFactor() * .9);
            updateZoomLabel();
            scene.validate();
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    /** Removes all non-layer widgets/clears the screen. */
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        for (Widget layers : scene.getChildren()){
            layers.removeChildren();
        }
        scene.validate();
}//GEN-LAST:event_clearButtonActionPerformed

    private void changeTermColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeTermColorButtonActionPerformed
        Color newColor = JColorChooser.showDialog(
                             this,
                             "Choose Term Color",
                             termColorPanel.getBackground());
        if(newColor != null){
            this.DEFAULT_TERM_COLOR = newColor;
            this.termColorPanel.setBackground(newColor);
            this.setTermColor(newColor);
        }
    }//GEN-LAST:event_changeTermColorButtonActionPerformed

    private void changeLinkColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeLinkColorButtonActionPerformed
        Color newColor = JColorChooser.showDialog(
                             this,
                             "Choose Link Color",
                             linkColorPanel.getBackground());
        if(newColor != null){
            this.DEFAULT_LINK_COLOR = newColor;
            this.linkColorPanel.setBackground(newColor);
            this.setConnectionColor();
        }
}//GEN-LAST:event_changeLinkColorButtonActionPerformed

    private void changeGapColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeGapColorButtonActionPerformed
        Color newColor = JColorChooser.showDialog(
                             this,
                             "Choose Gap Color",
                             gapColorPanel.getBackground());
        if(newColor != null){
            this.DEFAULT_GAP_COLOR = newColor;
            this.gapColorPanel.setBackground(newColor);
            this.setConnectionColor();
        }
}//GEN-LAST:event_changeGapColorButtonActionPerformed

    private void changeSceneColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeSceneColorButtonActionPerformed
        Color newColor = JColorChooser.showDialog(
                             this,
                             "Choose Background Color",
                             sceneColorPanel.getBackground());
        if(newColor != null){
            this.DEFAULT_SCENE_COLOR = newColor;
            this.sceneColorPanel.setBackground(newColor);
            this.scene.setBackground(newColor);
        }
}//GEN-LAST:event_changeSceneColorButtonActionPerformed

    private void showGapsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGapsCheckBoxActionPerformed
        if(this.showGapsCheckBox.isSelected()){
            showGapsConnections(true);
        }else{
            showGapsConnections(false);
        }
    }//GEN-LAST:event_showGapsCheckBoxActionPerformed

    private void showLinksCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLinksCheckBoxActionPerformed
        if(this.showLinksCheckBox.isSelected()){
            showLinkConnections(true);
        }else{
            showLinkConnections(false);
        }

    }//GEN-LAST:event_showLinksCheckBoxActionPerformed

    

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void fitGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitGraphMenuItemActionPerformed
        this.refit();
    }//GEN-LAST:event_fitGraphMenuItemActionPerformed

    private void savePictureMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePictureMenuItemActionPerformed
        Rectangle sceneBounds = scene.getBounds();
//        double f = scene.getZoomFactor();
//        float ww = this.centerPanel.getWidth();
//        float hh = this.centerPanel.getHeight();
//        int w = (int) (ww / f);
//        int h = (int) (hh / f);

        // If the image is too large a memory error occurs,
        // 2000 should be high enough resolution for now.
        // This occurs when zoomed-out far I noticed.
        int h = Math.min(sceneBounds.height, 2000);
        int w = Math.min(sceneBounds.width, 2000);


        BufferedImage bi = null;
        try{
            bi = new BufferedImage (w, h, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = bi.createGraphics ();
            scene.paint (graphics);
            graphics.dispose ();
        } catch(Exception ex){
          JOptionPane.showMessageDialog(null, "Exception:  " + ex.toString());
          return;
        }

        JFileChooser chooser = new JFileChooser ();
        chooser.setDialogTitle ("Export Graph ...");
        chooser.setDialogType (JFileChooser.SAVE_DIALOG);
        chooser.setMultiSelectionEnabled (false);
        chooser.setFileSelectionMode (JFileChooser.FILES_ONLY);

        chooser.showSaveDialog(this);
        File file = chooser.getSelectedFile();
        if(file != null){
            if (! file.getName ().toLowerCase ().endsWith (".png")){ // NOI18N
                file = new File (file.getParentFile (), file.getName () + ".png"); // NOI18N
            }
            if (file.exists ()) {
                int confirm = JOptionPane.showConfirmDialog(this, "This file already exists.  Overwrite?", "Save file", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION){
                    try {
                        ImageIO.write (bi, "png", file); // NOI18N
                        JOptionPane.showMessageDialog(this, "File saved to " + file.getAbsolutePath(), "File Saved", JOptionPane.PLAIN_MESSAGE);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Error Saving File: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else{
                try {
                   ImageIO.write (bi, "png", file); // NOI18N
                   JOptionPane.showMessageDialog(this, "File saved to " + file.getAbsolutePath(), "File Saved", JOptionPane.PLAIN_MESSAGE);
                } catch (IOException e) {
                   JOptionPane.showMessageDialog(this, "Error Saving File: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }//GEN-LAST:event_savePictureMenuItemActionPerformed

    private void showOtherCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOtherCheckBoxActionPerformed
        if(this.otherConnections.size() == 0){
            return;
        }
        if(this.showOtherCheckBox.isSelected()){
            showOtherTypeConnections(true);
        } else{
            showOtherTypeConnections(false);
        }
}//GEN-LAST:event_showOtherCheckBoxActionPerformed

    private void gapCentricMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gapCentricMenuitemActionPerformed
        this.arrangeGapCentric();
    }//GEN-LAST:event_gapCentricMenuitemActionPerformed


    public void showOtherTypeConnections(boolean show){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof ConnectionWidget){
                    ConnectionWidget cw = (ConnectionWidget) widget;
                    if(this.otherConnections.contains(cw) == true){
                        if(show){
                            cw.setVisible(true);
                        } else{
                           cw.setVisible(false);
                        }
                    }
                }
            }
        }
        scene.validate();
    }

    public void addToGapList(String termA, String termB){
        this.listModel.addElement(termA + " - " + termB);
    }

    public void showLinkConnections(boolean show){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof ConnectionWidget){
                    ConnectionWidget cw = (ConnectionWidget) widget;
                    if(this.connectionValues.containsKey(cw)){
                        if(show){
                            cw.setVisible(true);
                        } else{
                           cw.setVisible(false);
                        }
                    }
                }
            }
        }
        scene.validate();
    }

    public void showGapsConnections(boolean show){
        if(this.gapConnections == null || this.gapConnections.size() == 0){
            this.setStatusMessage("No gaps defined.");
            return;
        }
        for (Object object : this.gapConnections) {
            ConnectionWidget cw = (ConnectionWidget) object;
            if(show){
                cw.setVisible(true);
            } else{
               cw.setVisible(false);
            }
        }
        this.scene.validate();
    }

    /**
     * Adjusts term widgets to fit the scene view.
     * @return
     */
    public void refit(){
        Rectangle sceneBounds = scene.getBounds();

        double radius = (sceneBounds.getHeight() / 2);
        double a = 0;
        double b = 0;

        float h = this.centerPanel.getHeight();

        int height = (int) (h / this.scene.getZoomFactor());

        radius = height / 2;

        int numOfTerms = 0;
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof TermWidget){
                    numOfTerms++;
                }
            }
        }

        double startingangle = 0;
        double angles = Math.PI * 2; // number of possible angles in a circle, in radians
        double increment = angles / numOfTerms; // distance between each term widget, in angles

        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof TermWidget){
                    TermWidget tw = (TermWidget) widget;

                    double x = a + (radius * Math.cos(startingangle));
                    double y = b + (radius * Math.sin(startingangle));
                    int x1 = (int) x;
                    int y1 = (int) y;
                    Point p = new Point(x1, y1);
                    scene.getSceneAnimator().animatePreferredLocation(tw, p);

                    startingangle += increment;
                }
            }
        }
    }

    /**
     * Arranges the terms in a hierarchical fashion where the nodes with the most
     * gaps are at the top of the graph.
     */
    public void arrangeGapCentric(){

        if(this.gapConnections.size() == 0){
            return;
        }

        Vector<Vector> graphVector = new Vector<Vector>();
        Hashtable<TermWidget, Integer> gapsPerTerm = new Hashtable<TermWidget, Integer>();

        Enumeration<ConnectionWidget> e = this.gapConnections.elements();
        int max=0;
        while(e.hasMoreElements()){
            ConnectionWidget cw = e.nextElement();
            TermWidget a = (TermWidget) cw.getSourceAnchor().getRelatedWidget();
            TermWidget b = (TermWidget) cw.getTargetAnchor().getRelatedWidget();
            // Keep track of how many connection widgets are connected to each term widget.
            // The term widget with the most gap connections will be the root, or shared root.
            int value = 0;
            if(gapsPerTerm.containsKey(a)){
                value = gapsPerTerm.get(a);
                value++;
                gapsPerTerm.put(a, value);
            } else{
                gapsPerTerm.put(a, 1);
            }
            if(value > max){
                max = value;
            }
            if(gapsPerTerm.containsKey(b)){
                value = gapsPerTerm.get(b);
                value++;
                gapsPerTerm.put(b, value);
            } else{
                gapsPerTerm.put(b, 1);
            }

            if(value > max){
                max = value;
            }
        }

        // initalize each element in graphVector
        for (int i = 0; i <= max; i++) {
            Vector v = new Vector();
            v.add(null);
            graphVector.add(v);
        }

        //now put the values in a vector of vectors / directed graph
        Enumeration<TermWidget> e2 = gapsPerTerm.keys();
        int nonzeroelems = 0; // indexes that are non-zeroes (for ySpacing)
        while(e2.hasMoreElements()){
            TermWidget tw = e2.nextElement();
            int count = gapsPerTerm.get(tw);
            Vector vec = graphVector.get(count);
            if(vec.elementAt(0) == null){
                vec.removeElementAt(0);
                nonzeroelems++;
            }
            vec.add(tw);
        }

        float h = this.centerPanel.getHeight();
        int scenewidth = (int) (this.centerPanel.getWidth() / this.scene.getZoomFactor());
        int ySpacing = (int) (h / this.scene.getZoomFactor() / nonzeroelems); // how far apart each level in the tree is
        int y = 0; // starting y coordinate
        //draw the location for each term widget, starting at the highest index
        for (int j = max; j >= 0; j--) {
            Vector xterms = graphVector.get(j);
            int n = xterms.size();
            if(n > 0 && xterms.elementAt(0) != null){
                int xSpacing = scenewidth / (n + 1);
                int x = scenewidth / (n + 1); // starting x coordinate
                for (Object o : xterms) {
                    TermWidget t = (TermWidget) o;
                    Point p = new Point(x, y);
                    scene.getSceneAnimator().animatePreferredLocation(t, p);
                    x += xSpacing;
                }
                y += ySpacing;
            }
        }

        // any term widgets left over have no gaps, so put them at the bottom tier
        Vector<TermWidget> leftovers = new Vector<TermWidget>();
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof TermWidget){
                    TermWidget tw = (TermWidget) widget;
                    if(gapsPerTerm.containsKey(tw) == false){
                        leftovers.add(tw);
                    }
                }
            }
        }

        // do the animation
        y += ySpacing;
        int x = scenewidth / (leftovers.size() + 1);
        int xSpacing = scenewidth / (leftovers.size() + 1);
        for (TermWidget t : leftovers) {
            Point p = new Point(x, y);
            scene.getSceneAnimator().animatePreferredLocation(t, p);
            x += xSpacing;
        }

    }


    public boolean isAnimated(){
        
        return false;
    }

    /**
     * Randomly moves the term widgets in the scene by a small amount.
     */
    public void animateJitter(){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof TermWidget){
                    TermWidget tw = (TermWidget) widget;

                    Point p = tw.getPreferredLocation();
                    Random r = new Random();
                    boolean b = r.nextBoolean();
                    boolean b2 = r.nextBoolean();
                    int movespace = 3;
                    if(b){
                       p.setLocation(p.x - movespace, p.y);
                    }else{
                       p.setLocation(p.x + movespace, p.y);
                    }
                    if(b2){
                       p.setLocation(p.x, p.y - movespace);
                    }else{
                       p.setLocation(p.x, p.y + movespace);
                    }
                    scene.getSceneAnimator().animatePreferredLocation(tw, p);
                }
            }
        }
    }

    public void updateZoomLabel(){
        double zf = Math.round(scene.getZoomFactor() * 1000);
        this.zoomLabel.setText("Zoom Factor: " + (zf / 1000));
    }
    
    /** Adjusts color of child nodes.
     * @param c 
     */
    public void setNodeColor(Color c){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof CircleWidget){
                    CircleWidget cw = (CircleWidget) widget;
                    cw.setUserBackgroundColor(c);                     
                }
            }
            
            scene.validate();
            
        }        
    }

    /**
     * Sets the font colors for term widgets.
     * @param c
     */
    public void setTermColor(Color c){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                if (widget instanceof CircleWidget){
                    // do nothing
                } else if (widget instanceof LabelWidget) {
                    widget.setForeground(c);
//                    widget.setBorder(BorderFactory.createRoundedBorder(this.defaultBorderSize, this.defaultBorderSize, c, Color.BLACK));
                }
            
            }        
        }
        scene.validate();
    }

    /**
     * Sets the color of the connection lines based upon the connection line's type.
     */
    public void setConnectionColor(){
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                 if(widget instanceof ConnectionWidget){
                     ConnectionWidget cw = (ConnectionWidget) widget;
                     if(this.gapConnections.contains(cw)){
                         cw.setForeground(this.DEFAULT_GAP_COLOR);
                     } else{
                         cw.setForeground(this.DEFAULT_LINK_COLOR);
                     }
                     Color fg = cw.getForeground();
                    int red = fg.getRed();
                    int green = fg.getGreen();
                    int blue = fg.getBlue();
                    Color transparentColor = new Color(red, green, blue, this.DEFAULT_LINE_ALPHA);
                    cw.setForeground(transparentColor);
                 }
            }
        }
        scene.validate();
    }

    public void setSceneColor(Color c){
        this.scene.setBackground(c);
    }

    public void setTermList(String[] termList){
        this.termList = termList;
    }
    
    /**
     * Sets the filter checkboxes.
     * @param options
     */
    public void setFilterOptions(SearchCriteria criteria){
        this.filterPanel.setSelectedItems(criteria);
    }
    
    public void setStatusMessage(String msg){
        this.statusLabel.setText("Status: " + msg);
    }    
    
    public void setParentOwner(CustomDesktopPane cdp){
        this.parentOwner = cdp;
        FilterPanel fp = this.parentOwner.globals.createFilterPanel();
        fp.setLayoutStyle("vertical");
        fp.setWidth(110);
        setFilterPanel(fp);
    }

    public void setGapsLinkages(float[][] linkages){
        this.linkages = linkages;
    }

    /**
     * Checks if a linkage exists in the table between two terms.
     * If there is a linkage then mark as a gap.
     * Returns true if there is a gap.  Assumes that the coocurrence
     * between termA and termB is 0.
     * @param termA
     * @param termB
     * @return
     */
    public boolean isGap(String termA, String termB){
        if(this.linkages == null){
            return false;
        }
        int indexA = -1;
        int indexB = -1;
        // find the position of these terms in the term list
        for (int i = 0; i < termList.length; i++) {
            String term = termList[i];
            if(term.equalsIgnoreCase(termA)){
                indexA = i;
            }
            if(term.equalsIgnoreCase(termB)){
                indexB = i;
            }
        }

        // there was a problem; terms not found in the term list.
        if(indexA == -1 || indexB == -1){
            return false;
        }

        float[] row = this.linkages[indexA];
        float linkVal = row[indexB + 1]; // offset column by one because first column contains the terms, not linkages.
        if(linkVal > 0){
            return true;
        } else{
            return false;
        }

    }
    /**
     * Checks if termA and termB have 0 in the linkage table,
     * meaning that they are not a gap or linkage type.
     * Assumes that the coocccurence is greater than 0.
     * @param termA
     * @param termB
     * @return
     */
    public boolean isOtherType(String termA, String termB){
        if(this.linkages == null){
            return false;
        }
        int indexA = -1;
        int indexB = -1;
        // find the position of these terms in the term list
        for (int i = 0; i < termList.length; i++) {
            String term = termList[i];
            if(term.equalsIgnoreCase(termA)){
                indexA = i;
            }
            if(term.equalsIgnoreCase(termB)){
                indexB = i;
            }
        }

        // there was a problem; terms not found in the term list.
        if(indexA == -1 || indexB == -1){
            return false;
        }

        float[] row = this.linkages[indexA];
        float linkVal = row[indexB + 1]; // offset column by one because first column contains the terms, not linkages.
        // Is a linkage if the linkage table is greater than 0.
        if(linkVal > 0){
            return false;
        } else{
            return true;
        }
    }

    /**
     * Resizes all of the term nodes according to their value.
     * Terms that occur more often have a larger font.
     */
    public void adjustFontSizeToResults(){
        // Font size is a percentage compared to number of occurrences.
        for (Widget layers : scene.getChildren()){
            for (Widget widget : layers.getChildren()){
                 if(widget instanceof TermWidget){
                     TermWidget tw = (TermWidget)widget;
                     float sizepercent = tw.termCount / this.max_results_length;
                     int newfontsize = Math.max(this.defaultFontSize, (int) (this.maxFontSize * sizepercent));
                     newfontsize = Math.min(newfontsize, this.maxFontSize);
                     tw.setFontSizeRelativeToValue(newfontsize);
                 }
            }
        }
    }

    /**
     * Resizes all of the connection nodes based upon the frequency between two terms.
     */
    public void adjustLineSizeToResults(){
        Enumeration e = this.connectionValues.keys();
        while(e.hasMoreElements()){
            ConnectionWidget connection = (ConnectionWidget) e.nextElement();
            int hitSize = this.connectionValues.get(connection);

            if(hitSize == 0){
                return;
            }

            // The thickness of the connection line depends on the number of
            // results for those two term widgets.
            float sizepercent = hitSize / this.max_connection_size;
            float strokesize = sizepercent * this.maxLineThickness;
            strokesize = Math.min(strokesize, this.maxLineThickness);
            strokesize = Math.max(strokesize, 1);

            Stroke stroke = new BasicStroke(strokesize);
            connection.setStroke(stroke);

        }
    }
    
    /**
     * Adds term widgets to a page in the shape specified.
     * @param terms 
     * @param shape
     */
    public void addTerms(String[] terms, String shape){
            if (shape.equals("circle")){

                this.termList = terms;

                int numOfTerms = terms.length;
                Rectangle rec = scene.getBounds();  

                //int a = 150;
                //int b = 150;

                double radius = (rec.getWidth() / 2) * .8;
                double a = 0;
                double b = 0;

                double startingangle = 0;
                double angles = Math.PI * 2; // number of possible angles in a circle, in radians
                double increment = angles / numOfTerms;

                // Add each term as a term widget.
                for (int i = 0; i < terms.length; i++) {
                    String term = terms[i];

                    double x = a + (radius * Math.cos(startingangle));
                    double y = b + (radius * Math.sin(startingangle));

                    int x1 = (int) x;
                    int y1 = (int) y;

                    this.createTermWidget(term, x1, y1);

                    startingangle += increment;
                }
            }
            refit();
        }//end method addTerms
    
    /**
     * Starts rendering the child widgets for the terms on the page.  Called in DrawTask.
     */
    public void connectTermsByGroup(){

        SearchCriteria criteria = this.getSearchCriteria();

        if(criteria == null || criteria.getYears().size() < 1){
           this.sf.dispose();
           JOptionPane.showMessageDialog(this, "Please choose at least one filter criteria. ", "Search Criteria Not Found", JOptionPane.ERROR_MESSAGE);
           return;
        }

        int widgetCount = 0;
        this.setStatusMessage("Processing...");
        TermWidget[] parentWidgets;
        // Count how many widgets are on the page
        for (Widget w : mainLayer.getChildren()){
            if (w instanceof TermWidget) {
                widgetCount++;
            }
        }
        // Store each parent/term widget in an array
        parentWidgets = new TermWidget[widgetCount];
        int p=0;
        for (Widget w : mainLayer.getChildren()){
            if (w instanceof TermWidget){
                parentWidgets[p] = (TermWidget) w;
                p++;
            }
        } 
        MatrixController mc;
        // Grab the cooccurence values for each term on the screen
        // and render the connecting nodes.
        if (widgetCount > 1){
            mc = new MatrixController(this.parentOwner.getGlobals(), this.parentOwner.globals.getMetaDataController());
            for (int i = 0; i < parentWidgets.length; i++) {
                TermWidget a = parentWidgets[i];
                for (int j = 0; j < parentWidgets.length; j++) {
                    
                    //user cancelled the graphing
                    if(this.cancel == true){
                        return;
                    }

                    TermWidget b = parentWidgets[j];
                    // If the two term widgets have already been queried/processed,
                    // then skip over them.  This will allow term widgets to be added
                    // to the page after processing the Graph button.
                    if (j > i && a.relativeNodes.contains(b) == false){

                        String termA = a.getLabel();
                        String termB = b.getLabel();
                        // Go through each widget/term and grab all the documents.
                        try {
                            this.sf.setLabel("Processing " + termA);
                            float ii = i;
                            float len = parentWidgets.length;
                            float pr =  (ii / len) * 100;
                            int prog = (int) pr;
                            this.sf.setProgressBar("", prog);
                           // Get the values for each term by itself so we can adjust the font size
                           int asum = mc.countTerms(termA, termA, criteria);
                           int bsum = mc.countTerms(termB, termB, criteria);

                           if(asum > this.max_results_length){
                               this.max_results_length = asum;
                           }
                           if(bsum > this.max_results_length){
                               this.max_results_length = bsum;
                           }

                           // Store the results length in each widget.
                           a.setTermCountValue(asum);
                           b.setTermCountValue(bsum);

                           // Search for the term value for each widget, and draw a connecting widget.               
                           Hashtable<String, Integer> results = mc.getCooccurenceDocNames(termA, termB, criteria);
                           if (results.size() > 0){
                               drawBubbleAssociation(a, b, results);
                               a.addRelativeNode(b);
                               b.addRelativeNode(a);
                           }else{
                               //if a gaps table is provided, see if there is a gap.
                               if(this.linkages != null){
                                   boolean isgap = isGap(termA, termB);
                                   if(isgap){
                                       // Keep track of the gaps in gapsTable with TermWidget objects.
                                       if(this.gapsTable.contains(a)){
                                           Vector v = this.gapsTable.get(this.gapsTable.indexOf(a));
                                           v.add(b);
                                       }else{
                                           Vector<TermWidget> v = new Vector<TermWidget>();
                                           v.add(b);
                                           this.gapsTable.add(v);
                                       }
                                       addToGapList(termA, termB);
                                       drawConnection(a, b, 1, true, false);
                                   }
                               }
                           }
//                           scene.validate();
                        } catch (Exception ex) {
                            this.sf.dispose();
                            JOptionPane.showMessageDialog(this, ex.toString(), "Warning", JOptionPane.ERROR_MESSAGE);
                        }
                    }//end if
                }//end for
            }//end for

            // resize term nodes according to how often they occur
            this.adjustFontSizeToResults();
            // resize connections according to how often they occur
            this.adjustLineSizeToResults();

            //this.setConnectionColor(); // update the colors of the connectors


        }                                          
        else if (widgetCount == 1){
           mc = new MatrixController(this.parentOwner.getGlobals(), this.parentOwner.globals.getMetaDataController());
           Widget a = parentWidgets[0];
           LabelWidget lwA = (LabelWidget) a;
           String termA = lwA.getLabel();
            // Go through each widget/term and grab all the documents.
            try {
               // Search for the term value for each widget, and draw a connecting widget.               
               Hashtable<String, Integer> results = mc.getCooccurenceDocNames(termA, null, null);
               drawAssociation(lwA, null, results);
               //widgetHash.put(lwA, results);
               //System.out.println("Results has: " + results.length);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("ERROR: " + ex.toString());
            }
        }
        this.sf.dispose();
        this.setStatusMessage("Done.");
        scene.validate();

    }

    /**
     * Places a 'term widget' widget in the scene at the x - y coordinate specified.
     * @param term
     * @param x
     * @param y
     */
    private void createTermWidget(String term, int x, int y){
            TermWidget w = new TermWidget(scene, term);
//            w.setPreferredLocation(new Point(x, y));

            scene.getSceneAnimator().animatePreferredLocation (w, new Point (x,y));

            w.setFont(new java.awt.Font("Arial", Font.PLAIN, this.defaultFontSize));
            
            // width, height, background, text color.
//            w.setBorder(BorderFactory.createRoundedBorder(this.defaultBorderSize, this.defaultBorderSize, this.DEFAULT_TERM_COLOR, Color.BLACK));
            w.getActions().addAction(ActionFactory.createMoveAction(new TermMoveStrategy(), null));
            w.getActions().addAction(ActionFactory.createSelectAction(new EatEventSelectProvider()));
            w.getActions().addAction(ActionFactory.createInplaceEditorAction(new RenameEditor()));

            w.setToolTipText(term);

            mainLayer.addChild(w);
            scene.validate();
    }



    /**
     * Displays a new panel/graph of agency information for one or more terms.
     * @param a
     */
    @SuppressWarnings("static-access")
    void createAgencyPanel(String termA, String termB){
        
        SearchCriteria criteria = this.getSearchCriteria();
        if (criteria == null){
            return;
        }
        
        // Display the graph window.
        AgencyGraph aGraph = new AgencyGraph(this.parentOwner.globals, termA, termB, criteria);
        
    }

    public void connectTermsWorker(){
        try{
            this.sf = new StatusFrame(this);
            this.sf.setLabel("Processing...");
            this.sf.setProgressBar("0", 0);
            statusFrameWorker = new StatusFrameWorker(this);
            statusFrameWorker.execute();
//            connectTermsByGroup();
           // This will run the connectTermsByGroup function in a separate thread.
//           DrawTask task = new DrawTask(this);
//           task.execute();
        }
        catch(Exception ex){
            System.out.println("***********************");
            System.out.println(ex.toString());
            System.out.println("***********************");
        }
    }

    public void cancel(){
        // If the cancel button is pressed while drawing the graph
        this.cancel = true;
        if(this.statusFrameWorker != null){
           this.statusFrameWorker.cancel(true);
        }
    }

    public void resetCancel(){
        this.cancel = false;
    }
        
    /**
     * Renders nodes and connecting widgets from one or more term widgets.
     * All components rendered, except for the connecting widgets, are label widgets.
     * @param widgetA
     * @param widgetB
     * @param nodes
     */
    public void drawAssociation(LabelWidget widgetA, LabelWidget widgetB, Hashtable<String, Integer> nodes){

        Stack parentStack = new Stack();
        int parentBX;
        int parentBY;
        // Get the location of the widgets.
        Point locationA = widgetA.getLocation();
        int parentAX = (int) locationA.getX();
        int parentAY = (int) locationA.getY();

        int xPOS;
        int yPOS;
        int x_INC;
        int y_INC;
        // If widgetB is null, then we are just using one widget to draw nodes around.
        // If there is more than one parent widget then render the node between the two.
        // Otherwise, render the nodes in a shape around the single parent term widget.
        if(widgetB != null){
            Point locationB = widgetB.getLocation();
            parentBX = (int) locationB.getX();
            parentBY = (int) locationB.getY();
            xPOS = (parentAX + parentBX) / 2;
            yPOS = (parentAY + parentBY) / 2;
            x_INC = -1;
            y_INC = 1;
        } else{
            xPOS = parentAX + 90;
            yPOS = parentAY - 190;
            x_INC = -10;
            y_INC = 10;
        }

        // Draw nodes in a circle around the term.
        // Number of nodes per degree in the circle.
        int offset = nodes.size() / 360;

        // if offset is 0, then there are less nodes than degrees.
        if (offset == 0) {
                offset = 1;
            }

        int count=0;
        // go through each returned result and draw a connecting node
        for(String node: nodes.keySet()){

            Widget nodeWidget = new LabelWidget(scene, "");
            parentStack.push(nodeWidget);

            // Place the child widget
            nodeWidget.setPreferredLocation(new Point(xPOS, yPOS));

            xPOS += x_INC;
            yPOS += y_INC;

            // node size
            nodeWidget.setPreferredSize(new Dimension(10,10));

            // width, height, background, text color.
            nodeWidget.setBorder(BorderFactory.createRoundedBorder(2, 2, Color.YELLOW, Color.BLACK));
            nodeWidget.setToolTipText(node);
            nodeWidget.getActions().addAction(ActionFactory.createAlignWithMoveAction(mainLayer, interractionLayer, null));
            mainLayer.addChild(nodeWidget);

            ConnectionWidget connection = new ConnectionWidget(scene);
            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
            connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(widgetA));
            connection.setTargetAnchor(AnchorFactory.createRectangularAnchor((nodeWidget)));
            connectionLayer.addChild(connection);
            if (widgetB != null){
                ConnectionWidget connectionB = new ConnectionWidget(scene);
                connectionB.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
                connectionB.setSourceAnchor(AnchorFactory.createRectangularAnchor(widgetB));
                connectionB.setTargetAnchor(AnchorFactory.createRectangularAnchor((nodeWidget)));
                connectionLayer.addChild(connectionB);            
            }        

            count++;


        } // end for

            m1.put(widgetA.getLabel(), parentStack);
            objectScene.addObject(m1);
    }  

    /**
     * Draws a connecting widget between every term widget in the scene.
     * The connectors between each term widget represent the number of 
     * occurences that each term occur in together.
     * @param widgetA
     * @param widgetB
     * @param nodes
     */
    void drawBubbleAssociation(TermWidget widgetA, TermWidget widgetB, Hashtable<String, Integer> nodes){

        Stack parentStack = new Stack();
        int parentBX;
        int parentBY;
        // Get the location of the widgets.
        Point locationA = widgetA.getLocation();
        Point locationB;
        int parentAX = (int) locationA.getX();
        int parentAY = (int) locationA.getY();

        int xPOS;
        int yPOS;

        // If widgetB is null, then we are just using one widget.
        // If there is more than one parent widget then render the node between the two.
        // Otherwise, render the nodes in a shape around the single parent term widget.
        if(widgetB != null){
            locationB = widgetB.getLocation();
            parentBX = (int) locationB.getX();
            parentBY = (int) locationB.getY();
            xPOS = (parentAX + parentBX) / 2;
            yPOS = (parentAY + parentBY) / 2;
        } else{
            xPOS = parentAX + 90;
            yPOS = parentAY - 190;
        }

        widgetA.getActions().addAction(ActionFactory.createPopupMenuAction(new ChildPopupMenuProvider(this)));
        widgetB.getActions().addAction(ActionFactory.createPopupMenuAction(new ChildPopupMenuProvider(this)));
        boolean isOtherType = isOtherType(widgetA.getLabel(), widgetB.getLabel());

        try{
            int nodesize = 0;
            Enumeration<String> keys = nodes.keys();
            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                int v = nodes.get(key);
                nodesize += v;
            }
           drawConnection(widgetA, widgetB, nodesize, false, isOtherType);
        } catch(Exception ex){
           JOptionPane.showMessageDialog(null, "Error drawing connection: ", "Exception", JOptionPane.ERROR_MESSAGE);
        }

        m1.put(widgetA.getLabel(), parentStack);

        scene.validate();
    }

    /**
     * Draws a connection widget between two term (label) widgets.
     *
     * @param a
     * @param b
     * @param hitSize
     * @param isGap
     * @param isOtherType
     */
    private void drawConnection(TermWidget a, TermWidget b, int hitSize, boolean isGap, boolean isOtherType){
        if (b != null){
            ConnectionWidget connection = new ConnectionWidget(scene);
            connection.setTargetAnchorShape(AnchorShape.NONE);
//            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
//            connection.setSourceAnchorShape(AnchorShape.TRIANGLE_FILLED);
            connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(a));
            connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(b));

            a.addConnectionReference(connection);
            b.addConnectionReference(connection);

            
            connection.setToolTipText(a.getLabel() + " - " + b.getLabel() + ": " + hitSize);

            // keep track of the highest number of results, used for setting line thickness.
            if(hitSize > this.max_connection_size){
                this.max_connection_size = hitSize;
            }

//            double hardmin = 0; // by default show all connections at all zoom levels
//            double softmin = this.defaultzoomlevel; // by default show all connections at this zoom
//            double showall = 1.0;
//            float strokesize = (hitSize / 10) + 1; // min is 1
//
//            // keep track of these connection objects
            if(isGap){
                this.gapConnections.add(connection);
                connection.setForeground(this.DEFAULT_GAP_COLOR);
            }else if(isOtherType){
                this.otherConnections.add(connection);
            } else{
                this.connectionValues.put(connection, hitSize);
            }
//
//            if(strokesize >= this.maxLineThickness){
//               strokesize = this.maxLineThickness;
//            } else if(isGap == false){
//              double visfactor = (this.maxLineThickness - strokesize) / this.maxLineThickness; // percent between 0 and 1
//              softmin = showall * visfactor;
//              softmin = softmin + this.defaultzoomlevel;
//              hardmin = this.defaultzoomlevel;
//            }
//
//            // all lines visible at zoom factor of 1, and the thickest lines are
//            // visible always where hard min = 0.
//
//
//            // hard min, soft min, soft max, hard max.
//            // hard min = visible lightly
//            // soft min = completely visible
//            // As we zoom in the zoom factor increases, so to make a widget invisible
//            // we set it's hard min less than the desired zoom factor.
//            // Weak lines are visible only on higher zoom factors.
////            Widget root = new LevelOfDetailsWidget (scene, hardmin, softmin, Double.MAX_VALUE, Double.MAX_VALUE);
////            root.addChild(connection);
////            scene.addChild (root);
//
            Stroke stroke;
            if(isGap){
                float dash[] = {10.0f};
                // dashed line
                stroke = new BasicStroke(1,
                                         BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_MITER,
                                         10.0f,
                                         dash,
                                         0.0f);
            }else{
                stroke = new BasicStroke(1);
            }

            connection.setStroke(stroke);

            Color fg = connection.getForeground();
            int red = fg.getRed();
            int green = fg.getGreen();
            int blue = fg.getBlue();
            Color transparentColor = new Color(red, green, blue, this.DEFAULT_LINE_ALPHA);
            connection.setForeground(transparentColor);
            connectionLayer.addChild(connection);
            scene.validate();
        }
    }
    
    /**
     * Builds search filter criteria based upon which 
     * check boxes are checked.
     * @return criteria Hashtable Associated key/value pairs.
     */
    public SearchCriteria getSearchCriteria(){
        return this.filterPanel.getSelectedItems();
    }
    

    
    /***************************/
    /* END METHOD DECLARATIONS */
    /***************************/
    
    
    /**********************************/
    /*   CUSTOM WIDGET HELPER CLASSES */
    /**********************************/
    
    /* Controls implementation of new widgets. */
    private class CreateProvider implements SelectProvider {

        int defaultBorderSize = 2;
        
        @Override
        public boolean isAimingAllowed(Widget widget, Point point, boolean b) {
            return true;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point point, boolean b) {
            return true;
        }

        // Handles adding of new widgets and mouse clicks.
        @Override
        public void select(Widget relatedWidget, Point point, boolean b) {
            Widget w = new LabelWidget(scene, "Term - Double Click To Rename Me");
            w.setPreferredLocation(relatedWidget.convertLocalToScene(point));
  
            // width, height, background, text color.
            w.setBorder(BorderFactory.createRoundedBorder(this.defaultBorderSize, this.defaultBorderSize, Color.LIGHT_GRAY, Color.BLACK));
            w.getActions().addAction(ActionFactory.createAlignWithMoveAction(mainLayer, interractionLayer, null));
            w.getActions().addAction(ActionFactory.createSelectAction(new EatEventSelectProvider()));
            w.getActions().addAction(ActionFactory.createInplaceEditorAction(new RenameEditor()));
            
            mainLayer.addChild(w);
            scene.validate();

        }

    }
    
    /**
     * When term widgets are moved, the corresponding children widgets are moved.
     */
    
    private class TermMoveStrategy implements MoveStrategy{

        @Override
        public Point locationSuggested(Widget w, Point originalPoint, Point suggestedPoint) {
//            TermWidget tw = (TermWidget) w;
//            Hashtable children = tw.childNodes;
//            Enumeration e = children.elements();
//            // Go through each child node of this term widget and adjust it's position.
//            while(e.hasMoreElements()){
//                Object o = e.nextElement();
//                ChildNodeWidget childWidget = (ChildNodeWidget) o;
//                Vector parents = childWidget.parents;
//                Enumeration ep = parents.elements();
//
//                int[] parentX = new int[2];
//                int[] parentY = new int[2];
//
//                // Grab each parent's position so that the child node is centered between the two parents.
//                int parentCounter = 0;
//                while (ep.hasMoreElements() && parentCounter < 2){
//                    Object op = ep.nextElement();
//                    TermWidget twp = (TermWidget) op;
//                    // Get the position of this parent Widget
//                    Point location = twp.getLocation();
//                    parentX[parentCounter] = (int) location.getX();
//                    parentY[parentCounter] = (int) location.getY();
//                    parentCounter++;
//                }
//                int xPOS = (parentX[0] + parentX[1]) / 2;
//                int yPOS = (parentY[0] + parentY[1]) / 2;
//                childWidget.setPreferredLocation(new Point(xPOS, yPOS));
//
//            }
            
            return suggestedPoint;
        }
    }
     
    
  

    /* Allows user to rename the widget. */
    private class RenameEditor implements TextFieldInplaceEditor {
           
        @Override
        public boolean isEnabled(Widget arg0) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return ((LabelWidget) widget).getLabel();
        }

        @Override
        public void setText(Widget widget, String text) {
            TermWidget tw = (TermWidget) widget;
            tw.setLabel(text);
            // User removed all text, so delete the widget.
            if (text.length()==0){
                     //mainLayer.removeChild(widget);
                     tw.removeFromParent();
                     connectionLayer.removeChildren();
                    // Remove the connection widget.
                    // This doesn't work...so remove all connections for now.
                    for(Widget w : connectionLayer.getChildren()) {
                        ConnectionWidget connectionWidget = (ConnectionWidget)w;
                        if(connectionWidget.getSourceAnchor().getRelatedWidget().equals(w)) {
                            connectionLayer.removeChild(connectionWidget);
                            break;
                        }
                    }
            scene.validate();
            }
            widget.setToolTipText(text);
        }
    }

    /* Prevents new widget from appearing when user clicks existing widget. */
    private class EatEventSelectProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
            return false;
        }

        @Override
        public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
            return true;
        }

        @Override
        public void select(Widget w, Point p, boolean b) {

        }
    }
    
    /**
     * Pop-up menu provider for child nodes/widgets.
     */
    private class ChildPopupMenuProvider implements PopupMenuProvider, ActionListener{

         private JPopupMenu menu;
         private TermWidget thisWidget;
         
         public String ACTION_VIEW_LAWS = "View Documents";
         public String ACTION_AGENCY_GRAPH = "View Agency Graph";
         
         DrawFrame drawFramePointer;
         // Menu to display.
         public ChildPopupMenuProvider(DrawFrame df) {
            this.drawFramePointer = df;
        }
                
        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point p) {           
            this.thisWidget = (TermWidget) widget;

            menu = new JPopupMenu("Popup menu");

            // Create menu for each attached termwidget
            Vector relatives = thisWidget.relativeNodes;
            if(relatives != null && relatives.size() > 0){
                java.util.Collections.sort(relatives.subList(0, relatives.size() - 1 ));
                // build a sub menu of terms
                JMenu subMenu1 = new javax.swing.JMenu("View Documents");
                JMenu subMenu2 = new javax.swing.JMenu("View Agency Graph");

                for (Object o : relatives) {

                    TermWidget tw = (TermWidget) o;

                    JMenuItem item1 = new JMenuItem(tw.getLabel());
                    item1.setActionCommand(ACTION_VIEW_LAWS + "::" + tw.getLabel());
                    item1.addActionListener(this);
                    subMenu1.add(item1);

                    JMenuItem item2 = new JMenuItem(tw.getLabel());
                    item2.setActionCommand(ACTION_AGENCY_GRAPH + "::" + tw.getLabel());
                    item2.addActionListener(this);
                    subMenu2.add(item2);
                }
                menu.add(subMenu1);
                menu.add(subMenu2);
            }


            return menu;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            SearchCriteria criteria = this.drawFramePointer.getSearchCriteria();
            if (criteria == null){
                return;
            }

            // Display list of laws/granules.
            if (e.getActionCommand().contains(ACTION_VIEW_LAWS)){
                String s = e.getActionCommand();
                String[] arr = s.split("::");
                String termA = thisWidget.getLabel();
                String termB = arr[1];
                // Copied from FileProvider
                    if(termA != null && termB != null){
                        DocumentListWindow docWindow = new DocumentListWindow(
                                termA,
                                termB,
                                criteria,
                                this.drawFramePointer.parentOwner.globals );
                }
            } else if (e.getActionCommand().contains(ACTION_AGENCY_GRAPH)){
                String s = e.getActionCommand();
                String[] arr = s.split("::");
                String termA = thisWidget.getLabel();
                String termB = arr[1];
                // Display a graph of agencies and term counts
                createAgencyPanel(termA, termB);
            }
             
            
            scene.validate();
        } 
    }//end class declaration
    
    
    /**
     * Functionality for handling double-click display of files.
     */
    private class FileProvider implements EditProvider{
        DrawFrame drawFramePointer;
        
        public FileProvider(DrawFrame df){
            this.drawFramePointer = df;
        }
        
        @Override
        public void edit(Widget w) {
            SearchCriteria criteria = this.drawFramePointer.getSearchCriteria();
            if (criteria == null){
                return;
            }
               ChildNodeWidget cnw = (ChildNodeWidget) w;
                if(cnw.parentTermA != null && cnw.parentTermB != null){
                    DocumentListWindow docWindow = new DocumentListWindow(
                            cnw.parentTermA, 
                            cnw.parentTermB, 
                            criteria,
                            this.drawFramePointer.parentOwner.globals );
            }
        }
    } 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTermButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton changeGapColorButton;
    private javax.swing.JButton changeLinkColorButton;
    private javax.swing.JButton changeSceneColorButton;
    private javax.swing.JButton changeTermColorButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JPanel colorsPanel;
    private javax.swing.JPanel connectingLinesPanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel filterContainerPanel;
    private javax.swing.JMenuItem fitGraphMenuItem;
    private javax.swing.JMenuItem gapCentricMenuitem;
    private javax.swing.JPanel gapColorPanel;
    private javax.swing.JList gapJList;
    private javax.swing.JButton graphButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel linkColorPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JMenuItem savePictureMenuItem;
    private javax.swing.JPanel sceneColorPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JCheckBox showGapsCheckBox;
    private javax.swing.JCheckBox showLinksCheckBox;
    private javax.swing.JCheckBox showOtherCheckBox;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel termColorPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JLabel viewerLabel;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JPanel zoomPanel;
    // End of variables declaration//GEN-END:variables

}//end class DrawFrame


/* *****************************************/
/* CUSTOM WIDGET CLASSES AND SWING WORKER  */
/* *****************************************/



class TermWidget extends LabelWidget implements Comparable{

    MyScene scene;
    Vector<TermWidget> relativeNodes = new Vector<TermWidget>();
    Vector<ConnectionWidget> connections = new Vector();

    int sizemult = 1;
    int termCount = 0; // the number of times this term occurs by itself

    public TermWidget(MyScene scene, String label){
        super (scene, label);
        this.scene = scene;
        getActions ().addAction (scene.createWidgetHoverAction ());
        this.addRelativeNode(this);
    }
    @Override
    protected Rectangle calculateClientArea () {
        Rectangle r = this.adjustClientArea(this.sizemult);
        return r;
    }

    protected Rectangle adjustClientArea(int mult){
        this.sizemult = mult;
        Rectangle r = super.calculateClientArea();
        r.setSize(r.width * mult, r.height * mult);
        return r;
    }

    public void addConnectionReference(ConnectionWidget cw){
        this.connections.add(cw);
    }

    @Override
    protected void paintWidget () {
        super.paintWidget();
    }

    @Override
    protected void notifyStateChanged (ObjectState previousState, ObjectState state) {
        if (previousState.isHovered ()  == state.isHovered ())
            return;
        
        //getScene ().getSceneAnimator ().animateForegroundColor (this, state.isHovered () ? Color.YELLOW : Color.BLACK);
        if(state.isHovered()){
            // Set the color of the term widget
            Color fg = (Color) scene.getBackground();
            int red = 255 - fg.getRed();
            int green = 255 - fg.getGreen();
            int blue = 255 - fg.getBlue();
            Color c = new Color(red, green, blue, 255);
            getScene().getSceneAnimator().animateForegroundColor(this, c);

            // Set the color of the connections
            for (ConnectionWidget cw : this.connections) {
                // Make the connections darker
                Color cwfg = (Color) cw.getForeground();
                int cwred = cwfg.getRed();
                int cwgreen = cwfg.getGreen();
                int cwblue = cwfg.getBlue();
                Color cwc = new Color(cwred, cwgreen, cwblue, 255);
                getScene().getSceneAnimator().animateForegroundColor(cw, cwc);

            }

        } else{
            getScene().getSceneAnimator().animateForegroundColor(this, this.scene.dfPointer.DEFAULT_TERM_COLOR);
            scene.dfPointer.setConnectionColor(); // reset the connection colors
            
        }
        scene.validate();
    }

    /**
     * Sets the value for this term widget.  Updates the tooltip as well.
     * @param v
     */
    public void setTermCountValue(int v){
        this.termCount = v;
        this.setToolTipText(this.getLabel() + " - " + v);
    }

    /**
     * Set the font size in relation to the term counts.
     * @param fontsize
     */
    public void setFontSizeRelativeToValue(int fontsize){
      Font font = this.getFont();
      Font newfont = new Font(font.getName(), font.getStyle(), fontsize);
      this.setFont(newfont);
//      this.setToolTipText(this.getToolTipText() + " - font size: " + fontsize); // debugging
    }

    /**
     * Relative nodes are tracked for the right-click pop up menu.
     * @param rel
     */
    public void addRelativeNode(TermWidget rel){
        this.relativeNodes.add(rel);
    }


    @Override
    public int compareTo(Object o) {
        TermWidget c = (TermWidget) o;
        return this.getLabel().compareTo(c.getLabel());
    }

}


class ChildNodeWidget extends LabelWidget{
    public Color bgcolor;
    public boolean drawBorder = true;
    
    public DrawFrame drawFramePointer;
    
    public String parentTermA;
    public String parentTermB;
    
    public Vector parents;
    
    public ChildNodeWidget(Scene scene, String label, Color in_color, DrawFrame df, String termA, String termB) {
        super (scene);
        this.bgcolor = in_color;
        this.drawFramePointer = df;
        this.parentTermA = termA;
        this.parentTermB = termB;
        this.parents = new Vector();
    }
    
    public void setBorder(boolean border){
        this.drawBorder = border;
    }
    
    public void setUserBackgroundColor(Color c){
        this.bgcolor = c;
    }
    
    public void addParent(Widget w){
        this.parents.add(w);
    }
    
}


class CircleWidget extends ChildNodeWidget{
    private int r;
    public boolean isCircleWidget = true;
    
    public CircleWidget (Scene scene, String label, int radius, Color in_color, DrawFrame df, String termA, String termB) {
        super(scene, label, in_color, df, termA, termB);
        this.r = radius;
    }
    public void setRadius(int radius){
        this.r = radius;
    }

    @Override
    protected Rectangle calculateClientArea () {
        return new Rectangle (- r, - r, 2 * r + 1, 2 * r + 1);
    }
    @Override
    protected void paintWidget () {
        Graphics2D g = getGraphics ();
        g.setColor (super.bgcolor);
        g.setBackground(super.bgcolor);
        g.drawOval (- r, - r, 2 * r, 2 * r);
        g.fillOval(- r, - r, 2 * r, 2 * r);
        if (this.drawBorder) {
            paintBorder(g);
        }
    }
    // Paint the border black
    protected void paintBorder(Graphics g) {
        g.setColor(Color.black);
        g.drawOval(-r, -r , 2* r, 2 * r);
    }
    

}
 class DrawTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        private DrawFrame df;
        
        public DrawTask(DrawFrame df){
            this.df = df;
        }
        
        @Override
        public Void doInBackground() {
            try {
                df.connectTermsByGroup();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            
        }
 }


 class AnimatorWorker extends SwingWorker<Void, Void>{
    private DrawFrame df;

    public AnimatorWorker(DrawFrame df){
          this.df = df;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try{
            while (df.isAnimated()){
                df.animateJitter();
                Thread.sleep(500);
            }
        }
        catch(Exception e){

        }
        return null;
    }
    @Override
    public void done() {

    }

 }

 class MyScene extends ObjectScene{
     DrawFrame dfPointer;
     MyScene(DrawFrame df){
         super();
         this.dfPointer = df;
     }



     // Attempt at anti aliasing.
    @Override
     public void paintChildren () {
        Object anti = getGraphics ().getRenderingHint (RenderingHints.KEY_ANTIALIASING);
        Object textAnti = getGraphics ().getRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING);

        getGraphics ().setRenderingHint (
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        getGraphics ().setRenderingHint (
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        getGraphics ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, anti);
        getGraphics ().setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, textAnti);
        try{
            super.paintChildren ();
        }
        catch(Exception ex){
            // Exception will occur if you try and move the widgets while processing.
            // This appears to be a bug, so just capture the exception and don't do anything.
            //System.out.println("Paint Children Exception.");
        }
    }


 }

 class StatusFrameWorker extends SwingWorker<Void, Void> {
        /*
         * Executed in background thread.
         */
        StatusFrame sf;
        DrawFrame df;

        public StatusFrameWorker(DrawFrame df){
            // create table data for each panel
            this.df = df;
        }

        @Override
        public Void doInBackground() {
            df.connectTermsByGroup();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            if (this.isCancelled() == false && this.sf != null){
                // The status frame will dispose itself upon cancellation.
                this.sf.dispose();
            }
        }
}


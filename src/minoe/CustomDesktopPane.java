/*
 * 
 */

package minoe;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Dan Spiteri
 */
public class CustomDesktopPane extends javax.swing.JDesktopPane{

    Globals globals;
    Image img;
    public CustomDesktopPane(){
        try
        {
          //img = ImageIO.read(new File("gradient.png"));
                  
        }
        catch(Exception e){}//do nothing        
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img != null) {
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        }
        else {
            //g.drawString("Image not found", 50, 50);
        }
      }
    
    public void setGlobals(Globals globals){
        this.globals = globals;
    }
    
    public Globals getGlobals(){
        return this.globals;
    }
}

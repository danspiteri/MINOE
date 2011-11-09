package minoe;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
/**
 * Displays a splash image upon program execution.
 * @author Dan Spiteri
 */
public class SplashWindow extends JWindow{
    private BufferedImage img = null;
    private String statusMessage = "MINOE";
    
    public SplashWindow(){ 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x=100;
        int y=100;
        int width = 333;
        int height = 369;
        setBounds(x,y,width,height);
     
        try {
            img = ImageIO.read(new File("splash.png"));
        } catch (IOException e) {
        }
        
        // attempt to center the splash on the page
        this.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
        setVisible(true);
    }//end constructor
    
    //override paint method of panel
    @Override
    public void paint(Graphics g)
    {
        //draw the image
        if( img != null)
          g.drawImage(img,0,0, this);        
          g.setFont(new Font("Arial", Font.PLAIN, 11));
          g.drawString(this.statusMessage, 10, 360);

          
    }//end paint
    
    public void setStatusMessage(String message){
        this.statusMessage = message;
        repaint();
    }
    
}//end class SplashWindow

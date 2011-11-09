package minoe;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;

  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }

  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }

  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

    @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();
        double pageX = pageFormat.getImageableX();
        double pageY = pageFormat.getImageableY();
        int columns = (int)Math.ceil(componentToBePrinted.getWidth() / pageWidth);
        int rows = (int)Math.ceil(componentToBePrinted.getHeight() / pageHeight);
        if (pageIndex >= rows * columns)
        {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pageX, pageY);

        int sh = componentToBePrinted.getHeight();
        int sw = componentToBePrinted.getWidth();

        double x_factor = pageWidth / sw;
        double y_factor = pageHeight / sh;

        // Have to rescale according to the smallest dimension,
        // otherwise the image won't fit on the paper.
        // If you use two different scaling factors then
        // the image will look distorted.
        double scale_factor = 1;
        if(sh < pageHeight && sw < pageWidth){
            scale_factor = 1;
        } else {
            scale_factor = Math.min(x_factor, y_factor);
        }

        g2d.scale(scale_factor, scale_factor);

        disableDoubleBuffering(componentToBePrinted);
        componentToBePrinted.paint(g2d);
        enableDoubleBuffering(componentToBePrinted);
        return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}

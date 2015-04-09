/*
 * This class defines a panel with 2 axes and we can use it to draw three different lines
 */

package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JLabel;

public class Plot3Lines extends JLabel{
	public double[] datax = new double[800];
	public double[] datay = new double[800];
	public double[] dataz = new double[800];
	final int PAD = 20;
	 public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        int w = getWidth();
	        int h = getHeight();
	        // Draw Ordinate.
	        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
	        // Draw Abscissa.
	        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
	        // Draw labels.
	        Font font = g2.getFont();
	        FontRenderContext frc = g2.getFontRenderContext();
	        LineMetrics lm = font.getLineMetrics("0", frc);
	        float sh = lm.getAscent() + lm.getDescent();
	        // Ordinate label.
	        String s = "Value";
	        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
	        for(int i = 0; i < s.length(); i++) {
	            String letter = String.valueOf(s.charAt(i));
	            float sw = (float)font.getStringBounds(letter, frc).getWidth();
	            float sx = (PAD - sw)/2;
	            g2.drawString(letter, sx, sy);
	            sy += sh;
	        }
	        // Abscissa label.
	        s = "Sampel";
	        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
	        float sw = (float)font.getStringBounds(s, frc).getWidth();
	        float sx = (w - sw)/2;
	        g2.drawString(s, sx, sy);
	        
	        // Draw line acceX.
	        double xInc = (double)(w - 2*PAD)/(datax.length-1);
	        double scale = (double)(h - 2*PAD)/4;  // normalized to +-2g
	        g2.setPaint(Color.green .darker());
	        for(int i = 0; i < datax.length-1; i++) {
	            double x1 = PAD + i*xInc;
	            double y1 = h - PAD - scale*(2-datax[i]);
	            double x2 = PAD + (i+1)*xInc;
	            double y2 = h - PAD - scale*(2-datax[i+1]);
	            g2.draw(new Line2D.Double(x1, y1, x2, y2));
	        }
	        
	        // Draw line acceY.
	        double xInc1 = (double)(w - 2*PAD)/(datay.length-1);
	        double scale1 = (double)(h - 2*PAD)/4; // normalized to +-2g
	        g2.setPaint(Color.black.darker());
	        for(int i = 0; i < datay.length-1; i++) {
	            double x1 = PAD + i*xInc1;
	            double y1 = h - PAD - scale1*(2-datay[i]);
	            double x2 = PAD + (i+1)*xInc1;
	            double y2 = h - PAD - scale1*(2-datay[i+1]);
	            g2.draw(new Line2D.Double(x1, y1, x2, y2));
	        }
	        
	        // Draw line acceZ.
	        double xInc11 = (double)(w - 2*PAD)/(dataz.length-1);
	        double scale11 = (double)(h - 2*PAD)/4;  // normalized to +-2g.
	        g2.setPaint(Color.red.darker());
	        for(int i = 0; i < dataz.length-1; i++) {
	            double x1 = PAD + i*xInc11;
	            double y1 = h - PAD - scale11*(2-dataz[i]);
	            double x2 = PAD + (i+1)*xInc11;
	            double y2 = h - PAD - scale11*(2-dataz[i+1]);
	            g2.draw(new Line2D.Double(x1, y1, x2, y2));
	        }	        
	        
	    }
	 
	public void redraw(){
		 this.repaint();	 
	 }
	// 	This function will automatically adjust the signal amplitude to fit it into the panel
	/* 
	public double getMax(double[] data) {
		double max = -Integer.MAX_VALUE;
		for(int i = 0; i < data.length; i++) {
			if(data[i] > max)
				max = data[i];
		}
		return max;
	}
	*/
}

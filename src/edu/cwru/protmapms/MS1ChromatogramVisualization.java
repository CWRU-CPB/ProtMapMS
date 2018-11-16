/*

Copyright (C) Case Western Reserve University, 2018. All rights reserved. Please
read the LICENSE file carefully before using this source code.
 

 CASE WESTERN RESERVE UNIVERSITY EXPRESSLY DISCLAIMS ANY
 AND ALL WARRANTIES CONCERNING THIS SOURCE CODE AND DOCUMENTATION,
 INCLUDING ANY WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 FOR ANY PARTICULAR PURPOSE, AND WARRANTIES OF PERFORMANCE,
 AND ANY WARRANTY THAT MIGHT OTHERWISE ARISE FROM COURSE OF
 DEALING OR USAGE OF TRADE. NO WARRANTY IS EITHER EXPRESS OR
 IMPLIED WITH RESPECT TO THE USE OF THE SOFTWARE OR
 DOCUMENTATION.
 
Under no circumstances shall University be liable for incidental, special,
indirect, direct or consequential damages or loss of profits, interruption
of business, or related expenses which may arise from use of source code or 
documentation, including but not limited to those resulting from defects in
source code and/or documentation, or loss or inaccuracy of data of any kind.

*/
package edu.cwru.protmapms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;

/**
 * Helper class to draw an MS1 chromatogram as a PNG file.
 * 
 * @author Sean Maxwell
 */
public class MS1ChromatogramVisualization {
    /**
     * Tries to generate colors with enough contrast to allow for easy
     * distinction on the SIC plot. It starts to return black for all subsequent
     * queries after 30 unique colors have been returned.
     *
     * @return The next color available, or black if too many calls have been
     * made
     */
    private static Color getColor(int colorIndex) {
        switch(colorIndex) {
            case   0: return new Color(255,0,0);   /* Red */
            case   1: return new Color(0,255,0);   /* Green */
            case   2: return new Color(0,0,255);   /* Blue */
            case   3: return new Color(255,0,255); /* Violet */
            case   4: return new Color(255,128,0); /* Orange */
            case   5: return new Color(0,255,255); /* Cyan */
            case   7: return new Color(128,0,0);   /* Dark Red */
            case   8: return new Color(0,128,0);   /* Dark Green */
            case   9: return new Color(0,0,128);   /* Dark Blue */
            case  10: return new Color(128,0,128); /* Dark Violet */
            case  11: return new Color(128,64,128);/* Dark Orange */
            case  12: return new Color(0,128,128); /* Dark Cyan */
            case  13: return new Color(128,64,32); /* Brown */
            case  14: return new Color(128,0,255); /* Purple*/
            case  15: return new Color(208,208,208);/*Grey 3 */
            case  16: return new Color(192,192,192);/*Grey 3 */
            case  17: return new Color(176,176,176);/*Grey 4 */
            case  18: return new Color(160,160,160);/*Grey 5 */
            case  19: return new Color(144,144,144);/*Grey 6 */
            case  20: return new Color(128,128,128);/*Grey 7 */
            case  21: return new Color(255,128,128);/*Light Red */
            case  22: return new Color(128,255,128);/*Light Green*/
            case  23: return new Color(128,128,255);/*Light Blue */
            case  24: return new Color(255,128,255);/*Light Violet*/
            case  25: return new Color(255,128,255);/*Light Orange*/
            case  26: return new Color(128,255,255);/*Light Cyan */
            case  27: return new Color(64,32,16);  /* Dark Brown */
            case  28: return new Color(64,0,128);  /* Dark Purple*/
            case  29: return new Color(255,128,64);/* Light Brown */
            default : return new Color(0,0,0);     /* Black */
        }
    }
    
    public static void render(List<MS1Chromatogram> ms1s, Double fromRT, Double toRT, Map<String,List<Interval>> rtIntervals, String name, boolean logTransform) throws Exception {
        /* Full plot dimensions */
        int pwidth  = 800;
        int pheight = 600;

        /* Axis margins */
        int marginH = 20;
        int marginV = 20;

        /* Axis width and height */
        int cwidth    = pwidth  - (2*marginH);
        int cheight   = pheight - (2*marginV);
        
        /* Find max intensity across chromatograms to draw to use as scaling 
         * factor */
        Double maxIntensity = 0.0;
        for(MS1Chromatogram ms1 : ms1s) {
            if(ms1.maxIntensity() > maxIntensity) {
                maxIntensity = ms1.maxIntensity();
            }
        }
        
        if(logTransform) {
            maxIntensity = Math.log10(maxIntensity);
        }
        
        BufferedImage bi = new BufferedImage(pwidth, pheight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pwidth, pheight);
        
        Double rtai = (toRT-fromRT)/10.0;
        
        /* Draw the SIC spectrum file */
        g.setColor(Color.BLUE);
        g.setFont(new Font("Seriff",Font.BOLD,10));
        g.drawString(String.format("%s","foo"),
                        cwidth/3,
                        pheight+marginV-5);
            
        
        /* Draw the RT axis scale indicators */
        g.setFont(new Font("Seriff",Font.PLAIN,10));
        for(int i=0;i<11;i++) {
            g.setColor(Color.BLACK);
            g.drawString(String.format("%.2f",(fromRT+(i*rtai))/60),
                         (int)(i*(cwidth/10))+marginH-5,
                         pheight-marginV+15);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((int)(i*(cwidth/10))+marginH,
                       pheight-marginV,
                       (int)(i*(cwidth/10))+marginH,
                       pheight-marginV+5);
        }
        
        /* Draw the intensity scale indicators */
        g.setColor(Color.BLACK);
        if(logTransform) {
            g.drawString(String.format("10^%.0f",maxIntensity), 5, marginV);
            g.drawString(String.format("10^%.0f",maxIntensity/2), 5, (pheight-marginV)/2);
            g.drawString("1", 5, pheight-marginV);
        }
        else {
            g.drawString(String.format("%.1e",maxIntensity), 5, marginV);
            g.drawString(String.format("%.1e",maxIntensity/2), 5, (pheight-marginV)/2);
            g.drawString("0", 5, pheight-marginV);
        }

        /* Create the axis border */
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(marginH,marginV,cwidth,cheight);
        
               
        /* Draw each chromatogram */
        ms1s.sort(null);
        int colorIndex = 0;
        for(MS1Chromatogram ms1 : ms1s) {
            g.setColor(getColor(colorIndex));
            for(int i=1;i<ms1.size();i++) {
                if(logTransform) {
                    g.drawLine((int)((ms1.RT(i-1)-fromRT)/(toRT-fromRT)*cwidth)+marginH,
                               (int)(cheight-(((Math.log10(ms1.Intensity(i-1)+1)/maxIntensity)*cheight)))+marginV,
                               (int)((ms1.RT(i)-fromRT)/(toRT-fromRT)*cwidth)+marginH,
                               (int)(cheight-(((Math.log10(ms1.Intensity(i)+1)/maxIntensity)*cheight)))+marginV);
                }
                else {
                    g.drawLine((int)((ms1.RT(i-1)-fromRT)/(toRT-fromRT)*cwidth)+marginH,
                               (int)(cheight-(((ms1.Intensity(i-1)/maxIntensity)*cheight)))+marginV,
                               (int)((ms1.RT(i)-fromRT)/(toRT-fromRT)*cwidth)+marginH,
                               (int)(cheight-(((ms1.Intensity(i)/maxIntensity)*cheight)))+marginV);
                }
            }
            
            for(Interval rt : rtIntervals.get(ms1.key())) {
                Double mzRtFrom = rt.start;
                Double mzRtTo = rt.end;
                int x1 = (int)((mzRtFrom-fromRT)/(toRT-fromRT)*cwidth)+marginH;
                int x2 = (int)((mzRtTo-fromRT)/(toRT-fromRT)*cwidth)+marginH;
                g.fillRect(x1,
                           marginV+(colorIndex*5),
                           x2-x1,
                           4);
            }
            colorIndex++;
        }
        
        /* Draw the legend listing m/z values of the different peptide species
         * represented in the chromatogram. Drwan last so it is over top of any
         * chromatogram lines */
        colorIndex = 0;
        int baseX = pwidth-marginH-100;
        int baseY = marginV+5;
        for(MS1Chromatogram ms1 : ms1s) {
            g.setColor(getColor(colorIndex));

            g.drawLine(baseX,baseY+(colorIndex*10)+5,baseX+10,baseY+(colorIndex*10)+5);
            if(ms1.isLabeling()) {
                g.drawOval(baseX+3, baseY+(colorIndex*10)+3, 4, 4);
            }

            g.setColor(Color.BLACK);
            String legendStr = String.format("(%d) %s",ms1.chargeState(),ms1.key());
            if(ms1.massOffset() == 0)
                g.drawString(legendStr,baseX+15,baseY+(colorIndex*10)+10);
            else
                g.drawString(legendStr+String.format(" + %d",ms1.massOffset()),baseX+15,baseY+(colorIndex*10)+10);
            
            colorIndex++;
        }
        
        /* */
        File f = new File(name+".png");
        ImageIO.write(bi, "png", f);
    }
}

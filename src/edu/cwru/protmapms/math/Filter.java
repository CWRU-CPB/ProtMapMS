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
package edu.cwru.protmapms.math;

import java.util.ArrayList;

/**
 * Exposes methods for filtering a Vector of values using different criteria.
 * 
 * @author Sean Maxwell
 */
public class Filter {    
    /**
     * Determines at what values of xObserved, the yObserved value is greater 
     * than the interpolated yExpected, and adds the index of xObserved to the
     * return set.
     * 
     * @param x X data to interpolate from
     * @param y Y data to interpolate from
     * @param xObserved X data to interpolate to yInterpolated data
     * @param yObserved Y data corresponding to each xObserved
     * 
     * @return Indices at which yObserved is greater than yExpected
     */
    public static ArrayList<Integer> linearClip(double[] x, double[] y, double[] xObserved, double[] yObserved) {
        ArrayList<Integer> indices = new ArrayList<>();
        int i;
        int j;
        double slope;
        double b;
        double yInterpolated;
        boolean end = false;
        
        /* Check for insufficient number of points */
        if(y.length < 2) {
            return indices;
        }
        
        /* Initialize slope and y intercept before starting interpolation. The
         * next x for interpolation is checked to see if it falls beyond the 
         * upper bound of the current fit range, and if so, the slope and y
         * intercept are recomputed. Doing things in this manner requires 
         * computing m0 and b0 before starting the loop, and then updating 
         * them until the last fit range is reached 
         *      y1        y3 
         *      /\        /
         *     /  \      /
         *    /    \    /
         *   /      \  /
         *y0/      y2\/
         * x0   x1   x2   x3
         * 
         * m0 = (y1-y0)/(x1-x0)
         * b0 = y1 - x1*m0;
         * 
         */
        slope = (y[1] - y[0]) / (x[1] - x[0]);
        b = y[1] - x[1]*slope;
        
        /* iterate over input */
        j = 0;
        for(i=0;i<xObserved.length;i++) {            
            /* if the next x value should be interpolated using the next largest
             * bin, increment the bin number, and recompute the slope and
             * y intercept */
            while(xObserved[i] > x[j+1]) {
                if(x[j+1] < x[x.length-1]) {
                    j++;
                    slope = (y[j+1] - y[j]) / (x[j+1] - x[j]);
                    b     = y[j+1] - x[j+1]*slope;
                }
                
                /* If x data point is outside the range of data we are using to 
                 * interpolate, it cannot be interpolated, so we break out of
                 * the loop. This truncates all data points from xObserved that
                 * fall beyond the highest x. */
                else {
                    end = true;
                    break;
                }
            }
            
            /* If the data is beyond the last bin */
            if(end) {
                break;
            }
            
            /* Interpolate xObserved */
            yInterpolated = slope*xObserved[i]+b;
            
            /* If the observed value is greater than the interpolated value, 
             * add xObserved, and yObserved to buffers for return */
            if(yInterpolated-yObserved[i] < -0.0001) {
                indices.add(i);
            }
        }
                
        return indices;
    }
}

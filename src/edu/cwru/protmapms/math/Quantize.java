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
 * Exposes methods for quantizing a Vector.
 * 
 * @author Sean Maxwell
 */
public class Quantize {   
    /**
     * Find the median of a set of values. If the set contains an odd number of
     * elements, the middle element is returned after sorting. If the set
     * contains an even number of elements, the average of the middle two
     * elements is returned after sorting.
     *
     * @param v Values in set
     *
     * @return Median
     */
    public static double median(ArrayList<Double> v) {
        /* Java built in sort is implemented as Quicksort. Good enough for
         * our purposes */
        v.sort(null);
        int l = v.size();

        /* zero length array has median 0 */
        if(l == 0) {
            return 0.0;
        }
        
        /* If even number of elements, average the two elements in the middle */
        if(l % 2 == 0) {
            return (v.get(l / 2) + v.get((l/2)-1)) / 2.0;
        }
        
        /* If odd number of elements, return the middle element */
        else {
            return v.get(l/2);
        }
    }
    
    /**
     * Quantizes an array <code>y</code> using median value in each bin with
     * bins defined as fixed intervals of <code>x</code>. If dimensions of
     * <code>x</code> and <code>y</code> do not match, an Exception is thrown.
     * If <code>binWidth</code> is &lt;= 0, and Exception is thrown.
     * 
     * @param x values to use for determining bins
     * @param y values to quantize
     * @param binWidth Width of bins
     * 
     * @return Quantized Vector with each element storing the median of one of
     * the quantized bins.
     * 
     * @throws Exception 
     */
    public static double[][] median(double[] x, double[] y, double binWidth) throws Exception {
        double[][] output;
        int         i;
        int         j;
        int     nBins;
        double    min;
        double    max;
        ArrayList<Double> bin;
              
        /* Check dimensions */
        if(x.length != y.length) {
            throw(new Exception("dimension mismatch of x and y"));
        }
        
        /* Check division by 0 */
        if(binWidth <= 0) {
            throw(new Exception("bin width must be greater than 0"));
        }
        
        /* Get min and max of input */
        min = x[0];
        max = x[x.length-1];
        
        /* Allocate the return vector */
        nBins  = (int)Math.round((max - min) / binWidth) + 1;
        output = new double[2][nBins];
        
        /* Iterate over the input and compute the median for each bin */
        i = 0;
        j = 1;
        bin = new ArrayList<>();
        while(i<x.length) {
            /* If the value is past the end of the current bin. This looks 
             * complicated because the bin is centered at min+(j-1)*binWidth,
             * so the end of the bin is at + half the bin width.*/
            while(x[i] >= (min+(binWidth/2)+(j-1)*binWidth)) {
                output[0][j-1] = min+(j-1)*binWidth;
                output[1][j-1] = median(bin);
                bin.removeAll(bin);
                j++;
            }
            bin.add(y[i]);
            i++;
        }
        
        if(min+(j-1)*binWidth > max) {
            output[0][j-1] = max;
        }
        else {
            output[0][j-1] = min+(j-1)*binWidth;
        }
        output[1][j-1] = median(bin);
        
        
        return output;
    }
    
}

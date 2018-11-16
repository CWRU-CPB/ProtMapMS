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

/**
 * Implements methods for some common mathematical operations that are not in
 * the core Math object.
 * 
 * @author Sean Maxwell
 */
public class MathX {

    /**
     * Arithmetic mean
     *
     * @param v Values to use for calculating mean
     *
     * @return Arithmetic mean
     */
    public static double mean(double[] v) {
        double sum = 0.0;
        int i;

        for(i=0;i<v.length;i++) {
            sum += v[i];
        }

        return sum / v.length;
    }

    /**
     * Arithmetic standard deviation
     *
     * @param v Values to use for calculating arithmetic standard deviation
     * @param m Arithmetic mean of the argument values
     *
     * @return Arithmetic standard deviation
     */
    public static double sdev(double[] v, double m) {
        int i;
        double total = 0.0;
        double diff  = 0.0;

        for(i=0;i<v.length;i++) {
            diff = v[i]-m;
            total += Math.pow(diff,2);
        }

        if(v.length == 1) {
            return Math.sqrt(total);
        }
        else {
            return Math.sqrt(total/(v.length));
        }
    }

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
    public static double median(double[] v) {
        /* Java built in sort is implemented as Quicksort. Good enough for
         * our purposes */
        java.util.Arrays.sort(v);
        int l = v.length;

        /* zero length array has median 0 */
        if(v.length == 0) {
            return 0.0;
        }
        
        /* If even number of elements, average the two elements in the middle */
        if(l % 2 == 0) {
            return (v[l / 2] + v[(l/2)-1]) / 2.0;
        }
        
        /* If odd number of elements, return the middle element */
        else {
            return v[(l/2)];
        }
    }
    
    public static double[] product(double[] f, double v) {
        int i;

        for(i=0;i<f.length;i++) {
            f[i] = f[i]*v;
        }

        return f;
    }

    public static double[] shift(double[] f, double dist) {
        int i;

        for(i=0;i<f.length;i++) {
            f[i] = f[i] + dist;
        }

        return f;
    }
    
    /**
     * Raises an argument vector of values to a power in place.
     * 
     * @param f
     * @param e
     * @return 
     */
    public static double[] pow(double[] f, int e) {
        int i;
        
        for(i=0;i<f.length;i++) {
            f[i] = Math.pow(f[i],e);
        }

        return f;
    }
    
    public static double sum(double[] f) {
        int i;
        double sum = 0.0;
        
        for(i=0;i<f.length;i++) {
            sum += f[i];
        }

        return sum;
    }
    
    public static double pSum(double[] f1, double[] f2, int e) throws Exception{
        int i;
        double sum = 0.0;
        
        /* Check for length mismatch */
        if(f1.length != f2.length) {
            throw new Exception(String.format("Arrays differ in length: %d != %d",f1.length,f2.length));
        }
        
        for(i=0;i<f1.length;i++) {
            sum += f1[i]*Math.pow(f2[i],e);
        }

        return sum;
    }
        
}

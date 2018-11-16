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
package edu.cwru.protmapms.scoring;

import edu.cwru.protmapms.math.PearsonCorrelation;

import java.util.List;

/**
 * Computes a Pearson correlation between two vectors and assesses the 
 * significance by conversion to t-score and table of critical values.
 * 
 * @author Sean Maxwell
 */
public class PearsonCorrelationScoring implements ScoringFunction {

    /**
     * Determine if the p-value of a t-score for a system with
     * <strong>n</strong> degrees of freedom is significant at p <= 0.001
     * level by lookup in column of critical values.
     *
     * @param t t-score
     * @param n Degrees of freedom
     *
     * @return true if significant, false if not significant
     */
    private boolean pCriticalInterpolate(double t, int n) {
        int i;
        double[] ptable = {
         1,636.6,2,31.6,3,12.92,4,8.61,5,6.869,6,5.959,7,5.408,8,5.041,9,4.781,
         10,4.587,11,4.437,12,4.318,13,4.221,14,4.14,15,4.073,16,4.015,17,3.965,
         18,3.922,19,3.883,20,3.85,21,3.819,22,3.792,23,3.767,24,3.745,25,3.725,
         26,3.707,27,3.69,28,3.674,29,3.659,30,3.646,40,3.551,50,3.496,60,3.46,
         80,3.416,120,3.373,1000000,3.291};
        
        for(i=2;i<ptable.length;i+=2) {
            if(ptable[i-2] <= n && ptable[i] >= n) {
                double df1 = ptable[i-2];
                double df2 = ptable[i];
                double t1 = ptable[i-1];
                double t2 = ptable[i+1];
                double intCritT = t1+(((t2-t1)/(df2-df1))*(n - df1));
                if(t > intCritT) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;

    }
    
    /**
     * Transforms a Pearson Correlation Coefficient into a t-score
     *
     * Formula from:
     *
     * http://luna.cas.usf.edu/~mbrannic/files/regression/corr1.html#Hypothesis%20Tests
     *
     * @param r Pearson correlation coefficient
     * @param n Number of observation pairs
     *
     * @return T Score transformation of r
     */
    private double tScore(double r, int n) {
        return r*Math.sqrt(n-2)/Math.sqrt(1-Math.pow(r,2));
    }
    
    @Override
    public Score score(List<Double> f1, List<Double> f2) {
        Double r = PearsonCorrelation.pearsonCorrelationCoefficient(f1, f2);
        Double t = tScore(r,f1.size());
        boolean pCritical = pCriticalInterpolate(t,f1.size());
        
        Score s = new Score();
        s.isSignificant=pCritical;
        s.score=r;
        return s;
    }
    
}

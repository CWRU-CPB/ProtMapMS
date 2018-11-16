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
 * Computes median quantiles for argument values and then uses a linear 
 * interpolated line between medians to filter values below the line.
 * 
 * @author Sean Maxwell
 */
public class NoiseModelingFilter {
    
    public static ArrayList<Integer> filter(double[] mzo, double[] io, double width) throws Exception {
        double[][] res;
        ArrayList<Integer> indices;
        
        /* Quantize observed values to use as noise floor */
        try {
            res = Quantize.median(mzo, io, width);
        }
        catch(Exception e) {
            System.err.printf("Error quantizing the following:\n");
            System.err.printf("mzObserved[]  = %s\n",java.util.Arrays.toString(mzo));
            System.err.printf("intObserved[] = %s\n",java.util.Arrays.toString(io));
            System.err.printf("width         = %f\n",width);
            e.printStackTrace(System.err);
            throw new Exception(e);
        }
        
        /* Filter observed values by rejecting those under noise floor */
        try {
            indices = Filter.linearClip(res[0], res[1], mzo, io);
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            throw new Exception(e);
        }
        
        return indices;
        
        
    }
        
}

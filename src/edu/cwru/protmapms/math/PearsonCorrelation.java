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

import java.util.List;

/**
 * Compute Pearson correlation coefficient between two equal length lists of
 * values.
 * 
 * @author Sean Maxwell
 */
public class PearsonCorrelation {   
    private static Double mean(List<Double> values) {
        Double m = 0.0;
        for(Double value : values) {
            m += value;
        }
        return(m/values.size());
    }
    
    public static Double pearsonCorrelationCoefficient(List<Double> xs, List<Double> ys) {
        Double eX = mean(xs);
        Double eY = mean(ys);
        Double XY = 0.0;
        Double X = 0.0;
        Double Y = 0.0;
        for(int i=0;i<xs.size();i++) {
            Double x = xs.get(i);
            Double y = ys.get(i);
            XY += (x-eX)*(y-eY);
            X += Math.pow(x-eX, 2);
            Y += Math.pow(y-eY, 2);
        }
        return(XY/(Math.sqrt(X)*Math.sqrt(Y)));
    }
   
}

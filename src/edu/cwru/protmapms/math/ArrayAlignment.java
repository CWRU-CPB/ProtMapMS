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
import java.util.List;

/**
 * Stores the result of aligning two arrays. This needs to be improved by either
 * making it more generic, or making it less generic, but right now it is too
 * in between.
 * 
 * @author Sean Maxwell
 */
public class ArrayAlignment {
    public List<Double> theoreticalIntensities;
    public List<Double> observedIntensities;
    public List<Double> observedMzValues;
    public List<Double> theoreticalMzValues;
    public int count;
    public Double maxIntensity;
    
    public ArrayAlignment() {
        theoreticalIntensities = new ArrayList<>();
        observedIntensities = new ArrayList<>();
        observedMzValues = new ArrayList<>();
        theoreticalMzValues = new ArrayList<>();
        count = 0;
        maxIntensity = 0.0;
    }
    
    public void add(Double theoreticalMZ, Double theoreticalInt, Double observedMz, Double observedInt) {
        theoreticalIntensities.add(theoreticalInt);
        observedIntensities.add(observedInt);
        observedMzValues.add(observedMz);
        theoreticalMzValues.add(theoreticalMZ);
        
        if(theoreticalMZ != 0.0)
            count++;
        
        if(observedInt.compareTo(maxIntensity) > 0) 
            maxIntensity=observedInt;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("X1\tY1\tX2\tY2\n");
        for(int i=0;i<observedIntensities.size();i++) {
            sb.append(String.format("%.2f\t%.2f\t%.2f\t%.2f\n",theoreticalMzValues.get(i),theoreticalIntensities.get(i),observedMzValues.get(i),observedIntensities.get(i)));
        }
        return sb.toString();
    }
    
    public void print() {
        System.out.printf(this.toString());
    }
}

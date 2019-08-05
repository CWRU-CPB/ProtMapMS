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

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an MS1 chromatogram, which is a sequence of intensities for 
 * a specific m/z value over a range of retention times. The object exposes
 * a method to integrate the chromatogram over a subinterval of retention times
 * to compute a peak area.
 * 
 * @author Sean Maxwell
 */
public class MS1Chromatogram implements Comparable<MS1Chromatogram> {
    private final List<Double> retentionTimes;
    private final List<Double> intensities;
    private final String key;
    private Double maxInt;
    private Boolean isLabeling;
    private Integer massOffset;
    private Integer chargeState;
    
    public MS1Chromatogram(String key) {
        retentionTimes = new ArrayList<>();
        intensities = new ArrayList<>();
        maxInt = 0.0;
        this.key=key;
        isLabeling=Boolean.FALSE;
        massOffset = 0;
    }
    
    public void add(Double retentionTime, Double intensity) {
        retentionTimes.add(retentionTime);
        intensities.add(intensity);
        
        if(intensity > maxInt) {
            maxInt = intensity;
        }
    }
    
    public MS1Chromatogram setIsLabeling(Boolean b) {
        isLabeling = b;
        return this;
    }
    
    public MS1Chromatogram setMassOffset(Integer i) {
        massOffset = i;
        return this;
    }
    
    public MS1Chromatogram setChargeState(Integer i) {
        chargeState = i;
        return this;
    }
    
    public Double maxIntensity() {
        return maxInt;
    }
    
    public int size() {
        return retentionTimes.size();
    }
    
    public Double RT(int i) {
        return retentionTimes.get(i);
    }
    
    public Double Intensity(int i) {
        return intensities.get(i);
    }
    
    public String key() {
        return key;
    }
    
    public Boolean isLabeling() {
        return isLabeling;
    }
    
    public Integer massOffset() {
        return massOffset;
    }
    
    public Integer chargeState() {
        return chargeState;
    }
    
    /**
     *   /\  
     *  /  \/\
     * /      \
     * 0 1 2 3
     * @param rtFrom
     * @param rtTo
     * @return 
     */
    public Double integrate(Double rtFrom, Double rtTo) {
        Double area = 0.0;
        for(int i=0;i<retentionTimes.size()-1;i++) {
            if(retentionTimes.get(i).compareTo(rtFrom) >= 0 &&
               retentionTimes.get(i+1).compareTo(rtTo) <= 0) {
                Double rtDiff = retentionTimes.get(i+1)-retentionTimes.get(i);
                
                /* This is always non-negative */
                Double rectangle = rtDiff*intensities.get(i);
                
                /* This area can be negative when retention time i+1 has
                 * lower intensity than than retention time i */
                Double triangle = rtDiff*(intensities.get(i+1)-intensities.get(i))/2.0;
                
                /* Update total integrated area between the two retention 
                 * times */
                area += rectangle+triangle;
            }
        }
        return area;
    }

    @Override
    public int compareTo(MS1Chromatogram o) {
        int f1 = this.chargeState().compareTo(o.chargeState());
        int f2 = this.isLabeling().compareTo(o.isLabeling());
        int f3 = this.massOffset().compareTo(o.massOffset());
        if(f1 != 0) return f1;
        if(f2 != 0) return f2;
        return f3;
    }
    
    /**
     * Generates a JSON formatted map containing the chromatogram data. The map
     * contains key,value pairs:
     * <ul>
     * <li>key: the m/z value that this chromatogram corresponds to</li>
     * <li>maxInt: Maximum intensity value (y value) in the chromatogram</li>
     * <li>int: array of intensity values (y values)</li>
     * <li>rt: array of retention time values (x values)</li>
     * </ul>
     * <br>
     * The int array is filtered to suppress runs of zeros between the first and
     * last data point extracted.
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        
        /* Add chromatogram key */
        sb.append("\"key\":\"");
        sb.append(this.key);
        sb.append("\",");
        
        /* Add maximum intensity */
        sb.append("\"maxInt\":");
        sb.append(this.maxInt.longValue());
        sb.append(",");
        
        /* Append retention times */
        StringBuilder rt = new StringBuilder("[");
        StringBuilder in = new StringBuilder("[");
        rt.append(this.retentionTimes.get(0).longValue());
        in.append(this.intensities.get(0).longValue());
        for(int i=1;i<this.intensities.size()-1;i++) {
            /* In text output, surpress output of contiguous 0s */
            if(this.intensities.get(i-1)==0 && this.intensities.get(i)==0 && this.intensities.get(i+1)==0)
                continue;
                
            rt.append(",");
            in.append(",");
            rt.append(this.retentionTimes.get(i).longValue());
            in.append(this.intensities.get(i).longValue());
        }
        rt.append(",");
        in.append(",");
        rt.append(this.retentionTimes.get(this.intensities.size()-1).longValue());
        in.append(this.intensities.get(this.intensities.size()-1).longValue());
        rt.append("]");
        in.append("]");
        
        /* Append intensities */
        sb.append("\"int\":");
        sb.append(in.toString());
        sb.append(",\"rt\":");
        sb.append(rt.toString());
        sb.append("}");

        return sb.toString();
    }
}

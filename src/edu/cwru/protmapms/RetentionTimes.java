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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Organizes retention time information by spectrum and monisotopic mass.
 * 
 * @author Sean Maxwell
 */
public class RetentionTimes extends HashMap<String,HashMap<String,List<ComparableRetentionTime>>> {
    public String referenceMz = null;
    
    public RetentionTimes() {
        super();
    }
    
    public void add(Identification identification) {
        String expKey = String.format("%.4f",identification.getExposureTime());
        if(!this.containsKey(expKey)) {
            this.put(expKey, new HashMap<>());
        }
        
        Integer z = identification.getCharge();
        Double mz = identification.getPrecursorMz();
        Double mie = (mz*z)-(z*Constants.MASS_HYDROGEN);
        String mzKey = String.format("%.6f",mie);

        if(!this.get(expKey).containsKey(mzKey)) {
            this.get(expKey).put(mzKey, new ArrayList<>());
        }
        
        ComparableRetentionTime rt = new ComparableRetentionTime(identification);
        if(!this.get(expKey).get(mzKey).contains(rt))
            this.get(expKey).get(mzKey).add(new ComparableRetentionTime(identification));
    }
    
    public void addAll(List<Identification> identifications) {
        for(Identification identification : identifications) {
            this.add(identification);
        }
    }
    
    public void setReferenceMz(String mz) {
        referenceMz = mz;
    }
    
    public ComparableRetentionTime getRetentionTimeWithGreatestIntensity(String rtKey, String mzKey) {
        if(!this.containsKey(rtKey)) return null;
        if(!this.get(rtKey).containsKey(mzKey)) return null;
        this.get(rtKey).get(mzKey).sort(null);
        return this.get(rtKey).get(mzKey).get(this.get(rtKey).get(mzKey).size()-1);
    }
    
    public List<ComparableRetentionTime> getRetentionTimes(String rtKey, String mzKey) {
        if(!this.containsKey(rtKey)) return null;
        if(!this.get(rtKey).containsKey(mzKey)) return null;
        return this.get(rtKey).get(mzKey);
    }
    
    public Set<String> getMzKeys(String expKey) {
        return this.get(expKey).keySet();
    }
    
    public boolean hasRetentionTimes(String expKey, String mzKey) {
        return this.containsKey(expKey) && this.get(expKey).containsKey(mzKey);
    }
    
    public Map<String,Double> getMzMap(String mzKey) {
        Map<String,Double> rtMap = new HashMap<>();
        for(String expKey : this.keySet()) {
            rtMap.put(expKey, this.getRetentionTimeWithGreatestIntensity(expKey, mzKey).getRetentionTime());
        }
        return rtMap;
    }
    
    public void print() {
        for(String expKey : this.keySet()) {
            for(String mzKey : this.get(expKey).keySet()) {
                List<ComparableRetentionTime> intervals = this.get(expKey).get(mzKey);
                for(ComparableRetentionTime rti : intervals) {
                    System.out.printf("%s\t%s\t%.4f [%.4f,\t%.4f]\n",expKey,mzKey,rti.getRetentionTime(),rti.getIntensity(),rti.getScore());
                }
            }
        }
    }
    
    public static List<Interval> getNonOverlappingIntervals(List<ComparableRetentionTime> retentionTimes, Double widen) {
        List<Interval> intervals = new ArrayList<>();
        retentionTimes.sort(null);
        TOP:for(ComparableRetentionTime retentionTime : retentionTimes) {
            Double start = retentionTime.getRetentionTime()-widen;
            Double end = retentionTime.getRetentionTime()+widen;
            for(Interval interval : intervals) {
                if(start.compareTo(interval.end)  < 0 && end.compareTo(interval.end) > 0)
                    interval.end = end;
                break TOP;
            }
            intervals.add(new Interval(start,end));
        }
        return intervals;
    }
}

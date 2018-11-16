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

import java.util.Objects;

/**
 * Encapsulates a retention time in seconds with an associated intensity and
 * score so that retention times can be ordered/ranked.
 * 
 * @author Sean Maxwell
 */
public class ComparableRetentionTime implements Comparable<ComparableRetentionTime> {
    private final Double retentionTime;
    private final Double intensity;
    private final Double score;
    
    public ComparableRetentionTime(Identification identification) {
        retentionTime = identification.getRetentionTime();
        intensity = identification.getPrecursorIntensity();
        score = identification.getScore();
    }
    
    public ComparableRetentionTime(Double retentionTime) {
        this.retentionTime = retentionTime;
        this.intensity=0.0;
        this.score=0.0;
    }
    
    public Double getRetentionTime() {
        return retentionTime;
    }
    
    public Double getIntensity() {
        return intensity;
    }
    
    public Double getScore() {
        return score;
    }

    @Override
    public int compareTo(ComparableRetentionTime o) {
        return this.retentionTime.compareTo(o.retentionTime);
    }
    
    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(ComparableRetentionTime.class) && this.retentionTime.equals(((ComparableRetentionTime)o).retentionTime);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.retentionTime);
        return hash;
    }
}

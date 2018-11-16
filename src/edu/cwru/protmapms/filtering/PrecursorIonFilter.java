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
package edu.cwru.protmapms.filtering;

import edu.cwru.protmapms.math.SortedArraysAligner;
import edu.cwru.protmapms.spectra.Peaks;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Removes precursor ions from a collection of Peaks (MS/MS scan data).
 * 
 * @author Sean Maxwell
 */
public class PrecursorIonFilter extends PeakFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrecursorIonFilter.class);
    
    public PrecursorIonFilter() {
        super();
        parameterized = true;
    }
    
    private Set<Integer> toSet(int[] indices) {
        Set<Integer> r = new HashSet();
        for(int index : indices) {
            r.add(index);
        }
        return r;
    }
    
    @Override
    public Peaks filter(Peaks rawPeaks, double[] precursors, double error) throws Exception {
        /* Remove the precursor ions from the raw peak data */
        Set<Integer> precursorIndexSet = toSet(SortedArraysAligner.getInRangePairs(rawPeaks.MZ, precursors, error)[0]);
        
        double[] pcRemovedMz = new double[rawPeaks.MZ.length-precursorIndexSet.size()];
        double[] pcRemovedInt = new double[rawPeaks.MZ.length-precursorIndexSet.size()];
        int j=0;
        int removed = 0;
        for(int i=0;i<rawPeaks.MZ.length;i++) {
            if(!precursorIndexSet.contains(i)) {
                pcRemovedMz[j]=rawPeaks.MZ[i];
                pcRemovedInt[j]=rawPeaks.Intensity[i];
                j++;
            }
            else {
                removed++;
                LOGGER.trace("Removing precursor ion {} of {} from spectrum",rawPeaks.MZ[i],precursorIndexSet.size());
            }
        }
        
        /* Verify that we filled the whole array */
        if(j != pcRemovedMz.length) {
            LOGGER.error("Unexpected number of ions in arrays after precursor removal: this invalidates any following steps");
            throw new Exception("Did not fill the full ion array after precursor removal");
        }
        LOGGER.trace("Matched and removed {} of {} precusors",removed,precursors.length);
        
        return new Peaks(pcRemovedMz,pcRemovedInt);
    }
}

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

import edu.cwru.protmapms.spectra.Peaks;

import java.util.ArrayList;

/**
 * A default filter chain that applies precursor removal and noise modeling 
 * filter to MS/MS peak data. 
 * 
 * @author Sean Maxwell
 */
public class StandardPeakFilterChain implements PeakFilterChain {
    private final ArrayList<PeakFilter> filters;

    public StandardPeakFilterChain() {
        filters = new ArrayList<>();
        filters.add(new PrecursorIonFilter());
        filters.add(new NoiseModelingPeakFilter());
    }
    
    public void add(PeakFilter filter) {
        filters.add(filter);
    }
    
    @Override
    public Peaks filter(Peaks peaks, double[] precursorIons, double ms2Err) throws Exception {
        Peaks filteredPeaks = peaks;
        for(PeakFilter peakFilter : filters) {
            if(peakFilter.isParameterized()) {
                filteredPeaks = peakFilter.filter(filteredPeaks, precursorIons, ms2Err);
            }
            else {
                filteredPeaks = peakFilter.filter(filteredPeaks);
            }
        }
        return filteredPeaks;
    }
}

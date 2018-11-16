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

import edu.cwru.protmapms.math.NoiseModelingFilter;
import edu.cwru.protmapms.spectra.Peaks;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters ions of a peak by fitting a line between the median of 20 uniform 
 * bins across all m/z values, and rejecting m/z values with intensities that
 * fall below the line.
 * 
 * @author Sean Maxwell
 */
public class NoiseModelingPeakFilter extends PeakFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoiseModelingPeakFilter.class);
    
    public NoiseModelingPeakFilter() {
        super();
        parameterized = false;
    }
    
    @Override
    public Peaks filter(Peaks peaks) throws Exception {
        /* Filter the m/z and intensity values in the scan using a noise
         * modeling filter that removes ions below a linear threshold that
         * is fit to the median of sequence of bins across the ions */
        ArrayList<Integer> peakIndices = NoiseModelingFilter.filter(peaks.MZ, peaks.Intensity, 20.0);
        LOGGER.trace("Noise filtered {} peaks down to {}",peaks.MZ.length,peakIndices.size());
        
        /* Subset the spectrum to only those peaks that passed filtering */
        double[] filteredMz = new double[peakIndices.size()];
        double[] filteredInt = new double[peakIndices.size()];
        int i = 0;
        for(int index : peakIndices) {
            filteredMz[i] = peaks.MZ[index];
            filteredInt[i] = peaks.Intensity[index];
            i++;
        }
        
        /* Package as a new Peaks object for further analysis */
        return new Peaks(filteredMz,filteredInt);
    }
}

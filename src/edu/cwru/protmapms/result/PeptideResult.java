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
package edu.cwru.protmapms.result;

import edu.cwru.protmapms.Peptide;

import java.util.HashMap;
import java.util.Arrays;

/**
 * Stores identification results associated with a specific peptide.
 * 
 * @author Sean Maxwell
 */
public class PeptideResult extends HashMap<String,SpectrumResult> {
    public Peptide peptide;
    
    public PeptideResult(Peptide p) {
        super();
        peptide = p;
    }
    
    public String[] getSpectrumKeys() {
        String[] keys = this.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        return keys;
    }
    
    public SpectrumResult getSpectrumResult(String spectrumKeyString) {
        if(!this.containsKey(spectrumKeyString)) {
            this.put(spectrumKeyString, new SpectrumResult());
        }
        return this.get(spectrumKeyString);
    }
    
}

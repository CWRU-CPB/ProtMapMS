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
import java.util.Set;

/**
 * Stores results associated with a specific protein accession.
 * 
 * @author Sean Maxwell
 */
public class ProteinResult extends HashMap<String,PeptideResult> {
    public ProteinResult() {
        super();
    }
    
    public PeptideResult getPeptideResult(Peptide peptide) {
        if(!this.containsKey(peptide.sequence)) {
            this.put(peptide.sequence, new PeptideResult(peptide));
        }
        return this.get(peptide.sequence);
    }
    
    public Peptide getPeptide(String peptideSequence) {
        return this.get(peptideSequence).peptide;
    }
    
    public Set<String> getPeptideSequences() {
        return this.keySet();
    }
}

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

import edu.cwru.protmapms.Constants;
import edu.cwru.protmapms.Identification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Stores identifications associated with a specific spectrum.
 * 
 * @author Sean Maxwell
 */
public class SpectrumResult {
    private final HashMap<String,ArrayList<Identification>> unlabeled;
    private final HashMap<String,ArrayList<Identification>> labeled;
        
    public SpectrumResult() {
        unlabeled = new HashMap<>();
        labeled = new HashMap<>();
    }
    
    public Set<String> getUnlabeledKeys() {
        return unlabeled.keySet();
    }
    
    public Set<String> getLabeledKeys() {
        return labeled.keySet();
    }
    
    public List<Identification> getLabeledIdentification(String key) {
        return labeled.get(key);
    }
    
    public List<Identification> getUnlabeledIdentification(String key) {
        return unlabeled.get(key);
    }
    
    public void addIdentification(Identification identification, HashMap<String,ArrayList<Identification>> map) {
        Integer z = identification.getCharge();
        Double mz = identification.getPrecursorMz();
        Double mie = (mz*z)-(z*Constants.MASS_HYDROGEN);
        String mzKey = String.format("%.6f",mie);
        if(!map.containsKey(mzKey)) {
            map.put(mzKey,new ArrayList<>());
        }
        map.get(mzKey).add(identification);
    }
    
    public void addIdentification(Identification identification) {
        if(identification.isLabeled()) {
            addIdentification(identification,labeled);
        }
        else {
            addIdentification(identification,unlabeled);
        }
    }
    
    public void addAll(List<Identification> identifications) {
        for(Identification identification : identifications) {
            addIdentification(identification);
        }
    }
}

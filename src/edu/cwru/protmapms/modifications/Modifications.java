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
package edu.cwru.protmapms.modifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A collection of Modification objects organized by amino acid or absolute
 * position in amino acid sequence.
 * 
 * @author Sean Maxwell
 */
public class Modifications {
    private HashMap<String,List<Modification>> modifications;
    
    public Modifications() {
        modifications = new HashMap<>();
    }
    
    private void addModification(String key, Modification m) {
        if(!modifications.containsKey(key)) 
            modifications.put(key,new ArrayList<>());
        
        modifications.get(key).add(m);
    }
    
    
    
    public void addModification(Modification m) {
        if(m.position.compareTo(-1) == 0) {
            addModification(m.aminoAcid,m);
        }
        else {
            addModification(String.format("%d",m.position), m);
        }
    }
    
    public List<Modification> getModifications(String aminoAcid) {
        if(modifications.containsKey(aminoAcid))
            return modifications.get(aminoAcid);
        else
            return new ArrayList<>();
    }
    
    public List<Modification> getModifications(Integer position) {
        String key = String.format("%d",position);
        if(modifications.containsKey(key))
            return modifications.get(key);
        else
            return new ArrayList<>();
    }
}

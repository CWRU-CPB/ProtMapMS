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

import edu.cwru.protmapms.Peptide;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumerates all combinations of modifications that could exist on an argument
 * peptide.
 * 
 * @author Sean Maxwell
 */
public class ModificationSiteEnumerator {
    private final List<ModificationSite> fixedModifications;
    private final List<List<ModificationSite>> variableModifications;
    private ListOfListsEnumerator lole;
    private final int maxConcurrentVariableModifications;
    
    public ModificationSiteEnumerator(Peptide peptide, Modifications modifications, int maxConcurrentVariableModifications) {      
        fixedModifications = new ArrayList<>();
        variableModifications = new ArrayList<>();
        this.maxConcurrentVariableModifications = maxConcurrentVariableModifications;
        
        OUTER: for(int i=0;i<peptide.length();i++) {
            List<ModificationSite> variableModificationsAtThisPosition = new ArrayList<>();
            
            /* Process modifications that are specified for this position in the
             * protein sequence */
            List<Modification> siteModifications = modifications.getModifications(i+peptide.start());
            for(Modification modification : siteModifications) {
                /* Fixed modifications override any other modifications at
                 * this site, so if one is found, we break out of the nested
                 * loop and move to the next position */
                if(modification.fixed()) {
                    fixedModifications.add(new ModificationSite(modification,i+peptide.start()));
                    continue OUTER;
                }
                else {
                    variableModificationsAtThisPosition.add(new ModificationSite(modification,i+peptide.start()));
                }
            }
            
            String aminoAcid = peptide.sequence().substring(i,i+1);
            List<Modification> residueModifications = modifications.getModifications(aminoAcid);
            for(Modification modification : residueModifications) {
                /* Fixed modifications override any other modifications at
                 * this site, so if one is found, we break out of the nested
                 * loop and move to the next position */
                if(modification.fixed()) {
                    fixedModifications.add(new ModificationSite(modification,i+peptide.start()));
                    continue OUTER;
                }
                else {
                    variableModificationsAtThisPosition.add(new ModificationSite(modification,i+peptide.start()));
                }
            }
            
            /* Add the stack of variable modificiations that match this
             * position to the list of stacks for enumerating all possible
             * combinations of modifications on this peptide */
            if(variableModificationsAtThisPosition.size() > 0) {
                variableModifications.add(variableModificationsAtThisPosition);
            }
        }
                
        lole = new ListOfListsEnumerator(variableModifications, maxConcurrentVariableModifications);
    }
    
    public List<ModificationSite> getFixed() {
        return fixedModifications;
    }
    
    public List<List<ModificationSite>> getVariable() {
        return variableModifications;
    }
            
    public List<ModificationSite> getNext() {
        List<ModificationSite> variable = lole.getNext();
        if(variable != null) {
            variable.addAll(fixedModifications);
        }
        return variable;
    }
    
    public void startOver() {
        lole = new ListOfListsEnumerator(variableModifications, maxConcurrentVariableModifications);
    }
    
    public double getTotalFixedOffset() {
        double offset = 0.0;
        for(ModificationSite modSite : fixedModifications) {
            offset += modSite.modification.massOffset;
        }
        return offset;
    }

}

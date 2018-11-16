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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import edu.cwru.protmapms.Peptide;

/**
 *
 * @author sean-m
 */
public class ModificationSiteEnumeratorTest {
    private Modifications modifications;
    private Peptide peptide;
    ModificationSiteEnumerator mse;
    
    public ModificationSiteEnumeratorTest() {
        
    }
    
    @Before
    public void setUp() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification("C","Carbamidomethylation",56.00);
        carbox.setFixed(true);
        
        Modification phosS = new Modification("S","Phosphorylation",16.00);
        Modification phosT = new Modification("T","Phosphorylation",17.00);
        Modification phosY = new Modification("Y","Phosphorylation",18.00);
        
        Modification hPhosY = new Modification("Y","Double Phosphorylation",36.00);
        
        modifications.addModification(carbox);
        modifications.addModification(phosS);
        modifications.addModification(phosT);
        modifications.addModification(phosY);
        modifications.addModification(hPhosY);
        
        peptide = new Peptide("ACASTYA",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,2);
    }

    @Test
    public void testFixedResidueOverridesVariableResidue() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification("C","",56.00);
        carbox.setFixed(true);
        
        Modification carbox2 = new Modification("C","",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(0,mse.getVariable().size());
        assertEquals(1,mse.getFixed().size());
    }
    
    @Test
    public void testFixedPositionOverridesVariablePosition() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification(1,"",56.00);
        carbox.setFixed(true);
        
        Modification carbox2 = new Modification(1,"",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(0,mse.getVariable().size());
        assertEquals(1,mse.getFixed().size());
    }
    
    @Test
    public void testFixedPositionOverridesVariableResidue() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification(1,"",56.00);
        carbox.setFixed(true);
        
        Modification carbox2 = new Modification("C","",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(0,mse.getVariable().size());
        assertEquals(1,mse.getFixed().size());
    }
    
    @Test
    public void testFixedResidueOverridesVariablePosition() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification(1,"",56.00);
        
        Modification carbox2 = new Modification("C","",16.00);
        carbox2.setFixed(true);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(0,mse.getVariable().size());
        assertEquals(1,mse.getFixed().size());
    }
    
    @Test
    public void testVariableResiduesAccumulate() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification("C","",56.00);
        
        Modification carbox2 = new Modification("C","",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(1,mse.getVariable().size());
        assertEquals(2,mse.getVariable().get(0).size());
    }
    
    @Test
    public void testVariablePositionsAccumulate() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification(1,"",56.00);
        
        Modification carbox2 = new Modification(1,"",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(1,mse.getVariable().size());
        assertEquals(2,mse.getVariable().get(0).size());
    }
    
    @Test
    public void testVariablePositionsAndResiduesAccumulate() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification(1,"",56.00);
        
        Modification carbox2 = new Modification("C","",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("C",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(1,mse.getVariable().size());
        assertEquals(2,mse.getVariable().get(0).size());
    }
    
    @Test
    public void testStacksAreConsecutiveAndNonEmpty() throws Exception {
        modifications = new Modifications();
        Modification carbox = new Modification("C","",56.00);
        Modification carbox2 = new Modification("C","",16.00);
        
        modifications.addModification(carbox);
        modifications.addModification(carbox2);
        
        peptide = new Peptide("TTTCTTT",1);
        
        mse = new ModificationSiteEnumerator(peptide,modifications,1);
        
        assertEquals(1,mse.getVariable().size());
        assertEquals(2,mse.getVariable().get(0).size());
    }
    
}

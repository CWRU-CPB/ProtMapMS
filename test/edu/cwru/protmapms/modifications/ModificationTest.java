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

/**
 *
 * @author sean-m1
 */
public class ModificationTest {
    
    public ModificationTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testSetFixed() throws Exception {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals(false,m1.fixed());
        m1.setFixed(true);
        assertEquals(true,m1.fixed());
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals(false,m2.fixed());
        m2.setFixed(true);
        assertEquals(true,m2.fixed());
    }

    @Test
    public void testSetLabeling() throws Exception {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals(false,m1.labeling());
        m1.setLabeling(true);
        assertEquals(true,m1.labeling());
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals(false,m2.labeling());
        m2.setLabeling(true);
        assertEquals(true,m2.labeling());
    }

    @Test
    public void testName() {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals("Test",m1.name());
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals("Test",m2.name());
    }

    @Test
    public void testAminoAcid() {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals(null,m1.aminoAcid());
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals("A",m2.aminoAcid());
    }

    @Test
    public void testPosition() {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals(0,m1.position().compareTo(1));
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals(0,m2.position().compareTo(-1));
    }

    @Test
    public void testMassOffset() {
        /* Test using position specific modification */
        Modification m1 = new Modification(1,"Test",1.0);
        assertEquals(0,m1.massOffset().compareTo(1.0));
        
        /* Test using residue specific modification */
        Modification m2 = new Modification("A","Test",1.0);
        assertEquals(0,m2.massOffset().compareTo(1.0));
    }
    
}

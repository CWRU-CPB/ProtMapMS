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

/**
 *
 * @author sean-m
 */
public class ModificationsTest {
    Modifications modifications;
    
    public ModificationsTest() {
        
    }
    
    @Before
    public void setUp() {
        modifications = new Modifications();
        Modification resMod1 = new Modification("A","ResMod1",1.0);
        Modification resMod2 = new Modification("A","ResMod2",2.0);
        Modification posMod = new Modification(2,"PosMod",2.0);
        
        modifications.addModification(posMod);
        modifications.addModification(resMod1);
        modifications.addModification(resMod2);
    }

    @Test
    public void testGetModifications_String() {
        List<Modification> mods = modifications.getModifications("A");
        assertEquals(2,mods.size());
    }

    @Test
    public void testGetModifications_Integer() {
        List<Modification> mods = modifications.getModifications(2);
        assertEquals(1,mods.size());
    }
    
}

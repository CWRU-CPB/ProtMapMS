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
package edu.cwru.protmapms;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class ResiduesTest {
    
    public ResiduesTest() {
    }
    
    @Test
    public void testConfigureAminoAcid() {
        try {
            Residues.configureAminoAcid('!', 123.0, 321.0);
            Residues.configureAminoAcid('~', 456.0, 654.0);
            assertEquals(123.0,Residues.getMI('!'),0.001);
            assertEquals(456.0,Residues.getMI('~'),0.001);
            assertEquals(321.0,Residues.getAvg('!'),0.001);
            assertEquals(654.0,Residues.getAvg('~'),0.001);
        }
        catch(Exception e) {
            fail("The test threw an exception when none expected");
        }
        
        try {
            Residues.configureAminoAcid(' ', 123.0, 321.0);
            fail("An exception was expected, but not thrown");
        }
        catch(Exception e) {
            
        }
    }

    @Test
    public void testGetMI() {
        assertEquals(71.037114,Residues.getMI('A'),0.001);
        assertEquals(99.068414,Residues.getMI('V'),0.001);
    }

    @Test
    public void testGetAvg() {
        assertEquals(71.0779,Residues.getAvg('A'),0.001);
        assertEquals(99.1311,Residues.getAvg('V'),0.001);
    }

    @Test
    public void testGetResidueCodes() {
        Character[] CODES = {
            'A','R','N','D',
            'C','E','Q','G',
            'H','I','L','K',
            'M','F','P','S',
            'T','U','W','Y',
            'V'};
        Set<Character> expCodes = new HashSet<>();
        expCodes.addAll(Arrays.asList(CODES));
        assertEquals(expCodes,Residues.getResidueCodes());
    }
    
}

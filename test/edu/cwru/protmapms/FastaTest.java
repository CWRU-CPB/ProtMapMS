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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class FastaTest {
    
    public FastaTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testLoad_Uniprot_Enforce_Exception() {
        try {
            Fasta f = new Fasta("test-data/valid.fasta");
            fail("Exception expected and not thrown");
        }
        catch(Exception e) {
            
        }      
    }
    
    @Test
    public void testLoad_Uniprot_Enforce() {
        try {
            Fasta f = new Fasta("test-data/valid-uniprot.fasta");
        }
        catch(Exception e) {
            fail("Exception thrown when none expected");
        }      
    }
    
    @Test
    public void testLoad_Uniprot_NoEnforce() {
        try {
            Fasta f = new Fasta("test-data/valid.fasta",false);
        }
        catch(Exception e) {
            fail("Exception thrown when none expected");
        }      
    }
    
    @Test
    public void testGetSequence_No_Uniprot_Enforce() throws Exception {
        Fasta f = new Fasta("test-data/valid.fasta",false);
        assertEquals("ACDE",f.getSequence("A"));
    }

    @Test
    public void testGetSequences_Uniprot_Enforce_No_Gene_Symbol() throws Exception {
        Fasta f = new Fasta("test-data/valid-uniprot.fasta");
        assertEquals("ACDE",f.getSequence("Q96S44"));
    }
    
    @Test
    public void testGetSequences_Uniprot_Enforce_With_Gene_Symbol() throws Exception {
        Fasta f = new Fasta("test-data/valid-uniprot.fasta");
        assertEquals("FGHI",f.getSequence("Q96S45_TP53"));
    }

    @Test
    public void testGetAccessions() {
        
    }

    @Test
    public void testSize() {
        
    }
    
}

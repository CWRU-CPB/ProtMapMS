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
package edu.cwru.protmapms.protease;

import java.util.regex.Matcher;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class ProteaseTest {
    Protease t;
    
    public ProteaseTest() {
        
    }
    
    @Before
    public void setUp() {
        t = new Protease("[RK]",1);
        t.addMatcher("DRI",1,1,0);
        t.addExclusion("YYRYTI", 2, 3);
    }

    @Test
    public void testOffset_0args() {
        System.out.println("offset()");
        int expResult = 1;
        int result = t.offset();
        assertEquals(expResult, result);
    }

    @Test
    public void testOffset_int() {
        System.out.println("offset(int)");
        int i = 0;
        int expResult = 0;
        int result = t.offset(i);
        assertEquals(expResult, result);
    }

    @Test
    public void testMatcherCount() {
        System.out.println("matcherCount");
        int expResult = 1;
        int result = t.matcherCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testExcluderCount() {
        System.out.println("excluderCount");
        int expResult = 1;
        int result = t.excluderCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsExclusion_true() {
        System.out.println("isExclusion expecting true");
        int i = 0;
        String seq = "TTTYYRYTITTT";
        int site = 5;
        boolean expResult = true;
        boolean result = t.isExclusion(i, seq, site);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsExclusion_false() {
        System.out.println("isExclusion expecting false");
        int i = 0;
        String seq = "TTTZYRYTITTT";
        int site = 5;
        boolean expResult = false;
        boolean result = t.isExclusion(i, seq, site);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsCleaveSite_true() {
        System.out.println("isCleaveSite expecting true");
        int i = 0;
        String seq = "TTTDRIYYY";
        int site = 4;
        int expResult = 0;
        int result = t.isCleaveSite(i, seq, site);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsCleaveSite_false() {
        System.out.println("isCleaveSite expecting false");
        int i = 0;
        String seq = "TTTGRIYYY";
        int site = 4;
        int expResult = -1;
        int result = t.isCleaveSite(i, seq, site);
        assertEquals(expResult, result);
    }
    
}

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
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sean-m
 */
public class MS1ChomatogramVisualizationTest {
    
    public MS1ChomatogramVisualizationTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testRender() throws Exception {
        MS1Chromatogram ms1 = new MS1Chromatogram("1");
        ms1.add(240.0, 100000.0);
        ms1.add(3000.0, 300.0);
        ms1.setIsLabeling(true);
        ms1.setMassOffset(16);
        ms1.setChargeState(2);
        
        MS1Chromatogram ms2 = new MS1Chromatogram("2");
        ms2.add(240.0, 50000.0);
        ms2.add(3000.0, 100000.0);
        ms2.setIsLabeling(false);
        ms2.setMassOffset(0);
        ms2.setChargeState(2);
        
        ArrayList<MS1Chromatogram> ms1s = new ArrayList<>();
        ms1s.add(ms1);
        ms1s.add(ms2);
        
        HashMap<String,List<Interval>> rtIntervals = new HashMap<>();
        List<Interval> l1 = new ArrayList<>();
        l1.add(new Interval(200.0,300.0));
        l1.add(new Interval(400.0,500.0));
        List<Interval> l2 = new ArrayList<>();
        l2.add(new Interval(900.0,1000.0));
        rtIntervals.put("1",l1);
        rtIntervals.put("2",l2);
        
        MS1ChromatogramVisualization.render(ms1s, 240.0, 3000.0, rtIntervals, "test",false);
    }
    
}

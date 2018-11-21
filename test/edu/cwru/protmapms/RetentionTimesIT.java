/*

 Copyright (C) Case Western Reserve University, 2014. All rights reserved. 
 This source code and documentation constitute proprietary information
 belonging to Case Western Reserve University. None of the foregoing
 material may be copied, duplicated or disclosed without the express
 written permission of Case Western Reserve University.

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
import java.util.List;

/**
 *
 * @author sean-m
 */
public class RetentionTimesIT {
    List<ComparableRetentionTime> crts;
    
    public RetentionTimesIT() {
    }
    
    @Before
    public void setUp() {
        Identification ul1 = new Identification(300.0,10,0.3).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(500.0).
                setExposureTime(1.0);
        Identification ul2 = new Identification(500.0,20,0.45).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(510.0).
                setExposureTime(0.0);
        Identification ul3 = new Identification(600.0,40,0.51).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(505.0).
                setExposureTime(0.0);
        crts = new ArrayList<>();
        crts.add(new ComparableRetentionTime(ul1));
        crts.add(new ComparableRetentionTime(ul3));
        crts.add(new ComparableRetentionTime(ul2));
    }

    @Test
    public void testGetNonOverlappingIntervals() {
        List<Interval> rti = RetentionTimes.getNonOverlappingIntervals(crts, 50.0);
        assertEquals(2,rti.size());
        assertEquals(250.0,rti.get(0).start,0.0001);
        assertEquals(350.0,rti.get(0).end,0.0001);
        assertEquals(450.0,rti.get(1).start,0.0001);
        assertEquals(650.0,rti.get(1).end,0.0001);
    }
    
    @Test
    public void testGetNonOverlappingIntervals2() {
        List<Interval> rti = RetentionTimes.getNonOverlappingIntervals(crts, 100.0);
        assertEquals(1,rti.size());
        assertEquals(200.0,rti.get(0).start,0.0001);
        assertEquals(700.0,rti.get(0).end,0.0001);
    }
    
}

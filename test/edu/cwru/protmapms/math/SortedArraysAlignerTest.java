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
package edu.cwru.protmapms.math;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class SortedArraysAlignerTest {
    
    public SortedArraysAlignerTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testAlignClosest_symmetry() throws Exception {
        int L = 1000;
        Random r = new Random();
                
        for(int TEST=1;TEST<1000;TEST++) {
            double[] X = new double[5*TEST];
            double[] Y = new double[5*TEST];
            double threshold = r.nextDouble()*3;
            
            for(int i=0;i<5*TEST;i++) {
                double x = r.nextDouble()*TEST;
                double y = r.nextDouble()*TEST;
                X[i] = x;
                Y[i] = y;
            }

            Arrays.sort(X);
            Arrays.sort(Y);

            int[][] a1 = SortedArraysAligner.alignClosestPairs(Y, X, threshold);
            int[][] a2 = SortedArraysAligner.alignClosestPairs(X, Y, threshold);

            //System.out.printf("------%d (threshold=%.2f)----\n",TEST,threshold);
            //System.out.printf("X=%s\n",Arrays.toString(X));
            //System.out.printf("Y=%s\n",Arrays.toString(Y));
            //System.out.printf("===A1===\n");
            //MzAligner.printPairs(a1,X,Y);

            //System.out.printf("===A2===\n");
            //MzAligner.printPairs(a2, Y, X);

            Assert.assertArrayEquals(a1[0],a2[1]);
            Assert.assertArrayEquals(a2[0],a1[1]);
        }
    }
    
    @Test
    public void testAlignClosestDependent() throws Exception {
        double[] X = {1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9};
        double[] Xd= {300,400,500,400,300,200,100,100,10};
        double[] Y = {1.6,1.8};
        
        ArrayAlignment a1 = SortedArraysAligner.alignClosestDependent(X, Xd, Y, 0.5);
        System.out.printf("===A1===\n");
        a1.print();
    }
    
    @Test
    public void testAlignClosestDependent2() throws Exception {
        double[] X = {1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9};
        double[] Xd= {100,100,200,300,400,500,600,700,10};
        double[] Y = {1.5,1.8};
        
        ArrayAlignment a1 = SortedArraysAligner.alignClosestDependent(X, Xd, Y, 1.0);
        System.out.printf("===A2===\n");
        a1.print();
    }
    
    @Test
    public void testAlignClosestDependent3() throws Exception {
        /* Expect                  *   *                 */
        double[] X = {1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9};
        double[] Xd= {100,100,200,300,800,500,600,700,10};
        double[] Y = {1.35,1.6};
        
        Double[] exp={0.0,0.0,0.0,1.0,1.0,0.0,0.0,0.0,0.0};
        List<Double> expL = new ArrayList<>(Arrays.asList(exp));
        
        ArrayAlignment a1 = SortedArraysAligner.alignClosestDependent(X, Xd, Y, 1.0);
        assertEquals(true,expL.equals(a1.theoreticalIntensities));
    }
    
    @Test
    public void testGetInRangePairs() throws Exception {
        /* Expect              *   *   *   *   *          */
        double[] X = {1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9};
        double[] Y = {1.35,1.6};
        int[] exp = {2,3,4,5,6};
        int[][] pairs = SortedArraysAligner.getInRangePairs(X, Y, 0.1);
        SortedArraysAligner.printPairs(pairs, X, Y);
        assertArrayEquals(exp,pairs[0]);
    }
    
}

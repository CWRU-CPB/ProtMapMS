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

/**
 * Fits a quadratic polynomial to argument data.
 * 
 * @author Sean Maxwell
 */
public class Polynomial {

    /**
     * Computes the coefficients a, b and c such that the polynomial
     * a + bx + cx^2 matches the argument data with minimal error.
     * 
     * This is based directly on the tutorial and example at:
     * 
     * http://www.personal.psu.edu/jhm/f90/lectures/lsq2.html
     * 
     * where John shows how the optimal solution occurs where the partial 
     * derivatives with respect to each coefficient are 0. This forms a system
     * of 3 linear equations which John shows how to package into Ax=b form.
     * 
     * Thanks John!
     * 
     * @param x independent variable
     * @param y dependent variable
     * 
     * @return Optimal coefficients with a in position 0, b in position 1 and
     * c in position 2
     * 
     * @throws Exception 
     */
    public static double[] Quadratic(double[] x, double[] y) throws Exception {
        double[] fit;
        Matrix   A = new Matrix(3,3);
        double[] b = new double[3];
        
        /* Build the A matrix */
        try {
            A.element(0,0,x.length);
            A.element(0,1,MathX.sum(x.clone()));
            A.element(0,2,MathX.sum(MathX.pow(x.clone(),2)));  
            A.element(1,0,A.element(0,1));
            A.element(1,1,A.element(0,2));
            A.element(1,2,MathX.sum(MathX.pow(x.clone(),3)));
            A.element(2,0,A.element(1,1));
            A.element(2,1,A.element(1,2));
            A.element(2,2,MathX.sum(MathX.pow(x.clone(),4)));
        }
        catch(Exception e) {
            throw new Exception("Exception encountered building A matrix: "+e.getMessage());
        }
        
        /* Build the b vector */
        try {
            b[0]=MathX.sum(y);
            b[1]=MathX.pSum(y,x,1);
            b[2]=MathX.pSum(y,x,2);
        }
        catch(Exception e) {
            throw new Exception("Exception encountered bulding b vector: "+e.getMessage());
        }
        
        /* Solve the system of linear equations */
        try {
            fit = Solve.LU(A, b);
        }
        catch(Exception e) {
            throw new Exception("Exception encountered solving Ax=b: "+e.getMessage());
        }
                    
        
        return fit;
    }
    
    /*public static void main(String[] args) {
        //double[] x = {0,1,2,3,4,5,6,7,8,9};
        double[] y = {0.0, 2670.585205078125, 3225.215576171875, 5486.7138671875, 4689.92578125, 3458.312255859375, 1897.5648193359375};
        double[] x = {831.6353499151866, 831.6379148053805, 831.6404797074404, 831.6430446213658, 831.6456095471574, 831.6481744848148, 831.6507394343382};
        
        x = MathX.shift(x, -1.0*x[0]);
        //x = MathX.product(x, 1000.0);
        
        System.out.printf("x=%s\n",java.util.Arrays.toString(x));
        System.out.printf("y=%s\n",java.util.Arrays.toString(y));

        double[] fit;
        
        try {
            fit = Polynomial.Quadratic(x, y);
            System.out.printf("fit=%s\n",java.util.Arrays.toString(fit));
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
        
    }*/
    
}

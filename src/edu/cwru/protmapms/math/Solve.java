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
 * Solves a system of linear equations using LU decomposition.
 * 
 * @author Sean Maxwell
 */
public class Solve {
    
    /**
     * Solves a system of linear equations using LU decomposition.
     * 
     * Based heavily on the tutorial and examples of solving a system of 
     * linear equations using LU decomposition at:
     * 
     * http://tutorial.math.lamar.edu/Classes/LinAlg/SystemsRevisited.aspx
     * 
     * Thanks Paul!
     * 
     * @param A Matrix of coefficients
     * @param b Matrix of constants
     * 
     * @return Vector of solution values 
     * 
     * @throws Exception If matrix cannot be LU decomposed.
     */
    public static double[] LU(Matrix A, double[] b) throws Exception {
        Matrix[] LU;
        Matrix   L;
        Matrix   U;
        int i;
        int j;
        double[] y = new double[b.length];
        double[] x = new double[b.length];
        double partsum;
        
        /* Try to compute the LU decomposition of A, and throw exception if it
         * does not have one */
        try {
            LU = MatrixDecomposition.LU(A);
            L = LU[0];
            U = LU[1];
        }
        catch(Exception e) {
            throw(new Exception("Cannot solve Ax=b using LU decomposition: "+e.getMessage()));
        }
        
        /* Solve for y in Ly=b */
        for(i=0;i<L.height();i++) {
            partsum = 0.0;
            
            for(j=0;j<i;j++) {
                partsum += y[j]*L.element(i, j);
            }
            
            y[i] = (b[i]-partsum) / L.element(i,i);
        }
        
        /* Solve for x in Ux=y */
        for(i=U.height()-1;i>-1;i--) {
            partsum = 0.0;
            
            for(j=i+1;j<U.width();j++) {
                partsum += x[j]*U.element(i, j);
            }
            
            x[i] = (y[i]-partsum) / U.element(i,i);
        }
        
        return x;
    }
    
    /*public static void main(String[] args) {
        //double[][] Adata = { {3,6,-9}, {2,5,-3}, {-4,1,10}};
        //double[] bdata = {0,-4,3};
        double[][] Adata = { {1,799.9,Math.pow(799.9,2)}, {1,800,Math.pow(800,2)}, {1,800.1,Math.pow(800.1,2)}};
        double[] bdata = {0, 10, -10};
        
        Matrix A = new Matrix(Adata);
        Vector b = new Vector(bdata);
        
        try {
            Vector x = Solve.LU(A,b);
            System.out.printf("%s\n",x.toString());
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }*/
    
}

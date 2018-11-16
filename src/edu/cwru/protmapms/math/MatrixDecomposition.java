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
 * Performs LU decomposition of a Matrix.
 * 
 * @author Sean Maxwell
 */
public class MatrixDecomposition {
    
    /**
     * Convenience routine to multiple an array by a double 
     * 
     * @param r Array to multiply each element by
     * @param v Value to multiply each array element by
     * 
     * @return New array of values that are products of corresponding array 
     * element and value 
     */
    private static double[] mArray(double[] r, double v) {
        int i;
        
        for(i=0;i<r.length;i++) {
            r[i] = r[i]*v;
        }
        
        return r;
    }
    
    /**
     * Performs LU decomposition of matrix A. Based very heavily on the tutorial
     * and examples at:
     * 
     * http://tutorial.math.lamar.edu/Classes/LinAlg/LUDecomposition.aspx'
     * 
     * Thanks Paul!
     * 
     * @param A Matrix to decompose
     * 
     * @return L matrix in position 0 and U matrix in position 1
     * 
     * @throws Exception 
     */
    public static Matrix[] LU(Matrix A) throws Exception {
        Matrix[] LU = new Matrix[2];
        int i;
        int j;
        double v;
        Matrix U = A.copy();
        Matrix L = new Matrix(A.height(),A.width());
        double[] row;
        
        /* Iterate down the diagonal */
        for(i=0;i<U.width();i++) {
            /* Process the matrix by working down the diagonal */
            v = U.element(i, i);
            
            /* If the term is 0, we have a problem because we need to multiply
             * the row by the reciprocal of this term. For now this throws an
             * exception. It might be possible to recover from this by adding
             * a multiple of another row to this one, but for now this case 
             * means that A has no LU decomposition.*/
            if(v == 0) {
                throw new Exception("A cannot be brought to echelon form, and thus cannot be decomposed to L and U");
            }
            
            /* Multiply by the reciprocal of the leading term so that all
             * leading terms on the diagonal are 1 */
            U.rowMultiply(i, 1.0/v);
            L.element(i, i, v);

            /* 0 all terms in the column below the leading term by adding
             * multiple of the leading term */
            for(j=i+1;j<U.height();j++) {
                v = U.element(j,i);
                row = mArray(U.getRow(i).clone(), 0.0-v);
                U.rowAdd(j, row);
                L.element(j,i,v);
            }
        }
        
        /* Package L and U and return */
        LU[0] = L;
        LU[1] = U;
        
        return LU;
    }
    
}

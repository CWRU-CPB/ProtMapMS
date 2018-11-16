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
 * Dense matrix representation for use when most of the matrix entries are
 * non-zero.
 * 
 * @author Sean Maxwell
 */
public class Matrix {
    /**
     * Matrix data.
     */
    private final double[][] data;
    
    
    /**
     * Create a new empty matrix of height <code>m</code> and width 
     * <code>n</code>.
     * 
     * @param m Matrix height
     * @param n Matrix width
     */
    public Matrix(int m, int n) {
        data = new double[m][n];
    }
    
    /**
     * Fetch the width of the Matrix.
     * 
     * @return Width.
     */
    public int width() {
        if(data.length == 0) {
            return 0;
        }
        else {
            return data[0].length;
        }
    }
    
    /**
     * Fetch the height of the Matrix.
     * 
     * @return Height.
     */
    public int height() {
        return data.length;
    }
    
    /**
     * Fetch the value of the cell at the specified position in Matrix.
     * 
     * @param row Index of row.
     * @param col Index of column.
     * 
     * @return Value at requested position
     * 
     * @throws Exception If the requested position is invalid.
     */
    public double element(int row, int col) throws Exception {
        /* Check dimensions */
        if((row < 0 || row >= this.height()) ||
           (col < 0 || col >= this.width())) {
            throw new Exception(
                    String.format("The specified coordinates {%d,%d} are not valid for this Matrix of dimension %dx%d\n",
                                  row,col,this.height(),this.width()));
        }
        
        /* Return value */
        return data[row][col];
    }
    
    /**
     * Sets the value of the cell at the specified position in Matrix.
     * 
     * @param row Index of row.
     * @param col Index of column.
     * @param v Value for cell.
     * 
     * @throws Exception If the requested position is invalid.
     */
    public void element(int row, int col, double v) throws Exception {
        /* Check dimensions */
        if((row < 0 || row >= this.height()) ||
           (col < 0 || col >= this.width())) {
            throw new Exception(
                    String.format("The specified coordinates {%d,%d} are not valid for this Matrix of dimension %dx%d\n",
                                  row,col,this.height(),this.width()));
        }
        
        /* Set value */
        data[row][col] = v;
    }
        
    /**
     * Multiple a row of this matrix by a scalar.
     * 
     * @param row Index of row
     * @param v  Scalar multiplier
     */
    public void rowMultiply(int row, double v) {
        int i;
        for(i=0;i<this.width();i++) {
            this.data[row][i] = this.data[row][i]*v;
        }
    }
    
    /**
     * Add a scalar to each element of a row.
     * 
     * @param row Index of row
     * @param v  Scalar value to add
     */
    public void rowAdd(int row, double[] v) {
        int i;
        for(i=0;i<this.width();i++) {
            this.data[row][i] += v[i];
        }
    }
    
    /**
     * Fetch a row of this Matric.
     * 
     * @param row Index of row.
     * 
     * @return Row data. 
     */
    public double[] getRow(int row) {
        return data[row];
    }
    
    /**
     * Clone a Matrix object to a new copy.
     * 
     * @return Copy of this Matrix. 
     */
    public Matrix copy() {
        Matrix c = new Matrix(this.height(),this.width());
        int i;
        int j;
        for(i=0;i<c.height();i++) {
            for(j=0;j<c.width();j++) {
                c.data[i][j] = this.data[i][j];
            }
        }
        return c;
    }
    
    /**
     * Perform element by element comparison of this Matrix (M1) with an 
     * argument Matrix (M2)
     * 
     * @param m Matrix to compare with this Matrix.
     * 
     * @return false for not equal, true for equal. 
     */
    public boolean equals(Matrix m) {
        int i,j;
        
        /* Check dimensions */
        if(m.height() != this.height() || m.width() != this.width()) {
            return false;
        }
        
        /* Check for equality */
        try {
            for(i=0;i<this.height();i++) {
                for(j=0;j<this.width();j++) {
                    if(this.element(i,j) != m.element(i,j)) {
                        return false;
                    }
                }
            }
        }
        
        /* Catch exception thrown by element(row,col), but this NEVER happen
         * since we already checked the dimensions above */
        catch(Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
        
        /* No differences or errors */
        return true;
    }
 
}

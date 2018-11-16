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
package edu.cwru.protmapms.spectra;

/**
 * Encapsulates m/z and intensity pairs
 *
 * @author Sean-M
 */
public class Peaks {
    /**
     * Holds m/z values
     */
    public double MZ[];

    /**
     * Holds intensity values
     */
    public double Intensity[];

    /**
     * Constructor. Creates a new object initialized to hold <strong>n</strong>
     * corresponding m/z and intensity values.
     *
     * @param n Number of peaks to initialize object to hold
     */
    public Peaks(int n) {
        MZ        = new double[n];
        Intensity = new double[n];
    }
    
    /**
     * Constructor. Creates a new object wrapping the argument m/z and intensity
     * data.
     *
     * @param mz m/z values
     * @param intensities intensity values corresponding to each m/z value
     */
    public Peaks(double[] mz, double[] intensities) {
        MZ        = mz;
        Intensity = intensities;
    }

    /**
     * Print the m/z,intensity pairs to STDOUT
     */
    public void Dump() {
        int i;
        for(i=0;i<this.MZ.length;i++) {
            System.out.println(java.lang.String.valueOf(this.MZ[i])+"\t"+
                               java.lang.String.valueOf(this.Intensity[i]));
        }
    }
    
}

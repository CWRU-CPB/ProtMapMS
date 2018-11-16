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
 * Encapsulates the details for an MS or MS/MS scan
 * 
 * @author Sean Maxwell
 */
public class Scan {
    /**
     * MS Level (1 or 2)
     */
    public byte   MSLevel;

    /**
     * Precision of peak data (32=float or 64=double)
     */
    public byte   Precision;

    /**
     * Scan number
     */
    public int    ScanNum;
    
    /**
     * Centroid mode flag [1=centroid,0=not centroid]
     */
    public int centroid;

    /**
     * Number of peaks in this scan
     */
    public int    PeaksCount;

    /**
     * Precursor m/z if MS level is MS/MS, and 0 otherwise
     */
    public double PrecursorMZ;

    /**
     * Lowest m/z value in the scan
     */
    public double LowMZ;

    /**
     * Highest m/z value in the scan
     */
    public double HighMZ;

    /**
     * The base peak m/z value for the scan
     */
    public double BasePeakMZ;

    /**
     * The retention time at which this scan was generated
     */
    public double RetentionTime;

    /**
     * The precursor ion intensity of MS Level is MS/MS. Otherwise 0.
     */
    public double PrecursorInt;

    /**
     * The base peak intensity level
     */
    public double BasePeakIntensity;

    /**
     * Total Ion Current for this scan
     */
    public double TotalIonCurrent;

    /**
     * The approximate scan position in bytes from the beginning of the file
     */
    public long   ScanPos;

    /**
     * The scan length in bytes
     */
    public long   ScanLength;

    /**
     * Constructor. Creates a new empty Scan object
     */
    public Scan() {

    }
}

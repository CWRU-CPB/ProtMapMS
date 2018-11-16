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
 * Additional spectrum file formats can be accessed by classes implementing this
 * interface.
 * 
 * @author Sean Maxwell
 */
public interface SpectrumFile {
    public boolean connect(String path) throws Exception;
    public boolean disconnect() throws Exception;
    public Scan getScanProperties(int s) throws Exception;
    public Peaks getScanPeaks(int s) throws Exception;
    public int size() throws Exception;
    public int[] queryPrecursor(double minMz, double maxMz) throws Exception;
    public int[] queryPrecursor(double minMz, double maxMz, double fromRT, double toRT) throws Exception;
    public int[] queryMSLevel(int ms) throws Exception;
    public int[] queryRetentionTime(double start, double stop, int ms) throws Exception;
    public String file();
}

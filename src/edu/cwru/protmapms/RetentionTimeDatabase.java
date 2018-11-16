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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Organizes a set of retention time intervals by peptide sequence &rarr; 
 * spectrum &rarr; m/z value.
 * 
 * @author sean-m
 */
public class RetentionTimeDatabase extends HashMap<String,HashMap<String,HashMap<String,RetentionTimeDatabaseEntry>>> {
    private final HashMap<String,Integer> uniqueMz;
    
    
    public RetentionTimeDatabase() {
        super();
        uniqueMz = new HashMap<>();
    }
    
    /**
     * Adds a retention time to the database.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * @param mzKey Key identifying the m/z value of eluting peptide
     * @param rt Time at which the peptide eluted
     * @param labeling Indicates the peptide is modified with a labeling modification
     * @param z charge of the eluting peptide
     */
    public void addRetentionTime(String peptideSequence, String spectrumKey, String mzKey, List<ComparableRetentionTime> rt, Boolean labeling, Integer z, Integer massOffset) {
        if(!this.containsKey(peptideSequence)) this.put(peptideSequence, new HashMap<>());
        if(!this.get(peptideSequence).containsKey(spectrumKey)) this.get(peptideSequence).put(spectrumKey, new HashMap<>());
        this.get(peptideSequence).get(spectrumKey).put(mzKey,new RetentionTimeDatabaseEntry(labeling,massOffset,z,rt));
        uniqueMz.put(mzKey, z);
    }
    
    /**
     * Retrieve the retention time information for the peptide species in a
     * spectrum.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * @param mzKey Key identifying the m/z value of eluting peptide
     * @return retention time interval
     * 
     * @throws Exception if the argument keys do not match a record in the database
     */
    public List<ComparableRetentionTime> getRetentionTimes(String peptideSequence, String spectrumKey, String mzKey) throws Exception {
        if(!this.containsKey(peptideSequence) || !this.get(peptideSequence).containsKey(spectrumKey)) throw new Exception(String.format("Request for retention time that is not defined in this pool [%s,%s,%s]",peptideSequence,spectrumKey,mzKey));
        return this.get(peptideSequence).get(spectrumKey).get(mzKey).retentionTimes;
    }
    
    /**
     * Report if there are entries in the database for a peptide sequence in a
     * spectrum.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * 
     * @return true for yes, false for no
     */
    public Boolean contains(String peptideSequence, String spectrumKey) {
        return this.containsKey(peptideSequence) && 
               this.get(peptideSequence).containsKey(spectrumKey);
    }
    
    /**
     * Report if the peptide species in the database has labeling modifications.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * @param mzKey Key identifying the m/z value of eluting peptide 
     * 
     * @return true for yes, false for no
     * 
     * @throws Exception if the argument keys do not match a record in the database
     */
    public Boolean isLabeling(String peptideSequence, String spectrumKey, String mzKey) throws Exception {
        if(!this.containsKey(peptideSequence) || !this.get(peptideSequence).containsKey(spectrumKey)) throw new Exception(String.format("Request for retention time that is not defined in this pool [%s,%s,%s]",peptideSequence,spectrumKey,mzKey));
        return this.get(peptideSequence).get(spectrumKey).get(mzKey).labeled; 
    }
    
    /**
     * Report the charge of a peptide species in the database.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * @param mzKey Key identifying the m/z value of eluting peptide 
     * 
     * @return charge of peptide
     * 
     * @throws Exception if the argument keys do not match a record in the database
     */
    public Integer chargeState(String peptideSequence, String spectrumKey, String mzKey) throws Exception {
        if(!this.containsKey(peptideSequence) || !this.get(peptideSequence).containsKey(spectrumKey)) throw new Exception(String.format("Request for retention time that is not defined in this pool [%s,%s,%s]",peptideSequence,spectrumKey,mzKey));
        return this.get(peptideSequence).get(spectrumKey).get(mzKey).charge;
    }
    
    /**
     * Report the charge of a peptide species in the database.
     * 
     * @param peptideSequence sequence of associated eluting peptide
     * @param spectrumKey Key identifying the spectrum in which peptide is eluting
     * @param mzKey Key identifying the m/z value of eluting peptide 
     * 
     * @return charge of peptide
     * 
     * @throws Exception if the argument keys do not match a record in the database
     */
    public Integer massOffset(String peptideSequence, String spectrumKey, String mzKey) throws Exception {
        if(!this.containsKey(peptideSequence) || !this.get(peptideSequence).containsKey(spectrumKey)) throw new Exception(String.format("Request for retention time that is not defined in this pool [%s,%s,%s]",peptideSequence,spectrumKey,mzKey));
        return this.get(peptideSequence).get(spectrumKey).get(mzKey).massOffset;
    }
    
    /**
     * Utility method to print the contents of the database.
     */
    public void print() {
        for(String peptideSequence : this.keySet()) {
            for(String exposureTime : this.get(peptideSequence).keySet()) {
                for(String mz : this.get(peptideSequence).get(exposureTime).keySet()) {
                    RetentionTimeDatabaseEntry rtdbe = this.get(peptideSequence).get(exposureTime).get(mz);
                    List<ComparableRetentionTime> data = rtdbe.retentionTimes;
                    for(ComparableRetentionTime rt : data) {
                        System.out.printf("%s\t%s\t%s\t%d\t%.4f\t%b\t%d\n",peptideSequence,exposureTime,mz,rtdbe.charge,rt.getRetentionTime(),rtdbe.labeled,rtdbe.massOffset);
                    }
                }
            }
        }
    }
    
    /**
     * Get the peptide keys with entries in the database.
     * 
     * @return peptide sequence keys
     */
    public Set<String> getPeptideKeys() {
        return this.keySet();
    }
    
    /**
     * Get the spectrum keys corresponding to the argument peptide key.
     * @param peptideKey peptide key that spectrum keys should relate to
     * 
     * @return spectrum keys associated with peptide key
     */
    public Set<String> getSpectrumKeys(String peptideKey) {
        return this.get(peptideKey).keySet();
    }
    
    /**
     * Get m/z keys associate with peptide and spectrum.
     * 
     * @param peptideKey peptide key that spectrum keys should relate to
     * @param exposureTimeKey spectrum the m/z keys should relate to.
     * @return m/z keys related to peptide and spectrum
     */
    public Set<String> getMzKeys(String peptideKey, String exposureTimeKey) {
        return this.get(peptideKey).get(exposureTimeKey).keySet();
    }
    
    /**
     * Compile an array of unique m/z values that have retention time 
     * information in the database, and the charge associated with each
     * m/z value.
     * 
     * @return array with m/z values in entries 0[i] and charge values in 
     * entries [1][i].
     */
    public double[][] getUniqueSpeciesPropertiesForMS1Extraction() {
        double[][] keys = new double[2][uniqueMz.size()];
        int i = 0;
        for(String mzKey : uniqueMz.keySet()) {
            Double mz = Double.parseDouble(mzKey);
            Double charge = uniqueMz.get(mzKey).doubleValue();
            keys[0][i]=mz;
            keys[1][i]=charge;
            i++;
        }
        return keys;
    }
}

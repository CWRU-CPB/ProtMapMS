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

import java.util.ArrayList;
import java.util.List;

import edu.cwru.protmapms.modifications.ModificationSite;

/**
 * Encapsulates the properties necessary to describe a peptide species 
 * identification. This includes things like the scan number where the 
 * identification was made, what modifications were considered when making
 * the identification, charge state, etc.
 * 
 * @author Sean Maxwell
 */
public class Identification {
    private List<ModificationSite> modifications;
    private Double retentionTime;
    private Double exposureTime;
    private Double precursorMz;
    private Double precursorIntensity;
    private Integer scanNumber;
    private Double score;
    private Integer Z;
    private MSMSIons observedIons;
    private MSMSIons theoreticalIons;
    private Boolean isSignificant;
    
    
    public Identification(Double d, Integer i, Double s) {
        retentionTime = d;
        scanNumber = i;
        score = s;
        modifications = new ArrayList<>();
        isSignificant = false;
    }
    
    public Identification setRetentionTime(Double d) {
        retentionTime = d;
        return this;
    }
    
    public Identification setExposureTime(Double d) {
        exposureTime = d;
        return this;
    }
    
    public Identification setScanNumber(Integer i) {
        scanNumber = i;
        return this;
    }
    
    public Identification setScore(Double s) {
        score = s;
        return this;
    }
    
    public Identification setTheoreticalIons(MSMSIons msi) {
        theoreticalIons = msi;
        return this;
    }
    
    public Identification setObservedIons(MSMSIons msi) {
        observedIons = msi;
        return this;
    }
    
    public Identification setCharge(int z) {
        this.Z=z;
        return this;
    }
    
    public Identification setPrecursorMz(Double mz) {
        this.precursorMz=mz;
        return this;
    }
        
    public Identification setPrecursorIntensity(Double intensity) {
        this.precursorIntensity=intensity;
        return this;
    }
    
    public Identification setModifications(List<ModificationSite> lm) {
        this.modifications=new ArrayList<>(lm);
        return this;
    }
    
    public Identification setSignificant(Boolean b) {
        this.isSignificant = b;
        return this;
    }
    
    public Double getRetentionTime() {
        return retentionTime;
    }
    
    public Double getExposureTime() {
        return exposureTime;
    }
    
    public Integer getScanNumber() {
        return scanNumber;
    }
    
    public Double getScore() {
        return score;
    }
    
    public MSMSIons getTheoreticalIons() {
        return theoreticalIons;
    }
    
    public MSMSIons getObservedIons() {
        return observedIons;
    }
    
    public Integer getCharge() {
        return Z;
    }
    
    public Double getPrecursorMz() {
        return precursorMz;
    }
    
    public Double getPrecursorIntensity() {
        return precursorIntensity;
    }
    
    public boolean isSignificant() {
        return this.isSignificant;
    }
    
    public boolean isLabeled() {
        for(ModificationSite modSite : modifications) {
            if(modSite.modification.labeling) return true;
        }
        return false;
    }
    
    public List<ModificationSite> getModifications() {
        return modifications;
    }
    
    public Double getMassOffset() {
        Double offset = 0.0;
        for(ModificationSite modSite : modifications) {
            offset += modSite.modification.massOffset();
        }
        return offset;
    }
    
    /**
     * Generates a JSON formatted Map containing the identification data. The
     * generated Map contains key,value pairs:
     * <ul>
     * <li>mods: The modification present on the identified peptide</li>
     * <li>rt: The retention time where the identification was made</li>
     * <li>specVar: The ID of the spectrum in which the identification was made</li>
     * <li>mz: The m/z value of the peptide that was identified</li>
     * <li>pci: The intensity of the peptide that was identified</li>
     * <li>scan: The scan number where the identification was made</li>
     * <li>score: The Pearson correlation score of the identification</li>
     * <li>Z: The charge carried by the pepetide</li>
     * <li>oions: The observed ions (experimental MS2 spectra) which is a Map
     *     generated by MSMSIons.toJSON method with key,value pairs mz:[m/z values],
     *     I:[intensity values], label:[ion labels]</li>
     * <li>tions: The theoretical ions (the theoretical MS2 spectra) which is a Map
     *     generated by MSMSIons.toJSON method with key,value pairs mz:[m/z values],
     *     I:[intensity values], label:[ion labels]</li>
     * <li>sig: True if the identification was significant</li>
     * </ul>
     * 
     * @return The identification in JSON format
     * 
     * @see edu.cwru.protmapms.MSMSIons
     */
    public String toJSON() {
        StringBuilder sb = new StringBuilder("{\"mods\":\"");
        sb.append(modifications.toString());
        sb.append("\",\"rt\":");
        sb.append(retentionTime);
        sb.append(",\"specVar\":");
        sb.append(exposureTime);
        sb.append(",\"mz\":");
        sb.append(precursorMz);
        sb.append(",\"pci\":");
        sb.append(precursorIntensity);
        sb.append(",\"scan\":");
        sb.append(scanNumber);
        sb.append(",\"score\":");
        sb.append(score);
        sb.append(",\"z\":");
        sb.append(Z);
        sb.append(",\"oions\":");
        sb.append(observedIons.toJSON());
        sb.append(",\"tions\":");
        sb.append(theoreticalIons.toJSON());
        sb.append(",\"sig\":");
        sb.append(isSignificant);
        sb.append("}");
        return sb.toString();
    }
}

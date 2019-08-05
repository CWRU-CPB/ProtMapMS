/*

 Copyright (C) Case Western Reserve University, 2014. All rights reserved. 
 This source code and documentation constitute proprietary information
 belonging to Case Western Reserve University. None of the foregoing
 material may be copied, duplicated or disclosed without the express
 written permission of Case Western Reserve University.

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


import edu.cwru.protmapms.modifications.Modifications;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sean-m
 */
public class IdentificationFactoryConfig {
    private final List<String> spectrumFiles;
    private final List<Double> exposureTimes;
    private Fasta proteins;
    private Modifications modifications;
    private Integer minZ;
    private Integer maxZ;
    private Integer maxMissedCleavages;
    private Integer maxConcurrentModifications;
    private String proteaseName;
    private Double minMass;
    private Double maxMass;
    private Double fromRT;
    private Double toRT;
    private Integer ms1ErrPpm;
    private Double ms2Err;
    private String outDir;
    
    public IdentificationFactoryConfig() {
        spectrumFiles = new ArrayList<>();
        exposureTimes = new ArrayList<>();
        outDir = String.format("results/%d",System.currentTimeMillis());
    }
    
    public IdentificationFactoryConfig setFasta(Fasta fasta) {
        proteins = fasta;
        return this;
    } 
    
    public IdentificationFactoryConfig setModifications(Modifications mods) {
        modifications = mods;
        return this;
    }
    
    public IdentificationFactoryConfig addSpectrum(String file, Double exposureTime) {
        this.spectrumFiles.add(file);
        this.exposureTimes.add(exposureTime);
        return this;
    }
    
    public IdentificationFactoryConfig setChargeRange(Integer min, Integer max) {
        minZ=min;
        maxZ=max;
        return this;
    }
    
    public IdentificationFactoryConfig setChargeMin(Integer min) {
        minZ=min;
        return this;
    }
    
    public IdentificationFactoryConfig setChargeMax(Integer max) {
        maxZ=max;
        return this;
    }
    
    public IdentificationFactoryConfig setMassRange(Double min, Double max) {
        minMass=min;
        maxMass=max;
        return this;
    }
    
    public IdentificationFactoryConfig setMassMin(Double min) {
        minMass=min;
        return this;
    }
    
    public IdentificationFactoryConfig setMassMax(Double max) {
        maxMass=max;
        return this;
    }
    
    public IdentificationFactoryConfig setRTRange(Double min, Double max) {
        fromRT=min;
        toRT=max;
        return this;
    }
    
    public IdentificationFactoryConfig setRTMin(Double min) {
        fromRT=min;
        return this;
    }
    
    public IdentificationFactoryConfig setRTMax(Double max) {
        toRT=max;
        return this;
    }
    
    public IdentificationFactoryConfig setMS1ErrorPPM(Integer ppm) {
        ms1ErrPpm=ppm;
        return this;
    }
    
    public IdentificationFactoryConfig setMaxMissedCleavages(Integer mmc) {
        maxMissedCleavages=mmc;
        return this;
    }
    
    public IdentificationFactoryConfig setMaxConcurrentModifications(Integer mcm) {
        maxConcurrentModifications=mcm;
        return this;
    }
    
    public IdentificationFactoryConfig setProteaseName(String name) {
        proteaseName=name;
        return this;
    }
    
    public IdentificationFactoryConfig setMS2ErrorDa(Double da) {
        ms2Err=da;
        return this;
    }
    
    public IdentificationFactoryConfig setOutputDirectory(String s) {
        outDir=s;
        return this;
    }
    
    public List<String> getSpectrumFiles() {
        return spectrumFiles;
    }
    
    public List<Double> getSpectrumKeys() {
        return exposureTimes;
    }
    
    public Fasta getProteinDatabase() {
        return proteins;
    }
    
    public Modifications getModificationDatabase() {
        return modifications;
    }
    
    public Integer getMinCharge() {
        return minZ;
    }
    
    public Integer getMaxCharge() {
        return maxZ;
    }
    
    public Double getMinMass() {
        return minMass;
    }
    
    public Double getMaxMass() {
        return maxMass;
    }
    
    public Double getMinRT() {
        return fromRT;
    }
    
    public Double getMaxRT() {
        return toRT;
    }
    
    public Integer getMaxMissedCleavages() {
        return maxMissedCleavages;
    }
    
    public Integer getMaxConcurrentModifications() {
        return maxConcurrentModifications;
    }
    
    public String getProteaseName() {
        return proteaseName;
    }
    
    public Integer getMs1ErrorPPM() {
        return ms1ErrPpm;
    }
    
    public Double getMs2ErrorDa() {
        return ms2Err;
    }
    
    public Integer nSpectra() {
        return spectrumFiles.size();
    }
    
    public Double spectrumKey(Integer i) {
        return exposureTimes.get(i);
    }
    
    public String spectrumFile(Integer i) {
        return spectrumFiles.get(i);
    }
    
    public String getOutputDirectory() {
        return outDir;
    }
    
}

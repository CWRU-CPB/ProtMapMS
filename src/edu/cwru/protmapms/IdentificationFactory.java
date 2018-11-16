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

import edu.cwru.protmapms.filtering.*;
import edu.cwru.protmapms.math.SortedArraysAligner;
import edu.cwru.protmapms.math.ArrayAlignment;
import edu.cwru.protmapms.modifications.ModificationSite;
import edu.cwru.protmapms.modifications.Modifications;
import edu.cwru.protmapms.modifications.ModificationSiteEnumerator;
import edu.cwru.protmapms.result.*;
import edu.cwru.protmapms.scoring.*;
import edu.cwru.protmapms.spectra.SpectrumFile;
import edu.cwru.protmapms.spectra.mzXMLInterface;
import edu.cwru.protmapms.spectra.Peaks;
import edu.cwru.protmapms.spectra.Scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterates over all peptide species and searches for MS/MS spectra that show
 * a similar pattern of ions to a theoretical spectrum generated from the
 * peptide species. Helper classes used for several key steps have been 
 * abstracted so that different approaches can be developed and tested. Namely, 
 * the peak filtering applied to a scan prior to similarity scoring, and the 
 * scoring function itself can be changed using setter methods that accept 
 * objects implementing the correct interface or extending the correct abstract
 * class.
 * 
 * @author Sean Maxwell
 */
public class IdentificationFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentificationFactory.class);
    private final List<String> spectrumFiles;
    private final List<String> spectrumKeys;
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
    private Double integrationSlack;
    private Integer ms1ErrPpm;
    private Double ms2Err;
    private Double minScore;
    private ScoringFunction scoringFunction;
    private PeakFilterChain peakFilterChain;
    private String outDir;
    
    private double[] toArray(Peptide peptide, List<ModificationSite> modSites) {
        double[] offsets = new double[peptide.length];
        for(ModificationSite modSite : modSites) {
            offsets[modSite.position-peptide.start] = modSite.modification.massOffset;
        }
        return offsets;  
    }
    
    public IdentificationFactory() {
        minZ = 2;
        maxZ = 4;
        maxMissedCleavages=1;
        maxConcurrentModifications=2;
        spectrumFiles = new ArrayList<>();
        spectrumKeys = new ArrayList<>();
        exposureTimes = new ArrayList<>();
        ms1ErrPpm=10;
        ms2Err=0.25;
        minScore=0.2;
        integrationSlack=180.0;
        scoringFunction = new PearsonCorrelationScoring();
        peakFilterChain = new StandardPeakFilterChain();
        outDir = String.format("results/%d",System.currentTimeMillis());
    }
    
    public IdentificationFactory addSpectrum(String file, Double exposureTime) {
        this.spectrumFiles.add(file);
        this.spectrumKeys.add(String.format("%.4f",exposureTime));
        this.exposureTimes.add(exposureTime);
        return this;
    }
    
    public IdentificationFactory setProteinDatabase(Fasta proteins) {
        this.proteins = proteins;
        return this;
    }
    
    public IdentificationFactory setModifications(Modifications modifications) {
        this.modifications = modifications;
        return this;
    }
    
    public IdentificationFactory setProtease(String proteaseName) {
        this.proteaseName = proteaseName;
        return this;
    }
    
    public IdentificationFactory setMinZ(Integer z) {
        this.minZ = z;
        return this;
    }
    
    public IdentificationFactory setMaxZ(Integer z) {
        this.maxZ = z;
        return this;
    }
    
    public IdentificationFactory setMinMass(Double m) {
        this.minMass = m;
        return this;
    }
    
    public IdentificationFactory setMaxMass(Double m) {
        this.maxMass = m;
        return this;
    }
    
    public IdentificationFactory setRTFrom(Double t) {
        this.fromRT = t*60.0; // Convert to seconds
        return this;
    }
    
    public IdentificationFactory setRTTo(Double t) {
        this.toRT = t*60.0; // Convert to seconds
        return this;
    }
    
    public IdentificationFactory setMs2Err(Double e) {
        this.ms2Err=e;
        return this;
    }
    
    public IdentificationFactory setPrecursorErrPPM(Integer ppm) {
        this.ms1ErrPpm=ppm;
        return this;
    }
    
    public IdentificationFactory setMaxMissedCleavages(Integer m) {
        this.maxMissedCleavages = m;
        return this;
    }
    
    public IdentificationFactory setMaxConcurrentModifications(Integer m) {
        this.maxConcurrentModifications = m;
        return this;
    }
    
    public IdentificationFactory setIntegrationSlack(Double f) {
        this.integrationSlack = f;
        return this;
    }
    
    public IdentificationFactory setOutDir(String s) {
        this.outDir=s;
        return this;
    }
    
    
    public void validate() throws Exception {
        if(proteins == null) {
            throw new Exception("No protein database has been specified");
        }
        
        if(modifications == null) {
            throw new Exception("No modification database has been specified");
        }
        
        if(proteaseName == null) {
            throw new Exception("No protease has been specified");
        }
        
        if(minMass == null || maxMass == null) {
            throw new Exception("Mass window is not fully specified. Requires a min and max.");
        }
        
        if(fromRT == null || toRT == null) {
            throw new Exception("Retention time window is not fully specified. Requires a from and to.");
        }
    }
     
    private Identification confirmIdentification(Peptide peptide, double[] precursors, SpectrumFile sf, int scan, double[] theoreticalIons) throws Exception {
        /* Load the scan data and meta data into memory */
        Peaks peaks = sf.getScanPeaks(scan);
        Scan scanMeta = sf.getScanProperties(scan);
        
        /* Apply configured peak filtering chain. By default, this removes
         * precursor ions from the scan and applies a noise-modeling filter to
         * remove scan peaks below a linear interpolated cuttoff between the
         * medians of 20 partitions of the scan peaks */
        peaks = peakFilterChain.filter(peaks, precursors, ms2Err);
        
        /* Align the filtered peaks to the theoretical spectrum. The peaks
         * are in */
        ArrayAlignment peakAlignment = SortedArraysAligner.alignClosestDependent(peaks.MZ, peaks.Intensity, theoreticalIons, ms2Err);
        LOGGER.trace("{} theoretical peaks aligned to {} of {} observed peaks",theoreticalIons.length,peakAlignment.count,peaks.MZ.length);
        LOGGER.trace(peakAlignment.toString());
        
        Score s = scoringFunction.score(peakAlignment.theoreticalIntensities, peakAlignment.observedIntensities);
        LOGGER.trace("Score for peptide {} in scan {} is {}",peptide.sequence,scan,s.score);
        
        /* Return a match, with the significant bit set depending on the 
         * statistical significance of the Pearson correlation */
        return new Identification(scanMeta.RetentionTime,scan,s.score).
                setSignificant(s.isSignificant).
                setPrecursorIntensity(scanMeta.PrecursorInt).
                setObservedIons(new MSMSIons(peakAlignment.observedMzValues,peakAlignment.observedIntensities, new ArrayList<>())).
                setTheoreticalIons(new MSMSIons(peakAlignment.theoreticalMzValues,peakAlignment.theoreticalIntensities, new ArrayList<>()));
    }
    
    private List<Identification> identifySpecies(Peptide peptide, 
                                List<ModificationSite> modificationSites,
                                int Z,
                                SpectrumFile sf,
                                Double exposureTime) throws Exception {
        List<Identification> identifications = new ArrayList<>();
        
        /* Compute offsets at each residue for this species */
        double[] offsets = toArray(peptide,modificationSites);
                
        /* Compute precursor ions (2 isotopes, each with no loss, water loss os
         * ammonia loss) */
        double[] precursors = IonFactory.calculatePrecursorIonMass(peptide.sequence(),Z, offsets);
        double precursor = precursors[0];
        double err_win = precursor*this.ms1ErrPpm/1000000;
        
        LOGGER.trace("Query for precursor m/z in range [{},{}]",precursor-err_win,precursor+err_win);
        int[] scans = sf.queryPrecursor(precursor-err_win,
                                        precursor+err_win,
                                        this.fromRT,
                                        this.toRT);
        
        /* If any scans were found, generate a theoretical spectrum to perform
         * MS/MS confirmation */
        if(scans.length == 0) return identifications;
        double[] theoretical_ions = IonFactory.getTheoreticalIons(peptide.sequence(),Z,offsets);
        
        /* The follwing steps to filter ions and match theoretical ions to
         * precursor ions require the inputs to be sorted in ascending order. Do
         * that here to avoid sorting them multiple times */
        Arrays.sort(theoretical_ions);
        Arrays.sort(precursors);
        
        /* Iterate over scans and confirm identifications via MS/MS ion
         * matching to theoretical spectra */
        for(int scan : scans) {
            LOGGER.trace("Confirming scan {}",scan);
            Identification identification = this.confirmIdentification(peptide, precursors, sf, scan, theoretical_ions);
            if(identification == null) continue;
            
            identification.setPrecursorMz(precursor).
                setCharge(Z).
                setModifications(modificationSites).
                setExposureTime(exposureTime);
            
            /* If the identification is significant, add it to the result */
            if(identification.isSignificant() && identification.getScore().compareTo(minScore) >= 0) {
                identifications.add(identification);
            }
        }
        
        
        return identifications;
    }
            
    public FootprintingResult identify() throws Exception {
        /* Validate all parameters have been configured */
        validate();
        
        /* Instantiate result container to hold results */
        FootprintingResult result = new FootprintingResult();
        
        /* Iterate over spectrum files. This is the outer most loop but 
         * connecting to a spectrum has the longest delay of any operation */
        for(int spectrumIndex=0;spectrumIndex<spectrumFiles.size();spectrumIndex++) {
            /* Prepare to process next spectrum file */
            String file = spectrumFiles.get(spectrumIndex);
            Double exposureTime = exposureTimes.get(spectrumIndex);
            String spectrumKey = spectrumKeys.get(spectrumIndex);
            
            /* Connect to spectrum (an expensive operation usually) */
            LOGGER.info("Starting process spectrum {}",file);
            SpectrumFile sf = new mzXMLInterface();
            sf.connect(file);
            
            /* Iterate over proteins */
            for(String accession : proteins.getAccessions()) {
                String sequence = proteins.getSequence(accession);
                LOGGER.trace("Processing protein {}",accession);

                /* Create a peptide factory to cleave the protein sequence to 
                 * peptides */
                PeptideFactory pf = new PeptideFactory(false);
                pf.setProtease(proteaseName);
                pf.setMissedCleavages(maxMissedCleavages);
                pf.setSequence(sequence);
                pf.start();

                /* Iterate over cleaved peptides */
                List<Peptide> peptides = pf.getNext();
                while(peptides != null) {
                    for(Peptide peptide : peptides) {
                        LOGGER.trace("Processing peptide {}",peptide.sequence());

                        /* Map modifications to peptide residues */
                        ModificationSiteEnumerator mse = new ModificationSiteEnumerator(peptide,modifications,maxConcurrentModifications);

                        /* Compute the mass of this peptide for filtering by mass 
                         * constraints as the mass of the amino acids plus the mass
                         * of all fixed modifications that should be present on the
                         * peptide sequence */
                        double miMass = IonFactory.calculateIonMass(peptide.sequence)+mse.getTotalFixedOffset();
                        if(miMass < minMass || miMass > maxMass) {
                            LOGGER.trace("Skipping peptide {} with mass {} outside configures window [{},{}]",peptide.sequence,miMass,minMass,maxMass);
                            continue;
                        }
                        LOGGER.trace("Peptide is within mass window at {}",miMass);
                        

                        for(int Z=minZ;Z<=maxZ;Z++) {
                            LOGGER.trace("Processing charge state {}",Z);
                            SpectrumResult spectrumResult = result.
                                    getProteinResult(accession).
                                    getPeptideResult(peptide).
                                    getSpectrumResult(spectrumKey);
                            
                            /* Search the species with no variable modifications 
                             * first (only fixed if any are present) */
                            spectrumResult.addAll(identifySpecies(peptide,mse.getFixed(),Z,sf,exposureTime));
                            
                            
                            /* Iterate over species containing variable 
                             * modifications */
                            List<ModificationSite> modificationSites = mse.getNext();
                            while(modificationSites != null) {
                                LOGGER.trace("Processing species {}:{}",peptide.sequence(),modificationSites.toString());
                                spectrumResult.addAll(identifySpecies(peptide,modificationSites,Z,sf,exposureTime));


                                modificationSites = mse.getNext();
                            }

                            /* Rewind the modification site enumerator to enumerate
                             * modification sites for the next charge state */
                            mse.startOver();
                        }
                    }

                    peptides = pf.getNext();
                }      
            }
        }
        
        System.out.printf("----Reference-----\n");
        RetentionTimes referenceRetentionTimes = result.getReferenceRetentionTimeIntervals();
        referenceRetentionTimes.print();
        
        //System.out.printf("----Interpolated Pool----\n");
        RetentionTimeDatabase rtp = result.getRetentionTimeDatabase(referenceRetentionTimes);
        rtp.print();
        
        System.out.printf("----MS1 Extract----\n");
        double[][] mzAndCharge = rtp.getUniqueSpeciesPropertiesForMS1Extraction();
        MS1ExtractWithGaussianConfirmation ms1e = new MS1ExtractWithGaussianConfirmation(
                mzAndCharge[0],
                mzAndCharge[1],
                ms1ErrPpm,
                60000,
                fromRT,
                toRT,
                spectrumFiles,
                spectrumKeys,
                new mzXMLInterface());
        ms1e.extract(false);
               
        /* Output results */
        //ResultWriter.drawChromatograms(outDir,result,rtp,ms1e,integrationSlack,fromRT,toRT);
        ResultWriter.writeIdentificationReport(outDir,result);
        ResultWriter.writePeakAreas(outDir,result,rtp,ms1e,integrationSlack);
        //ResultWriter.writePeakAreasHTML(outDir, result, rtp, ms1e, integrationSlack);
        ResultWriter.writePeakAreasJSON(outDir, result, rtp, ms1e, integrationSlack);
        ResultWriter.writeChromatogramsJSON(outDir, ms1e, rtp);
        ResultWriter.writeIdentificationsJSON(outDir,result);
        return result;
    }
    
    
}

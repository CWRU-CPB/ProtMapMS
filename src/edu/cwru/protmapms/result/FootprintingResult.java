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
package edu.cwru.protmapms.result;

import edu.cwru.protmapms.Constants;
import edu.cwru.protmapms.Identification;
import edu.cwru.protmapms.ComparableRetentionTime;
import edu.cwru.protmapms.RetentionTimes;
import edu.cwru.protmapms.RetentionTimeDatabase;
import edu.cwru.protmapms.Peptide;
import edu.cwru.protmapms.math.Quantize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top level result container that stores the full set of identifications made
 * during an analysis.
 * 
 * @author Sean Maxwell
 */
public class FootprintingResult extends HashMap<String,ProteinResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FootprintingResult.class);

    /**
     * Builtin method to compute the mean retention time from a list of 
     * identifications.
     *
     * @param identifications the identifications to evaluate
     *
     * @return mean of retention times
     */
    private static double rtMean(ArrayList<Identification> identifications) {
        double sum = 0.0;
        int i;

        for(i=0;i<identifications.size();i++) {
            sum += identifications.get(i).getRetentionTime();
        }

        return sum / identifications.size();
    }

    /**
     * Builtin method to compute the variance in retention times from a list of
     * identifications.
     *
     * @param identifications the identifications to evaluate
     *
     * @return variance of retention times
     */
    private static double rtVariance(ArrayList<Identification> identifications) {
        int i;
        double total = 0.0;
        double diff  = 0.0;
        double m = rtMean(identifications);
        
        for(i=0;i<identifications.size();i++) {
            diff = identifications.get(i).getRetentionTime()-m;
            total += Math.pow(diff,2);
        }

        return total/identifications.size();

    }
    
    public FootprintingResult() {
        super();
    }
    
    public ProteinResult getProteinResult(String accession) {
        if(!this.containsKey(accession)) {
            this.put(accession, new ProteinResult());
        }
        
        return this.get(accession);
    }
    
    public Set<String> getProteinAccessions() {
        return this.keySet();
    }
    
    public Set<String> getPeptideSequences(String proteinAccession) {
        return this.get(proteinAccession).keySet();
    }
            
    /* Find a peptide species that is identified across the most number of 
     * spectra and break ties using the median correlation of the 
     * identifications to prioritize species with more significant 
     * identifications 
     */
    public RetentionTimes getReferenceRetentionTimeIntervals() {
        Map<String,ArrayList<Double>> totalIntensity = new HashMap<>();
        Map<String,ArrayList<Identification>> totalIdentifications = new HashMap<>();
        Map<String,Set<String>> hits = new HashMap<>();
   
        /* Count significant identifications (those that passed the p < 0.001
         * significance level for correlation of theoretical to observed ions)
         * of unlabeled peptides to find the peptide with the strongest overall
         * signal in the data set (the one identified in the most spectra).
         */
        for(String accession : this.getProteinAccessions()) {
            ProteinResult pr = this.getProteinResult(accession);
            
            for(String peptideSequence : pr.getPeptideSequences()) {
                Peptide peptide = pr.getPeptide(peptideSequence);
                PeptideResult pepResult = pr.getPeptideResult(peptide);
                                   
                for(String exposureTime : pepResult.getSpectrumKeys()) {
                    SpectrumResult sr = pepResult.getSpectrumResult(exposureTime);

                    /*
                     * Iterate over un-labeled identifications only 
                     */
                    for(String miKey : sr.getUnlabeledKeys()) {
                        
                        for(Identification identification : sr.getUnlabeledIdentification(miKey)) {
                            String speciesKey = peptide.sequence+"_"+miKey;
                            if(!hits.containsKey(speciesKey)) {
                                totalIntensity.put(speciesKey, new ArrayList<>());
                                totalIdentifications.put(speciesKey, new ArrayList<>());
                                hits.put(speciesKey, new HashSet<>());
                            }


                            hits.get(speciesKey).add(exposureTime);
                            totalIntensity.get(speciesKey).add(identification.getPrecursorIntensity());
                            totalIdentifications.get(speciesKey).add(identification);
                        }
                    }
                }
            }
        }
        
        if(hits.keySet().isEmpty()) {
            System.out.printf("No interpolation possible. No unlabeled species detected\n");
            return new RetentionTimes();
        }
        
        /* Identify the unlabeled peptide species that was identified in the
         * most spectra, breaking ties using the median correlation values of
         * candidate peptide species */
        Integer maxHits = 0;
        Double minIntensity = Double.MIN_VALUE;
        String bestSpecies = "";
        for(String speciesKey : hits.keySet()) {
            //Double medCorr = Quantize.median(totalCorrelation.get(speciesKey));
            //Double rtVariance = FootprintingResult.rtVariance(totalIdentifications.get(speciesKey));
            Double medIntensity = Quantize.median(totalIntensity.get(speciesKey));
            Integer hitCount = hits.get(speciesKey).size();
            
            if(hitCount > maxHits || 
               (hitCount.compareTo(maxHits) == 0 && medIntensity > minIntensity)) {
                maxHits = hitCount;
                minIntensity = medIntensity;
                bestSpecies = speciesKey;
            }
        }
        System.out.printf("Best species: %s\n",bestSpecies);
        
        /* Return the identifications made for the species that can be used to 
         * to compute a high confidence retention time */
        RetentionTimes referenceIntervals = new RetentionTimes();
        referenceIntervals.addAll(totalIdentifications.get(bestSpecies));
        
        /* Set the m/z value of the reference species to the object to make
         * lookups easier */
        String[] parts = bestSpecies.split("_");
        referenceIntervals.setReferenceMz(parts[1]);
        return referenceIntervals;
    }
    
    /**
     * Returns the retention time intervals over which a mono-isotopic mass was 
     * identified in all spectra. Interpolates the retention time based on the 
     * best identification made in a different spectrum relative to a
     * "reference" retention time. This has no affect when the spectrum in
     * the request matches the spectrum where the identification was made,
     * i.e., the interpolated distance adjustment has magnitude 0.
     * 
     * @param spectrumKey The spectrum key for the spectrum under consideration
     * @param mzKey The m/z value key of the peptide species under consideration
     * @param reference The reference retention times in each spectrum (keyed on
     *                  the values exposureTime takes)
     * @param retentionTimes The retention time intervals across all spectra 
     *                        where this peptide was identified.
     * @return 
     */
    private List<ComparableRetentionTime> getRetentionTime(String spectrumKey, 
            String mzKey, 
            Map<String,Double> reference, 
            RetentionTimes retentionTimes) {
        
        List<ComparableRetentionTime> retentionTimeValues = new ArrayList<>();
        for(String spectrumKey2 : retentionTimes.keySet()) {
            if(retentionTimes.hasRetentionTimes(spectrumKey2, mzKey) &&
               reference.containsKey(spectrumKey2) &&
               reference.containsKey(spectrumKey)) {
                List<ComparableRetentionTime> spectrumRetentionTimes = retentionTimes.getRetentionTimes(spectrumKey2, mzKey);
                for(ComparableRetentionTime spectrumRetentionTime : spectrumRetentionTimes) {
                    Double reffInThisSpectrum = reference.get(spectrumKey);
                    Double reffInOtherSpectrum = reference.get(spectrumKey2);
                    Double mzRtInOtherSpectrum = spectrumRetentionTime.getRetentionTime();
                    Double mzRtInThisSpectrum = reffInThisSpectrum + (mzRtInOtherSpectrum-reffInOtherSpectrum);
                    retentionTimeValues.add(new ComparableRetentionTime(mzRtInThisSpectrum));
                }
            }
        }

        return retentionTimeValues;
    }
    
    public RetentionTimeDatabase getRetentionTimeDatabase(RetentionTimes reference) throws Exception {
        HashMap<String,RetentionTimes> retentionTimeIntervals = new HashMap<>();
        Set<String> exposureTimes = new HashSet<>();
        Set<String> peptideSequences = new HashSet<>();
        HashMap<String,HashMap<String,Set<Integer>>> chargeStates = new HashMap<>();
        HashMap<String,Set<String>> monoIsotpoicMasses = new HashMap<>();
        HashMap<String,HashMap<String,Boolean>> labelingMz = new HashMap<>();
        HashMap<String,HashMap<String,Set<Integer>>> massOffsets = new HashMap<>();
        HashMap<String,Boolean> hasUnlabeled = new HashMap<>();
        HashMap<String,Boolean> hasLabeled = new HashMap<>();
        Map<String,Double> referenceRt = reference.getMzMap(reference.referenceMz);
                
        
        /* First pass populates retention time intervals for each peptide+m/z
         * values from the identifications stored in this result object */
        for(String accession : this.getProteinAccessions()) {
            ProteinResult pr = this.getProteinResult(accession);
            
            for(String peptideSequence : pr.getPeptideSequences()) {
                Peptide peptide = pr.getPeptide(peptideSequence);
                PeptideResult pepResult = pr.getPeptideResult(peptide);
                     
                /* To compute the ratio of labeled to unlabeld peptide abundance
                 * we must detect an unlabeled species of the peptide in at 
                 * least on spectrum (it will be interpolated across the 
                 * others). Set this to false, and it will be set to true if
                 * at least one unlabeled species was identified.*/
                if(!hasUnlabeled.containsKey(peptideSequence))
                            hasUnlabeled.put(peptideSequence, Boolean.FALSE);
                if(!hasLabeled.containsKey(peptideSequence))
                            hasLabeled.put(peptideSequence, Boolean.FALSE);
                
                for(String exposureTime : pepResult.getSpectrumKeys()) {
                    SpectrumResult sr = pepResult.getSpectrumResult(exposureTime);
                    exposureTimes.add(exposureTime);

                    /* If any identifications were made in this spectrum, 
                     * allocate the necessary data structures to aggregate the
                     * results */
                    if(!sr.getLabeledKeys().isEmpty() || !sr.getUnlabeledKeys().isEmpty()) {
                        peptideSequences.add(peptideSequence);
                        if(!chargeStates.containsKey(peptideSequence)) {
                            chargeStates.put(peptideSequence, new HashMap<>());
                        }
                        if(!massOffsets.containsKey(peptideSequence)) {
                            massOffsets.put(peptideSequence, new HashMap<>());
                        }
                        if(!monoIsotpoicMasses.containsKey(peptideSequence)) {
                            monoIsotpoicMasses.put(peptideSequence, new HashSet<>());
                        }
                        if(!retentionTimeIntervals.containsKey(peptideSequence)) {
                            retentionTimeIntervals.put(peptideSequence, new RetentionTimes());
                        }

                        if(!labelingMz.containsKey(peptideSequence))
                            labelingMz.put(peptideSequence, new HashMap<>());
                    }

                    /* If there is at least one unlabeled species identified,
                     * it can be used for computing ratios of labaled/unlabeled
                     * peptide abundance. */
                    if(!sr.getUnlabeledKeys().isEmpty())
                        hasUnlabeled.put(peptideSequence,Boolean.TRUE);
                    if(!sr.getLabeledKeys().isEmpty())
                        hasLabeled.put(peptideSequence,Boolean.TRUE);
                    
                    for(String miKey : sr.getUnlabeledKeys()) {
                        if(!chargeStates.get(peptideSequence).containsKey(miKey))
                            chargeStates.get(peptideSequence).put(miKey,new HashSet<>());
                        
                        if(!massOffsets.get(peptideSequence).containsKey(miKey))
                            massOffsets.get(peptideSequence).put(miKey,new HashSet<>());
                        
                        for(Identification identification : sr.getUnlabeledIdentification(miKey)) {
                            labelingMz.get(peptideSequence).put(miKey, false);
                            monoIsotpoicMasses.get(peptideSequence).add(miKey);
                            chargeStates.get(peptideSequence).get(miKey).add(identification.getCharge());
                            massOffsets.get(peptideSequence).get(miKey).add(identification.getMassOffset().intValue());
                        }
                        retentionTimeIntervals.get(peptideSequence).addAll(sr.getUnlabeledIdentification(miKey));
                    }

                    for(String miKey : sr.getLabeledKeys()) {
                        if(!chargeStates.get(peptideSequence).containsKey(miKey))
                            chargeStates.get(peptideSequence).put(miKey,new HashSet<>());
                        
                        if(!massOffsets.get(peptideSequence).containsKey(miKey))
                            massOffsets.get(peptideSequence).put(miKey,new HashSet<>());
                        
                        for(Identification identification : sr.getLabeledIdentification(miKey)) {
                            labelingMz.get(peptideSequence).put(miKey, true);
                            monoIsotpoicMasses.get(peptideSequence).add(miKey);
                            chargeStates.get(peptideSequence).get(miKey).add(identification.getCharge());
                            massOffsets.get(peptideSequence).get(miKey).add(identification.getMassOffset().intValue());
                        }
                        retentionTimeIntervals.get(peptideSequence).addAll(sr.getLabeledIdentification(miKey));
                    }

                } 
                
            }
            
        }
        
        /* The second pass uses the reference retention time to interpolate 
         * retention times for peptide+m/z values in spectra where they
         * were not detected using the difference between the reference 
         * retention time and the peptide+m/z retention time in a spectrum
         * where it was detected.
         *
         * Iterates over every cell of the 4-dimensional hypercube defined by 4-tuples
         * drawn from peptideSequence[], exposureTime[], chargeStates[] and 
         * miKey[] where the cell value is retention time. This defines a 
         * retention time in every spectrum for each peptide+m/z values that was
         * identified in at least one spectra */
        RetentionTimeDatabase rtp = new RetentionTimeDatabase();
        for(String peptideSequence : peptideSequences) {
        
            /* Do not include peptides where no unlabeled species were 
             * identified */
            if(!hasUnlabeled.get(peptideSequence) || !hasLabeled.get(peptideSequence)) {
                LOGGER.info("Removing peptide {} from retention time interpolation because it does not have both labeled and unlabeled identifications",peptideSequence);
                continue;
            }
        
            for(String exposureTime : exposureTimes) {
                for(String monoIsotopicMassStr : monoIsotpoicMasses.get(peptideSequence)) {
                    /* Lookup retention time by mono isotopic mass. This 
                     * is a pool of retention times shared across all 
                     * detected charges states of this peptide species */
                    List<ComparableRetentionTime> retentionTimes = this.getRetentionTime(exposureTime, monoIsotopicMassStr, referenceRt, retentionTimeIntervals.get(peptideSequence));
                    Double monoIsotopicMass = Double.parseDouble(monoIsotopicMassStr);
                    
                    /* Verify that the mass offsets are all equal */
                    Set<Integer> miMassOffsets = massOffsets.get(peptideSequence).get(monoIsotopicMassStr);
                    if(miMassOffsets.size() > 1) {
                        LOGGER.error("Peptide {} with MI={} has multiple mass offsets {}",peptideSequence,monoIsotopicMass,miMassOffsets.toString());
                        throw new Exception("Multiple mass offsets associate with the same peptide+monoIsotopicMass");
                    }
                    
                    /* Iterate over charge states that the peptide species was
                     * detected at to compute m/z values */
                    for(Integer z : chargeStates.get(peptideSequence).get(monoIsotopicMassStr)) {
                        Double mz = (monoIsotopicMass+z*Constants.MASS_HYDROGEN)/z;
                        String mzKey = String.format("%.4f",mz);
                        
                        /* Add best retention time to output map */
                        rtp.addRetentionTime(peptideSequence, exposureTime, mzKey, retentionTimes, labelingMz.get(peptideSequence).get(monoIsotopicMassStr), z, miMassOffsets.iterator().next());
                    }
                }
            }
        }
        
        return rtp;
    }
    
    
}

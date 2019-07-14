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

import edu.cwru.protmapms.result.FootprintingResult;
import edu.cwru.protmapms.result.PeptideResult;
import edu.cwru.protmapms.result.ProteinResult;
import edu.cwru.protmapms.result.SpectrumResult;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Writes results to spreadsheet and JSON formats.
 * 
 * @author Sean Maxwell
 */
public class ResultWriter {
    /**
     * Writes tab-delimited information about each peptide identification deemed 
     * significant to file. 
     * @param outDir The directory where the result should be written
     * @param result The result to write to file
     * @throws Exception if an underlying operation throws an exception
     */
    public static void writeIdentificationReport(String outDir, FootprintingResult result) throws Exception {
        
        /* Create output directory */
        File outDirFile = new File(outDir);
        if(!outDirFile.exists())
            outDirFile.mkdirs();
        
        FileWriter fw = new FileWriter(outDir+"/identifications.tdv");
        fw.write("Accession\tPeptideSequence\tPeptideStart\tPeptideEnd\tSpectrumKey\tm/z\tZ\tRT\tScan\tScore\tModifications\tLabeling?\n");
        for(String accession : result.getProteinAccessions()) {
            ProteinResult protResult = result.getProteinResult(accession);
            for(String peptideSequence : protResult.getPeptideSequences()) {
                Peptide peptide = protResult.getPeptide(peptideSequence);
                PeptideResult pepResult = protResult.getPeptideResult(peptide);
                for(String spectrumKey : pepResult.getSpectrumKeys()) {
                    SpectrumResult specResult = pepResult.getSpectrumResult(spectrumKey);
                    for(String miKey : specResult.getUnlabeledKeys()) {
                        for(Identification id : specResult.getUnlabeledIdentification(miKey)) {
                            fw.write(String.format("%s\t%s\t%d\t%d\t%s\t%.4f\t%d\t%.4f\t%d\t%.4f\t%s\t%b\n",
                                    accession,
                                    peptideSequence,
                                    peptide.start()+1,
                                    peptide.end()+1,
                                    spectrumKey,
                                    id.getPrecursorMz(),
                                    id.getCharge(),
                                    id.getRetentionTime(),
                                    id.getScanNumber(),
                                    id.getScore(),
                                    id.getModifications().toString(),
                                    id.isLabeled()
                                    ));
                        }
                    }
                    
                    for(String miKey : specResult.getLabeledKeys()) {
                        for(Identification id : specResult.getLabeledIdentification(miKey)) {                        
                            fw.write(String.format("%s\t%s\t%d\t%d\t%s\t%.4f\t%d\t%.4f\t%d\t%.4f\t%s\t%b\n",
                                    accession,
                                    peptideSequence,
                                    peptide.start()+1,
                                    peptide.end()+1,
                                    spectrumKey,
                                    id.getPrecursorMz(),
                                    id.getCharge(),
                                    id.getRetentionTime(),
                                    id.getScanNumber(),
                                    id.getScore(),
                                    id.getModifications().toString(),
                                    id.isLabeled()
                                    ));
                        }
                    }
                }
            }
        }
        fw.close();
    }
    
    /**
     * Writes JSON format information about each peptide identification deemed 
     * significant to file. The JSON object is hierarchically organized as
     * protein id =&gt; peptide sequence =&gt; spectrum key =&gt; mono-isotopic 
     * mass =&gt; [identifications]
     * where the identifications are JSON returned by the toJSON() method of
     * each Identification object.
     * @param outDir The directory where the result should be written
     * @param result The result to write to file
     * @throws Exception if an underlying operation throws an exception
     */
    public static void writeIdentificationsJSON(String outDir, FootprintingResult result) throws Exception {
        
        /* Create output directory */
        File outDirFile = new File(outDir);
        if(!outDirFile.exists())
            outDirFile.mkdirs();
        
        FileWriter fw = new FileWriter(outDir+"/identifications.json");
        fw.write("{");
        boolean firstAccession = true;
        for(String accession : result.getProteinAccessions()) {
            if(!firstAccession) fw.write(",");
            else firstAccession = false;
            fw.write("\"");
            fw.write(accession);
            fw.write("\":{");
            
            ProteinResult protResult = result.getProteinResult(accession);
            boolean firstPeptide=true;
            for(String peptideSequence : protResult.getPeptideSequences()) {
                if(!firstPeptide) fw.write(",");
                else firstPeptide=false;
                fw.write("\"");
                fw.write(peptideSequence);
                fw.write("\":{");
                
                Peptide peptide = protResult.getPeptide(peptideSequence);
                PeptideResult pepResult = protResult.getPeptideResult(peptide);
                boolean firstSpectrum = true;
                for(String spectrumKey : pepResult.getSpectrumKeys()) {
                    if(!firstSpectrum) fw.write(",");
                    else firstSpectrum=false;
                    fw.write("\"");
                    fw.write(spectrumKey);
                    fw.write("\":[");
                
                    SpectrumResult specResult = pepResult.getSpectrumResult(spectrumKey);
                    boolean firstIdentification=true;
                    for(String miKey : specResult.getUnlabeledKeys()) {  
                        for(Identification id : specResult.getUnlabeledIdentification(miKey)) {
                            if(!firstIdentification) fw.write(",");
                            else firstIdentification=false;
                            fw.write(id.toJSON());
                        }
                    }
                    
                    for(String miKey : specResult.getLabeledKeys()) {                        
                        for(Identification id : specResult.getLabeledIdentification(miKey)) {                        
                            if(!firstIdentification) fw.write(",");
                            else firstIdentification=false;
                            fw.write(id.toJSON());
                        }
                    }
                    
                    fw.write("]");
                }
                fw.write("}");
            }
            fw.write("}");
        }
        fw.write("}");
        fw.close();
    }
    
    /**
     * Writes peak area information for each peptide across all spectra. This 
     * includes raw labeled/unlabeled areas, the percentage of the total area
     * extracted that is labeled and the ratio of the percent labeled in 
     * spectrum one versus spectrum 2.
     * @param outDir Directory where results should be written
     * @param result The result to write to file
     * @param rtp The database of retention times of peptides in the result
     * @param ms1e Extracted chromatograms of peptides in result
     * @param integrationSlack An interval to expand each retention time by when
     *                         constructing the integration intervals from 
     *                         retention times
     * @param spectrumFileMap A map of spectrum keys to spectrum file names
     * @throws Exception If an underlying operation throws an exception
     */
    public static void writePeakAreas(String outDir,
            FootprintingResult result, 
            RetentionTimeDatabase rtp, 
            MS1ExtractWithGaussianConfirmation ms1e,
            Double integrationSlack,
            Map<String,String> spectrumFileMap) throws Exception {
        
        /* Create output directory */
        File outDirFile = new File(outDir);
        if(!outDirFile.exists())
            outDirFile.mkdirs();
        
        FileWriter fw = new FileWriter(outDir+"/peak-areas.tdv");
        fw.write("Accession\tPeptide\tPeptideStart\tPeptideEnd\tSpectrumKey\tLabeled Area\tUnlabeled Area\t%Labeled\tR_1/0\n");
        for(String accession : result.getProteinAccessions()) {
            ProteinResult protResult = result.getProteinResult(accession);
            for(String peptideKey : protResult.getPeptideSequences()) {
                Peptide peptide = protResult.getPeptide(peptideKey);
                PeptideResult pepResult = protResult.getPeptideResult(peptide);
                Double lastRatio = Double.NaN;
                for(String spectrumKey : pepResult.getSpectrumKeys()) {
                    if(rtp.contains(peptideKey, spectrumKey)) {
                        Double labeled = 0.0;
                        Double unlabeled = 0.0;
                        for(String mzKey : rtp.getMzKeys(peptideKey, spectrumKey)) {
                            List<Interval> retentionTimes = RetentionTimes.getNonOverlappingIntervals(rtp.getRetentionTimes(peptideKey, spectrumKey, mzKey), integrationSlack);
                            for(Interval rt : retentionTimes) {
                                Double area = ms1e.getChromatogram(spectrumKey, mzKey).integrate(rt.start, rt.end);
                                if(rtp.isLabeling(peptideKey, spectrumKey, mzKey))
                                    labeled += area;
                                else
                                    unlabeled += area;
                            }
                        }
                        if(lastRatio.isNaN())
                            fw.write(String.format("%s\t%s\t%d\t%d\t%s\t%.4f\t%.4f\t%.4f\t\n",
                                    accession,
                                    peptideKey,
                                    peptide.start()+1,
                                    peptide.end()+1,
                                    spectrumFileMap.get(spectrumKey),
                                    labeled,
                                    unlabeled,
                                    labeled/(unlabeled+labeled)));
                        else
                            fw.write(String.format("%s\t%s\t%d\t%d\t%s\t%.4f\t%.4f\t%.4f\t%.4f\n",
                                    accession,
                                    peptideKey,
                                    peptide.start()+1,
                                    peptide.end()+1,
                                    spectrumFileMap.get(spectrumKey),
                                    labeled,
                                    unlabeled,
                                    labeled/(unlabeled+labeled),
                                    (labeled/(unlabeled+labeled))/lastRatio));
                        lastRatio = labeled/(unlabeled+labeled);
                    }
                }
            }
        }
        fw.close();
    }
    
    /**
     * Writes peak area information for each peptide across all spectra in JSON
     * format. The JSON is hierarchically organized as 
     * protein id =&gt; peptide sequence =&gt; spectrum key =&gt; Map
     * where each Map contains keys:
     * <ul>
     * <li>protein id (accession)</li>
     * <li>peptide (peptide sequence)</li>
     * <li>p_start (the start index of the peptide within the full protein sequence)</li>
     * <li>p_end (the end index of the peptide within the full protein sequence)</li>
     * <li>spectrum (the spectrum file id)</li>
     * <li>labeled (the raw peak area labeled)</li>
     * <li>unlabeled (the raw peak area unlabeled)</li>
     * <li>ratio (the ration of labeled/unlabeled)</li>
     * <li>c-ratio (the ratio from spectrum 2 divided by the ratio from spectrum 1)</li>
     * <li>species (a Map of m/z values to attributes for all different forms of the
     *  peptide that were identified)</li>
     * </ul>
     * and each m/z key in the species Map points to a map of attributes with
     * keys:
     * <ul>
     * <li>z (charge in m/z)</li>
     * <li>massOffset (the total mass of all modifications)</li>
     * <li>labeling (boolean indicating the modifications are considered labeling)</li>
     * <li>rti (an array of [start,end] retention time intervals)</li>
     * </ul>
     * 
     * @param outDir Directory where results should be written
     * @param result The result to write to file
     * @param rtp The database of retention times of peptides in the result
     * @param ms1e Extracted chromatograms of peptides in result
     * @param integrationSlack An interval to expand each retention time by when
     *                         constructing the integration intervals from 
     *                         retention times
     * @throws Exception If an underlying operation throws an exception
     */
    public static void writePeakAreasJSON(String outDir,
            FootprintingResult result, 
            RetentionTimeDatabase rtp, 
            MS1ExtractWithGaussianConfirmation ms1e,
            Double integrationSlack) throws Exception {
        
        /* Create output directory */
        File outDirFile = new File(outDir);
        if(!outDirFile.exists())
            outDirFile.mkdirs();
        
        /* Start output file */
        FileWriter fw = new FileWriter(outDir+"/peak-areas.json");
        fw.write("{");
        
        boolean firstProtein = true;
        for(String accession : result.getProteinAccessions()) {
            if(!firstProtein) fw.write(",");
            else firstProtein = false;
            
            fw.write("\"");
            fw.write(accession);
            fw.write("\":{");
            
            ProteinResult protResult = result.getProteinResult(accession);
            boolean firstPeptide=true;
            for(String peptideKey : protResult.getPeptideSequences()) {
                Peptide peptide = protResult.getPeptide(peptideKey);
                PeptideResult pepResult = protResult.getPeptideResult(peptide);
                
                if(!firstPeptide) fw.write(",");
                else firstPeptide = false;
                
                fw.write("\"");
                fw.write(peptideKey);
                fw.write("\":{");
                
                Double lastRatio = Double.NaN;
                for(String spectrumKey : pepResult.getSpectrumKeys()) {
                    if(rtp.contains(peptideKey, spectrumKey)) {
                        if(!lastRatio.isNaN()) {
                            fw.write(",");
                        }
                        fw.write("\"");
                        fw.write(spectrumKey);
                        fw.write("\":{");
                        
                        Double labeled = 0.0;
                        Double unlabeled = 0.0;
                        StringBuilder rti = new StringBuilder("{");
                        
                        boolean firstMz = true;
                        for(String mzKey : rtp.getMzKeys(peptideKey, spectrumKey)) {     
                            if(!firstMz) rti.append(",");
                            else firstMz=false;
                            
                            rti.append("\"");
                            rti.append(mzKey);
                            rti.append("\":{\"z\":");
                            rti.append(rtp.chargeState(peptideKey, spectrumKey, mzKey));
                            rti.append(",\"massOffset\":");
                            rti.append(rtp.massOffset(peptideKey, spectrumKey, mzKey));
                            rti.append(",\"labeling\":");
                            rti.append(rtp.isLabeling(peptideKey, spectrumKey, mzKey));
                            rti.append(",\"rti\":[");
                            
                            List<Interval> retentionTimes = RetentionTimes.getNonOverlappingIntervals(rtp.getRetentionTimes(peptideKey, spectrumKey, mzKey), integrationSlack);
                            boolean firstInterval = true;
                            for(Interval rt : retentionTimes) {
                                if(!firstInterval) rti.append(",");
                                else firstInterval = false;
                                rti.append("[");
                                rti.append(rt.start);
                                rti.append(",");
                                rti.append(rt.end);
                                rti.append("]");
                                
                                Double area = ms1e.getChromatogram(spectrumKey, mzKey).integrate(rt.start, rt.end);
                                if(rtp.isLabeling(peptideKey, spectrumKey, mzKey))
                                    labeled += area;
                                else
                                    unlabeled += area;
                            }
                            
                            rti.append("]");
                            rti.append("}");
                        }
                        rti.append("}");

                        Double ratio = labeled/(unlabeled+labeled);

                        fw.write("\"accession\":\"");
                        fw.write(accession);
                        fw.write("\",\"peptide\":\"");
                        fw.write(peptideKey);
                        fw.write("\",\"p_start\":");
                        fw.write(String.format("%d",peptide.start()+1));
                        fw.write(",\"p_end\":");
                        fw.write(String.format("%d",peptide.end()+1));
                        fw.write(",\"spectrum\":\"");
                        fw.write(spectrumKey);
                        fw.write("\",\"labeled\":");
                        fw.write(String.format("%.4e", labeled));
                        fw.write(",\"unlabeled\":");
                        fw.write(String.format("%.4e", unlabeled));
                        fw.write(",\"ratio\":");
                        if(ratio.isNaN()) {
                            fw.write("\"NaN\"");
                        }
                        else if(ratio.isInfinite()) {
                            fw.write("\"Inf\"");
                        }
                        else {
                            fw.write(String.format("%.4f",ratio));
                        }
                        if(!lastRatio.isNaN()) {
                            fw.write(",\"c-ratio\":");
                            Double cRatio = (labeled/(unlabeled+labeled))/lastRatio;
                            if(cRatio.isNaN()) {
                                fw.write("\"NaN\"");
                            }
                            else if(cRatio.isInfinite()) {
                                fw.write("\"Inf\"");
                            }
                            else {
                                fw.write(String.format("%.4f",(labeled/(unlabeled+labeled))/lastRatio));
                            }
                        }
                        fw.write(",\"species\":");
                        fw.write(rti.toString());
                        fw.write("}");
                        
                        lastRatio = labeled/(unlabeled+labeled);
                    }
                }
                fw.write("}");
            }
            fw.write("}");
        }
        fw.write("}");
        fw.close();
    }
    
    public static void writeChromatogramsJSON(String outDir,
            MS1ExtractWithGaussianConfirmation ms1e,
            RetentionTimeDatabase rtp) throws Exception {
        
        /* Create output directory */
        File outDirFile = new File(outDir);
        if(!outDirFile.exists())
            outDirFile.mkdirs();
        
        /* Start output file */
        FileWriter fw = new FileWriter(outDir+"/chromatograms.json");
        fw.write("{");
        boolean firstSpectrum = true;
        for(String spectrumKey : ms1e.chromatograms.keySet()) {
            if(!firstSpectrum) fw.write(",");
            else firstSpectrum = false;
        
            fw.write("\"");
            fw.write(spectrumKey);
            fw.write("\":{");
            
            boolean firstMz = true;
            for(String mzKey : ms1e.chromatograms.get(spectrumKey).keySet()) {
                if(!firstMz) fw.write(",");
                else firstMz = false;

                fw.write("\"");
                fw.write(mzKey);
                fw.write("\":");
                
                fw.write(ms1e.getChromatogram(spectrumKey, mzKey).toString());
            }
            fw.write("}");
        }
        fw.write("}");
        fw.close();
    }
}

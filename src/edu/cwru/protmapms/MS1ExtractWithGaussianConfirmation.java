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

import edu.cwru.protmapms.math.MathX;
import edu.cwru.protmapms.math.IntervalTree;
import edu.cwru.protmapms.math.Polynomial;
import edu.cwru.protmapms.spectra.SpectrumFile;
import edu.cwru.protmapms.spectra.Peaks;
import edu.cwru.protmapms.spectra.Scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts MS1 data from mzXML spectrumFiles and stores it in per-species
 * per-spectrum packages. This is the most complex portion of the algorithm. 
 * For a given m/z value we extract the m/z values at each retention time that 
 * are within the instrument resolution range around the m/z value, and if 3 or
 * more are within the window we fit a parabola to the three points and record
 * the intensity at the parabola height if the parabola center is within a 
 * specified error range of the target m/z value.
 *
 * @author Sean Maxwell
 */
public class MS1ExtractWithGaussianConfirmation {
    private static final Logger LOGGER = LoggerFactory.getLogger(MS1ExtractWithGaussianConfirmation.class);
    
    /**
     * The list of m/z values to extract MS1 data for
     */
    private final double[] mzValues;

    /**
     * The accuracy (PPM error) with which to extract MS1 intensities
     */
    private final double accuracy;
    
    /**
     * Resolution of instrument
     */
    private final double resolution;

    /**
     * The start retention time for MS1 extraction
     */
    private final double rtFrom;

    /**
     * The end retention time for MS1 extraction
     */
    private final double rtTo;

    /**
     * The spectrum file paths to extract MS1 data from
     */
    private final List<String> spectrumFiles;

    /**
     * Spectrum ids
     */
    private final List<String> spectrumKeys;

    /**
     * mzXML interface for opening and reading the spectrum files
     */
    private final SpectrumFile spectrumFileInterface;

    /**
     * Used to match wider ranges of m/z values so that more data is available
     * to assign peaks within the true ranges
     */
    private final IntervalTree roq;

    /**
     * Hash of maximum intensity by species and spectrum
     */
    public final HashMap<String,Double> maxint;
    
    /**
     * MS1 Chromatograms for each spectrum and m/z value
     */
    public final HashMap<String,HashMap<String,MS1Chromatogram>> chromatograms;
    
    /**
     * Charge state of each m/z value
     */
    private final double[] z;

    /**
     * Constructor
     *
     * @param mz The array of mz values for which to extract intensities
     * @param charge The charge states associated with each m/z value
     * @param acc The margin within which to m/z value are considered matches
     * @param res The resolution of the instrument
     * @param from The start retention time
     * @param to The end retention time
     * @param spectrumFiles The array of spectrum files
     * @param spectrumKeys The array of spectrum keys
     * @param sf A spectrum interface that is appropriate for accessing the
     *           argument spectrum files
     */
    public MS1ExtractWithGaussianConfirmation(double[] mz,
                                              double[] charge,
                                              double acc, 
                                              double res, 
                                              double from,
                                              double to, 
                                              List<String> spectrumFiles,  
                                              List<String> spectrumKeys,
                                              SpectrumFile sf) {
        mzValues = mz;
        z        = charge;
        accuracy = acc;
        this.spectrumFiles = spectrumFiles;
        this.spectrumKeys = spectrumKeys;
        rtFrom   = from;
        rtTo     = to;
        resolution = res;
        spectrumFileInterface = sf;
        roq      = new IntervalTree();
        maxint   = new HashMap<>();
        chromatograms = new HashMap<>();
    }

    /**
     * Populates the artificially wide, and the true ranges of m/z values that
     * we want to extract.
     */
    private void fillQueryDatabase() {
        int i;
        double window;

        /* Iterate over the array of mz values for extraction, and add them to
         * the filter using the requested accuracy */
        for(i=0;i<this.mzValues.length;i++) {
            window = this.mzValues[i] / resolution;
            this.roq.add(this.mzValues[i]-window, this.mzValues[i]+window, this.mzValues[i], i);
        }

    }

    /**
     * Stores intensity values by spectrum and m/z value.
     *
     * @param retentionTime Retention time that the intensity value occurs at
     * @param intensity Intensity
     * @param mzKey Species m/z value
     * @param spectrumKey Spectrum id
     *
     */
    private void storeIntensity(Double retentionTime, Double intensity, String mzKey, String spectrumKey) {
        if(!chromatograms.containsKey(spectrumKey))
            chromatograms.put(spectrumKey,new HashMap<>());
        
        if(!chromatograms.get(spectrumKey).containsKey(mzKey))
            chromatograms.get(spectrumKey).put(mzKey, new MS1Chromatogram(mzKey));
        
        chromatograms.get(spectrumKey).get(mzKey).add(retentionTime, intensity);
    }
    
    /**
     * For centroid data, returns the most intense centroid peak in the 
     * buffers that meet the error ppm window.
     * 
     * @param mz_buffer m/z values corresponding to centroided peaks
     * @param int_buffer intensities of centroid peaks
     * @param nIons number of centroid ions in buffer
     * @param mz m/z value we are extracting
     * @param charge charge (z) of m/z value we are extracting
     * @param ppm error parts per million to enforce for matching ions to m/z
     * 
     * @return intensity of most intense centroid ion in the error window
     */
    private static double fitCentroid(double[] mz_buffer, double[] int_buffer, int nIons, double mz, double charge, double ppm) {
        double mmass = (mz*charge)-(charge*Constants.MASS_PROTON);
        double tmass;
        double intensity = 0;
        int    i;
        
        /* Pick most intense ion from inside the user PPM error window */
        for(i=0;i<nIons;i++) {
            tmass = (mz_buffer[i]*charge)-(charge*Constants.MASS_PROTON);
            if(Math.abs((mmass-tmass)/mmass) * 1000000 <= ppm) {
                if(int_buffer[i] > intensity) {
                    intensity = int_buffer[i];
                }
            }
        }
        
        return intensity;
    }
    
    /**
     * Fits a parabola to the maximum intensity and two adjacent ions in the
     * argument buffer, and returns the intensity at the fitted peak center.
     * 
     * @param mz_buffer m/z values to use for fitting peak
     * @param int_buffer intensity values to use for fitting peak
     * @param lMax index of the maximum intensity in the buffer
     * @param mz m/z value we are extracting
     * @param charge charge (z) of the m/z value we are extracting
     * 
     * @return intensity at peak center if it could be fit, and 0.0 otherwise.
     */
    private double fitParabola(double[] mz_buffer, double[] int_buffer, int lMax, double mz, double charge) throws Exception {
        double[] mz_trunc  = new double[3];
        double[] int_trunc = new double[3];
        double[] mz_norm;
        double[] fit;
        double sdev;
        double mean;
        double center;
        double tcenter;
        double mmass;
        double tmass;
        
        /* Copy the maximum along with the preceding and 
         * following points to fixed size buffer */
        System.arraycopy(mz_buffer,  (lMax-1), mz_trunc,  0, 3);
        System.arraycopy(int_buffer, (lMax-1), int_trunc, 0, 3);

        /* Center and scale data. This helps when the peak is very far from the
         * origin and all the x values are almost the same */
        mean = MathX.mean(mz_trunc);
        sdev = MathX.sdev(mz_trunc, mean);
        mz_norm = mz_trunc.clone();
        mz_norm = MathX.shift(mz_norm, -1.0*mean);
        mz_norm = MathX.product(mz_norm, 1.0/sdev);
        
        /* Fit a quadratic to the 3 data points */
        fit = Polynomial.Quadratic(mz_norm,int_trunc);

        /* Could not fit polynomial */
        if(fit[2] == 0) {
            //System.out.printf("a is 0\n");
            return 0.0;
        }
        else {
            /* This combines several steps together. 
             * Fitting the quadratic provided the 
             * coefficient values a,b,c for the 
             * quadratic y = a + bx+cx^2. The parabola
             * peak is at x = -b/(2*c), and thus the
             * peak intensity is 
             * 
             * y = a + b*(-b/(2*c)) + c*(-b/(2*c))^2
             * 
             * and in the return a=fit[0],b=fit[1],
             * c=fit[2]
             */
            center = -1*fit[1]/(2*fit[2]);
            tcenter = (center*sdev)+mean;

            mmass = (mz*charge)-(charge*Constants.MASS_PROTON);
            tmass = (tcenter*charge)-(charge*Constants.MASS_PROTON);

            if(Math.abs((mmass-tmass)/mmass) * 1000000 <= accuracy) {
                return fit[0] + 
                       fit[1]*center +
                       fit[2]*Math.pow(center,2);
            }
            else {
                return 0.0;
            }
        }

    }
    
    /**
     * Iterates over the list of m/z values and extracts intensities from
     * the scans of each spectrum that fall within the specified start and end
     * retention time
     *
     * @param verbose When set to true debug/trace output will be sent to STDOUT
     * 
     * @return true on success, false on failure
     * 
     * @throws Exception if any errors are encountered
     */
    public boolean extract(boolean verbose) throws Exception {
        int[] scans;
        int[] ids;
        Peaks peaks;
        Scan scan;
        int i;
        int j;
        int k;
        int m;
        double[][] maxints    = new double[this.spectrumFiles.size()][this.mzValues.length];
        double[][] mz_buffer  = new double[this.mzValues.length][100];
        double[][] int_buffer = new double[this.mzValues.length][100];
        int[] nIons = new int[this.mzValues.length];
        String[] mzKeys = new String[this.mzValues.length];
        double[] local_max = new double[this.mzValues.length];
        int[] lMax = new int[this.mzValues.length];
        double intensity;
        
        /* Fill the range database with the m/z keys and their associated
         * ranges generated with the accuracy argument passed to the
         * constructor */
        this.fillQueryDatabase();

        /* Fill the key array to avoid overahead with computing keys ad-hoc in
         * the loop */
        for(k=0;k<mzKeys.length;k++) {
            mzKeys[k] = String.format("%.4f",this.mzValues[k]);
        }
        
        /* Iterate over each spectrum */
        for(k=0;k<this.spectrumFiles.size();k++) {
            /* Connect to the spectrum */
            spectrumFileInterface.connect(this.spectrumFiles.get(k));
            
            /* Query the spectrum for MS1 scans in the retention time range */
            scans = spectrumFileInterface.queryRetentionTime(this.rtFrom, this.rtTo, 1);
            LOGGER.info("MS1 extract will iterate over {} scans in spectrum {}",scans.length,this.spectrumFiles.get(k));
            
            /* Iterate over the list of scans */
            for(i=0;i<scans.length;i++) {
                /* Reset the local maximum variables */
                java.util.Arrays.fill(local_max, 0.0);
                java.util.Arrays.fill(lMax, 0);
                
                /* Reset the number of ions in each buffer to 0 */
                java.util.Arrays.fill(nIons, 0);
                
                
                /* Report some progress */

                
                /* Get the scan properties and the scan peak data */
                scan  = spectrumFileInterface.getScanProperties(scans[i]);
                peaks = spectrumFileInterface.getScanPeaks(scans[i]);
                              
                /* Iterate over the peaks */
                for(j=0;j<peaks.MZ.length;j++) {
                    /* Query the m/z range database to determine if this peak
                     * corresponds to one of the species of interest */
                    ids = this.roq.find(peaks.MZ[j]);
                    
                    /* Otherwise, we are processing a run of matches, so
                     * add the intensity to the buffer for processing */
                    for(m=0;m<ids.length;m++) {
                        mz_buffer[ids[m]][nIons[ids[m]]] = peaks.MZ[j];
                        int_buffer[ids[m]][nIons[ids[m]]] = peaks.Intensity[j];
                                                
                        /* If this is the most intense peak in the buffer,
                         * update the local maximum location */
                        if(peaks.Intensity[j] > local_max[ids[m]]) {
                            local_max[ids[m]] = peaks.Intensity[j];
                            lMax[ids[m]] = nIons[ids[m]];
                        }
                        
                        nIons[ids[m]]++;
                    }
                    
                }
                
                /* The flip-flop above can leave the last match in the buffer 
                 * unprocessed, so process it now if there is data waiting */
                for(m=0;m<mzValues.length;m++) {
                    /* If a profile scan and enough data is available, fit a
                     * parabola to the peaks inside the buffer that were within
                     * error window for the m/z value of interest */
                    if(scan.centroid == 0 && nIons[m] > 2 && lMax[m] > 0 && lMax[m] < (nIons[m]-1)) {
                        intensity = fitParabola(mz_buffer[m],int_buffer[m],lMax[m],this.mzValues[m],this.z[m]);
                        if(verbose) {
                            System.out.printf("P %d %.12f,%.2f,%f\n",scans[i],this.mzValues[m],scan.RetentionTime/60.0,intensity);
                        }
                        this.storeIntensity(scan.RetentionTime,
                                            intensity,
                                            mzKeys[m],
                                            this.spectrumKeys.get(k));
                        if(intensity > maxints[k][m]) {
                            maxints[k][m] = intensity;
                        }
                    }
                    
                    /* If a centroid scan and any data available, choose the 
                     * most intense peak among all peaks that were within error
                     * window for m/z of interest */
                    else if(scan.centroid == 1 && nIons[m] > 0) {
                        intensity = fitCentroid(mz_buffer[m],int_buffer[m],nIons[m],this.mzValues[m],z[m],this.accuracy);
                        if(verbose) {
                            System.out.printf("C %d %.12f,%.2f,%f,[%d]\n",scans[i],this.mzValues[m],scan.RetentionTime/60.0,intensity,nIons[m]);
                        }
                        this.storeIntensity(scan.RetentionTime,
                                            intensity,
                                            mzKeys[m],
                                            this.spectrumKeys.get(k));
                        if(intensity > maxints[k][m]) {
                            maxints[k][m] = intensity;
                        }
                    }
                    
                    /* If neither of the previous conditions were met, we do not
                     * have evidence to support an intensity signal for the m/z
                     * of interest at this time point, so record 0 */ 
                    else {
                        if(verbose) {
                            System.out.printf("E %d %.12f,%.2f,%f,[%d]\n",scans[i],this.mzValues[m],scan.RetentionTime/60.0,0.0,nIons[m]);
                        }
                        this.storeIntensity(scan.RetentionTime,
                                            0.0,
                                            mzKeys[m],
                                            this.spectrumKeys.get(k));
                    }
                }

            }

            spectrumFileInterface.disconnect();
        }

        /* Store the maximum intensities */
        for(k=0;k<this.spectrumFiles.size();k++) {
            for(i=0;i<mzKeys.length;i++) {
                maxint.put(String.format("%s_%s",mzKeys[i],this.spectrumKeys.get(k)),maxints[k][i]);
            }
        }
                
        return true;

    }
    
    /**
     * Retrieve the chromatogram extracted for an m/z value over a spectrum.
     * 
     * @param spectrumKey The key that identifies the spectrum of interest
     * @param mzKey The key for the m/z value of interest
     * 
     * @return Chromatogram, or null if request parameters are invalid
     */
    public MS1Chromatogram getChromatogram(String spectrumKey, String mzKey) {
        return chromatograms.get(spectrumKey).get(mzKey);
    }
    
    /**
     * Retrieve the chromatogram extracted for an m/z value over a spectrum.
     * 
     * @param spectrumKey The key that identifies the spectrum of interest
     * @param mzKey The m/z value of interest
     * 
     * @return Chromatogram, or null if request parameters are invalid
     */
    public MS1Chromatogram getChromatogram(String spectrumKey, Double mzKey) {
        return chromatograms.get(spectrumKey).get(String.format("%.4f",mzKey));
    }
    
}

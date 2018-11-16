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

/**
 * Generates theoretical ions (m/z values) for a peptide sequence and associated
 * mass offsets. The primary use of this class is to generate a list of 
 * precursor ions for a peptide (m/z of the full peptide sequence) and b/y/a 
 * ions (m/z values of peptide fragments).
 *
 * @author Sean Maxwell
 */
public class IonFactory {
    private static final double isotopeOffset = 1.002;
    
    /**
     * Calculates the mass of the argument amino acid sequence
     *
     * @param s Amino acid sequence
     *
     * @return Mono-isotpic mass
     */
    public static double calculateIonMass(String s) {
        int i;
        double m = 0.0;

        for(i=0;i<s.length();i++) {
            m += Residues.getMI(s.charAt(i));
        }

        /* Add mass of water */
        return m + 18.01056470;
    }
    
    public static double isotopeOffset(double m) {
        if(m < 1800) {
            return 0.0;
        }
        else if(m < 3300) {
            return isotopeOffset;
        }
        else if(m < 5000) {
            return 2*isotopeOffset;
        }
        else if(m < 6500) {
            return 3*isotopeOffset;
        }
        else if(m < 7500) {
            return 4*isotopeOffset;
        }
        else {
            return 5*isotopeOffset;
        }
    }
    
    /**
     * Generates the mass of a b or y with an associated list of Species (
     * modifications by site and mass offset).
     *
     * @param s The ion sequence
     * @param offsets array of mass offsets at each residue of sequence
     *
     * @return The ion mass
     */
    public static double calculateIonMass(String s, double[] offsets) {
        int i;
        double m = 0.0;

        /* Sum amino acid weights */
        for(i=0;i<s.length();i++) {
            /* Add the mass of the amino acid residue */
            m += Residues.getMI(s.charAt(i));
        }
        
        /* Sum modification weights */
        for(double offset : offsets) {
            m += offset;
        }
        
   
        return m+isotopeOffset(m);
        
    }
    
    private static double removeWater(double m) {
        return m-Constants.MASS_WATER;
    }
    
    private static double addWater(double m) {
        return m + Constants.MASS_WATER;
    }
    
    private static double removeAmmonia(double m) {
        return m - Constants.MASS_AMMONIA;
    }
    
    private static double removeCarbonMonoxide(double m) {
        return m - (Constants.MASS_CARBON+Constants.MASS_OXYGEN);
    }
    
    private static double applyCharge(double m, int z) {
        return (m+(z*Constants.MASS_PROTON))/z;
    }

    /**
     * For the argument charge state and mass offset, calculates the expected
     * precursor ion m/z values
     *
     * @param sequence Amino acid sequence
     * @param z Charge state
     * @param offsets array of mass offsets at each residue of sequence
     *
     * @return Precursor ion m/z values
     */
    public static double[] calculatePrecursorIonMass(String sequence, int z, double[] offsets) {
        double[] precursors = new double[6];
        double mass = calculateIonMass(sequence);
        double mass_offset = 0.0;
        for(double offset : offsets) {
            mass_offset += offset;
        }
        
        
        if(z==0) {
            /* First isotope */
            precursors[0]=mass + mass_offset;
            precursors[1]=mass-18.0106 + mass_offset;
            precursors[2]=mass-17.0265 + mass_offset;

            /* Second isotope */
            precursors[3] =mass+1.003355 + mass_offset;
            precursors[4] =mass-18.0106+1.003355 + mass_offset;
            precursors[5] =mass-17.0265+1.003355 + mass_offset;
        }
        else {
       
            /*
              START_BLOCK: Taken from genmzbyions2.m in original code base
            */
        
            /* First isotope */
            precursors[0]=((mass+mass_offset+z*1.007825)/z);// + mass_offset/z;
            precursors[1]=((mass+mass_offset-18.0106+z*1.007825)/z);// + mass_offset/z;
            precursors[2]=((mass+mass_offset-17.0265+z*1.007825)/z);// + mass_offset/z;

            /* Second isotope */
            precursors[3] =((mass+mass_offset+z*1.007825+1.003355)/z);// + mass_offset/z;
            precursors[4] =((mass+mass_offset-18.0106+z*1.007825+1.003355)/z);// + mass_offset/z;
            precursors[5] =((mass+mass_offset-17.0265+z*1.007825+1.003355)/z); // + mass_offset/z;
        
            /*
              END_BLOCK
            */

        }

        return precursors;
        
    }

    /**
     * Wrapper that calls calculatePrecursorIonMass with a mass offset of 0
     *
     * @param sequence Amino acid sequence
     * @param z Charge state
     *
     * @return Unmodified precursor ions masses
     */
    public static double[] calculateUnmodifiedPrecursorIonMass(String sequence, int z) {
        return calculatePrecursorIonMass(sequence, z, new double[sequence.length()]);
    }

    /**
     * Wrapper that calls calculatePrecursorIonMass with a mass offset from
 modifications
     *
     * @param sequence Amino acid sequence
     * @param z Charge state
     * @param o Mass offsets
     *
     * @return Modified precursor ions masses
     */
    public static double[] calculateModifiedPrecursorIonMass(String sequence, int z, double[] o) {
        return calculatePrecursorIonMass(sequence, z, o);
    }
    
    /**
     * Evaluate the amino acid sequence in <code>s</code> and determine if the
     * sequence is likely to exhibit water loss.
     * 
     * @param s Amino acid sequence of interest
     * 
     * @return True for likely, false of not likely
     */
    public static boolean probableWaterLoss(String s) {
        int i;
        char r;

        /* Check for E at N-terminus */
        if(s.charAt(0) == 'E') {
            return true;
        }
        
        for(i=0;i<s.length();i++) {
            r = s.charAt(i);
            if(r == 'S' || r == 'T') {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Evaluate the amino acid sequence in <code>s</code> and determine if the
     * sequence is likely to exhibit ammonia loss.
     * 
     * @param s Amino acid sequence of interest
     * 
     * @return True for likely, false of not likely
     */
    public static boolean probableAmmoniaLoss(String s) {
        int i;
        char r;
        
        for(i=0;i<s.length();i++) {
            r = s.charAt(i);
            if(r == 'R' || r == 'K' || r == 'Q' || r == 'N') {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Returns an array of b and y ion masses for the peptide. Both b and y are
     * in the same array with b ions stored in the first third before y ions in
     * the last two thirds
     *
     * @param sequence Amino acid sequence
     * @param z Charge state of peptide
     * @param offsets Array of mass offsets that correspond to each amino acid
     *                residue of the sequence
     *
     * @return b and y ion mz values
     */
    public static double[] getTheoreticalIons(String sequence, int z, double[] offsets) {
        int i;
        int j;
        int p = 0;
        double running_mass;
        double ion_mass;

        /* The number of b and y ions is equal to the 2*number of charge states*
         * (peptide length - 1) */
        double[] ions = new double[z*6*(sequence.length()-1)];

        /* Generate b ions */
        for(j=1;j<=z;j++) {
            running_mass = 0.0;
            for(i=0;i<sequence.length()-1;i++) {                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * B-ION without water loss
                 */
                
                /* Calculate the base mass, which is residue mass plus 
                 * modification mass */
                running_mass += Residues.getMI(sequence.charAt(i));
                running_mass += offsets[i];
                
                ion_mass = running_mass+isotopeOffset(running_mass);
                ions[p] = applyCharge(ion_mass,j);
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * B-ION with water loss
                 */
                
                /* if water loss is probable, remove mass of water */
                if(probableWaterLoss(sequence)) {
                    ion_mass = removeWater(ion_mass);
                    ions[p] = applyCharge(ion_mass,j);
                    ion_mass = addWater(ion_mass);
                }
                else {
                    ions[p] = -1.0;
                }
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * A-ION (B-ION minus mass of Caron Monoxide)
                 */
                ion_mass = removeCarbonMonoxide(ion_mass);
                ions[p] = applyCharge(ion_mass,j);
                p++;
                
            }
        }

        /* Generate y ions */
        for(j=1;j<=z;j++) {
            running_mass = 0.0;
            for(i=sequence.length()-1;i>0;i--) {                
                /* Calculate the bass mass with no water loss */
                running_mass += Residues.getMI(sequence.charAt(i));
                running_mass += offsets[i];
                ion_mass = running_mass;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION with water added (???)
                 */
                ion_mass = addWater(ion_mass);
                ions[p] = applyCharge(ion_mass,j);
                p++;

                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION without water added (minus water???)
                 */
                if(probableWaterLoss(sequence)) {
                    ion_mass = removeWater(ion_mass);
                    ions[p] = applyCharge(ion_mass,j);
                    ion_mass = addWater(ion_mass);
                }
                else {
                    ions[p] = -1.0;
                }
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION with water added and ammonia removed
                 */
                if(probableAmmoniaLoss(sequence)) {
                    ion_mass = removeAmmonia(ion_mass);
                    ions[p] = applyCharge(ion_mass,j);
                }
                else {
                    ions[p] = -1.0;
                }
                p++;
            }
        }

        return ions;

    }
    
    /**
     * Returns an array of b and y ion masses for the peptide. Both b and y are
     * in the same array with b ions stored in the first third before y ions in
     * the last two thirds
     *
     * @param sequence Amino acid sequence
     * @param z Charge state of peptide
     * @param offsets Array of mass offsets that correspond to each amino acid
     *                residue of the sequence
     *
     * @return b and y ion mz values
     */
    public static String[] getTheoreticalIonLabels(String sequence, int z, double[] offsets) {
        int i;
        int j;
        int p = 0;

        /* The number of b and y ions is equal to the 2*number of charge states*
         * (peptide length - 1) */
        String[] ions = new String[z*6*(sequence.length()-1)];

        /* Generate b ions */
        for(j=1;j<=z;j++) {
            for(i=0;i<sequence.length()-1;i++) {                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * B-ION without water loss
                 */
                ions[p] = String.format("%d b%d",j,i+1);
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * B-ION with water loss
                 */
                
                /* if water loss is probable, remove mass of water */
                if(probableWaterLoss(sequence)) {
                    ions[p] = String.format("%d b%d-H2O",j,i+1);
                }
                else {
                    ions[p] = "---";
                }
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * A-ION (B-ION minus mass of Caron Monoxide)
                 */
                ions[p] = String.format("%d a%d",j,i+1);
                p++;
            }
        }

        /* Generate y ions */
        for(j=1;j<=z;j++) {
            for(i=sequence.length()-1;i>0;i--) {                               
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION with water added (???)
                 */
                ions[p] = String.format("%d y%d",j,sequence.length()-i);
                p++;

                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION without water added (minus water???)
                 */
                if(probableWaterLoss(sequence)) {
                    ions[p] = String.format("%d y%d-H2O",j,sequence.length()-i);
                }
                else {
                    ions[p] = "---";
                }
                p++;
                
                /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                 * Y-ION with water added and ammonia removed
                 */
                if(probableAmmoniaLoss(sequence)) {
                    ions[p] = String.format("%d y%d-NH3",j,sequence.length()-i);
                }
                else {
                    ions[p] = "---";
                }
                p++;
            }
        }

        return ions;

    }
    
    public static void main(String[] args) {
        String sequence = "PEPTIDER";
        double[] offsets = new double[sequence.length()];
        int Z = 2;
        double[] ions = IonFactory.getTheoreticalIons(sequence, Z, offsets);
        String[] labels = IonFactory.getTheoreticalIonLabels(sequence, Z, offsets);
        for(int i=0; i<ions.length; i++) {
            System.out.printf("%.4f\t%s\n",ions[i],labels[i]);
        }
    }

}

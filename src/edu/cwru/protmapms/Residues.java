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

import java.util.HashSet;
import java.util.Set;

/**
 * A database of AminoAcids. Exposes routines to retrieve mono-isotopic and 
 * average mass by amino acid, and to define non-standard amino acids.
 *
 * @author Sean Maxwell
 */
public class Residues {
    /**
     * Array of amino acid objects to use for fast lookup by char to int 
     * conversion. The array is large enough to fit all ascii characters from
     * '!'=33 to '~'=126, but by default it is only initialized with entries for
     * the 21 standard amino acids that start at 'A'=65.
     */
    private static final AminoAcid[] AMINOACIDS = new AminoAcid[94];
    
    /*
     * Static initialization creates entries for the standard amino acids.
     */
    static {
        char[] CODES = {
            'A','R','N','D',
            'C','E','Q','G',
            'H','I','L','K',
            'M','F','P','S',
            'T','U','W','Y',
            'V'};
        
        double[] MIMASSES = {
            71.037114, 156.101111, 114.042927, 115.026943,
            103.009185, 129.042593, 128.058578, 57.021464,
            137.058912, 113.084064, 113.084064, 128.094963,
            131.040485, 147.068414, 97.052764, 87.032028,
            101.047679, 150.95363, 186.079313, 163.06332,
            99.068414};
        
        double[] AVMASSES = {
            71.0779, 156.1857, 114.1026, 115.0874,
            103.1429, 129.114, 128.1292, 57.0513,
            137.1393, 113.1576, 113.1576, 128.1723,
            131.1961, 147.1739, 97.1152, 87.0773,
            101.1039, 150.0379, 186.2099, 163.1733,
            99.1311};
        
        /* Create a new amino acid for each array element */
        int code;
        for(int i=0;i<21;i++) {
            code = ((int)CODES[i])-33;
            AMINOACIDS[code] = new AminoAcid();
            AMINOACIDS[code].residue = CODES[i];
            AMINOACIDS[code].miMass  = MIMASSES[i];
            AMINOACIDS[code].avMass  = AVMASSES[i];
        }

    }

    /**
     * Add a non-standard amin acid, or set custom data for one of the default
     * entries.
     * 
     * @param residue The ascii char that represents the amnio acid
     * @param miMass mono-isotopic mass of residue
     * @param avMass average mass of residue
     * 
     * @throws Exception if the code is not in the valid range of non-control
     * ASCII values.
     */
    public static void configureAminoAcid(char residue, double miMass, double avMass) throws Exception {
        int code = (int)residue-33;
        if(code < 0 || code > 93)
            throw new Exception("The residue code "+residue+" is out of the valid range of ascii characters (33-126)");
        
        AMINOACIDS[code]=new AminoAcid();
        AMINOACIDS[code].residue = residue;
        AMINOACIDS[code].miMass  = miMass;
        AMINOACIDS[code].avMass  = avMass;
    }
    
    /**
     * getMI returns the mono-isotopic mass for the argument amino acid.
     *
     * @param residue the residue to retrieve the mass for
     *
     * @return the mono-isotopic mass
     *
     */
    public static double getMI(char residue) {
        int code = (int)residue-33;
        return AMINOACIDS[code].miMass;
    }

    /**
     * getAvg returns the average mass for the argument amino acid.
     *
     * @param residue the residue to retrieve the mass for
     *
     * @return the average mass
     *
     */
    public static double getAvg(char residue) {
        int code = (int)residue-33;
        return AMINOACIDS[code].avMass;
    }
    
    /**
     * Retrieve a set of all codes that correspond to configured amino acids.
     * Currently used by Fasta to ensure protein sequences do not contain 
     * amino acids with no configured masses.
     * @return 
     */
    public static Set<Character> getResidueCodes() {
        Set<Character> codes = new HashSet<>();
        for(int i=0;i<94;i++) {
            if(AMINOACIDS[i]!=null) {
                codes.add(AMINOACIDS[i].residue);
            }
        }
        return codes;
    }

}

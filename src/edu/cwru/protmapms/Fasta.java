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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Encapsulates access to databases of protein sequences in the FASTA format.
 * The format specification is very sparse, and this class follows some non-
 * standard conventions set forth in the default Uniprot format for protein
 * accessions in order to extract gene symbols if present.
 *
 * @author Sean Maxwell
 */
public class Fasta {
    private Map<String,String> sequences;
    
    /**
     * Test a protein accession to determine if it is a valid Uniprot accession.
     * @param accession Protein accession to test
     * @return true if the accession is a valid Uniprot accession and false
     * otherwise.
     */
    private boolean isUniprot(String accession) {
        return accession.matches("[OPQ][0-9][A-Z0-9]{3}[0-9](_.+)?|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}(_.+)?");
    }
    
    /**
     * Parse the protein identifier(accession number) from the accession line
     * of a FASTA database. It supports two formats that cover most scenarios:
     * <br>
     * <ul>
     * <li>&gt;{ACCESSION}</li>
     * <li>&gt;sp|{ACCESSION} {DESCRIPTION}</li>
     * </ul>
     * 
     * @param line The accession line to parse
     * 
     * @return Accession number parsed from the line
     */
    private String parseAccession(String line) {
        if(line.contains("|")) {
            String[] parts = line.split("\\|");
            return parts[1].trim();
        }
        else {
            return line.trim();
        }
    }
    
    /**
     * Parse the gene name (GN) from the accession line of a FASTA database. 
     * 
     * @param line The accession line to parse
     * 
     * @return Gene symbol parsed from the line
     */
    private String parseGeneSymbol(String line) {
        if(line.contains("GN=")) {
            int start = line.indexOf("GN=");
            int end = line.indexOf(" ",start);
            if(end == -1) {
                end = line.length();
            }
            return "_"+line.substring(start+3, end);
        }
        else {
            return "";
        }
    }
    
    /**
     * Verifies that all amino acids in the sequence are defined.
     * 
     * @param line sequence of amino acid single letter codes
     * @param accession accession number of sequence being parsed
     * @param allowed set of allowed single letter codes (those that are defined)
     * 
     * @throws Exception if the sequence contains an undefined single letter code
     */
    private void scanSequence(String line, String accession, Set<Character> allowed) throws Exception {
        for(int i=0;i<line.length();i++) {
            if(!allowed.contains(line.charAt(i)))
                throw new Exception("sequence "+accession+"contains undefined amino acid "+line.charAt(i));
        }
    }
    
    /**
     * Constructor initializes a new database and populates it with the content
     * of the file located at the argument path.
     * 
     * @param path location of a FASTA protein sequence file
     * @param enforceUniprotAccession stipulates that accession lines must 
     *        contain accession numbers in Uniprot format.
     * 
     * @throws Exception if the request cannot be fulfilled. This can be 
     * because a sequence contains undefined amino acid codes, because Uniprot
     * accession parsing is turned on and encounters non-Uniprot accessions,
     * or because the database is empty.
     */
    public Fasta(String path, boolean enforceUniprotAccession) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        String accessionLine = null;
        StringBuilder sequence = new StringBuilder();
        Set<Character> allowed = Residues.getResidueCodes();
        sequences = new HashMap<>();
        
        while((line=br.readLine()) != null) {
            /* Be flexible, and ignore blank lines */
            if(line.equals("")) continue;
            
            /* Start of new sequence */
            if(line.startsWith(">")) {
                if(accessionLine != null) {
                    /* If not a Uniprot accession, terminate here */
                    if(isUniprot(accessionLine) || !enforceUniprotAccession) {
                        sequences.put(accessionLine,sequence.toString());
                    }
                    else {
                        throw new Exception(String.format("Encountered non-Uniprot accession %s parsing database",accessionLine));
                    }
                }
                accessionLine = parseAccession(line.substring(1))+parseGeneSymbol(line.substring(1));
                sequence.delete(0, sequence.length());
            }
            
            /* Extension of amino acid sequence */
            else {
                scanSequence(line,accessionLine,allowed);
                sequence.append(line);
            }
            
        }
        
        if(accessionLine != null) {
            if(isUniprot(accessionLine) || !enforceUniprotAccession) {
                sequences.put(accessionLine,sequence.toString());
            }
            else {
                throw new Exception(String.format("Encountered non-Uniprot accession %s parsing database",accessionLine));
            }
        }
        
        /* If database in empty, it was invalid */
        if(sequences.isEmpty()) {
            throw new Exception("The database contained no protein sequences");
        }
        
        br.close();
    }
    
    /**
     * Constructor initializes a new database and populates it with the content
     * of the file located at the argument path.
     * 
     * @param path location of a FASTA protein sequence file
     * 
     * @throws Exception if the request cannot be fulfilled. 
     */
    public Fasta(String path) throws Exception {
        this(path,true);
    }
    
    /**
     * Retrieve the protein sequence corresponding to a protein accession.
     * @param accession Protein accession identify sequence of interest
     * @return Protein amino acid sequence.
     */
    public String getSequence(String accession) {
        return sequences.get(accession);
    }
    
    /**
     * Retrieve the map of all accessions to sequences.
     * @return all accessions and sequences.
     */
    public Map<String,String> getSequences() {
        return sequences;
    }
    
    /**
     * Retrieve all accessions from database.
     * @return all protein accessions.
     */
    public Set<String> getAccessions() {
        return sequences.keySet();
    }
    
    /**
     * Report how many sequences are stored in the object.
     * @return Number of sequences stored in object.
     */
    public int size() {
        return sequences.size();
    }
    
}

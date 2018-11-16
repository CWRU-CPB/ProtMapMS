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
package edu.cwru.protmapms.protease;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Encapsulates the rules necessary for a protease to be used for effectively
 * cleaving a protein sequence. This includes the amino acids on which the
 * protease cleaves, as well as CleavageFilters that confirm a site is correct,
 * and CleavageExceptions that are edge cases to the general cut rules for the
 * protease.
 *
 * @author Sean Maxwell
 */
public class Protease {
    /**
     * A regular expression defining at what sites the protease cleaves.
     */
    private final Pattern cutSitePattern;

    /**
     * An array of optional matchers that confirm if a potential cleave site is 
     * acceptable.
     */
    private final CleavageSiteMatcher[] siteMatchers;

    /**
     * An array of optional siteExcluders that will negate cleave sites that 
     * would otherwise be acceptable.
     */
    private final CleavageSiteExceptionMatcher[] siteExcluders;
    
    /**
     * The offset from the cut site at which to cut. 0 being before the site,
     * and 1 being after the site.
     */
    private final int offset;
    
    /* Counters for how full internal arrays are populated */
    private int n_filters = 0;
    private int n_exceptions = 0;
    

    /**
     * Constructor creates a new instance of a Protease that cleaves at the
     * amino acid pattern c_amino at an offset of c_offset. The offset is only 
     * used if the Protease has no CleaveageSiteMatchers attached, as each 
     * CleaveageSiteMatcher has it's own offset specified for cut sites.
     *
     * @param c_amino The amino acids on which the Protease cuts (regex)
     * @param c_offset The offset for cut. 0 before, 1 for after.
     */
    public Protease(String c_amino, int c_offset) {
        cutSitePattern    = Pattern.compile(c_amino);
        siteMatchers    = new CleavageSiteMatcher[10];
        siteExcluders = new CleavageSiteExceptionMatcher[10];
        offset     = c_offset;
    }

    /**
     * Retrieve the offset for this Protease.
     *
     * @return offset (0 to cut before, 1 to cut after)
     */
    public int offset() {
        return this.offset;
    }

    /**
     * Retrieve the offset from the argument site matcher index.
     * @param i The matcher index to retrieve the offset from.
     * @return offset (0 to cut before, 1 to cut after)
     */
    public int offset(int i) {
        return this.siteMatchers[i].offset();
    }

    /**
     * Retrieve the number of CleavageSiteMatchers defined for this Protease
     * @return The number of site matchers
     */
    public int matcherCount() {
        return this.n_filters;
    }

    /**
     * Retrieve the number of CleaveageSiteExceptionMatchers defined for this 
     * Protease.
     * @return The number of site excluders
     */
    public int excluderCount() {
        return this.n_exceptions;
    }

    /**
     * Constructs a Matcher that will find potential cut sites for this Protease
     * in the argument sequence.
     * 
     * @param seq Amino acid sequence to cut
     * 
     * @return A matcher that will find potential cut sites in the sequence. 
     */
    public Matcher getMatcher(String seq) {
        return this.cutSitePattern.matcher(seq);
    }
    
    /**
     * Add a CleaveageSiteMatcher to the Protease. The CleavageSiteMatcher 
     * specifies a window to look at around the potential cut site, a regex to 
     * match to the potential cut site, and an offset to use for cutting if the
     * potential cut site matches the regex.
     *
     * @param pattern The regular expression to match to the cut site window
     * @param left Number of amino acids left of the cut site to use in window
     * @param right Number of amino acid right of the cut site to use in window
     * @param c_offset The offset to cut at from cut site (0 before, 1 after)
     * @return true if the matcher was added, and false if there was no space.
     */
    public boolean addMatcher(String pattern, int left, int right, int c_offset) {
        /* Make sure it isn't full */
        if(this.n_filters == 10) {
            return false;
        }

        /* Create a new filter in the array */
        this.siteMatchers[this.n_filters] = 
                new CleavageSiteMatcher(pattern,left,right,c_offset);
        
        /* Update the count and return */
        this.n_filters++;
        return true;

    }

    /**
     * Add a CleaveageSiteExceptionMatcher to the Protease. The exception 
     * specifies a window to view left and right of the potential cut site, and
     * a regex to match to the window.
     *
     * @param pattern The regular expression to match to the window
     * @param left Number of amino acids left to look from potential cut site
     * @param right Number of amino acids right to look from potential cut site
     * @return true if exception was added, and false if there was no space
     */
    public boolean addExclusion(String pattern, int left, int right) {
        /* Make sure it isn't full */
        if(this.n_exceptions == 10) {
            return false;
        }

        /* Create a new filter in the array */
        this.siteExcluders[this.n_exceptions] = 
                new CleavageSiteExceptionMatcher(pattern,left,right);

        /* Update the count and return */
        this.n_exceptions++;
        return true;

    }

    /**
     * Tests a position in the peptide for a match to an attached exclusion.
     *
     * @param i The exclusion index
     * @param seq The protein sequence
     * @param site The potential cut site
     * @return true if the site is an exclusion, and false otherwise
     *
     */
    public boolean isExclusion(int i, String seq, int site) {
        return this.siteExcluders[i].matches(seq, site);
    }

    /**
     * Tests a position in the peptide for a match to an attached cleave
     * site.
     *
     * @param i The site matcher index
     * @param seq The protein sequence
     * @param site The potential cut site
     * @return -1 if the cleavage site does not match, and the offset from
     * the argument position at which to cleave the peptide otherwise (0 for
     * before and 1 for after)
     *
     */
    public int isCleaveSite(int i, String seq, int site) {
        return this.siteMatchers[i].matches(seq, site);
    }

    /**
     * Serialize the protease to a JSON string.
     * 
     * @param indent Indentation string to be appended to the JSON (for pretty 
     * output during debugging)
     * 
     * @return Protease as JSON 
     */
    public String toString(String indent) {
        String r = String.format("%s{\"Protease\":{\n%s\t\"pattern\":\"%s\",\n%s\t\"offset\":%d,\n%s\t\"filters\":[",indent,indent,cutSitePattern.pattern(),indent,offset,indent);
        for(int i=0; i<n_filters;i++) {
            if(i == 0) {
                r += "\n";
            }
            r += this.siteMatchers[i].toString(indent+"\t\t");
        }
        r += indent+"\t],\n\t"+indent+"\"exceptions\":[";
        for(int i=0; i<n_exceptions;i++) {
            if(i == 0) {
                r += "\n";
            }
            r += this.siteExcluders[i].toString(indent+"\t\t");
        }
        r += indent+"\t]\n\t"+indent+"}\n"+indent+"}\n";
        return r;
    }

}

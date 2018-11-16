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

/**
 * Exposes methods to define an exception to a cleavage site and compare a site
 * to the definition. It uses a regular expression and a window size to
 * search within (specified as amino acids left, and amino acids right) for a
 * match to the regular expression.
 * <br><br>
 * <strong>EXAMPLE:</strong>
 * <br><br>
 * Trypsin cleaves after R or K, but does not cleave at [CD]KD
 * <br><br>
 * To check for this exception, we would create a cleavage
 * exception that looks 1 amino acid left and one amino acid right of K
 * and checks for C or D before and D after:
 * <br><br>
 * <pre>CleaveageException ce = new CleavageSiteExceptionMatcher("[CD]KD",1,1);</pre>
 * <br><br>
 If sequences TDKDT and TCKDT were test with matches(seq,pos):
 <br><pre>
 matches("TCKDT",2);
 matches("TDKDT",2);
 </pre><br>
 * They would both return true, and this information could be used to reject
 * this potential cleave site.
 *
 * @author Sean Maxwell
 */
public class CleavageSiteExceptionMatcher {
    private final Pattern exception;
    private final int left;
    private final int right;

    /**
     * Constructor. Compiles the regular expression string to a Pattern and
     * stores the left and right distances to look.
     * @param pattern The regular expression
     * @param c_left amino acids to look left
     * @param c_right amino acids to look right
     */
    public CleavageSiteExceptionMatcher(String pattern, int c_left, int c_right) {
        exception = Pattern.compile(pattern);
        left      = c_left;
        right     = c_right;
    }

    /**
     * Tests if the defined exception matches a site in a protein sequence.
     *
     * @param sequence The full protein sequence
     * @param site The potential cut site position
     *
     * @return true if the site is an exception. false if it is not.
     */
    public boolean matches(String sequence, int site) {
        int adjusted_left;
        int adjusted_right;
        int pad_left;
        int pad_right;
        String pad_string = "####################";

        /* Safely set the left boundary of the site */
        if(site < this.left) {
            adjusted_left = 0;
            pad_left = this.left - site;
        }
        else {
            adjusted_left = site - this.left;
            pad_left = 0;
        }

        /* Safely set the right boundary of the site */
        if(site+this.right > sequence.length()-1) {
            adjusted_right = sequence.length();
            pad_right = ((site+this.right)-sequence.length())+1;
        }
        else {
            adjusted_right = site + this.right+1;
            pad_right = 0;
        }

        /* Extract the cleaveage site window for confirmation check */
        String s = pad_string.substring(0,pad_left)+
                   sequence.substring(adjusted_left,adjusted_right)+
                   pad_string.substring(0,pad_right);
        
        /* Test for match */
        return this.exception.matcher(s).find();

    }
    
    public String toString(String indent) {
        return String.format("%s{\"CleavageException\":{\n%s\t\"pattern\":\"%s\",\n%s\t\"left\":%d,\n%s\t\"right\":%d\n%s\t}\n%s}\n",indent,indent,exception.pattern(),indent,left,indent,right,indent,indent);
    }

}

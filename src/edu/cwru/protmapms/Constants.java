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
 * Defines constant values used throughout the application, mostly atomic 
 * masses of certain atoms and molecules.
 * 
 * @author Sean Maxwell
 */
public class Constants {
    public static final String VERSION_STR   = "3.0.0a";
    public static final int    VERSION_INT   = 300;
    public static final double MASS_CARBON   = 12.0;
    public static final double MASS_OXYGEN   = 15.994915;
    public static final double MASS_NITROGEN = 14.003074;
    public static final double MASS_HYDROGEN = 1.007825;
    public static final double MASS_PROTON   = 1.007276466812;
    public static final double MASS_ELECTRON = 0.000548579909;
    public static final double MASS_WATER    = (2*MASS_HYDROGEN)+MASS_OXYGEN;
    public static final double MASS_AMMONIA  = (3*MASS_HYDROGEN)+MASS_NITROGEN;

}

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
package edu.cwru.protmapms.modifications;

/**
 * Encapsulates all details about a modification. This is a modification type
 * like "phosphorylated serine" not phsophorylated serine 125 which is a 
 * modification site.
 * 
 * @author Sean Maxwell
 */
public class Modification {
    public String aminoAcid;
    public Integer position;
    public String name;
    public Double massOffset;
    public boolean fixed;
    public boolean labeling;
    
    public Modification(String aminoAcid, String name, Double massOffset) {
        this.aminoAcid = aminoAcid;
        this.position = -1;
        this.name = name;
        this.massOffset = massOffset;
    }
    
    public Modification(Integer position, String name, Double massOffset) {
        this.position = position;
        this.name = name;
        this.massOffset = massOffset;
    }
    
    public Modification setFixed(boolean b) throws Exception {
        if(b && this.labeling) {
            throw new Exception(String.format("Modification %s[%d]+%.4f cannot be both labeling and fixed",aminoAcid,position,massOffset));
        }
        this.fixed = b;
        return this;
    }
    
    public Modification setLabeling(boolean b) throws Exception {
        if(b && this.fixed) {
            throw new Exception(String.format("Modification %s[%d]+%.4f cannot be both labeling and fixed",aminoAcid,position,massOffset));
        }
        this.labeling = b;
        return this;
    }
    
    public String name() {
        return name;
    }
    
    public String aminoAcid() {
        return aminoAcid;
    }
    
    public Integer position() {
        return position;
    }
    
    public Double massOffset() {
        return massOffset;
    }
    
    public boolean fixed() {
        return fixed;
    }
    
    public boolean labeling() {
        return labeling;
    }
    
}

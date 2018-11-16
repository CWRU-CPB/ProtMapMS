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

import java.util.List;

/**
 * Encapsulates the data related to an MS2 (MS/MS) scan. Namely, the ion m/z
 * values and intensities. Includes a "label" parameter that could be used to
 * associate informative labels on each ion, but currently unused.
 * 
 * @author Sean Maxwell
 */
public class MSMSIons {
    public List<Double> mz;
    public List<Double> intensity;
    public List<String> label;
    
    public MSMSIons(List<Double> d1, List<Double> d2, List<String> s) {
        mz = d1;
        intensity = d2;
        label = s;
    }
    
    public String toJSON() {
        boolean first;
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"mz\":[");
        
        first = true;
        for(int i=0;i<mz.size();i++) {
            if(intensity.get(i).compareTo(0.0) == 0) continue;
            if(!first) sb.append(",");
            else first = false;
            
            sb.append(String.format("%.1f",mz.get(i)));
        }
        sb.append("],\"I\":[");
        first = true;
        for(int i=0;i<mz.size();i++) {
            if(intensity.get(i).compareTo(0.0) == 0) continue;
            
            if(!first) sb.append(",");
            else first = false;
            
            sb.append(intensity.get(i).longValue());
        }
        sb.append("],\"label\":[");
        first = true;
        for(String s : label) {
            if(!first) sb.append(",");
            else first = false;
            
            sb.append("\"");
            sb.append(s);
            sb.append("\"");
        }
        sb.append("]}");
        return sb.toString();
    }
}

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

import java.io.FileReader;
import java.io.BufferedReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load modifications from a file into memory.
 * 
 * @author Sean Maxwell
 */
public class ModificationTableLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModificationTableLoader.class);
    
    public static Modifications load(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        Modifications modifications = new Modifications();
        while((line=br.readLine()) != null) {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#")) continue;
            
            String[] tokens = line.split(",");
            if(tokens.length != 4) {
                LOGGER.error("Modification table line {} has length < 4\n",line);
                continue;
            }
            
            String indexOrResidue = tokens[0].trim();
            Double massOffset = Double.parseDouble(tokens[1].trim());
            Boolean labeling = Boolean.parseBoolean(tokens[2].trim());
            Boolean fixed = Boolean.parseBoolean(tokens[3].trim());
            
            Modification modification;
            if(indexOrResidue.matches("[0-9]+")) {
                Integer index = Integer.parseInt(indexOrResidue);
                modification = new Modification(index,"",massOffset);
            }
            else {
                modification = new Modification(indexOrResidue,"",massOffset);
            }
            LOGGER.info("Loading modification {}+{} fixed={},labeling={}",indexOrResidue,massOffset,fixed,labeling);
            
            modification.setFixed(fixed);
            modification.setLabeling(labeling);
            modifications.addModification(modification);
        }
        br.close();
        return modifications;
    }
}

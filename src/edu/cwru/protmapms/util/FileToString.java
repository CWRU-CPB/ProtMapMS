/*

 Copyright (C) Case Western Reserve University, 2014. All rights reserved. 
 This source code and documentation constitute proprietary information
 belonging to Case Western Reserve University. None of the foregoing
 material may be copied, duplicated or disclosed without the express
 written permission of Case Western Reserve University.

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
package edu.cwru.protmapms.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author sean-m
 */
public class FileToString {
    public static String read(String path) throws Exception {
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }
        catch(Exception e) {
            throw e;
        }
    }
}

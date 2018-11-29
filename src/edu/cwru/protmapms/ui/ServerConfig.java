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
package edu.cwru.protmapms.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

/**
 *
 * @author sean-m
 */
public class ServerConfig extends HashMap<String,String> {
    
    public static ServerConfig loadServerConfig(String path) {
        ServerConfig sc = new ServerConfig();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while((line=br.readLine()) != null) {
                String[] tokens = line.split("=");
                sc.put(tokens[0].trim(), tokens[1].trim());
            }
            return sc;
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public static void saveServerConfig(ServerConfig sc, String path) throws Exception {
        try (FileWriter fw = new FileWriter(path)) {
            for(String paramName : sc.keySet()) {
                fw.write(String.format("%s=%s\n",paramName,sc.get(paramName)));
            }
            fw.close();
        }
    }
    
    public String getLocation() {
        return this.get("location");
    }
    
    public String getPin() {
        return this.get("pin");
    }
    
    public ServerConfig setLocation(String s) {
        this.put("location",s);
        return this;
    }
    
    public ServerConfig setPin(String s) {
        this.put("pin",s);
        return this;
    }
}

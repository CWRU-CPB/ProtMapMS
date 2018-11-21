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
package edu.cwru.protmapms.result;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.protmapms.ComparableRetentionTime;
import edu.cwru.protmapms.Identification;
import edu.cwru.protmapms.Peptide;
import edu.cwru.protmapms.RetentionTimes;
import edu.cwru.protmapms.RetentionTimeDatabase;
import edu.cwru.protmapms.modifications.Modification;
import edu.cwru.protmapms.modifications.ModificationSite;

/**
 *
 * @author sean-m
 */
public class FootprintingResultIT {
    FootprintingResult fp;
    String accession = "A";
    String refPepSeq = "REFPEPTIDE";
    String ulPeptideSeq = "PEPTIDEA";
    
    public FootprintingResultIT() {
    }
    
    @Before
    public void setUp() throws Exception {
        fp = new FootprintingResult();
        ProteinResult pro = new ProteinResult();
        
        /* Add a reference identification. It appears in both spectra, so it 
         * should be selected as reference */
        Peptide refPeptide = new Peptide(refPepSeq,1);
        PeptideResult refPepRes = new PeptideResult(refPeptide);
        SpectrumResult sp0 = new SpectrumResult();
        SpectrumResult sp1 = new SpectrumResult();
        Identification ref1 = new Identification(10.0,1,0.5).
                setCharge(2).
                setPrecursorMz(100.0).
                setPrecursorIntensity(1000.0).
                setExposureTime(0.0);
        Identification ref2 = new Identification(12.0,2,0.4).
                setCharge(2).
                setPrecursorMz(100.0).
                setPrecursorIntensity(1000.0).
                setExposureTime(1.0);
        sp0.addIdentification(ref1);
        sp1.addIdentification(ref2);
        refPepRes.put("0", sp0);
        refPepRes.put("1", sp1);
        pro.put(refPeptide.sequence,refPepRes);
        
        /* Add non-reference unlabaled identifications for interpolation */
        Peptide nonRefPeptide = new Peptide(this.ulPeptideSeq,1);
        PeptideResult nonRefPepRes = new PeptideResult(nonRefPeptide);
        Identification ul1 = new Identification(20.0,10,0.3).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(500.0).
                setExposureTime(1.0);
        Identification ul2 = new Identification(25.0,20,0.45).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(510.0).
                setExposureTime(0.0);
        Identification ul3 = new Identification(35.0,40,0.51).
                setCharge(2).
                setPrecursorMz(150.0).
                setPrecursorIntensity(505.0).
                setExposureTime(0.0);
        
        /* Add labeled identifications for interpolation */
        Modification cMod = new Modification("E","",10.0).setLabeling(true);
        ModificationSite cModSite = new ModificationSite(cMod,2);
        List<ModificationSite> modSites = new ArrayList<>();
        modSites.add(cModSite);
        Identification l1 = new Identification(23.0,15,0.3).
                setCharge(2).
                setPrecursorMz(155.0).
                setModifications(modSites).
                setPrecursorIntensity(200.0).
                setExposureTime(0.0);
        Identification l2 = new Identification(26.0,25,0.45).
                setCharge(2).
                setPrecursorMz(155.0).
                setModifications(modSites).
                setPrecursorIntensity(200.0).
                setExposureTime(1.0);
        Identification l3 = new Identification(38.0,45,0.51).
                setCharge(2).
                setPrecursorMz(155.0).
                setModifications(modSites).
                setPrecursorIntensity(200.0).
                setExposureTime(1.0);
        
        SpectrumResult psp0 = new SpectrumResult();
        SpectrumResult psp1 = new SpectrumResult();
        psp0.addIdentification(ul1);
        psp0.addIdentification(ul2);
        psp0.addIdentification(ul3);
        psp1.addIdentification(l1);
        psp1.addIdentification(l2);
        psp1.addIdentification(l3);
        
        nonRefPepRes.put("0.0000", psp0);
        nonRefPepRes.put("1.0000", psp1);
        pro.put(nonRefPeptide.sequence,nonRefPepRes);
        
        fp.put("A", pro);
    }

    @Test
    public void testGetReferenceRetentionTimeIntervals() {
        System.out.println("test getReferenceRetentionTimeIntervals()");
        RetentionTimes rts = fp.getReferenceRetentionTimeIntervals();
        List<ComparableRetentionTime> crts;
        
        crts = rts.getRetentionTimes("0.0000", "197.984350");
        assertEquals(1,crts.size());
        assertEquals(10.0,crts.get(0).getRetentionTime(),0.001);
        
        crts = rts.getRetentionTimes("1.0000", "197.984350");
        assertEquals(1,crts.size());
        assertEquals(12.0,crts.get(0).getRetentionTime(),0.001);
    }

    @Test
    public void testGetRetentionTimeDatabase() throws Exception {
        System.out.println("test getRetentionTimeDatabase()");
        RetentionTimes rts = fp.getReferenceRetentionTimeIntervals();
        RetentionTimeDatabase rtdb = fp.getRetentionTimeDatabase(rts);
        List<ComparableRetentionTime> crts;
        
        rtdb.print();
        
        crts = rtdb.getRetentionTimes(ulPeptideSeq, "0.0000", "150.0000");
        crts.sort(null);
        assertEquals(3,crts.size());
        assertEquals(18.0,crts.get(0).getRetentionTime(),0.0001);
        assertEquals(25.0,crts.get(1).getRetentionTime(),0.0001);
        assertEquals(35.0,crts.get(2).getRetentionTime(),0.0001);
        
        crts = rtdb.getRetentionTimes(ulPeptideSeq, "1.0000", "150.0000");
        crts.sort(null);
        assertEquals(3,crts.size());
        assertEquals(20.0,crts.get(0).getRetentionTime(),0.0001);
        assertEquals(27.0,crts.get(1).getRetentionTime(),0.0001);
        assertEquals(37.0,crts.get(2).getRetentionTime(),0.0001);
        
        crts = rtdb.getRetentionTimes(ulPeptideSeq, "0.0000", "155.0000");
        crts.sort(null);
        assertEquals(3,crts.size());
        assertEquals(23.0,crts.get(0).getRetentionTime(),0.0001);
        assertEquals(24.0,crts.get(1).getRetentionTime(),0.0001);
        assertEquals(36.0,crts.get(2).getRetentionTime(),0.0001);
        
        crts = rtdb.getRetentionTimes(ulPeptideSeq, "1.0000", "155.0000");
        crts.sort(null);
        assertEquals(3,crts.size());
        assertEquals(25.0,crts.get(0).getRetentionTime(),0.0001);
        assertEquals(26.0,crts.get(1).getRetentionTime(),0.0001);
        assertEquals(38.0,crts.get(2).getRetentionTime(),0.0001);
    }
    
}

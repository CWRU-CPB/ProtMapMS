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
package edu.cwru.protmapms.spectra;

// Java...
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

// Org.XML.SAX...
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

// org.apache...
import org.apache.commons.codec.binary.Base64;

// org.slf4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for directly accessing scan and peak information in an mzXML file
 * 
 * @author Sean Maxwell
 *
 */
public class mzXMLInterface extends DefaultHandler implements SpectrumFile {
    private static final Logger logger = LoggerFactory.getLogger(mzXMLInterface.class);
    
    /* Members used for processing and control */
    private String buffer = "";
    private String file;
    private Base64 b64 = new Base64();
    private RandomAccessFile raf;
    private boolean connected;
    private boolean hasIndex;

    /* Members that hold persistent data  */
    private int      currentscan;
    private int      scancount;
    private int      parsedscans;
    private int      firstscan;
    private int      centroid;
    private Scan[]   scans;
    private HashMap<Integer,Integer> scanMap;

    /* The state integer is used for tracking what type of data should be
     * parsed from the file next.
     *
     * 0 = None. Looking for scan.
     * 1 = Scan information.
     * 2 = Peak information.
     * 3 = Precursor MZ information.
     * 4 = Index information (scan offsets)
     *
     */
    private Integer state = 0;

    /**
     * Constructor
     */
    public mzXMLInterface() {
        super();
        file        = null;
        scancount   = 0;
        parsedscans = -1;
        currentscan = -1;
        firstscan   = -1;
        centroid    = -1;
        connected   = false;
        hasIndex    = false;
        scanMap     = new HashMap<>();
    }

    /**
     * Increases the length of the argument array by 1000 units. Creates a new
     * array of appropriate length, copies over existing data and returns the
     * new array.
     *
     * @param tokens The array of integers to extend.
     *
     * @return A new array that is 100 units longer than the argument
     *
     */
    private int[] ExtendIntArray(int[] tokens) {
        int i;
        int[] larger = new int[tokens.length+100];

        for(i=0;i<tokens.length;i++) {
            larger[i] = tokens[i];
        }

        return larger;
    }
    
    /**
     * Trim Scan array to the argument length
     *
     * @param array Array to trim
     * @param length New array length
     *
     * @return Trimmed array
     */
    private Scan[] TrimScanArray(Scan[] array, int length) {
        Scan[] ia = new Scan[length];
        int i;

        for(i=0;i<length;i++) {
            ia[i] = array[i];
        }

        return ia;
    }
    
    /**
     * Increases the length of the argument array by EXTEND units. Creates a new
     * array of appropriate length, copies over existing data and returns the
     * new array.
     *
     * @param tokens The array of Scans to extend.
     *
     * @return A new array that is EXTEND units longer than the argument
     *
     */
    private Scan[] ExtendScanArray(Scan[] tokens) {
        int i;
        Scan[] larger = new Scan[tokens.length+100];

        for(i=0;i<tokens.length;i++) {
            larger[i] = tokens[i];
        }

        return larger;
    }

    /**
     * Overrides the XMLReader DefaultHandler SAX event for the start of a
     * document.
     *
     */
    @Override
    public void startDocument() {
        // No need to do anything
    }

    /**
     * Overrides the XMLReader DefaultHandler SAX event for the end of a
     * document.
     *
     */
    @Override
    public void endDocument() {
        // No need to do anything
    }

    /**
     * Overrides the XMLReader DefaultHandler SAX event for the start of an
     * element. It explicitly processes four distinct elements, and performs
     * element specific actions for each.
     *
     * <pre>
     * msRun : Extracts the number of scans in the file (scanCount attribute),
     *         and allocates the scan array of required size
     *
     * scan  : Extracts many parameters and stores in Scan object
     *
     * precursorMz : Extracts the precursor ion intensity (precursorIntensity
     *               attribute) and stores in Scan object.
     *
     * peaks : Extracts the peak precision (precision attribute) and stores in
     *         San object.
     *
     * index : Extracts index name attribute and checks for "scan" index.
     *
     * offset : Extracts offset id attribute and uses as current scan number.
     *
     * </pre>
     *
     * @param namespaceURI The name space for the element.
     * @param localName The short name for the element (the name that appears
     *                    in the tag)
     * @param qName ?
     * @param atts An Attributes structure that encapsulates the parsed
     *             attributes for this element.
     *
     */
    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts) throws SAXException {

        Integer i;
        String attname;
        String attvalue;
        long scanbytes;

        /* If this is an mzRun element, get the number of scans it contains */
        if(localName.equals("msRun")) {
            // Store the parameters of interest
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("scanCount")) {
                    this.scancount = new Integer(atts.getValue(i));
                }
            }

            /* Initialize the scan array to hold the scans in this file */
            this.scans = new Scan[this.scancount];
            
            /* Set/Re-set first scan to -1 so that the first scan element will
             * be used as the offset (file starts at a scan higher than 1) */
            this.firstscan = -1;

        }
        
        /* if this is the start of a dataProcessing element, get gloal centroid
         * flag */
        if(localName.equals("dataProcessing")) {
            /* Get scan number attribute to allocate new scan */
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("centroided")) {
                    if(atts.getValue(i).equals("true")) {
                        //this.scans[this.parsedscans].centroid = 1;
                        this.centroid = 1;
                    }
                    else if(atts.getValue(i).equals("false")) {
                        //this.scans[this.parsedscans].centroid = 0;
                        this.centroid = 0;
                    }
                    else {
                        try {
                            //this.scans[this.parsedscans].centroid = new Integer(atts.getValue(i));
                            this.centroid = new Integer(atts.getValue(i));
                        }
                        catch(NumberFormatException e) {
                            logger.error("Spectrum {} has an invalid global centroid attribute value \"{}\"",file,atts.getValue(i));
                        }
                    }
                }
            }
        }

        /* if this is the start of a scan element, get scan number and ms level.
         */
        if(localName.equals("scan")) {
            this.state = 1;
            int scanNum = -1;

            /* Get scan number attribute to allocate new scan */
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("num")) {
                    scanNum = new Integer(atts.getValue(i));
                }
            }

            /* Make sure we found the scan number */
            if(scanNum == -1) {
                logger.error("A scan element has no ID number.");
                return;
            }
            
            /* If this is the first scan, store it for use as offset */
            if(this.firstscan == -1) {
                this.firstscan = scanNum;
            }
            
            /* Increment global parsed scan count to the next values where
             * we will store this scan */
            this.parsedscans++;
                        
            /* Make sure we have enough room in the scan array (if the scanCount
             * attribute value is wrong */
            if(this.parsedscans == this.scans.length) {
                this.scans = ExtendScanArray(scans);
                logger.warn("Extended scan storage. scanCount attribute value does not match file structure!");
            }

            /* Store all the attributes */
            this.scans[this.parsedscans] = new Scan();
            this.scans[this.parsedscans].ScanNum = scanNum;
            this.scans[this.parsedscans].centroid = -1;
            
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                
                if(attname.equals("msLevel")) {
                    this.scans[this.parsedscans].MSLevel = new Byte(atts.getValue(i));
                }
                else if(attname.equals("peaksCount")) {
                    this.scans[this.parsedscans].PeaksCount = new Integer(atts.getValue(i));
                }
                else if(attname.equals("lowMz")) {
                    this.scans[this.parsedscans].LowMZ = new Double(atts.getValue(i));
                }
                else if(attname.equals("highMz")) {
                    this.scans[this.parsedscans].HighMZ = new Double(atts.getValue(i));
                }
                else if(attname.equals("basePeakMz")) {
                    this.scans[this.parsedscans].BasePeakMZ = new Double(atts.getValue(i));
                }
                else if(attname.equals("basePeakIntensity")) {
                    this.scans[this.parsedscans].BasePeakIntensity = new Double(atts.getValue(i));
                }
                else if(attname.equals("totIonCurrent")) {
                    this.scans[this.parsedscans].TotalIonCurrent = new Double(atts.getValue(i));
                }
                else if(attname.equals("retentionTime")) {
                    attvalue = atts.getValue(i);
                    this.scans[this.parsedscans].RetentionTime = new Double(attvalue.substring(2,attvalue.length()-1));
                }
                else if(attname.equals("centroided")) {
                    if(atts.getValue(i).equals("true")) {
                        this.scans[this.parsedscans].centroid = 1;
                    }
                    else if(atts.getValue(i).equals("false")) {
                        this.scans[this.parsedscans].centroid = 0;
                    }
                    else {
                        try {
                            this.scans[this.parsedscans].centroid = new Integer(atts.getValue(i));
                        }
                        catch(NumberFormatException e) {
                            logger.error("Scan {} of spectrum {} has an invalid centroid attribute value \"{}\"",scanNum,file,atts.getValue(i));
                        }
                    }
                }
            }
            
            /* If no scan specififc centroid flag, apply the spectrum centroid 
             * flag to this scan */
            if(this.scans[this.parsedscans].centroid == -1) {
                this.scans[this.parsedscans].centroid = this.centroid;
            }
            
            /* Add the scan number to the scanMap */
            scanMap.put(scanNum,this.parsedscans);
            
        }

        /* If it is a precursorMz element, get the intensity from the element
         * and set the state to 3 for reading the mz value from the child
         * text node.
         */
        else if(localName.equals("precursorMz")) {
            this.state = 3;

            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("precursorIntensity")) {
                    this.scans[this.parsedscans].PrecursorInt = new Double(atts.getValue(i));
                }
            }

        }

        /* If this is a peak element, get the precision from the element and
         * set the reading state to 2 (for reading peak data).
         */
        else if(localName.equals("peaks")) {
            this.state = 2;

            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("precision")) {
                    this.scans[this.parsedscans].Precision = new Byte(atts.getValue(i));

                    /* Compute the number of bytes required to represent the
                     * the number of peaks in scan at the specified precision
                     */
                    scanbytes = ((this.scans[this.parsedscans].Precision*2)/8)*
                                this.scans[this.parsedscans].PeaksCount;

                    /* Following calculation taken from:
                     * http://en.wikipedia.org/wiki/Base64#Padding
                     * calculates how many bytes are required to represent the
                     * peak data after base64 encoding
                     */
                    this.scans[this.parsedscans].ScanLength =
                            (scanbytes + 2 - ((scanbytes + 2) % 3)) / 3 * 4;
                }
            }
        }

        /* If this is an index element, get the index name from the element and
         * set the reading state to 4 if it is a scan index).
         */
        else if(localName.equals("index")) {
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("name")) {
                    if(atts.getValue(i).equals("scan")) {
                        this.state = 4;
                        hasIndex = true;
                    }
                }
            }
        }

        /* If this is an offset element, and we are in reading state 4, use the
         * id attribute to set the current scan number
         */
        else if(localName.equals("offset") && this.state == 4) {
            int scanNum = -1;
            
            for(i=0;i<atts.getLength();i++) {
                attname = atts.getLocalName(i);
                if(attname.equals("id")) {
                    scanNum = new Integer(atts.getValue(i));
                }
            }
            
            /* Check if the offset element is missing an id */
            if(scanNum == -1) {
                logger.error("Index offset element is missing id so we cannot correlate it to any scans");
                return;
            }
            
            /* Check if we have a first scan defined (the first scan in the 
             * sequence */
            if(this.firstscan == -1) {
                this.firstscan = scanNum;
            }
            
            /* Set current scan based on availabel information */
            this.currentscan = mapScan(scanNum);
            
        }

    }

    /**
     * Overrides the XMLReader DefaultHandler SAX event for the end of an
     * element. It explicitly processes three distinct elements, and performs
     * element specific actions for each.
     *
     * <pre>
     *
     * scan  : Reset reading state to 0
     *
     * precursorMz : Extract MZ value from character buffer and set reading
     *               state to 1
     *
     * peaks : Empty the buffer and set reading state to 1
     *
     * index : Set reading state to 1
     *
     * offset: Get offset value from character buffer, empty buffer.
     *
     * </pre>
     *
     * @param namespaceURI The name space for the element.
     * @param localName The short name for the element (the name that appears in
     *                  the tag)
     * @param qName ?
     *
     */
    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {


        /* If closing a scan, fill in the header information */
        if(localName.equals("scan")) {
            /* Set the state to 0, because we are looking for the next scan */
            this.state = 0;
        }

        /* If closing a precursor mz, create the value from the content in the
         * text buffer and set the state to 1 because we are looking for the
         * peak data in this scan.
         */
        else if(localName.equals("precursorMz")) {
            this.scans[this.parsedscans].PrecursorMZ = new Double(buffer);
            this.buffer = "";
            this.state = 1;
        }

        /* If closing peaks, we need to convert the base64 encoded data in the
         * text buffer to the correct floating point data type (float or double)
         */
        else if(localName.equals("peaks")) {
            /* Empty the buffer, set the state to 1 because we are looking for
             * the next peak element.
             */
            this.state     = 1;
            this.buffer    = "";
        }

         /* If closing index, set reading state back to 1
         */
        else if(localName.equals("index")) {
            this.state     = 1;
        }

        /* If closing offset, store the peak data offset in the header
         */
        else if(localName.equals("offset")) {
            /* Empty the buffer, set the state to 1 because we are looking for
             * the next peak element.
             */
            if(this.currentscan >= 0 && 
               this.currentscan < this.scans.length &&
               this.scans[this.currentscan] != null) {
                this.scans[this.currentscan].ScanPos = new Long(this.buffer.trim());
            }
            else {
                logger.error("Dropping scan {} in index, because there is no corresponding scan in the msRun data",this.currentscan);
            }

            this.currentscan = -1;
            this.buffer    = "";
        }

        /* When closing the mzXML element, truncate the scan list to the number
         * of scans actually parsed. This corrects issues where the mzXML has
         * and incorrect scanCount attribute value */
        else if(localName.equals("mzXML")) {
            /* Trim scans to actual length */
            if(scancount != parsedscans+1) {
                logger.warn("Truncating scan list from {} to {}",scancount,parsedscans+1);
                this.scans = TrimScanArray(scans, parsedscans+1);
                this.scancount = this.parsedscans+1;
            }
           
        }

    }

    /**
     * Overrides the XMLReader DefaultHandler SAX event for character data.
     *
     * @param ch Array of chars that were parsed
     * @param start At what position in ch[] the character data begins
     * @param length At what position in ch[] the character data ends
     *
     */
    @Override
    public void characters(char[] ch,
                           int start,
                           int length) throws SAXException {

        int i;
        char[] chars;

        /* If we are not processing an element with known text content, we can
         * ignore this event and return early. */
        if(this.state != 3 && this.state != 4) {
            return;
        }

        /* initialize buffer to length of character data */
        chars = new char[length];

        /* Copy the chars from their position in the buffer, to the temporary
         * array */
        for(i=start;i<length+start;i++) {
            chars[i-start] = ch[i];
        }

        /* Add these characters to the text buffer. We don't process any text
         * until an endElement event occurs, because the text is read by the
         * SAX parser in chunks, and we need to concatenate it all together
         * before processing it. */
        buffer += new String(chars);

    }

    /**
     * Opens an mzXML file and reads the scan information into memory.
     *
     * @param mzxml - The path to the input mzXML file.
     *
     * @return true for success and false for failure.
     * 
     * @throws Exception if the request cannot be fulfilled
     * 
     */
    @Override
    public boolean connect(String mzxml) throws Exception {
        XMLReader xr;
        FileInputStream fr;
        InputStreamReader r;

        /* If the object is being reused, and the user did not correctly 
         * disconnect from the last Spectrum before reusing it, there are 
         * persistent members that can interfere with parsing the new file.
         * Reset those, prior to starting to load the new file */
        this.scans       = new Scan[0];
        this.scancount   = 0;
        this.parsedscans = -1;
        this.currentscan = -1;
        this.firstscan   = -1;
        this.connected   = false;
        this.hasIndex    = false;
        this.file        = mzxml;
        
        /* Setup XML parsing event handlers */
        xr = XMLReaderFactory.createXMLReader();
	xr.setContentHandler(this);
	xr.setErrorHandler(this);

        /* Create a new FileReader object for the XMLReader to read from */
        r = new InputStreamReader(new FileInputStream(mzxml),"ISO-8859-1");       

        /* Read the mzXML file and store the scan properties */
        xr.parse(new InputSource(r));

        /* Open a RandomAccessFile stream to the file, for reading scan data */
        this.raf  = new RandomAccessFile(mzxml,"r");
        
        /* If no index, we have to brute force build one */
        if(!this.hasIndex) {
            logger.warn("Spectrum {} has no index. Building one...",mzxml);
            if(!this.buildIndex()) {
                logger.error("Could not build index for spectrum file. The file cannot be used.");
                throw new Exception("The argument file "+mzxml+" does not cintain an index, and one could not be constructed");
            }
            else {
                logger.info("Successfully build index for spectrum file.");
            }
        }

        /* All OK, return true */
        this.connected = true;
        return true;
    }

    /**
     * disconnect from an mzXML file and clean up object for reuse.
     *
     * @return true for success and false if an error occurs
     * 
     * @throws Exception if the request cannot be fulfilled
     */
    @Override
    public boolean disconnect() throws Exception {
        /* Check to make sure object is connected to a JRAF file */
        if(!this.connected) {
            logger.warn("Call to disconnect when not connected");
            return false;
        }

        /* Close the random access file stream */
        this.raf.close();

        /* empty the scans array */
        this.scans = new Scan[0];

        /* Reset the other persistent members */
        this.scancount   = 0;
        this.parsedscans = -1;
        this.currentscan = -1;
        this.firstscan   = -1;
        this.connected   = false;
        this.hasIndex    = false;
        this.file        = null;
        
        return true;
    }
    
    public int mapScan(int s) {
        if(this.scanMap.containsKey(s)) {
            return this.scanMap.get(s);
        }
        else {
            return -1;
        }
    }
        
    private int parseInteger(String buf) {
        return new Integer(buf);
    }

    
    private long[] findNextScan() {
        String buf = "";
        byte b = 0;
        boolean con = true;
        int scan = -1;
        int numPeaks = -1;
        long position = 0;
        int parsing = 0;
        long seek = 0;
        long[] r = {-1,-1,-1};

        /* First seek to the next scan */
        while(con) {
            try {
                b = this.raf.readByte();
            }
            
            /* End of file reached, no more scans */
            catch(java.io.EOFException e) {
                return r;
            }
            
            /* Other exception */
            catch(Exception e) {
                logger.error("Error searching for next scan element: "+e.getMessage());
                logger.error(e.toString());
                return r;
            }

            buf += (char)b;
            if(b == '<') {
                buf = "";
            }
            else if(buf.equals("scan ")) {
                con = false;
                try {
                    position = raf.getFilePointer();
                }
                catch(Exception e) {
                    logger.error("Could not store file pointer position for scan start");
                    logger.error(e.toString());
                }
            }
           
        }

        /* Parse scan attribute values */
        con = true;
        buf = "";
        while(con) {
            try {
                b = this.raf.readByte();
            }
            catch(Exception e) {
                logger.error("Error searching for num attribute: "+e.getMessage());
                logger.error(e.toString());
                return r;
            }
            
            /* Space delimits attributes, so reset buffer */
            if(b == ' ') {
                buf = "";
            }
            
            /* End of element, so no attributes left to parse */
            else if(b == '>') {
                con = false;
            }
            
            /* double quote delimits attribute values. If we are parsing an
             * attribute value, handle conversion of value to int. Otherwise,
             * add the double quote to the buffer for attribute name comparison
             */
            else if(b == '"') {
                if(parsing == 1) {
                    scan = parseInteger(buf);
                    buf = "";
                    parsing = 0;
                }
                else if(parsing == 2) {
                    numPeaks = parseInteger(buf);
                    buf = "";
                    parsing = 0;
                }
                else {
                    buf += (char)b;
                }
            }
            
            /* If no other special case matches, add the character to the 
             * buffer */
            else {
                buf += (char)b;
            }
            
            /* Check for attribute value of interest to parse */
            if(buf.equals("num=\"")) {
                buf = "";
                parsing = 1;
            }
            else if(buf.equals("peaksCount=\"")) {
                buf = "";
                parsing = 2;
            }
           
        }
        
        /* Map the scan to array position */
        scan = mapScan(scan);
        
        /* Try to skip as far forward as possible. This isn't perfect, but it
         * skip over a lot of the peak data so we don't spend a lot of time 
         * scanning it one byte at a time */
        try {
            seek = this.raf.getFilePointer()+(this.scans[scan].Precision/8)*numPeaks*2;
            raf.seek(seek);
        }
        catch(Exception e) {
            logger.error("Error trying to fast-forward after scan");
            logger.error(e.toString());
            return r;
        }
        
        /* Polulate first scan if index is at begining of file */
        if(this.firstscan == -1) {
            this.firstscan = scan;
        }
                
        /* Package values for return */
        r[0] = scan;
        r[1] = numPeaks;
        r[2] = position;
        
        return r;
    }
    
    private boolean buildIndex() {
        long[] scan;
        long last = -1;
        
        while((scan = findNextScan())[0] != -1) {
            try {                
                /* Update the scan position for the specified scan number */
                this.scans[(int)scan[0]].ScanPos = scan[2];
                if(last != -1) {
                    if(scan[0] - last > 1) {
                        logger.info("Discontinuity between scans {} and {}",last,scan[0]);
                    }
                }
                last = scan[0];
            }
            catch(Exception e) {
                logger.error("Error storing file pointer position for scan index"+e.getMessage());
                logger.error(e.toString());
                return false;
            }
        }
        
        return true;
    }
        
    @Override
    public String file() {
        return file;
    }
    
    /**
     * Returns the Scan object for the requested scan
     * number s.
     *
     * @param scanNumber scan number
     *
     * @return Scan object on success, null on failure.
     *
     * @throws Exception if the request cannot be fulfilled
     */
    @Override
    public Scan getScanProperties(int scanNumber) throws Exception {
        /* Check to make sure object is connected to a JRAF file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }
        
        if(!this.scanMap.containsKey(scanNumber)) {
            logger.error("Requested scan number {} is not present in the spectrum file",scanNumber);
            throw new Exception("Invalid scan number requested");
        }
        
        /* Map scan to array index and return scan object */
        int s = this.scanMap.get(scanNumber);
        return this.scans[s];        
    }

    /**
     * The scan positions are only approximate in the mzXML file (they are to
     * the start of the scan element, not to the peak data), so this function
     * seeks forward in the file until it find the start of the peak data.
     *
     * @return true if the start of peak data was found, false if error occurs
     */
    private boolean seekToPeaks() {
        String buf = "";
        byte b = 0;
        boolean con = true;

        while(con) {
            try {
                b = this.raf.readByte();
            }
            catch(Exception e) {
                return false;
            }

            buf += (char)b;
            if(b == '<') {
                buf = "";
            }
            else if(buf.equals("peaks ")) {
                con = false;
            }
           
        }

        while(b != 62) {
            try {
                b = this.raf.readByte();
            }
            catch(Exception e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a Peak object that contains the MZ and Intensity
     * information for the argument scan number s
     *
     * @param s scan number to load
     *
     * @return Peak object for success, and null for failure.
     * 
     * @throws Exception if the request cannot be fulfilled
     */
    @Override
    public Peaks getScanPeaks(int s) throws Exception {
        Peaks p;
        byte[] b;
        byte[] decode;
        int i;
        long lmask;
        int imask;
        double dvalue;
        float fvalue;
        int peakn = 0;
        boolean mz = true;

        /* Check to make sure object is connected to a JRAF file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }
        
        /* Validate scan number requested */
        if(!this.scanMap.containsKey(s)) {
            logger.error("Requested scan number {} is not present in the spectrum file",s);
            throw new Exception("Invalid scan number requested");
        }
        
        /* Map scan to array index */
        s = this.scanMap.get(s);
        
        /* allocate a peaks structure of adequate length */
        p = new Peaks(this.scans[s].PeaksCount);
        b = new byte[(int)this.scans[s].ScanLength];
        
        /* This just gets the pointer close. It is the offset of the scan
         * element start in the document */
        if(this.scans[s].ScanPos != -1) {
            this.raf.seek(this.scans[s].ScanPos);
        }
        else {
            logger.error("Scan {} is missing data for offset. This is a very strange event. Check the spectrum file manually for errors.",scans[s]);
            return null;
        }

        /* This finds the begining of the base64 content from the start of
         * the scan element */
        this.seekToPeaks();

        /* Read the peak data */
        this.raf.read(b);


        /* Decode the base 64 data to an array of bytes */
        decode = this.b64.decode(b);

        /* Precision was set as 32, so these are floats */
        if(this.scans[s].Precision == 32) {

            /* Check for correct data length. They are in pairs so we devide
             * by 8 (2 floats at 4 bytes a piece) */
            if(decode.length % 8 != 0) {
                throw new Exception("The length of 32bit peak data is "+
                               "incorrect. It is not divisible by"+
                               " 8. Length is "+decode.length+"\nDATA="+(new String(decode)));
            }

            /* The loop increments by 4 because even though the data is in
             * pairs, they are both floats, so we can process them one at a
             * time to make the code shorter.
             */
            for(i=0;i<decode.length;i+=4) {
                /* 1. Initialize the int mask to 0.
                 * 2. Convert the next byte to int (mask to width 255)
                 * 3. Shift the bits to the correct position, depending on
                 *    the byte being processed. They are in big endian order
                 *    in the file.
                 * 4. Repeat until 4 bytes have been processed.
                 * 5. Create a float from the int mask
                 */
                imask = 0;
                imask += (((int)decode[i]   & 0xFF) << 24);
                imask += (((int)decode[i+1] & 0xFF) << 16);
                imask += (((int)decode[i+2] & 0xFF) << 8);
                imask += (((int)decode[i+3] & 0xFF));
                fvalue = java.lang.Float.intBitsToFloat(imask);

                /* Store the value */
                if(mz) {
                    p.MZ[peakn] = (double)fvalue;
                    mz = false;
                }
                else {
                    p.Intensity[peakn] = (double)fvalue;
                    mz = true;
                    peakn++;
                }
            }
        }

        /* Precision was set as 64, so these are doubles */
        else if(this.scans[s].Precision == 64) {

            /* Check for correct length of data. They are in pairs so devide
             * by 16 (2 doubles at 8 bytes a piece) */
            if(decode.length % 16 != 0) {
                throw new Exception("The length of 64bit peak data is "+
                             "incorrect. It is not divisible by"+
                             " 16. Length is "+decode.length);
            }

            /* The loop increments by 8 because even though the data is in
             * pairs, they are both doubles, so we can process them one at a
             * time to make the code shorter.
             */
            for(i=0;i<decode.length;i+=8) {
                /* 1. Initialize the long mask to 0.
                 * 2. Convert the next byte to int (mask to width 255)
                 * 3. Shift the bits to the correct position, depending on
                 *    the byte being processed. They are in big endian order
                 *    in the file.
                 * 4. Repeat until 8 bytes have been processed.
                 * 5. Create a double from the int mask
                 */
                lmask = 0;
                lmask += (((long)decode[i]   & 0xFF) << 56);
                lmask += (((long)decode[i+1] & 0xFF) << 48);
                lmask += (((long)decode[i+2] & 0xFF) << 40);
                lmask += (((long)decode[i+3] & 0xFF) << 32);
                lmask += (((long)decode[i+4] & 0xFF) << 24);
                lmask += (((long)decode[i+5] & 0xFF) << 16);
                lmask += (((long)decode[i+6] & 0xFF) << 8);
                lmask += (((long)decode[i+7] & 0xFF) );
                dvalue = java.lang.Double.longBitsToDouble(lmask);

                /* Store the value */
                if(mz) {
                    p.MZ[peakn] = dvalue;
                    mz = false;
                }
                else {
                    p.Intensity[peakn] = dvalue;
                    mz = true;
                    peakn++;
                }

            }

        }

        return p;

    }

    /**
     * Loads the requested scan number, and prints the scan information to the
     * requested output file.
     *
     * @param scan number to load
     * @param file output file path
     *
     * @return true on success, and false on failure. The failure message can be
     *         retrieved via GetLastError() when the method returns false.
     *
     * @see #getScanPeaks(int)
     */
    public boolean exportScan(int scan, String file) throws Exception {
        int i;
        FileOutputStream of;
        String s = "";
        Peaks p;
        
        /* Check to make sure object is connected to a JRAF file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }
        
        /* Validate scan number requested */
        if(!this.scanMap.containsKey(scan)) {
            logger.error("Requested scan number {} is not present in the spectrum file",scan);
            throw new Exception("Invalid scan number requested");
        }

        /* load the scan data into the internal members */
        p = this.getScanPeaks(scan);

        /* Map scan to array index */
        scan = this.scanMap.get(scan);
        
        /* Try to open a FileOutputStream to the requested header file */
        of = new FileOutputStream(file);

        /* Output parent file, blank line */
        of.write("# Parent File [Unused]\r\n# \r\n".getBytes());

        /* Output index */
        s = "# index: "+java.lang.String.valueOf(this.scans[scan].ScanNum-1)+"\r\n";
        of.write(s.getBytes());

        /* Output id */
        of.write("# id: [Unused]\r\n".getBytes());

        /* Output scan number */
        s = "# scanNumber: "+java.lang.String.valueOf(this.scans[scan].ScanNum)+"\r\n";
        of.write(s.getBytes());

        /* Output centroid */
        of.write(String.format("# centroid: %d\r\n",this.scans[scan].centroid).getBytes());

        /* Output mass analyzer */
        of.write("# massAnalyzerType: [Unused]\r\n".getBytes());

        /* Output scan event */
        of.write("# scanEvent: [Unused]\r\n".getBytes());

        /* Output MS Level */
        s = "# msLevel: "+java.lang.String.valueOf(this.scans[scan].MSLevel)+"\r\n";
        of.write(s.getBytes());

        /* Output Retention time */
        s = "# retentionTime: "+java.lang.String.valueOf(this.scans[scan].RetentionTime)+"\r\n";
        of.write(s.getBytes());

        /* Output filter string */
        of.write("# filterString: [Unused]\r\n".getBytes());

        /* Output Low MZ */
        s = String.format("# mzLow: %.3f\r\n",this.scans[scan].LowMZ);
        of.write(s.getBytes());

        /* Output High MZ */
        s = String.format("# mzHigh: %.3f\r\n",this.scans[scan].HighMZ);
        of.write(s.getBytes());

        /* Output base peak MZ */
        s = String.format("# basePeakMZ: %.3f\r\n",this.scans[scan].BasePeakMZ);
        of.write(s.getBytes());

        /* Output base peak intensity */
        s = "# basePeakIntensity: "+java.lang.String.valueOf(this.scans[scan].BasePeakIntensity)+"\r\n";
        of.write(s.getBytes());

        /* Output total ion current */
        s = "# totalIonCurrent: "+java.lang.String.valueOf(this.scans[scan].TotalIonCurrent)+"\r\n";
        of.write(s.getBytes());

        /* Output precursor count */
        of.write("# precursorCount: [Unused]\r\n".getBytes());

        /* Output the precursor ion information */
        if(this.scans[scan].MSLevel == 2) {
             s = String.format("# precursor 0: %f %f\r\n",
                               this.scans[scan].PrecursorMZ,
                               this.scans[scan].PrecursorInt);
            of.write(s.getBytes());
        }

        /* Output "binary(" + #peaks + ")" */
        s = "# binary ("+java.lang.String.valueOf(this.scans[scan].PeaksCount)+"):\r\n";
        of.write(s.getBytes());

        /* Output the peak list */
        for(i=0;i<this.scans[scan].PeaksCount;i++) {
            s = String.format("%12.4f\t%12.4f\r\n",
                              p.MZ[i],
                              p.Intensity[i]);
            of.write(s.getBytes());
        }

        /* Clean up the FileOutputStream, catch IOException */
        of.close();

        /* Return success */
        return true;
    }

    /**
     * Writes the header information in memory out to the requested header file
     * in a tab delimited format.
     *
     * @param header Output header file path
     *
     * @return true on success, and false on failure. The failure message can be
     *         retrieved via GetLastError() when the method returns false.
     *
     * @throws Exception if the request could not be fulfilled
     */
    public boolean writeHeader(String header) throws Exception {
        int i;
        FileOutputStream of;
        String s = "";
        byte[] bytes;

        /* Try to open a FileOutputStream to the requested header file */
        of = new FileOutputStream(header);
        
        /* Output the column names */
        of.write("#Scan	msLevel	retentionTime (sec)	BasePeakIntensity	totIonCurrent	precursorMz	# peaks	lowMz	highMz\r\n".getBytes());
        

        /* Output the header to the FileOutputStream */
        for(i=0;i<this.scancount;i++) {
            /* Build the line to output by concatenating the numerical data into
             * a String and then converting the string to a byte array */
            s=String.format("%d\t%d\t%.4f\t%.2f\t%.2f\t%.6f\t%d\t%.6f\t%.6f\r\n",
                            this.scans[i].ScanNum,
                            this.scans[i].MSLevel,
                            this.scans[i].RetentionTime,
                            this.scans[i].BasePeakIntensity,
                            this.scans[i].TotalIonCurrent,
                            this.scans[i].PrecursorMZ,
                            this.scans[i].PeaksCount,
                            this.scans[i].LowMZ,
                            this.scans[i].HighMZ);

            bytes = s.getBytes();

            /* Write the byte array to file, catch IOException */
            of.write(bytes);
        }

        /* Clean up the FileOutputStream, catch IOException */
        of.close();
        
        /* Return success */
        return true;
    }

    /**
     * Returns the number of scans currently loaded in the object.
     *
     * @return number of loaded scans
     * 
     * @throws Exception if the underlying request could no be fulfilled.
     *
     */
    @Override
    public int size() throws Exception {
        /* Check to make sure object is connected to a JRAF file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }

        return this.scancount;
    }

    /**
     * Searches the header information for scans with a precursor ion between
     * the argument maximum and minimum (inclusive).
     *
     * @param min The inclusive lowerbound for the precursor m/z value
     * @param max The inclusive upperbound for the precursor m/z value
     *
     * @return scan numbers of the matches
     * 
     * @throws Exception if the request could not be fulfilled.
     */
    @Override
    public int[] queryPrecursor(double min, double max) throws Exception {
        int i;
        int j = 0;
        int[] scratch = new int[100];
        int[] hits;

        /* Check to make sure object is connected to a spectrum file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }


        /* Find all the matching scans using a scaling container for storage */
        for(i=0;i<this.scancount;i++) {
            if(this.scans[i].MSLevel == 2) {
                if(this.scans[i].PrecursorMZ >= min && this.scans[i].PrecursorMZ <= max) {
                    scratch[j] = this.scans[i].ScanNum;
                    j++;
                    if(j == scratch.length) {
                        scratch = this.ExtendIntArray(scratch);
                    }
                }
            }
        }

        /* Copy the matching scans to an array of correct length */
        hits = new int[j];
        for(i=0;i<j;i++) {
            hits[i] = scratch[i];
        }

        return hits;
        
    }

    /**
     * Searches the header information for scans with a precursor ion between
     * the argument maximum and minimum (inclusive).
     *
     * @param min The inclusive lowerbound for the precursor m/z value
     * @param max The inclusive upperbound for the precursor m/z value
     * @param from The start retention time
     * @param to The end retention time
     *
     * @return scan numbers of the matches
     * 
     * @throws Exception if the request could not be fulfilled.
     */
    @Override
    public int[] queryPrecursor(double min, double max, double from, double to) throws Exception {
        int i;
        int j = 0;
        int[] scratch = new int[100];
        int[] hits;

        /* Check to make sure object is connected to a spectrum file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }

        /* Find all the matching scans using a scaling container for storage */
        for(i=0;i<this.scancount;i++) {

            if(this.scans[i].RetentionTime >= from &&
               this.scans[i].RetentionTime <= to) {
                if(this.scans[i].MSLevel == 2) {
                    if(this.scans[i].PrecursorMZ >= min &&
                       this.scans[i].PrecursorMZ <= max) {
                        scratch[j] = this.scans[i].ScanNum;
                        j++;
                        if(j == scratch.length) {
                            scratch = this.ExtendIntArray(scratch);
                        }
                    }
                }
            }
        }

        /* Copy the matching scans to an array of correct length */
        hits = new int[j];
        for(i=0;i<j;i++) {
            hits[i] = scratch[i];
        }

        return hits;

    }

    /**
     * Searches the header information for scans of the argument MS or MS/MS
     * level
     *
     * @param ms level (1 or 2)
     *
     * @return scan numbers of the matches
     * 
     * @throws Exception if the request cannot be fulfilled.
     */
    @Override
    public int[] queryMSLevel(int ms) throws Exception {
        int i;
        int j = 0;
        int[] scratch = new int[100];
        int[] hits;

        /* Check to make sure object is connected to a spectrum file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }

        /* Find all the matching scans using a scaling container for storage */
        for(i=0;i<this.scancount;i++) {
            if(this.scans[i].MSLevel == ms) {
                scratch[j] = this.scans[i].ScanNum;
                j++;
                if(j == scratch.length) {
                    scratch = this.ExtendIntArray(scratch);
                }
            }
        }

        /* Copy the matching scans to an array of correct length */
        hits= new int[j];

        for(i=0;i<j;i++) {
            hits[i] = scratch[i];
        }

        return hits;

    }

    /**
     * Searches the header information for scans that fall between the argument
     * start and end retention times (inclusive).
     *
     * @param start The inclusive start retention time
     * @param stop The inclusive stop retention time
     * @param ms The MS level to filter on. 0 means any.
     *
     * @return scan numbers of the matches
     * 
     * @throws Exception if the request cannot be fulfilled
     */
    @Override
    public int[] queryRetentionTime(double start, double stop, int ms) throws Exception {
        int i;
        int j = 0;
        int[] scratch = new int[100];
        int[] hits;

        /* Check to make sure object is connected to a spectrum file */
        if(!this.connected) {
            throw new Exception("There is no file connected");
        }

        /* Find all the matching scans using a scaling container for storage */
        for(i=0;i<this.scancount;i++) {
            if(ms == 0 || this.scans[i].MSLevel == ms) {
                if(this.scans[i].RetentionTime >= start &&
                   this.scans[i].RetentionTime <= stop) {
                    scratch[j] = this.scans[i].ScanNum;
                    j++;
                    if(j == scratch.length) {
                        scratch = this.ExtendIntArray(scratch);
                    }
                }
            }
        }

        /* Copy the matching scans to an array of correct length */
        hits = new int[j];
        for(i=0;i<j;i++) {
            hits[i] = scratch[i];
        }

        return hits;
 
    }
    
}

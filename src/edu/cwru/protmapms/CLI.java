/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cwru.protmapms;

import edu.cwru.protmapms.modifications.ModificationTableLoader;
import edu.cwru.protmapms.ui.GraphicalInterface;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author sean-m
 */
public class CLI { 
    public static void printUsage() {
        String usage = "USAGE: 	java -jar ProtMapMS.jar [OPTIONS]\n" +
"\n" +
"OPTIONS:\n" +
"	--gui\n" +
"		Specifies that the graphical user interface should be opened.\n" +
"		It must be the only argument.\n" +
"\n" +
"	--out-dir <path>\n" +
"		Specifies a path to a folder where results will be written. If\n" +
"		the does not exist, it will be created.\n" +
"\n" +
"	--spectrum \"<file>;<id>\" \n" +
"		Specify a spectrum file for processing where <file> is the full \n" +
"		path to the spectrum file and <id> is a decimal value that \n" +
"		identifies the spectrum (should be unique to each spectrum file\n" +
"		specified). The option can be passed multiple times, and files\n" +
"		are processed in the order they appear on the command line.\n" +
"\n" +
"	--fasta <file>\n" +
"		The full path to a FASTA format database of protein sequences.\n" +
"\n" +
"	--min-charge <int>\n" +
"		Only search for peptides with a charge greater than or equal\n" +
"		to the argument value. Default value is 2.\n" +
"\n" +
"	--max-charge <int>\n" +
"		Only search for peptides with a charge less than or equal to\n" +
"		the argument value. Default value is 4.\n" +
"\n" +
"	--max-missed-cleavages <int>\n" +
"	Only consider peptides with this less than or equal to this\n" +
"		many missed cleavages. Default value is 1.\n" +
"\n" +
"        --max-mods <int>\n" +
"		Only consider peptides with less than or equal to this many\n" +
"		concurrent modifications. Default value is 2.\n" +
"\n" +
"	--enzyme <name>\n" +
"		The name of the enzyme to use when theoretically digesting \n" +
"		proteins to peptides. Valid names are \"AspN\", \"AspN/N->D\", \n" +
"		\"Chymotrypsin\", \"GluC\", \"LysC\", \"Pepsin, pH=1.3\", \n" +
"		\"Pepsin, pH=2.0\", \"Trypsin\", \"Non-specific\". Default value\n" +
"		is Trypsin.\n" +
"\n" +
"	--min-mass <double>\n" +
"		Only consider peptides with monoisotopic mass greater than\n" +
"		or equal to this molecular weight. Default value is 500.0.\n" +
"\n" +
"        --max-mass <double>\n" +
"		Only consider peptides with monoisotopic mass less than or\n" +
"		equal to this molecular weight. Default value is 4000.0.\n" +
"\n" +
"        --from-retention-time <double>\n" +
"		Only search for peptides that elute at greater than or \n" +
"		equal to this time (specified in minutes). Default value\n" +
"		is 20.0.\n" +
"\n" +
"        --to-retention-time <double>\n" +
"		Only search for peptides that elute at less than or \n" +
"		equal to this time (specified in minutes). Default value\n" +
"		is 170.0.\n" +
"\n" +
"        --ms1-error-ppm <int>\n" +
"		When identifying candidate scans for MS2 confirmation,\n" +
"		this parameter specifies how far from the theoretical\n" +
"		precursor m/z value we will match an experimental precursor\n" +
"		m/z value. It is parts-per-million, so the distance above\n" +
"		and below the theoretical value is given by:\n" +
"\n" +
"		(precursor m/z) * PPM / 1000000\n" +
"\n" +
"		Default value is 10.\n" +
"\n" +
"	--ms2-error <double>\n" +
"		When matching ions from theoretical MS2 spectra to experimental\n" +
"		spectra, this value specifies an absolute difference in Daltons\n" +
"		within which ions will be considered as matching. Default value\n" +
"		is 0.25.";
        System.out.println(usage);
    }
    
    public static Integer getIntegerOption(String option, String value) {
        try {
            return Integer.parseInt(value);
        }
        catch(Exception e) {
            System.out.printf("Error parsing option %s. Value %s cannot be parsed as required integer\n",option,value);
            throw e;
        }
    }
    
    public static Double getDoubleOption(String option, String value) {
        try {
            return Double.parseDouble(value);
        }
        catch(Exception e) {
            System.out.printf("Error parsing option %s. Value %s cannot be parsed as required double\n",option,value);
            throw e;
        }
    }
    
    public static void main(String[] args) throws Exception {
        /*If no args, print usage */
        if(args.length == 0) {
            printUsage();
            return;
        }
        
        /* If starting the GUI, args[] should be single argument */
        if(args.length == 1 && args[0].equals("--gui")) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new GraphicalInterface().setVisible(true);
                }
            });
            return;
        }
        
        /* Using command line, so validate basic usage requirements */
        if(args.length < 2) {
            throw new Exception("Invalid command. Reference the documentation for usage examples.");
        }
        if(args.length % 2 != 0) {
            throw new Exception("Invalid command line. There are an odd number of options/values.");
        }
        
        
        String[] names = {"AspN", "AspN/N->D", "Chymotrypsin", "GluC", "LysC", "Pepsin, pH=1.3", "Pepsin, pH=2.0", "Trypsin", "Non-specific"};
        HashSet<String> enzymeNames = new HashSet<>();
        enzymeNames.addAll(Arrays.asList(names));
        
        /* Populate reasonable defaults in initial config */
        IdentificationFactoryConfig ifc = new IdentificationFactoryConfig();
        ifc.setChargeMin(2);
        ifc.setChargeMax(4);
        ifc.setRTMin(20.0);
        ifc.setRTMax(170.0);
        ifc.setMassMin(500.0);
        ifc.setMassMax(4000.0);
        ifc.setMS1ErrorPPM(10);
        ifc.setMS2ErrorDa(0.25);
        ifc.setMaxMissedCleavages(1);
        ifc.setMaxConcurrentModifications(2);
        ifc.setProteaseName("Trypsin");
        
        /* Populate configuration from command line options */
        for(int i=0;i<args.length;i=i+2) {
            String option = args[i];
            String value = args[i+1];
            
            switch(option) {
                case "--spectrum":
                    String[] parts = value.split(";");
                    if(parts.length < 2) {
                        throw new Exception("Spectrum file option value format is fileName.extension_id. Note the underscore after the file extension.");
                    }
                    ifc.addSpectrum(parts[0], CLI.getDoubleOption(option,parts[1]));
                    break;
                case "--fasta":
                    ifc.setFasta(new Fasta(value));
                    break;
                case "--mods-file":
                    ifc.setModifications(ModificationTableLoader.load(value));
                    break;
                case "--min-charge":
                    ifc.setChargeMin(CLI.getIntegerOption(option, value));
                    break;
                case "--max-charge":
                    ifc.setChargeMax(CLI.getIntegerOption(option, value));
                    break;
                case "--max-missed-cleavages":
                    ifc.setMaxMissedCleavages(CLI.getIntegerOption(option, value));
                    break;
                case "--max-mods":
                    ifc.setMaxConcurrentModifications(CLI.getIntegerOption(option, value));
                    break;
                case "--enzyme":
                    if(!enzymeNames.contains(value)) {
                        throw new Exception(String.format("Enzyme name \"%s\" is not valid. Must be one of %s", value, enzymeNames));
                    }
                    ifc.setProteaseName(value);
                    break;
                case "--min-mass":
                    ifc.setMassMin(CLI.getDoubleOption(option, value));
                    break;
                case "--max-mass":
                    ifc.setMassMax(CLI.getDoubleOption(option, value));
                    break;
                case "--from-retention-time":
                    ifc.setRTMin(CLI.getDoubleOption(option, value));
                    break;
                case "--to-retention-time":
                    ifc.setRTMax(CLI.getDoubleOption(option, value));
                    break;
                case "--ms1-error-ppm":
                    ifc.setMS1ErrorPPM(CLI.getIntegerOption(option, value));
                    break;
                case "--ms2-error":
                    ifc.setMS2ErrorDa(CLI.getDoubleOption(option, value));
                    break;
                case "--out-dir":
                    ifc.setOutputDirectory(value);
                    break;
                default:
                    throw new Exception("Invalid option "+option+" specified");
            }
        }
        
        IdentificationFactory factory = new IdentificationFactory();
        factory.configure(ifc);
        factory.identify();
    }
}

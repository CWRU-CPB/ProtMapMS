ProtMapMS is a search engine for protein footprinting experiments.

```
USAGE: 	java -jar ProtMapMS.jar [OPTIONS]

OPTIONS:
	--gui
		Specifies that the graphical user interface should be opened.
		It must be the only argument.

	--out-dir <path>
		Specifies a path to a folder where results will be written. If
		the does not exist, it will be created.

	--spectrum "<file>;<id>" 
		Specify a spectrum file for processing where <file> is the full 
		path to the spectrum file and <id> is a decimal value that 
		identifies the spectrum (should be unique to each spectrum file
		specified). The option can be passed multiple times, and files
		are processed in the order they appear on the command line.

	--fasta <file>
		The full path to a FASTA format database of protein sequences.

	--min-charge <int>
		Only search for peptides with a charge greater than or equal
		to the argument value. Default value is 2.

	--max-charge <int>
		Only search for peptides with a charge less than or equal to
		the argument value. Default value is 4.

	--max-missed-cleavages <int>
	Only consider peptides with this less than or equal to this
		many missed cleavages. Default value is 1.

        --max-mods <int>
		Only consider peptides with less than or equal to this many
		concurrent modifications. Default value is 2.

	--enzyme <name>
		The name of the enzyme to use when theoretically digesting 
		proteins to peptides. Valid names are "AspN", "AspN/N->D", 
		"Chymotrypsin", "GluC", "LysC", "Pepsin, pH=1.3", 
		"Pepsin, pH=2.0", "Trypsin", "Non-specific". Default value
		is Trypsin.

	--min-mass <double>
		Only consider peptides with monoisotopic mass greater than
		or equal to this molecular weight. Default value is 500.0.

        --max-mass <double>
		Only consider peptides with monoisotopic mass less than or
		equal to this molecular weight. Default value is 4000.0.

        --from-retention-time <double>
		Only search for peptides that elute at greater than or 
		equal to this time (specified in minutes). Default value
		is 20.0.

        --to-retention-time <double>
		Only search for peptides that elute at less than or 
		equal to this time (specified in minutes). Default value
		is 170.0.

        --ms1-error-ppm <int>
		When identifying candidate scans for MS2 confirmation,
		this parameter specifies how far from the theoretical
		precursor m/z value we will match an experimental precursor
		m/z value. It is parts-per-million, so the distance above
		and below the theoretical value is given by:

		(precursor m/z) * PPM / 1000000

		Default value is 10.

	--ms2-error <double>
		When matching ions from theoretical MS2 spectra to experimental
		spectra, this value specifies an absolute difference in Daltons
		within which ions will be considered as matching. Default value
		is 0.25.
```

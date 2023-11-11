package net.marioosh.pdfcleaner;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfcleanup.PdfCleaner;
import com.itextpdf.pdfcleanup.autosweep.CompositeCleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;

public class Cleaner {

	public Cleaner(String pdf, String out, String text) {
		try (PdfReader reader = new PdfReader(pdf); 
			 PdfWriter writer = new PdfWriter(out);) 
		{
			PdfDocument pdfDocument = new PdfDocument(reader, writer);
			replaceText(pdfDocument, text);
			pdfDocument.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void replaceText(PdfDocument pdfDocument, String text) throws IOException {
		CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
		RegexBasedCleanupStrategy st = new RegexBasedCleanupStrategy(text);
		st.setRedactionColor(null);
		strategy.add(st);

		PdfCleaner.autoSweepCleanUp(pdfDocument, strategy);
	}

	public static void main(String[] args) {
		Options options = new Options();
		options
		.addRequiredOption("i", "input", true, "input pdf file")
		.addRequiredOption("o", "output", true, "output pdf file")
		.addRequiredOption("t", "regex", true, "regular expression for text to be removed");
		
	    CommandLineParser parser = new DefaultParser();
	    HelpFormatter helper = new HelpFormatter();
	    
	    try {
	    	CommandLine cmd = parser.parse(options, args);
			new Cleaner(cmd.getOptionValue("i"), cmd.getOptionValue("o"), cmd.getOptionValue("t"));
	    } catch (ParseException e) {
			helper.printHelp("pdf-cleaner -i <input.pdf> -o <output.pdf> -t <regex>", options);
			System.exit(0);
		}
	    
	}
}

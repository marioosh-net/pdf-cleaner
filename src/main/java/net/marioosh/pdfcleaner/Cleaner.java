package net.marioosh.pdfcleaner;

import java.io.File;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cleaner {

	public Cleaner(String pdf, String out, String text) {
		File inPdf = new File(pdf);
		File outPdf = new File(out);
		
		log.info("in: {}", inPdf);
		log.info("out: {}", outPdf);
		
		try (PdfReader reader = new PdfReader(inPdf); 
			 PdfWriter writer = new PdfWriter(outPdf);) 
		{
			PdfDocument pdfDocument = new PdfDocument(reader, writer);
			replaceText(pdfDocument, text);
			pdfDocument.close();
			
			log.info("Done");
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

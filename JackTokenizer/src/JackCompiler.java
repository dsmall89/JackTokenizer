import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JackCompiler {

    public static void main(String[] args) {
	if (args.length != 0) {  // Assume file/folder name passed on command line.
	    new JackCompiler().compile(args[0]);
	    return;
	}

	// Pop-up a JFileChooser.
	final JFileChooser fc = new JFileChooser();

	try {
	    SwingUtilities.invokeAndWait(new Runnable() {
		public void run() {
		    UIManager.put("swing.boldMetal", Boolean.FALSE);
		    fc.setFileFilter(new FileNameExtensionFilter("Jack files", "jack"));
		    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.println("No file selected; terminating.");
			System.exit(1);
		    }
		}
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	new JackCompiler().compile(fc.getSelectedFile().getPath());
    }

    private void compile(String path) {
	String basename;
	File folder = new File(path);
	ArrayList<File> files;
	XMLWriter xmlWriter;

	if (folder.isFile()) {   // Folder is actually a file; translating a single
	    // Jack file.
	    files = new ArrayList<File>();
	    files.add(folder);
	    basename = folder.getPath(); 
	    basename = basename.substring(0,basename.lastIndexOf('/'));
	}
	else {   // folder is a folder; translating a folder of Jack files.
	    files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
	    basename = folder.getPath();
	}

	

	// Iterate through all the Jack files, or file, that we're compiling.

	for (File file : files) {

	    if (!file.getName().endsWith(".jack"))   // Skip non-jack files.
		continue;

	    String filename = file.getName(); 
	    filename = filename.substring(0, filename.indexOf('.'));
	    JackTokenizer tokenizer = new JackTokenizer(file);
	 
	    xmlWriter = new XMLWriter(basename+"/"+filename);
	    
	    // Iterate through all the tokens, printing the xml
	    xmlWriter.writeCode("<tokens>\n");
	    while (tokenizer.hasMoreTokens()) {
	    	tokenizer.advance();
	    	if (!tokenizer.hasMoreTokens()) break;
	    	switch (tokenizer.tokenType()){
	    	case IDENTIFIER:
	    		xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+" </identifier>\n");
	    		break;
	    	case KEYWORD:
	    		xmlWriter.writeCode("<keyword> "+tokenizer.identifier()+" </keyword>\n");
	    		break;
	    	case SYMBOL:
	    		String sym = tokenizer.symbol()+"";
	    		if (sym.equals("<")) sym = "&lt;";
	    		else if (sym.equals(">")) sym = "&gt;";
	    		else if (sym.equals("&")) sym = "&amp;";
	    		xmlWriter.writeCode("<symbol> "+sym+" </symbol>\n");
	    		break;
	    	case INT_CONST:
	    		xmlWriter.writeCode("<integerConstant> "+tokenizer.intVal()+" </integerConstant>\n");
	    		break;
	    	case STRING_CONST:
	    		xmlWriter.writeCode("<stringConstant> "+tokenizer.stringVal()+" </stringConstant>\n");
	    		break;
	    	}
	    	

	    }
	    xmlWriter.writeCode("</tokens>\n");
	    xmlWriter.close();
	}

	
    }
}

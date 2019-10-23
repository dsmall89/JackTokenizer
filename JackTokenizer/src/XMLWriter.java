import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class XMLWriter {

    
    private FileWriter xmlFile;
    


    public XMLWriter(String basename) {
	 try {
	    xmlFile = new FileWriter(basename + ".xml");
	 } catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	 }
	}

	

    // Write a sequence of XML code to the XML file.
    public void writeCode(String code) {
	try {
	    xmlFile.write(code);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void close() {
	try {
	    xmlFile.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

 
}
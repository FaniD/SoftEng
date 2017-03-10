/*
 * PrintXML.java
 *
 * Created on January 21, 2017, 6:48 PM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package net.java.sip.communicator ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class PrintXML {

	protected Document doc;
	protected File f;

	public PrintXML (Document doc, File f) throws TransformerException {
		this.doc = doc;
		this.f = f;
	}

	public void updateXML () {
		Document doc = this.doc;
		File f = this.f;
                try {
                        //Make a string out of Document and print it to the file indicated
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                        StreamResult result = new StreamResult(new StringWriter());
                        DOMSource source = new DOMSource(doc);
                        transformer.transform(source, result);

                        String xmlString = result.getWriter().toString();
                        PrintStream original = System.out;
                        PrintStream outta = new PrintStream(new FileOutputStream(f));
                        System.setOut(outta);
                        System.out.println(xmlString);
                        System.setOut(original);
                }
                catch( Exception e) {
                        e.printStackTrace();
                }
                return ;
	}

}

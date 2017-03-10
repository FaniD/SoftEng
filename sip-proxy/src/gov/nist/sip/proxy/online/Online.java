/*
 * Online.java
 *
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package gov.nist.sip.proxy.online ;

import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import gov.nist.sip.proxy.*;

/**
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

public class Online {

	protected Document doc;
	protected File f;
	protected String user;
	
	public Online(String displayName) {
		//constructor
		this.user = displayName;
		this.f = new File("src/gov/nist/sip/proxy/online/onlines.xml");
		try {
            		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            		Document doc = dBuilder.parse(this.f);
			doc.getDocumentElement().normalize();
			this.doc = doc;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean existingUser() {
		String usr=this.user,user_=null;
		Document doc = this.doc;
		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("User");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			user_ = e.getTextContent();
                        if (usr.equals(user_)) {
				return true;
           		 }
		};
		return false;
	}

	public void newlogin() {
		//make a record in xml and save it
		File f = this.f;
		Document doc = this.doc; 
		String usr=this.user;

		Element rootElement = doc.getDocumentElement();
		Node childNode = doc.createElement("User");
		rootElement.appendChild(childNode);
		childNode.setTextContent(usr);		
	try {
                    PrintXML p = new PrintXML(doc, f);
                    p.updateXML();
            }
    catch (Exception ex) {
                    ex.printStackTrace();
            }
            return;
	}

	public void removelogin() {
		File fp = this.f;
		Document doc = this.doc; 
		String usr=this.user, user_=null;
		Element rootElement = doc.getDocumentElement();
                NodeList children = doc.getElementsByTagName("User");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        user_ = e.getTextContent();
                        if (usr.equals(user_)) {
				rootElement.removeChild(child);
				break;
                        }
                };
	
		try {
                        PrintXML p = new PrintXML(doc, fp);
                        p.updateXML();
                }
                catch (Exception ex) {
                        ex.printStackTrace();
                }
	}
}

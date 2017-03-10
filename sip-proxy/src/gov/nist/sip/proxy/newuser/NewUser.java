/*
 * NewUser.java
 *
 * Created on January 9, 2017, 1:04 AM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package gov.nist.sip.proxy.newuser ;

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

public class NewUser {

	protected Document docac;
	protected File fac;
	protected Document pdoc;
	protected File fp;
	protected String user;
	
	public NewUser(String displayName) {
		//constructor
		this.user = displayName;
		this.fac = new File("src/gov/nist/sip/proxy/newuser/accounts.xml");
		this.fp = new File("src/gov/nist/sip/proxy/configuration/passwords.xml");
		try {
            		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            		Document doc = dBuilder.parse(this.fac);
			doc.getDocumentElement().normalize();
			this.docac = doc;
 			DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
            		DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
            		Document doc2 = dBuilder2.parse(this.fp);
			doc.getDocumentElement().normalize();
			this.pdoc = doc2;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean existingUser() {
		//ΜΕSSAGE: The display name you are trying to use already exists, please try another username.
		//search passwords.xml or accounts 
		String usr=this.user,usr_=null;
		Document doc = this.pdoc;
		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("User");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			usr_ = e.getAttribute("name");
			if (usr.equals(usr_)) {
				return true;
            }
		};
		return false;
	}

	public void saveNew(String name, String surname, String email, String address) {
		//make a record in xml and save it
		File f = this.fac;
		Document doc = this.docac; 
		String usr=this.user;

		Element rootElement = doc.getDocumentElement();
		Node childNode = doc.createElement("USER");
		rootElement.appendChild(childNode);
		Attr newuser = doc.createAttribute("nickname");
		newuser.setValue(usr);
		Element e = (Element) childNode;
		e.setAttributeNode(newuser);
		
		Node nameNode = doc.createElement("Name");
		e.appendChild(nameNode);
		nameNode.setTextContent(name);

		Node surnameNode = doc.createElement("Surname");
		e.appendChild(surnameNode);
		surnameNode.setTextContent(surname);
		
		Node emailNode = doc.createElement("Email");
		e.appendChild(emailNode);
		emailNode.setTextContent(email);
		
		Node adrNode = doc.createElement("Address");
		e.appendChild(adrNode);
		adrNode.setTextContent(address);
		
	try {
                    PrintXML p = new PrintXML(doc, f);
                    p.updateXML();
            }
    catch (Exception ex) {
                    ex.printStackTrace();
            }
            return;
	}

	public void savePassword(String pass) {
		File fp = this.fp;
		Document doc = this.pdoc; 
		String usr=this.user;
		Element rootElement = doc.getDocumentElement();
		Node childNode = doc.createElement("User");
    		rootElement.appendChild(childNode);
		Attr name = doc.createAttribute("name");
		name.setValue(usr);
		Element e = (Element) childNode;
		e.setAttributeNode(name);
		Attr passw = doc.createAttribute("password");
		passw.setValue(pass);
		e.setAttributeNode(passw);
		
		try {
                        PrintXML p = new PrintXML(doc, fp);
                        p.updateXML();
                }
                catch (Exception ex) {
                        ex.printStackTrace();
                }
	}
}

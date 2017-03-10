/*
 * NewUser.java
 *
 * Created on January 9, 2017, 1:04 AM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package net.java.sip.communicator.newuser ;

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

import net.java.sip.communicator.*;

/**
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

public class NewUser {

	protected Document doc;
	protected File f;
	protected String user;
	
	public NewUser(String displayName) {
		//constructor
		this.user = displayName;
		this.f = new File("src/net/java/sip/communicator/newuser/accounts.xml");
		File fXmlFile = f;
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			this.doc = doc;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean existingUser() {
		//ΜΕSSAGE: The display name you are trying to use already exists, please try another username.
		//search passwords.xml or accounts 
		String usr=this.user,usr_=null;
		Document doc = this.doc;
		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("USER");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			usr_ = e.getAttribute("nickname");
			if (usr.equals(usr_)) {
				return true;
            }
		};
		return false;
	}

	public void saveNew(String name, String surname, String email, String address) {
		//make a record in xml and save it
		File f = this.f;
		Document doc = this.doc; 
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
                    PrintXML p = new PrintXML(doc, this.f);
                    p.updateXML();
            }
    catch (Exception ex) {
                    ex.printStackTrace();
            }
            return;
	}
}

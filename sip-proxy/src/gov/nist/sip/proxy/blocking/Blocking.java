/*
 * Blocking.java
 *
 * Created on January 9, 2017, 6:25 PM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */
package gov.nist.sip.proxy.blocking ;
import gov.nist.sip.proxy.*;

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

/* Example record of blocks.xml:
 *
 * <BLOCKS>
 *   <BLOCK blocker="Fani">
 *      <blocked>Roula</blocked>
 *   </BLOCK>
 * ...
 * </BLOCKS>
 */

public class Blocking {

	protected File f;
	protected Document doc;
	protected String caller; 
	protected String callee;
	protected String blocker;
	protected String blocked;
	protected boolean block_on;

	public Blocking(String caller, String callee) {
		//constructor
		//The caller is the one who is estimated to be blocked and the callee is our blocker
		this.caller = caller;
		this.callee = callee;
		this.blocker = callee;
		this.blocked = caller;
		this.block_on = false;
		this.f = new File("src/gov/nist/sip/proxy/blocking/blocks.xml");
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

	public boolean check_if_blocked() {
		Document doc = this.doc;
		String blocker_, blocked_;
		String blocker=this.blocker, blocked=this.blocked;

		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("BLOCK");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			blocker_ = e.getAttribute("blocker");
			NodeList elemNodeList = e.getElementsByTagName("blocked");
			
			if (blocker_.equals(blocker)) {
				for (int j=0; j<elemNodeList.getLength(); j++) {
					blocked_ = elemNodeList.item(j).getTextContent();
					if (blocked_.equals(blocked)) {
                                                this.block_on = true;
                                                return (this.block_on);
                                	}
				};
			}
		};	
		return (this.block_on);	
	}

	public void newBlock() {
		//create a new blocking contact
		Document doc = this.doc; 
		String blocker=this.blocker,blocked=this.blocked,blocker_=null,blocked_=null;

		Node child=null;
		Element rootElement = doc.getDocumentElement();
		int flag=1;
                NodeList children = doc.getElementsByTagName("BLOCK");
                for (int i = 0; i < children.getLength(); i++) {
                        child = children.item(i);
                        Element e = (Element)child;
                        blocker_ = e.getAttribute("blocker");
                        NodeList elemNodeList = e.getElementsByTagName("blocked");

                        if (blocker_.equals(blocker)) { //brikame ton blocker
                        		flag=2;
                        		for (int j=0; j<elemNodeList.getLength(); j++) {
                                        blocked_ = elemNodeList.item(j).getTextContent();
                                        if (blocked_.equals(blocked)) {
                                        		flag=0;
                                        		break;
                                        }
                                };
                        }
                        if (flag==2 || flag==0) break;
                };
            if (flag==1) { //BLOCK record doesn't exist at all
            	Node childNode = doc.createElement("BLOCK");
    			rootElement.appendChild(childNode);
    			Attr newblocker = doc.createAttribute("blocker");
    			newblocker.setValue(blocker);
    			Element e = (Element) childNode;
    			e.setAttributeNode(newblocker);
    			Node newblocked = doc.createElement("blocked");
    			e.appendChild(newblocked);
    			newblocked.setTextContent(blocked);
            }
		else if (flag==2) { //Record exists but not element blocked
			Element e = (Element) child;
			Node newblocked = doc.createElement("blocked");
			e.appendChild(newblocked);
                        newblocked.setTextContent(blocked);
		};
                try {
                        PrintXML p = new PrintXML(doc, this.f);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
		return;  
	}

	public void stopBlocking() {
		//find pair of blocker and bloocked and remove it from xml
		Document doc = this.doc;
        String blocker=this.blocker,blocked=this.blocked,blocker_,blocked_;

		Element rootElement = doc.getDocumentElement();
		boolean flag=false;
                NodeList children = doc.getElementsByTagName("BLOCK");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        blocker_ = e.getAttribute("blocker");
                        NodeList elemNodeList = e.getElementsByTagName("blocked");

                        if (blocker_.equals(blocker)) { //brikame ton blocker
                                for (int j=0; j<elemNodeList.getLength(); j++) {
                                        blocked_ = elemNodeList.item(j).getTextContent();
                                        if (blocked_.equals(blocked)) {
						if (elemNodeList.getLength()==1) 
							rootElement.removeChild(child);
						else
							e.removeChild(elemNodeList.item(j));
                                        }
                                };
                                flag=true;
                        };
			if (flag) break;
                };
		
		try { 
                	PrintXML p = new PrintXML(doc, this.f);
                	p.updateXML();
		}
		catch (Exception e) {
                        e.printStackTrace();
		}
                return;
	}
		
}

/*
 * Forwarding.java
 *
 * Created on January 9, 2017, 7:04 PM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package gov.nist.sip.proxy.forwarding ;
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
 * <FORWARDS>
 *   <FORWARD fromUser="Fani">
 *      <toUser>Roula</toUser>
 *   </FORWARD>
 * ...
 * </FORWARDS>
 */

public class Forwarding {

	protected Document doc;
	protected File f;
	protected Document doc_c;
	protected File f_c;
	String fromUser;
	String toUser;

	public Forwarding(String fromUser) {
		//constructor
		this.fromUser = fromUser;
		this.f = new File("src/gov/nist/sip/proxy/forwarding/forwards.xml");
		this.f_c = new File("src/gov/nist/sip/proxy/forwarding/cycles.xml");
		try {
                	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                	Document doc = dBuilder.parse(this.f);
			doc.getDocumentElement().normalize();
			this.doc = doc;

                	DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
                	DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
                	Document doc1 = dBuilder1.parse(this.f_c);
			doc1.getDocumentElement().normalize();
			this.doc_c = doc1;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String check_if_forwarding() {
		//find if the user forwards the call - XML search
		String forward_to = null;
		String fromUser=this.fromUser,user_;
		Document doc = this.doc;
		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("FORWARD");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			user_ = e.getAttribute("fromUser");
			NodeList elemNodeList = e.getElementsByTagName("toUser");
			if (fromUser.equals(user_)) {
				forward_to = elemNodeList.item(((elemNodeList.getLength()) - 1)).getTextContent();
				forward_to = forward_to.replaceAll("(\\r|\\n)", "");
				this.toUser = forward_to;
                                return (this.toUser);
                        }
		};
		this.toUser = null;
		return (null);
	};	

	public void newForward(String toUser) {
                //create a new forwarding contact 
                if (check_if_forwarding()!=null)
                        //Forward allready exists in XML
                        return ;
                else {
                        //create a new record at the xml file
			File f = this.f;
			Document doc = this.doc; 
			String fromUser=this.fromUser;
			this.toUser = toUser;

			Element rootElement = doc.getDocumentElement();
			Node childNode = doc.createElement("FORWARD");
    			rootElement.appendChild(childNode);
			Attr newforward = doc.createAttribute("fromUser");
			newforward.setValue(fromUser);
			Element e = (Element) childNode;
			e.setAttributeNode(newforward);
			Node newforwardto = doc.createElement("toUser");
			e.appendChild(newforwardto);
			newforwardto.setTextContent(toUser);
		}
		try {
                        PrintXML p = new PrintXML(doc, this.f);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
                return;
	}

	public void removeForward() {
		//Find the caller in xml and erase the whole record
                String fromUser=this.fromUser,user_;
                Document doc = this.doc;
                Element rootElement = doc.getDocumentElement();
                NodeList children = doc.getElementsByTagName("FORWARD");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        user_ = e.getAttribute("fromUser");
                        if (fromUser.equals(user_)) {
                                this.toUser = null;
				this.fromUser = null;
				rootElement.removeChild(child);
				break;
                        }
                };
                try {
                        PrintXML p = new PrintXML(doc, this.f);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }

		return ;
	}

	public boolean add_callee_without_cycle(String caller) {
		File fc = this.f_c;
		Document dc = this.doc_c;
		String callee = this.fromUser;
		String callee_=null, caller_=null, newcallee=null;
		Element rootElement = dc.getDocumentElement();
		boolean flag = true;
		int j=0;
		boolean existing = false;

		NodeList children = dc.getElementsByTagName("PATH");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			caller_ = e.getAttribute("caller");
			NodeList elemNodeList = e.getElementsByTagName("callee");
			if (caller.equals(caller_)) { //The first callee is the original call, one call between caller and callee can only exist
				//for (j=0; j<elemNodeList.getLength(); j++) {
                                //        callee_ = elemNodeList.item(j).getTextContent();
					callee_ = elemNodeList.item(((elemNodeList.getLength()) - 1)).getTextContent(); //We are always looking the last callee
                                        if (callee_.equals(callee)) {
						existing = true;
						//Found the record? Check for cycles with the new callee
						//Add the new callee
						newcallee = check_if_forwarding(); //The new callee
						//Check if there is anywhere in the callees
						for (j=0; j<elemNodeList.getLength(); j++) {
                                        		callee_ = elemNodeList.item(j).getTextContent();
                                        		if (callee_.equals(newcallee)) {
								//CYCLE ALERT!
								flag=false;
								break;
							}
						}
						if (!flag) break;
						//Continue if not cycle by adding new callee to the record
				//		Element e = (Element) child;
						Node newc = dc.createElement("callee");
						e.appendChild(newc);
                        			newc.setTextContent(newcallee);
						break;
                                        }
                        };
		}				

		if (!existing) {
			//No record found, so it's the first forwarding
			if (callee.equals(check_if_forwarding())) return false; //Not a record yet, so never will be 
			//No cycle, build a record		
			Node childNode = dc.createElement("PATH");
    			rootElement.appendChild(childNode);
			Attr newpath = dc.createAttribute("caller");
			newpath.setValue(caller);
			Element e = (Element) childNode;
			e.setAttributeNode(newpath);
			Node newc1 = dc.createElement("callee");
			e.appendChild(newc1);
			newc1.setTextContent(callee);

			e.setAttributeNode(newpath);
			Node newc2 = dc.createElement("callee");
			e.appendChild(newc2);
			newc2.setTextContent(check_if_forwarding());
		}
		
		if (!flag) {//delete the record
			delete_path_record(caller,null,j);
			return flag;
		}

		try {
                        PrintXML p = new PrintXML(dc, this.f_c);
                        p.updateXML();
                }
                catch (Exception ex) {
                        ex.printStackTrace();
                }
        return (flag);		
	}

	public void delete_path_record(String caller, String callee, int k) {
		File fc = this.f_c;
		Document dc = this.doc_c;
		String callee_=null, caller_=null;
		Element rootElement = dc.getDocumentElement();
                NodeList children = dc.getElementsByTagName("PATH");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("caller");
                        NodeList elemNodeList = e.getElementsByTagName("callee");

                        if (caller_.equals(caller)) { 
				if (k==-1) {
                        	//	callee_ = elemNodeList.item((elemNodeList.getLength()) - 1).getTextContent(); //We are always looking the last callee
					rootElement.removeChild(child);break;
				}
				else {
					callee = check_if_forwarding();
					callee_ = elemNodeList.item(k).getTextContent(); 
				
				if (callee.equals(callee_)) {
					rootElement.removeChild(child);
					break;}
				}
			}
		}

		//print
		try {
                        PrintXML p = new PrintXML(dc, this.f_c);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
		return ;
	}
}

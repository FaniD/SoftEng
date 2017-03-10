/*
 * Billing.java
 *
 * Created on January 9, 2017, 6:48 PM
 * Authors: Fani Dimou, Sotiria Kyriakopoulou
 */

package gov.nist.sip.proxy.billing ;
import gov.nist.sip.proxy.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Double;
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
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.xml.sax.InputSource;
import java.text.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/* Example record of billing.xml
 *
 * <BILLING>
 *	<BILL user="Fani">
 *	   <total_cost>100</total_cost>
 *	</BILL>
 *	<BILL user="Roula">
 *	   <total_cost>120</total_cost>
 *	</BILL>
 *	...
 * <BILLING>
 *
 */

public class Billing {

	protected File billf;
	protected Document billdoc;
	protected File friendsf;
        protected Document frienddoc;
	protected File businessf;
        protected Document businessdoc;
	protected File callsf;
        protected Document callsdoc;
	protected String caller;
	protected String callee;
	protected double startmili;
	protected double endmili;
	protected double duration;
	protected int start_hour1;
	protected int start_hour2;
	protected boolean business_alert = false;
	protected boolean friend_alert = false;
	
	public Billing(String caller, String callee) {
		//constructor
		this.caller = caller;
		this.callee = callee;
		this.billf = new File("src/gov/nist/sip/proxy/billing/billing.xml");
		this.friendsf = new File("src/gov/nist/sip/proxy/billing/friends.xml");
		this.businessf = new File("src/gov/nist/sip/proxy/billing/business.xml");
		this.callsf = new File("src/gov/nist/sip/proxy/billing/calls.xml");
		try {
                	DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
                	DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
                	Document doc1 = dBuilder1.parse(billf);
			doc1.getDocumentElement().normalize();
			this.billdoc = doc1;
                        DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                        Document doc2 = dBuilder2.parse(friendsf);
                        doc2.getDocumentElement().normalize();
                        this.frienddoc = doc2;
                        DocumentBuilderFactory dbFactory3 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder3 = dbFactory3.newDocumentBuilder();
                        Document doc3 = dBuilder3.parse(businessf);
                        doc3.getDocumentElement().normalize();
                        this.businessdoc = doc3;
                        DocumentBuilderFactory dbFactory4 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder4 = dbFactory4.newDocumentBuilder();
                        Document doc4 = dBuilder4.parse(callsf);
                        doc4.getDocumentElement().normalize();
                        this.callsdoc = doc4;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void callStart() {
		//open session call of caller to callee and save it to startCall
		//in the file sip-communicator/scr/net/java/sip/communicator/sip/CallProcessing at the fuction processInviteOK
		this.startmili = System.currentTimeMillis();
		Timestamp t = new Timestamp(System.currentTimeMillis());

		//Save the information on calls.xml
		Document doc = this.callsdoc; 
		String caller=this.caller,callee=this.callee;

		Element rootElement = doc.getDocumentElement();
            Node childNode = doc.createElement("CALL");
    		rootElement.appendChild(childNode);
    		Attr newcall = doc.createAttribute("caller");
    		newcall.setValue(caller);
    		Element e = (Element) childNode;
    		e.setAttributeNode(newcall);
    		Node newcallee = doc.createElement("callee");
    		e.appendChild(newcallee);
    		newcallee.setTextContent(callee);
		Node strtime = doc.createElement("startinmilli");
    		e.appendChild(strtime);
    		strtime.setTextContent(String.valueOf(this.startmili));

		String S = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(t);
		this.start_hour1 = Character.getNumericValue(S.charAt(11));
		this.start_hour2 = Character.getNumericValue(S.charAt(12));

		Node str_h1 = doc.createElement("start_hour1");
    		e.appendChild(str_h1);
    		str_h1.setTextContent(String.valueOf(this.start_hour1));
		Node str_h2 = doc.createElement("start_hour2");
    		e.appendChild(str_h2);
    		str_h2.setTextContent(String.valueOf(this.start_hour2));

		try { 
                	PrintXML p = new PrintXML(doc, this.callsf);
                	p.updateXML();
		}
		catch (Exception ex) {
                        ex.printStackTrace();
		}
		return;
	}

	public void callEnd() {
		//find session call from caller to callee,save to endCall
		//and calculate the duration of the call
		//save the result at duration
		//in the file sip-communicator/scr/net/java/sip/communicator/sip/CallProcessing.java at the fuction processByeOK
		this.endmili = System.currentTimeMillis();

		//Find start time from calls.xml and save to the instance the variables that we need for the billing, then remove the record 
		Document doc = this.callsdoc; 
		String caller=this.caller,callee=this.callee,caller_=null,callee_=null;

		Node child=null;
		Element rootElement = doc.getDocumentElement();
		int flag=1;
                NodeList children = doc.getElementsByTagName("CALL");
                for (int i = 0; i < children.getLength(); i++) {
                        child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("caller");
                        NodeList elemNodeList = e.getElementsByTagName("callee");
                        callee_ = elemNodeList.item(0).getTextContent();
                        if ((caller_.equals(caller) && (callee_.equals(callee))) || (caller_.equals(callee) && (callee_.equals(caller)))) { //brikame to record mas
				this.caller = caller_;
				this.callee = callee_;
                        	/*NodeList*/ elemNodeList = e.getElementsByTagName("startinmilli");
                                this.startmili = Double.parseDouble(elemNodeList.item(0).getTextContent());
				/*NodeList*/ elemNodeList = e.getElementsByTagName("start_hour1");
				this.start_hour1 = Integer.parseInt(elemNodeList.item(0).getTextContent());
				/*NodeList*/ elemNodeList = e.getElementsByTagName("start_hour2");				
				this.start_hour2 = Integer.parseInt(elemNodeList.item(0).getTextContent());
				break;
                        }
                };
		//Now remove the record since the call is terminated
		rootElement.removeChild(child);
		try { 
                	PrintXML p = new PrintXML(doc, this.callsf);
                	p.updateXML();
		}
		catch (Exception e) {
                        e.printStackTrace();
		}

		//Ypologismos tou duration
		this.duration=this.endmili-this.startmili;
		return;
	}

	public boolean checkBusinessCall() {
		// open business.xml and search for the pair caller-callee at the records 
		Document doc = this.businessdoc;
		String caller_, callee_;
		String caller=this.caller, callee=this.callee;

		Element rootElement = doc.getDocumentElement();
		NodeList children = doc.getElementsByTagName("BIZ");
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Element e = (Element)child;
			caller_ = e.getAttribute("user");
			NodeList elemNodeList = e.getElementsByTagName("partner");
			
			if (caller_.equals(caller)) {
				for (int j=0; j<elemNodeList.getLength(); j++) {
					callee_ = elemNodeList.item(j).getTextContent();
					if (callee_.equals(callee)) {
                                                return (true);
                                	}
				};
			}
		};	
		return false ;	
	}

	public boolean checkFriendCall() {
		//opean friends.xml and search for the pair caller-callee at the records
                Document doc = this.frienddoc;
                String caller_, callee_;
                String caller=this.caller, callee=this.callee;

                Element rootElement = doc.getDocumentElement();
                NodeList children = doc.getElementsByTagName("friendship");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("friend");

                        if (caller_.equals(caller)) {
                                for (int j=0; j<elemNodeList.getLength(); j++) {
                                        callee_ = elemNodeList.item(j).getTextContent();
                                        if (callee_.equals(callee)) {
                                                return (true);
                                        }
                                };
                        }
                };
                return false ;	
	}

	public boolean checkNightCall() {
		//Night call from 00.00 until 06.59
		if (this.start_hour1==0) 
			if (this.start_hour2<7)
				return true;
		return false;
	}

	public void setBusinessCallee() {
	//Business partners can be up to 2!
		Document doc = this.businessdoc; 
		String caller=this.caller,callee=this.callee,caller_=null,callee_=null;

		Node child=null;
		Element rootElement = doc.getDocumentElement();
		int flag=1;
                NodeList children = doc.getElementsByTagName("BIZ");
                for (int i = 0; i < children.getLength(); i++) {
                        child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("partner");
                        if (caller_.equals(caller)) { 
				if (elemNodeList.getLength()==2) {
                	                //Up to 2!
					this.business_alert = true;
        	                        return;
	                        }
				flag=2;
		 		for (int j=0; j<elemNodeList.getLength(); j++) {
                                        callee_ = elemNodeList.item(j).getTextContent();
                                        if (callee_.equals(callee)) {
						return;
                                        }
                                };
                        }
			if (flag==2 || flag==0) break;
               };
		if (flag==1) { //record doesn't exist at all
			Node childNode = doc.createElement("BIZ");
    			rootElement.appendChild(childNode);
			Attr newcaller = doc.createAttribute("user");
			newcaller.setValue(caller);
			Element e = (Element) childNode;
			e.setAttributeNode(newcaller);
			Node newcallee = doc.createElement("partner");
			e.appendChild(newcallee);
			newcallee.setTextContent(callee);
		}
		else if (flag==2) { //Record exists but not with this element
			Element e = (Element) child;
			Node newcallee = doc.createElement("partner");
			e.appendChild(newcallee);
                        newcallee.setTextContent(callee);
		};
                try {
                        PrintXML p = new PrintXML(doc, this.businessf);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
		return;  
	}

	public void removeBusinessCallee() {
		//find pair of caller and callee and remove it from xml
		Document doc = this.businessdoc;
        	String caller=this.caller,callee=this.callee,caller_,callee_;

		Element rootElement = doc.getDocumentElement();
		boolean flag=false;
                NodeList children = doc.getElementsByTagName("BIZ");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("partner");

                        if (caller_.equals(caller)) { //brikame ton user
                                for (int j=0; j<elemNodeList.getLength(); j++) {
                                        callee_ = elemNodeList.item(j).getTextContent();
                                        if (callee_.equals(callee)) {
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
                	PrintXML p = new PrintXML(doc, this.businessf);
                	p.updateXML();
		}
		catch (Exception ex) {
                        ex.printStackTrace();
		}
                return;
	}


	public void setFriendCallee() {
	//Friend contacts can be up to 5!
                Document doc = this.frienddoc;
                String caller=this.caller,callee=this.callee,caller_=null,callee_=null;

                Node child=null;
                Element rootElement = doc.getDocumentElement();
                int flag=1;
                NodeList children = doc.getElementsByTagName("friendship");
                for (int i = 0; i < children.getLength(); i++) {
                        child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("friend");
                        if (caller_.equals(caller)) {
                                if (elemNodeList.getLength()==5) {
                                        //Up to 5!
                                        this.friend_alert = true;
                                        return;
                                }
                                flag=2;
                                for (int j=0; j<elemNodeList.getLength(); j++) {
                                        callee_ = elemNodeList.item(j).getTextContent();
                                        if (callee_.equals(callee)) {
                                                //flag=0;
                                                //break;
                                                return;
                                        }
                                };
                        }
                        if (flag==2 || flag==0) break;
               };
                if (flag==1) { //record doesn't exist at all
                        Node childNode = doc.createElement("friendship");
                        rootElement.appendChild(childNode);
                        Attr newcaller = doc.createAttribute("user");
                        newcaller.setValue(caller);
                        Element e = (Element) childNode;
                        e.setAttributeNode(newcaller);
                        Node newcallee = doc.createElement("friend");
                        e.appendChild(newcallee);
                        newcallee.setTextContent(callee);
                }
		else if (flag==2) { //Record exists but not with this element
                        Element e = (Element) child;
                        Node newcallee = doc.createElement("friend");
                        e.appendChild(newcallee);
                        newcallee.setTextContent(callee);
                };
                try {
                        PrintXML p = new PrintXML(doc, this.friendsf);
                        p.updateXML();
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
                return;
	}

	public boolean get_biz_alert () {
		return this.business_alert;
	}

	public boolean get_friend_alert () {
		return this.friend_alert;
	}

	public void removeFriendCallee() {
		//find pair of caller and callee and remove it from xml
		Document doc = this.frienddoc;
        	String caller=this.caller,callee=this.callee,caller_,callee_;

		Element rootElement = doc.getDocumentElement();
		boolean flag=false;
                NodeList children = doc.getElementsByTagName("friendship");
                for (int i = 0; i < children.getLength(); i++) {
                        Node child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("friend");

                        if (caller_.equals(caller)) { //brikame ton user
                                for (int j=0; j<elemNodeList.getLength(); j++) {
                                        callee_ = elemNodeList.item(j).getTextContent();
                                        if (callee_.equals(callee)) {
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
                	PrintXML p = new PrintXML(doc, this.friendsf);
                	p.updateXML();
		}
		catch (Exception ex) {
                        ex.printStackTrace();
		}
                return;
	}
	
	public int setprogramid() {
		int program=0 ;
		if (checkNightCall()) program=1;
		if (checkFriendCall()) program=2;
		if (checkBusinessCall()) program=3;
		return program;
	}

	public double costCalculate() {
		double n;
		int program = setprogramid();
		if (program == 0)
			n=0.5;
		else if (program == 1)
			n=0.03;
		else if (program == 2)
			n=0.015;
		else
			n=0.01;
		double current_cost = (n*this.duration);
		
		//Calculate total cost with users information of billing.xml
		Document doc = this.billdoc; 
		String caller=this.caller,caller_=null;
		double amount = 0;
		Node child=null;
		Element rootElement = doc.getDocumentElement();
		int flag=1;
                NodeList children = doc.getElementsByTagName("BILL");
                for (int i = 0; i < children.getLength(); i++) {
                        child = children.item(i);
                        Element e = (Element)child;
                        caller_ = e.getAttribute("user");
                        NodeList elemNodeList = e.getElementsByTagName("total_cost");

                        if (caller_.equals(caller)) {
				flag=2;
                                amount = Double.parseDouble(elemNodeList.item(0).getTextContent());
				e.removeChild(elemNodeList.item(0));
				break;
                        };
               };
		if (flag==1) { //record doesn't exist at all
			Node childNode = doc.createElement("BILL");
    			rootElement.appendChild(childNode);
			Attr new1 = doc.createAttribute("user");
			new1.setValue(caller);
			Element e = (Element) childNode;
			e.setAttributeNode(new1);
			Node new2 = doc.createElement("total_cost");
			e.appendChild(new2);
			new2.setTextContent(String.valueOf(current_cost));
		}
		else if (flag==2) { //Record exists but not element b
			Element e = (Element) child;
			Node new2 = doc.createElement("total_cost");
			e.appendChild(new2);
                        new2.setTextContent(String.valueOf(current_cost+amount));
		};
                try {
                        PrintXML p = new PrintXML(doc, this.billf);
                        p.updateXML();
                }
                catch (Exception ex) {
                        ex.printStackTrace();
                }
		return (current_cost);  		
	}
 
}

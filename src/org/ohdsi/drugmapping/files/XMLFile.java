package org.ohdsi.drugmapping.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLFile {
	private String fileName = null;
	private Document xmlDocument = null;

	
	public XMLFile(String fileName) {
		this.fileName = fileName;
	}
	
	
	public boolean openFile() {
		boolean result = false;
		if ((fileName != null) && (!fileName.equals(""))) {
			File xmlFile = new File(fileName);
			if (xmlFile.canRead()) {
				boolean readError = false;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder builder = factory.newDocumentBuilder();
					xmlDocument = builder.parse(xmlFile);
					xmlDocument.getDocumentElement().normalize();
					result = true;
				} catch (ParserConfigurationException e) {
					readError = true;
				} catch (SAXException e) {
					readError = true;
				} catch (IOException e) {
					readError = true;
				}
				if (readError) {
					JOptionPane.showMessageDialog(null, "Error reading file \"" + fileName + "\"!", "XML Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Cannot open file \"" + fileName + "\"!", "XML Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "No file name specified!", "XML Error", JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}
	
	
	public XMLRoot getXMLRoot() {
		return new XMLRoot(xmlDocument.getDocumentElement());
	}
	
	
	public class XMLRoot {
		private Element root = null;
		
		
		public XMLRoot(Element root) {
			this.root = root;
		}
		
		
		public String getAttribute(String attributeName) {
			return root.getAttributes().getNamedItem(attributeName).getNodeValue();
		}
		
		
		public XMLNode getChild(String tagName) {
			XMLNode firstChild = null;
			NodeList children = root.getChildNodes();
			for (int childNr = 0; childNr < children.getLength(); childNr++) {
				Node child = children.item(childNr);
				if (child.getNodeName().equals(tagName)) {
					 firstChild = new XMLNode(child);
					 break;
				}
			}
			return firstChild;
		}
		
		
		public List<XMLNode> getChildren(String tagName) {
			NodeList children = root.getChildNodes();
			List<XMLNode> selectedChildren = new ArrayList<XMLNode>();
			for (int childNr = 0; childNr < children.getLength(); childNr++) {
				Node child = children.item(childNr);
				if (child.getNodeName().equals(tagName)) {
					selectedChildren.add(new XMLNode(child));
				}
			}
			return selectedChildren;
		}
	}
	
	
	public class XMLNode {
		private Node node = null;
		
		
		public XMLNode(Node node) {
			this.node = node;
		}
		
		
		public String getAttribute(String attributeName) {
			return node.getAttributes().getNamedItem(attributeName).getNodeValue();
		}
		
		
		public XMLNode getChild(String tagName) {
			XMLNode firstChild = null;
			NodeList children = node.getChildNodes();
			for (int childNr = 0; childNr < children.getLength(); childNr++) {
				Node child = children.item(childNr);
				if (child.getNodeName().equals(tagName)) {
					 firstChild = new XMLNode(child);
					 break;
				}
			}
			return firstChild;
		}
		
		
		public List<XMLNode> getChildren(String tagName) {
			NodeList children = node.getChildNodes();
			List<XMLNode> selectedChildren = new ArrayList<XMLNode>();
			for (int childNr = 0; childNr < children.getLength(); childNr++) {
				Node child = children.item(childNr);
				if (child.getNodeName().equals(tagName)) {
					selectedChildren.add(new XMLNode(child));
				}
			}
			return selectedChildren;
		}
		
		
		public String getValue(String valueName) {
			String value = null;
			NodeList valueNodes = ((Element) node).getElementsByTagName(valueName);
			for (int childNr = 0; childNr < valueNodes.getLength(); childNr++) {
				value = valueNodes.item(childNr).getTextContent();
			}
			return value;
		}
		
		
		public List<String> getValues(String valueName) {
			List<String> values = new ArrayList<String>();
			NodeList valueNodes = ((Element) node).getElementsByTagName(valueName);
			for (int childNr = 0; childNr < valueNodes.getLength(); childNr++) {
				values.add(valueNodes.item(childNr).getTextContent());
			}
			return values;
		}
		
		
		public String toString() {
			return toString("");
		}
		
		
		public String toString(String prefix) {
			String description = prefix + node.getNodeName();
			NamedNodeMap attributes = node.getAttributes();
			if (attributes.getLength() > 0) {
				for (int attributeNr = 0; attributeNr < attributes.getLength(); attributeNr++) {
					description += (attributeNr == 0 ? " (" : ", ");
					description += attributes.item(attributeNr).getNodeName();
					description += " = ";
					description += attributes.item(attributeNr).getNodeValue();
				}
				description += ")";
			}
			
			NodeList children = node.getChildNodes();
			if (children.getLength() > 1) {
				description += "\r\n";
				
				String newPrefix = "";
				for (int spaceNr = 0; spaceNr < (prefix.length() + 2); spaceNr++) {
					newPrefix += " ";
				}
				
				for (int childNr = 1; childNr < children.getLength(); childNr = childNr + 2) {
					description += (new XMLNode(children.item(childNr))).toString(newPrefix);
				}
			}
			else {
				description += " = " + node.getTextContent() + "\r\n";
			}
			
			return description;
		}
	}
}

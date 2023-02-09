package com.northern.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class CompareXMLService {

	public StreamResult compareXML(MultipartFile xml1, MultipartFile xml2) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		//Defining a factory to obtain a parser that produces DOM object trees from XML documents
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		//Defining the API to obtain DOM Document instances from an XMLdocument. 
		//Using this class, we can obtain a Document from XML.
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		//Parse the content of the given file as an XML document and return a new DOM Document object
		Document document1 = dBuilder.parse(xml1.getInputStream());
		Document document2 = dBuilder.parse(xml2.getInputStream());

		//Storing all elements from both xml files in two separate list
		List<Element> elements1= getAllElements(document1);
		List<Element> elements2= getAllElements(document2);
		
		System.out.println(elements1.toString());
		System.out.println(elements2.toString());
		//create a new tag for each element in xml2
		for(Element element2: elements2) {
			String elementName=element2.getNodeName();
			if(isElementExist(elements1, elementName)) {
				element2.setAttribute("PresentIn", "both");
			}else {
				element2.setAttribute("PresentIn", "2");
			}
		}
		
		for(Element element1: elements1) {
			String elementName=element1.getNodeName();
			if(!isElementExist(elements2, elementName)) {
				Element newElement=document2.createElement(elementName);
				newElement.setAttribute("PresentIn", "1");
				Element root = document2.getDocumentElement();
				root.appendChild(newElement);

				//elements2.get(0).getParentNode().appendChild(newElement);
			}
		}
		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		//creating StreamResult object to hold updated xml
		StreamResult result=new StreamResult(outputStream);
		
		//creating xml transformer from transFormerfactory
		TransformerFactory transformerFactory=TransformerFactory.newInstance();
		Transformer transformer=transformerFactory.newTransformer();
		
		DOMSource source=new DOMSource(document2);
//		StreamResult result=new StreamResult(new File("updated_xml2"));
		transformer.transform(source, result);
		
		return result;

	}

	private List<Element> getAllElements(Document doc) {
		NodeList nodeList=doc.getElementsByTagName("*");
		List<Element> elements=new ArrayList<>();
		
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node=nodeList.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				elements.add((Element) node);
			}
		}
		return elements;
	}

	private boolean isElementExist(List<Element> elements, String elementName) {
		for(Element element: elements) {
			if(element.getNodeName().equals(elementName)) {
				return true;
			}
		}
		return false;
	}
}

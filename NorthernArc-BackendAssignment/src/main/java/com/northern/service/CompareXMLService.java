package com.northern.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	public StreamResult compareXML(MultipartFile xml1, MultipartFile xml2)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document document1 = dBuilder.parse(xml1.getInputStream());
		Document document2 = dBuilder.parse(xml2.getInputStream());

		List<Element> elements1 = getAllElements(document1);
		List<Element> elements2 = getAllElements(document2);

		for (Element element2 : elements2) {
			String elementName = element2.getNodeName();
			if (isElementExist(elements1, elementName)) {
				element2.setAttribute("PresentIn", "both");
			} else {
				element2.setAttribute("PresentIn", "2");
			}
		}

		for (Element element1 : elements1) {
			String elementName = element1.getNodeName();
			if (!isElementExist(elements2, elementName)) {
				Element newElement = document2.createElement(elementName);
				newElement.setAttribute("PresentIn", "1");
				Element root = document2.getDocumentElement();
				root.appendChild(newElement);

				// elements2.get(0).getParentNode().appendChild(newElement);
			}
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(outputStream);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		DOMSource source = new DOMSource(document2);
		transformer.transform(source, result);

		return result;

	}

	private List<Element> getAllElements(Document doc) {
		NodeList nodeList = doc.getElementsByTagName("*");
		List<Element> elements = new ArrayList<>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elements.add((Element) node);
			}
		}
		return elements;
	}

	private boolean isElementExist(List<Element> elements, String elementName) {
		for (Element element : elements) {
			if (element.getNodeName().equals(elementName)) {
				return true;
			}
		}
		return false;
	}
}

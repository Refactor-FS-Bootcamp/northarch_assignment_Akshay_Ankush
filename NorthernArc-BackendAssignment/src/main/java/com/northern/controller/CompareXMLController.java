package com.northern.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.northern.service.CompareXMLService;

@RestController
@RequestMapping("/xml")
public class CompareXMLController {

	@Autowired
	private CompareXMLService service;

	@PostMapping("/compare")
	private ResponseEntity<?> compareXML(@RequestParam("file1") MultipartFile xml1,
			@RequestParam("file2") MultipartFile xml2)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		StreamResult result = service.compareXML(xml1, xml2);

		ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();
		String xmlContent = baos.toString("UTF-8");

		return new ResponseEntity<String>(xmlContent, HttpStatus.OK);
	}
}

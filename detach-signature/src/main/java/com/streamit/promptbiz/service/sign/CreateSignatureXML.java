package com.streamit.promptbiz.service.sign;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CreateSignatureXML {
    
    public String xmlBytesToString(byte[] xmlSource) {
        return new String(xmlSource, StandardCharsets.UTF_8);
    }

    /**
     * Writeout the parsed document to an xml file in the path given as parameter.
     * @param xmlString to write out, in this case detach xml string
     * @param detachSignaturePath path to write out the xml file
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws TransformerException
     */
    public void xmlStringToDom(String xmlString, String detachSignaturePath) throws SAXException, ParserConfigurationException, IOException, TransformerException {
        // Parse the given input
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

        // Write the parsed document to an xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result =  new StreamResult(new File(detachSignaturePath));
        transformer.transform(source, result);
    }
}

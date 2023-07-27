package com.streamit.promptbiz.service.sign;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.streamit.promptbiz.exception.XmlDigitalSignatureException;

public class XmlDigitalSignature {

    private static final String SIGNATURE_NODE = "Signature";

    private static final String DIGEST_METHOD = "http://www.w3.org/2001/04/xmlenc#sha256";
    private static final String SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    static {
        System.setProperty("com.sun.org.apache.xml.internal.security.ignoreLineBreaks", "true");
    }

    /**
     * Generates byte[] of detached signature, can convert these byte[] to string and xml further.
     * @param file in any extension that need to signing
     * @param privateKey
     * @param certificate
     * @return
     * @throws XmlDigitalSignatureException
     * @throws IOException
     */
    public byte[] generateDetachedSignature(File file, PrivateKey privateKey, X509Certificate certificate)
            throws XmlDigitalSignatureException, IOException {
        return generateDetachedSignature(Files.readAllBytes(file.toPath()), file.getName(), privateKey, certificate);
    }

    /**
     * Generates byte[] of detached signature, can convert these byte[] to string and xml further.
     * @param data to signing
     * @param fileName to be reference uri in xml schema
     * @param privateKey 
     * @param certificate
     * @return byte array of detached signature
     * @throws XmlDigitalSignatureException
     */
    public byte[] generateDetachedSignature(byte[] data, String fileName, PrivateKey privateKey, X509Certificate certificate)
            throws XmlDigitalSignatureException {
        try {

            XMLSignatureFactory signFactory = XMLSignatureFactory.getInstance("DOM");
            Reference ref = signFactory.newReference(
                    fileName, signFactory.newDigestMethod(DIGEST_METHOD, null));

            SignedInfo signedInfo = signFactory.newSignedInfo(
                    signFactory
                            .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                                    (C14NMethodParameterSpec) null),
                    signFactory.newSignatureMethod(SIGNATURE_METHOD, null),
                    Collections.singletonList(ref));

            KeyInfoFactory keyInfoFactory = signFactory.getKeyInfoFactory();
            List<Object> x509Content = new ArrayList<>();
            List<Object> keyInfoList = new ArrayList<>();

            x509Content.add(certificate.getSubjectX500Principal().getName());
            x509Content.add(certificate);
            X509Data x509data = keyInfoFactory.newX509Data(x509Content);

            KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());

            keyInfoList.add(x509data);
            keyInfoList.add(keyValue);

            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList);
            XMLSignature signature = signFactory.newXMLSignature(signedInfo, keyInfo);

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);

            Document signatureDoc = docBuilderFactory.newDocumentBuilder().newDocument();
            DOMSignContext signContext = new DOMSignContext(privateKey, signatureDoc);

            signContext
                    .setURIDereferencer((uriReference, context) -> new OctetStreamData(new ByteArrayInputStream(data)));
            signature.sign(signContext);

            return transformDocument(signatureDoc);
        } catch (Exception ex) {
            throw new XmlDigitalSignatureException("Error occurred on signing document: ", ex);
        }
    }

    /**
     * Verify byte array of original data and detached signature. This original data must not be modified after the signature was generated, or the signature will be invalid.
     * @param data of original file.
     * @param detachSignature of original file in .xml format.
     * @param certificate will be the same certificate that was used to generate the signature.
     * @return true if the signature is valid, otherwise false.
     * @throws XmlDigitalSignatureException
     * @throws IOException
     */
    public boolean verifyDetachedSignature(File data, File detachSignature, X509Certificate certificate)
            throws XmlDigitalSignatureException, IOException {
        return verifyDetachedSignature(Files.readAllBytes(data.toPath()), Files.readAllBytes(detachSignature.toPath()), certificate);
    }

    /**
     * Verify byte array of original data and detached signature. This original data must not be modified after the signature was generated, or the signature will be invalid.
     * @param data of original file.
     * @param detachSignature of original file in .xml format.
     * @param certificate will be the same certificate that was used to generate the signature.
     * @return true if the signature is valid, otherwise false.
     * @throws XmlDigitalSignatureException
     */
    public boolean verifyDetachedSignature(byte[] data, byte[] detachSignature, X509Certificate certificate)
            throws XmlDigitalSignatureException {
        try {

            PublicKey publicKey = certificate.getPublicKey();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document signatureDocument = documentBuilder
                    .parse(new ByteArrayInputStream(detachSignature));

            NodeList nodeList = signatureDocument.getElementsByTagNameNS(XMLSignature.XMLNS, SIGNATURE_NODE);
            NodeList signatureNode = signatureDocument.getElementsByTagNameNS(XMLSignature.XMLNS, SIGNATURE_NODE);

            if (nodeList == null || nodeList.getLength() == 0) {
                signatureNode = signatureDocument.getDocumentElement().getElementsByTagName(SIGNATURE_NODE);
            }
            boolean isSigAvailable = Optional.ofNullable(signatureNode).map(it -> it.getLength() != 0)
                    .isPresent();

            if (!isSigAvailable) {
                throw new XmlDigitalSignatureException("Signature node not present");
            }

            XMLValidateContext valContext = new DOMValidateContext(publicKey, signatureNode.item(0));

            valContext.setURIDereferencer(
                    (uriReference, context) -> new OctetStreamData(new ByteArrayInputStream(data)));

            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = signatureFactory.unmarshalXMLSignature(valContext);

            return signature.validate(valContext);
        } catch (Exception ex) {
            throw new XmlDigitalSignatureException("Error occurred on verifying document: ", ex);
        }
    }

    private byte[] transformDocument(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);

        return writer.getBuffer().toString()
                .getBytes(StandardCharsets.UTF_8);
    }
}

package com.streamit.promptbiz.service.utils;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Utility class for handle PrivateKey and X509Certification, in real project we stored both in HSM, so please assumed that we already generated this
 */
public class KeyStoreUtil {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        // Prepare certificate information
        String subjectDN = "CN=Self-Signed Certificate";
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date validityStartDate = new Date();
        Date validityEndDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L); // 1 year from now

        // Create X509v3 certificate builder
        X500Name issuer = new X500Name(subjectDN);
        X500Name subject = new X500Name(subjectDN);
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, serialNumber, validityStartDate, validityEndDate, subject, keyPair.getPublic());

        // Self-sign the certificate using the private key
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certHolder);

        // Verify the generated certificate
        certificate.verify(keyPair.getPublic());

        return certificate;
    }

}

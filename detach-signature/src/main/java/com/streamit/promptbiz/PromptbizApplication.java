package com.streamit.promptbiz;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.streamit.promptbiz.service.FileService;
import com.streamit.promptbiz.service.sign.CreateSignatureXML;
import com.streamit.promptbiz.service.sign.XmlDigitalSignature;
import com.streamit.promptbiz.service.utils.KeyStoreUtil;

@SpringBootApplication
public class PromptbizApplication {

	public static void main(String[] args) throws Exception {
		// SpringApplication.run(PromptbizApplication.class, args);

		String pathToListFiles = "D:\\test\\PGP\\original";
		FileService fileService = new FileService();
		// list all .txt files in directory
		List<File> fileList = fileService.getAllFileWithTypeInDirectory(new File(pathToListFiles), ".txt");

		// only generate for dev envi. in uat/prd we usually stored these in kms/hsm
		KeyPair keyPair = KeyStoreUtil.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		X509Certificate cert = KeyStoreUtil.generateSelfSignedCertificate(keyPair);

		for (File file : fileList) {
			System.out.println("file name: " + file.getName());

			XmlDigitalSignature xmlSignService = new XmlDigitalSignature();
			byte[] signatureByte = xmlSignService.generateDetachedSignature(file, privateKey, cert);

			CreateSignatureXML gen = new CreateSignatureXML();

			String pathToDetachSignature = pathToListFiles + File.separator + file.getName().replace(".txt", "") + "-signature.xml";
			gen.xmlStringToDom(gen.xmlBytesToString(signatureByte), pathToDetachSignature);
			System.out.println("generate signature: " + pathToDetachSignature);

			boolean verifiedStatus = xmlSignService.verifyDetachedSignature(Files.readAllBytes(file.toPath()),
					Files.readAllBytes(new File(pathToDetachSignature).toPath()),
					cert);
			System.out.println("verify signature: " + verifiedStatus);
			System.out.println("--------------------");
		}

	}
}

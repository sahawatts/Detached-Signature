package com.streamit.promptbiz.service.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
public class HashGenerator {

  private final String HASH_ALGORITHM = "SHA256";

  public HashGenerator(){}

  public byte[] toByteArray(String filePath) throws IOException, NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

    byte[] messageBytes = Files.readAllBytes(Paths.get(filePath));
    byte[] messageHash = md.digest(messageBytes);

    return messageHash;
  }

  public String byteArrToHexString(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    // toUpperCase due to ITMX's requirement
    return hexString.toString().toUpperCase();
  }

  public String toBASE64(String hexadecimalHash) {
    // toUpperCase because ITMX's requirement
    return Base64.getEncoder().encodeToString(hexadecimalHash.getBytes());
  }
}

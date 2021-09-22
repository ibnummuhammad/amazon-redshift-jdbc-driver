package com.amazon.redshift.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Extensible hashing utility function to obfuscate passwords before network transmission.
 *
 */
public class ExtensibleDigest {
  private ExtensibleDigest() {
  }

  /**
   * Encodes user/password/salt information in the following way: SHA2(SHA2(password + user) + salt).
   *
   * @param clientNonce The client nonce.
   * @param password The connecting user's password.
   * @param salt salt sent by the server.
   * @param algoName Algorithm name such as "SHA-256" etc.
   * @param serverNonce random number generated by server 
   * @return A byte array of the digest.
   */
  public static byte[] encode(byte[] clientNonce, 
  														byte[] password, 
  														byte[] salt, 
  														String algoName,
  														byte[] serverNonce) {
    MessageDigest md;
    byte[] passDigest;

    try {
      md = MessageDigest.getInstance(algoName);
      md.update(password);
      md.update(salt);
      passDigest = md.digest();

      md = MessageDigest.getInstance(algoName);
      md.update(passDigest);
      md.update(serverNonce);
      md.update(clientNonce);
      passDigest = md.digest();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Unable to encode password with extensible hashing:" + algoName, e);
    }

    return passDigest;
  }
}

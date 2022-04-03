package encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**

* @author Ioanna Pashalidi Kozelj - nnp8llf
 *  *
 *  *This class contains methods that implement encryption, decryptioon and deals with the file storage
 */
public class EncryptedSharedFile implements Serializable {

	private static final long serialVersionUID = -6673024660015796987L;

	public class SharedFileContents {
		private byte[] contents;
		private String fileName;

		/**

* @param contents
 * 		 * @param fileName
		 */
		public SharedFileContents(byte[] contents, String fileName) {
			super();
			this.contents = contents;
			this.fileName = fileName;
		}

		/**
		 * @return file contents
		 */
		public byte[] getContents() {
			return contents;
		}

		/**

* @return name of the files
		 */
		public String getFileName() {
			return fileName;
		}

	}

	/**

* Load an EncryptedSharedFile from disk.
 * 	 * 
 * 	 * @param input the file to attempt to load from.
 * 	 * @return the EncryptedSharedFile instance.
 * 	 * @throws IOException in the event of IO errors or if the file doesn't contain
 * 	 *                     an EncryptedSharedFile.
	 */
	public static EncryptedSharedFile load(File input) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(input));
		try {
			Object obj = ois.readObject();
			if (obj instanceof EncryptedSharedFile)
				return (EncryptedSharedFile) obj;

		} catch (ClassNotFoundException e) {
			// No-op, exception will be handled later.
		} finally {
			ois.close();
		}

		throw new IOException("The supplied file could not be read or does not contain an EncryptedSharedFile.");
	}

	private byte[] encryptedContents;
	private byte[] encryptedOriginalName;
	private byte[] ivSpecBytes;
	private byte[] systemEncryptedKey;
	private Map<User, byte[]> userEncryptionKeys;

	/**

* Standard constructor requiring the original file data.
 * 	 * 
 * 	 * @param filename       the original filename.
 * 	 * @param fileContents   the contents of the file to be encrypted.
 * 	 * @param sharingParties the details of the users sharing this file.
 * 	 * @param systemKey      the system (internal) encryption key.
 * 	 * @throws GeneralSecurityException in the event of a cryptography error.
	 */
	public EncryptedSharedFile(String filename, byte[] fileContents, List<User> sharingParties, byte[] systemKey)
			throws GeneralSecurityException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom rnd = new SecureRandom();

		keyGen.init(256);
		SecretKey systemEncryptionKey = keyGen.generateKey();

		byte[] iv = new byte[16];
		rnd.nextBytes(iv);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		ivSpecBytes = iv;

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, systemEncryptionKey, ivSpec);
		encryptedOriginalName = cipher.doFinal(filename.getBytes());

		cipher.init(Cipher.ENCRYPT_MODE, systemEncryptionKey, ivSpec);
		encryptedContents = cipher.doFinal(fileContents);

		Cipher rsaCipher = Cipher.getInstance("RSA");
		
		userEncryptionKeys = new HashMap<User, byte[]>();

		for (User u : sharingParties) {
			X509EncodedKeySpec ks = new X509EncodedKeySpec(u.getPublicKey());
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey pub = kf.generatePublic(ks);

			rsaCipher.init(Cipher.ENCRYPT_MODE, pub, rnd);
			userEncryptionKeys.put(u, rsaCipher.doFinal(systemEncryptionKey.getEncoded()));
		}

		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey sysKey = kf.generatePublic(new X509EncodedKeySpec(systemKey, "RSA"));
		rsaCipher.init(Cipher.ENCRYPT_MODE, sysKey, rnd);
		systemEncryptedKey = rsaCipher.doFinal(systemEncryptionKey.getEncoded());
	}

	/**

* Decrypt this EncryptedSharedFile for a given user.
 * 	 * 
 * 	 * @param id        the ID of the user to decrypt for.
 * 	 * @param key       the private key of the user.
 * 	 * @param systemKey the system encryption key.
 * 	 * @return a decrypted SharedFileContents instance.
 * 	 * @throws GeneralSecurityException in the event of cryptographic error or if
 * 	 *                                  the user encryption key cannot be accepted.
	 */
	public SharedFileContents decryptForUser(String id, byte[] key, byte[] systemKey) throws GeneralSecurityException {
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey priv = kf.generatePrivate(ks);

		Cipher rsaCipher = Cipher.getInstance("RSA");
		SecureRandom rnd = new SecureRandom();

		ks = new PKCS8EncodedKeySpec(systemKey);
		PrivateKey sysPriv = kf.generatePrivate(ks);

		for (User u : userEncryptionKeys.keySet()) {
			if (id.equals(u.getId())) {
				rsaCipher.init(Cipher.DECRYPT_MODE, priv, rnd);
				byte[] userDec = rsaCipher.doFinal(userEncryptionKeys.get(u));

				rsaCipher.init(Cipher.DECRYPT_MODE, sysPriv, rnd);
				byte[] sysDec = rsaCipher.doFinal(systemEncryptedKey);

				if (Arrays.equals(userDec, sysDec)) {
					// Keys match between system key and user key.
					// Decrypt file.
					Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
					SecretKeySpec systemSymmKey = new SecretKeySpec(sysDec, "AES");
					IvParameterSpec spec = new IvParameterSpec(ivSpecBytes);
					
					cipher.init(Cipher.DECRYPT_MODE, systemSymmKey, spec);
					
					byte[] fileContents = cipher.doFinal(encryptedContents);

					cipher.init(Cipher.DECRYPT_MODE, systemSymmKey, spec);
					String name = new String(cipher.doFinal(encryptedOriginalName));
					return new SharedFileContents(fileContents, name);
				}
			}
		}

		throw new GeneralSecurityException("A matching user could not be found, or encryption keys do not match.");
	}

	/**

* Store this EncryptedSharedFile to disk.
 * 	 * 
 * 	 * @param outputDirectory the directory the file is to be written to.
 * 	 * @return the path name to the file as saved.
 * 	 * @throws IOException in the event of any errors storing the object.
	 */
	public String store(File outputDirectory) throws IOException {
		File output = File.createTempFile("efs", ".enc", outputDirectory);

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output));
		oos.writeObject(this);
		oos.flush();
		oos.close();

		return output.getCanonicalPath();
	}
}

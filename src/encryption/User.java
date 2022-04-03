package encryption;

import java.io.Serializable;
import java.util.Base64;
/**

* @author Ioanna PK - nnp18llf
 * 
 * This class contains getter methods for user id and public key
 *  *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 589277442761251888L;

	private String id;
	private byte[] publicKey;

	public User(byte[] publicKey, String id) {
		super();
		this.publicKey = publicKey;
		this.id = id;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return pubKey
	 */
	public byte[] getPublicKey() {
		return publicKey;
	}

	
	@Override
	public String toString() {
		return String.format("%s: %s", id, Base64.getEncoder().encodeToString(publicKey));
	}
}

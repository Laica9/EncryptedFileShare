package encryption;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAGenerator {

	public static void main(String[] args) throws Exception {
		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
		
		SecureRandom random = SecureRandom.getInstanceStrong();
		kg.initialize(4096, random);

		KeyPair pair = kg.generateKeyPair();
		File priv = new File("/Users/ioann/private.rsa");
		priv.createNewFile();
		File pub = new File("/Users/ioann/public.rsa");
		pub.createNewFile();
		
		Files.write(priv.toPath(), new PKCS8EncodedKeySpec(pair.getPrivate().getEncoded()).getEncoded());
		Files.write(pub.toPath(), new X509EncodedKeySpec(pair.getPublic().getEncoded()).getEncoded());
	}
}

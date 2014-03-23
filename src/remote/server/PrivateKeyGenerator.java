package remote.server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import remote.api.Packet;

public class PrivateKeyGenerator {
	public static void generate() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator
				.getInstance(Packet.SECURE_ALGORITHM_NAME);
		keyGen.initialize(Packet.SECURE_KEY_SIZE);

		KeyPair keys = keyGen.generateKeyPair();

		RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
		System.out.println("modulus:");
		System.out.println(privateKey.getModulus());
		System.out.println("private exponent:");
		System.out.println(privateKey.getPrivateExponent());
	}
}

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


public class Test {
	public static void main(String[] a){
		String msg= "admin:admin";
		String key = "12";
		String hash = hmacSha1(msg, key);
		System.out.println(hash);
	}
	
	public static String hmacSha1(String value, String key) {
		try {
			byte[] keyBytes = (byte[]) Hex.decodeHex(key.toCharArray());
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(keyBytes, mac.getAlgorithm());
			mac.init(spec);

			byte[] rawHmac = mac.doFinal(value.getBytes());

			return Hex.encodeHexString(rawHmac);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}

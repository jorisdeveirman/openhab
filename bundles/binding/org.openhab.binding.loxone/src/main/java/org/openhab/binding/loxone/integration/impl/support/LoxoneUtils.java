package org.openhab.binding.loxone.integration.impl.support;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class LoxoneUtils {
	/**
	 * Calculates the hmacSha1 hash that is compatible with Loxone authentication for  
	 * @param value for Loxone, the concat of username and password
	 * @param key for loxone, the value of /dev/sys/getkey
	 * @return the hash, HEX formatted.
	 */
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

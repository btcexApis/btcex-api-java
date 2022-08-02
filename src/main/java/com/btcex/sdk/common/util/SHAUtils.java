package com.btcex.sdk.common.util;

import org.apache.commons.codec.binary.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * @author lq
 * @version 创建时间 2018年9月20日 &下午7:01:09
 *          <H1>类的说明</H1>: <br/>
 *
 */
public class SHAUtils {


	/**
	 * SHA1
	 * @author blt @date: 2021-03-05
	 */
	public static String SHA1(String inStr) throws Exception {
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance("SHA");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}

		byte[] byteArray = inStr.getBytes("UTF-8");
		byte[] md5Bytes = sha.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * sha256
	 * 
	 * @param a
	 * @return
	 */
	public static String SHA256(final String a) {
		return encodeBySHA(a, "SHA-256");
	}

	/**
	 * sha512
	 * 
	 * @param a
	 * @return
	 */
	public static String SHA512(final String a) {
		return encodeBySHA(a, "SHA-512");
	}


	private static String encodeBySHA(final String a, final String type) {

		String strResult = null;


		if (a != null && a.length() > 0) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance(type);

				messageDigest.update(a.getBytes());

				byte byteBuffer[] = messageDigest.digest();

				StringBuffer strHexString = new StringBuffer();

				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return strResult;
	}



	public static String sha256_HMAC(String message, String secret) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}


	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}


	public static Boolean checkSign(Object obj,String secret) throws Exception {
		Class clazz = obj.getClass();
		TreeMap<String, Object> treeMap = new TreeMap<>();
		Field[] fields = clazz.getDeclaredFields();
		String sign = null;
		for(int i = 0 ; i < fields.length ; i++) {
			Field field = fields[i];
			String name = field.getName();
			field.setAccessible(true);
			if(StringUtils.equals(name, "sign")) {
				sign = String.valueOf(field.get(obj));
				continue;
			}
			treeMap.put(name, field.get(obj));
		}
		String signResult = sign(secret, treeMap);
        if(!StringUtils.equals(signResult, sign)) {
        	return false;
        }
        return true;
	}

	public static String sign(String secret, TreeMap<String, Object> treeMap) {
		StringBuilder sb = new StringBuilder();
        treeMap.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
        String toSignString = sb.toString().substring(0, sb.length() - 1);
        String signResult = SHAUtils.sha256_HMAC(toSignString,secret);
		return signResult;
	}


}

package com.wordplay.unit.starter.util.base;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * AES 对称加密算法
 *
 * @author zhuangpf
 */
public class AESUtil {

	static {
		String errorString = "Failed manually overriding key-length permissions.";

		int newMaxKeyLength;
		try {
			if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
				Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
				Constructor con = c.getDeclaredConstructor();
				con.setAccessible(true);
				Object allPermissionCollection = con.newInstance();
				Field f = c.getDeclaredField("all_allowed");
				f.setAccessible(true);
				f.setBoolean(allPermissionCollection, true);
				c = Class.forName("javax.crypto.CryptoPermissions");
				con = c.getDeclaredConstructor();
				con.setAccessible(true);
				Object allPermissions = con.newInstance();
				f = c.getDeclaredField("perms");
				f.setAccessible(true);
				((Map) f.get(allPermissions)).put("*", allPermissionCollection);
				c = Class.forName("javax.crypto.JceSecurityManager");
				f = c.getDeclaredField("defaultPolicy");
				f.setAccessible(true);
				Field mf = Field.class.getDeclaredField("modifiers");
				mf.setAccessible(true);
				mf.setInt(f, f.getModifiers() & -17);
				f.set((Object) null, allPermissions);
				newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
			}
		} catch (Exception var) {
			throw new RuntimeException(errorString, var);
		}

		if (newMaxKeyLength < 256) {
			throw new RuntimeException(errorString);
		}
	}

	public static String base64Encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public static byte[] base64Decode(String base64Code) {
		return Base64.decodeBase64(base64Code);
	}

	public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(1, new SecretKeySpec(encryptKey.getBytes(), "AES"));
		return cipher.doFinal(content.getBytes("utf-8"));
	}

	public static String aesEncrypt(String content, String encryptKey) throws Exception {
		return base64Encode(aesEncryptToBytes(content, encryptKey));
	}

	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(2, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes);
	}

	public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
		return aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
	}

	public static byte[] generateDesKey(int length) throws Exception {
		KeyGenerator kgen = null;
		kgen = KeyGenerator.getInstance("AES");
		kgen.init(length);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}

}

package com.sunz.framework.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * 加密帮助类
 * 	为提升类识别度，所涉及结构和接口均定义为内部类/接口
 * 	另：【加密器】 和 【解密器】输入输出相同，区别定义仅为在语义上警示调用方
 *	@author Xingzhe
 */
public class EncryptHelper {

	/**
	 * 【加】密器
	 * 
	 * @remark 注意分公钥和私钥
	 * 
	 * @author Xingzhe
	 *
	 */
	@FunctionalInterface
	public static interface IEncrypter {
		/**
		 * 对数据【加】密
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 */
		byte[] encrypt(byte[] data) throws Exception;
	}

	/**
	 * 【解】密器
	 * 
	 * @remark 注意分公钥和私钥，公钥解密由私钥加密的数据，私钥解密由公钥加密的数据
	 * 
	 * @author Xingzhe
	 *
	 */
	@FunctionalInterface
	public static interface IDecrypter {
		/**
		 * 对数据【解】密
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 */
		byte[] decrypt(byte[] data) throws Exception;
	}

	/**
	 * 输入和输出都是字符串的【加】密器
	 * 
	 * @author Xingzhe
	 *
	 */
	public static interface IStringEncrypter {
		/**
		 * 【加密】字符串
		 * 
		 * @param orig
		 * @return
		 */
		String encrypt(String orig);
	}

	/**
	 * 输入和输出都是字符串的【解】密器
	 * 
	 * @author Xingzhe
	 *
	 */
	public static interface IStringDecrypter {
		/**
		 * 对【已加密】的字符--即由IStringEncrypter加密的密文--进行解密
		 * 
		 * @param enryptedStr
		 * @return
		 */
		String decrypt(String enryptedStr);
	}

	/**
	 * 将IEncrypter（输入输出为byte[]类型）转为Base64型（保证字符串可以显示）的IStringEncrypter
	 * 
	 * @param encrypter
	 * @return
	 */
	public static IStringEncrypter toBase64Encrypter(IEncrypter encrypter) {
		return new IStringEncrypter() {
			@Override
			public String encrypt(String orig) {
				try {
					return Base64.getUrlEncoder().encodeToString(encrypter.encrypt(orig.getBytes("UTF-8")));
				} catch (Exception e) {
					throw new RuntimeException("加密出错", e);
				}
			}
		};
	}

	/**
	 * 将IDecrypter（输入输出为byte[]类型）转为Base64型（保证字符串可以显示）的IStringDecrypter
	 * 
	 * @param decrypter
	 * @return
	 */
	public static IStringDecrypter toBase64Decrypter(IDecrypter decrypter) {
		return new IStringDecrypter() {
			@Override
			public String decrypt(String enryptedBase64String) {
				try {
					return new String(decrypter.decrypt(Base64.getUrlDecoder().decode(enryptedBase64String)), "UTF-8");
				} catch (Exception e) {
					throw new RuntimeException("解密出错", e);
				}
			}
		};
	}

	/**
	 * 创建MD5加密器--MD5是单向加密的，无解密器
	 * 
	 * @return
	 */
	public static IEncrypter createMD5Encrypter() {
		return createMD5Encrypter(null);
	}
	
	/**
	 * 创建MD5加密器--MD5是单向加密的，无解密器
	 * 
	 * @param salt 
	 * @return
	 */
	public static IEncrypter createMD5Encrypter(String salt) {
		try {
			MessageDigest chiper = MessageDigest.getInstance("md5");
			byte[] bSalt=StringUtil.isEmpty(salt)?null:salt.getBytes("utf-8");
			return (input) -> {
				if(bSalt==null)
					return chiper.digest(input);
				
				byte[] real=new byte[bSalt.length+input.length];
				System.arraycopy(bSalt, 0, real, 0, 0);
				System.arraycopy(input, 0, real, bSalt.length, 0);
				return chiper.digest(real);
			};
		} catch (Exception e) {
			throw new RuntimeException("当前系统不支持md5", e);
		}
	}

	private final static String DES = "DES";

	private static Cipher createDESCipher(String key, int mode) throws Exception {
		DESKeySpec dks = new DESKeySpec(key.getBytes(Charset.forName("UTF-8")));
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(mode, SecretKeyFactory.getInstance(DES).generateSecret(dks), new SecureRandom());
		return cipher;
	}

	/**
	 * 创建DES加密器
	 * 
	 * @param key
	 * @return
	 */
	public static IEncrypter createDESEncrypter(String key) {
		try {
			Cipher cipher = createDESCipher(key, Cipher.ENCRYPT_MODE);
			return (input) -> cipher.doFinal(input);
		} catch (Exception e) {
			throw new RuntimeException("DES加密器创建出错：", e);
		}
	}

	/**
	 * 创建DES解密器
	 * 
	 * @param key
	 * @return
	 */
	public static IDecrypter createDESDecrypter(String key) {
		try {
			Cipher cipher = createDESCipher(key, Cipher.DECRYPT_MODE);
			return (input) -> cipher.doFinal(input);
		} catch (Exception e) {
			throw new RuntimeException("DES解密器创建出错：", e);
		}
	}
}
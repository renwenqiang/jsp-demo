package com.sunz.framework.util;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.sunz.framework.util.EncryptHelper.IDecrypter;
import com.sunz.framework.util.EncryptHelper.IEncrypter;

/**
 * 非对称加密算法RSA算法帮助类
 * 	使用步骤：
 * 		1.createKeyPair 创建密钥对，进行分发
 * 		2.createPrivateEncrypter/createPublicEncrypter，对目标数据加密码（然后发发）
 * 		3.createPublicDecrypter/createPrivateDecrypter，对2中分发的密文解密
 * 
 * 		其中2可以通过EncryptHelper.toBase64Encrypter转为EncryptHelper.IStringEncrypter直接操作字符串
 * 		　　3可以通过EncryptHelper.toBase64Decrypter转为EncryptHelper.IStringDecrypter直接操作2生成的字符串
 * 
 * 
 *	@author Xingzhe
 */
public class RSAEncryptHelper {
	/**
	 * 加解密 密钥对
	 * 
	 * @author Xingzhe
	 *
	 */
	public static class KeyPair {
		byte[] privateKey;
		byte[] publicKey;

		/**
		 * 私钥
		 * 
		 * @return
		 */
		public byte[] getPrivateKey() {
			return privateKey;
		}

		/**
		 * 公钥
		 * 
		 * @return
		 */
		public byte[] getPublicKey() {
			return publicKey;
		}

		public KeyPair(byte[] privateKey, byte[] publicKey) {
			this.privateKey = privateKey;
			this.publicKey = publicKey;
		}
	}
    private static final String KEY_ALGORITHM = "RSA";    
    private static final int Key_Size=512;
    /**
     * 创建 指定长度的密钥对
     * 
     * @param size 指定密钥长度，必须为64的倍数
     * @return
     */
    public static KeyPair createKeyPair(int size){
    	//实例化->初始化密钥生成器->生成密钥对
		try {
			KeyPairGenerator keyPairGenerator= KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGenerator.initialize(size);
			java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			return new KeyPair(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("指定的加密算法【"+KEY_ALGORITHM+"】不正确");
		}
    }
    
    /**
     * 使用默认参数创建密钥对
     * 
     * @return
     */
    public static KeyPair createKeyPair() {
    	return createKeyPair(Key_Size);
    }
    
    /**
     * 创建指定【私钥】的【加】密器
     *	用于加密数据给 public Decrypter（公钥） 持有方（@see createPublicDecrypter）
     * 
     * @param privateKey	私钥--通常来源于createKeyPair--调用方需要自行保存
     * @return
     * @throws Exception
     */
    public static IEncrypter createPrivateEncrypter(byte[] privateKey) throws Exception {
    	Cipher cipher=createPrivateCipher(privateKey, Cipher.ENCRYPT_MODE);
    	return (data)->cipher.doFinal(data);
    }
    
    /**
     * 创建指定【公钥】的【加】密器
     * 	用于加密数据给 private Decrypter（私钥） 持有方（@see createPrivateDecrypter）
     * 
     * @param publicKey		公钥--通常来源于createKeyPair--调用方需要自行保存
     * @return
     * @throws Exception
     */
    public static IEncrypter createPublicEncrypter(byte[] publicKey) throws Exception {
    	Cipher cipher=createPublicCipher(publicKey, Cipher.ENCRYPT_MODE);
    	return (data)->cipher.doFinal(data);
    }

    /**
     * 创建指定【私钥】的【解】密器
     * 	用于解密由 public Encrypter 方输入--即由公钥加密--的数据（@see createPublicEncrypter）
     * 
     * @param privateKey	私钥--通常来源于createKeyPair--调用方需要自行保存
     * @return
     * @throws Exception
     */
    public static IDecrypter createPrivateDecrypter(byte[] privateKey) throws Exception {
    	Cipher cipher=createPrivateCipher(privateKey, Cipher.DECRYPT_MODE);
    	return (data)->cipher.doFinal(data);
    }
    
    /**
     * 创建指定【公钥】的【解】密器
     * 	用于解密由 private Encrypter 方输入--即由私钥加密--的数据（@see createPublicEncrypter）
     * 
     * @param publicKey		公钥--通常来源于createKeyPair--调用方需要自行保存
     * @return
     * @throws Exception
     */
    public static IDecrypter createPublicDecrypter(byte[] publicKey) throws Exception {
    	Cipher cipher=createPublicCipher(publicKey, Cipher.DECRYPT_MODE);
    	return (data)->cipher.doFinal(data);
    }

    private static Cipher createPrivateCipher(byte[] privateKey,int mode) throws Exception{
    	Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(mode, KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey)));
        return cipher;
    }
    private static Cipher createPublicCipher(byte[] publicKey,int mode) throws Exception{
    	 Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
         cipher.init(mode, KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey)));
         return cipher;
    }
}

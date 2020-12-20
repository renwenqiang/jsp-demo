package com.sunz.framework.system.login.unified;

import java.util.Base64;

import com.sunz.framework.util.EncryptHelper;
import com.sunz.framework.util.RSAEncryptHelper;

/**
 * 票据解析器，用于将认证票据串解析为Ticket对象
 * 
 * @author Xingzhe
 *
 */
public class TicketParser {
	
	private EncryptHelper.IStringDecrypter decrypter;
	
	/**
	 * 将认证票据串解析为Ticket对象
	 * @param enryptedTicket
	 * @return
	 */
	public Ticket parse(String enryptedTicket) {
		String realString;
		synchronized (decrypter) {	// 加解密过程线程不安全
			realString= decrypter.decrypt(enryptedTicket);
		}
		return new Ticket(Long.parseLong(realString.substring(0, 13)), realString.substring(13));
	}
	
	/**
	 * 从二进制密钥构建
	 * 
	 * @param publicKey
	 * @throws Exception
	 */
	public TicketParser(byte[] publicKey) throws Exception {
		decrypter=EncryptHelper.toBase64Decrypter(RSAEncryptHelper.createPublicDecrypter(publicKey));
	}
	
	/**
	 * 从base64字符串密钥构建
	 * 
	 * @param base64PublicKey
	 * @throws Exception
	 */
	public TicketParser(String base64PublicKey) throws Exception {
		decrypter=EncryptHelper.toBase64Decrypter(RSAEncryptHelper.createPublicDecrypter(Base64.getDecoder().decode(base64PublicKey)));
	}
}

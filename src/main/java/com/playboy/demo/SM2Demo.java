package com.playboy.demo;

import com.playboy.sm.SM2Utils;
import com.playboy.sm.SM4Utils;
import com.playboy.sm.util.Util;

import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author firmboy
 *
 */
public class SM2Demo {

	public static void main(String[] args) throws IllegalArgumentException, IOException {
		 sm2GetKey(); //获取sm2公钥和密钥
		//sm4GetKey(); // 获取sm4的密钥
		 //sm2Test();
		 //sm4Test();
		// test();
	}

	/**
	 * 通过这个方法，可以测试获取sm2加密算法的公钥和私钥
	 */
	public static void sm2GetKey() {
		Map keyPair = SM2Utils.generateKeyPair();

		System.err.println("公钥pub:" + keyPair.get(SM2Utils.PUBLIC_KEY).toString());
		System.err.println("私钥pri:" + keyPair.get(SM2Utils.PRIVATE_KEY).toString());
	}

	public static void sm4GetKey() {
		String key = SM4Utils.generateKey();

		System.err.println("sm4密钥：" + key);
	}

	// 对sm2加密算法的测试
	public static void sm2Test() throws IllegalArgumentException, IOException {
		// 公钥和密钥是通过上面的sm2GetKey获取的
		String publicKey = "0430B8FE9F86E895E03774B79310947BD733BBA0408D92AC59CC2464A91D7F02AB7558CFF28DC233E422EB67630FD4439968FAB9338BC2CCFA75663215C2D91EC0";
		String privateKey = "37871BFB758814FB5F633F9B2CDA7F8B53C1C9409FBDA986CDEE05CE96144001";

		String plainText = "hello world"; // 要加密的密文

		System.err.println("加密：");
		String cipherText = SM2Utils.encrypt(Util.hexToByte(publicKey), plainText.getBytes());
		System.out.println("密文：" + cipherText);

		System.err.println("解密：");
		plainText = new String(SM2Utils.decrypt(Util.hexToByte(privateKey), Util.hexToByte(cipherText)));
		System.out.println("明文：" + plainText);

	}

	// 对sm4加密算法的测试
	public static void sm4Test() {
		String plainText = "hello world"; //要加密的密文
		String key = "31ec905e69ca4855";	//sm4密钥

		SM4Utils sm4 = new SM4Utils();
		sm4.secretKey = key;

		System.out.println("加密");
		String cipherText = sm4.encryptData_ECB(plainText);
		System.err.println("加密结果："+cipherText);
		
		System.out.println("解密");
		String result = sm4.decryptData_ECB(cipherText);
		System.err.println("解密结果："+result);

	}
}

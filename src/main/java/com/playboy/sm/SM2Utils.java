package com.playboy.sm;

import com.bouncycastle.asn1.*;
import com.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.bouncycastle.math.ec.ECPoint;
import com.bouncycastle.util.encoders.Base64;
import com.playboy.sm.util.Cipher;
import com.playboy.sm.util.SM2;
import com.playboy.sm.util.Util;
import com.playboy.sm.util.SM2Result;
import com.playboy.sm.util.SM3Digest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SM2Utils {
	
	public static String PUBLIC_KEY = "publicKey"; //公钥
	
	public static String PRIVATE_KEY = "privateKey"; //私钥
	
	// 生成随机秘钥对
	public static Map generateKeyPair() {
		SM2 sm2 = SM2.Instance();
		AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
		BigInteger privateKey = ecpriv.getD();
		ECPoint publicKey = ecpub.getQ();

		//System.out.println("公钥: " + Util.byteToHex(publicKey.getEncoded()));
		//System.out.println("私钥: " + Util.byteToHex(privateKey.toByteArray()));
		
		Map map = new HashMap();
		map.put(PUBLIC_KEY, Util.byteToHex(publicKey.getEncoded()));
		map.put(PRIVATE_KEY, Util.byteToHex(privateKey.toByteArray()));
		return map;
	}

	//简单数据加密
	public static String encrypt(String publicKey, String source) throws IOException {
		return encrypt(Util.hexToByte(publicKey), source.getBytes());
	}
	
	// 数据加密
	public static String encrypt(byte[] publicKey, byte[] data) throws IOException {
		if (publicKey == null || publicKey.length == 0) {
			return null;
		}

		if (data == null || data.length == 0) {
			return null;
		}

		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);

		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);

		// System.out.println("C1 " + Util.byteToHex(c1.getEncoded()));
		// System.out.println("C2 " + Util.byteToHex(source));
		// System.out.println("C3 " + Util.byteToHex(c3));
		// C1 C2 C3拼装成加密字串
		return Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3);

	}

	// 数据解密
	public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException {
		if (privateKey == null || privateKey.length == 0) {
			return null;
		}

		if (encryptedData == null || encryptedData.length == 0) {
			return null;
		}
		// 加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
		String data = Util.byteToHex(encryptedData);
		/***
		 * 分解加密字串 （C1 = C1标志位2位 + C1实体部分128位 = 130） （C3 = C3实体部分64位 = 64） （C2 =
		 * encryptedData.length * 2 - C1长度 - C2长度）
		 */
		byte[] c1Bytes = Util.hexToByte(data.substring(0, 130));
		int c2Len = encryptedData.length - 97;
		byte[] c2 = Util.hexToByte(data.substring(130, 130 + 2 * c2Len));
		byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(1, privateKey);

		// 通过C1实体字节来生成ECPoint
		ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
		Cipher cipher = new Cipher();
		cipher.Init_dec(userD, c1);
		cipher.Decrypt(c2);
		cipher.Dofinal(c3);

		// 返回解密结果
		return c2;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(args[0]);
		System.out.println(args[1]);
		// 生成密钥对
		generateKeyPair();

		String plainText = "hello world";
		byte[] sourceData = plainText.getBytes();

		// 下面的秘钥可以使用generateKeyPair()生成的秘钥内容
		// 国密规范正式私钥
		// String prik =
		// "3690655E33D5EA3D9A4AE1A1ADD766FDEA045CDEAA43A9206FB8C430CEFE0D94";
		String prik = "009A196F3E81205F8F57497FA4DDAF9216DC9276F5433310E023CA82B3FC8ACA47";
		// 国密规范正式公钥
		// String pubk =
		// "04F6E0C3345AE42B51E06BF50B98834988D54EBC7460FE135A48171BC0629EAE205EEDE253A530608178A98F1E19BB737302813BA39ED3FA3C51639D7A20C7391A";
		String pubk = "045F0DD7D84F4F22B886D80E443D5228335AD7D4263BF6C92E5E2A527059626E6B43003EAE1F2E4FC395724C06DEBB81E3FDB26E3A43C0831073A92008534E5FE1";

		System.out.println("加密: ");
		long start1 = System.currentTimeMillis();
		String cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
		long end1 = System.currentTimeMillis();
		System.err.println("加密时间：" + (end1 - start1));
		 System.out.println(cipherText);
		System.out.println("解密: ");
		long start2 = System.currentTimeMillis();
		plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
		long end2 = System.currentTimeMillis();
		System.err.println("解密时间：" + (end2 - start2));
		 System.out.println(plainText);

		String userId = "ALICE123@YAHOO.COM";
		System.out.println("签名: ");
		String prikS = new String(Base64.encode(Util.hexToByte(prik)));
		byte[] c = SM2Utils.sign(userId.getBytes(), Base64.decode(prikS.getBytes()), sourceData);
		System.out.println("sign: " + Util.getHexString(c));
		
		
		System.out.println("验签: ");
		String pubkS = new String(Base64.encode(Util.hexToByte(pubk)));
		boolean vs = SM2Utils.verifySign(userId.getBytes(), Base64.decode(pubkS.getBytes()), sourceData, c);
		System.out.println("验签结果: " + vs);

	}

	public static byte[] sign(byte[] userId, byte[] privateKey, byte[] sourceData) throws IOException {
		if (privateKey == null || privateKey.length == 0) {
			return null;
		}

		if (sourceData == null || sourceData.length == 0) {
			return null;
		}

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(privateKey).abs();
		System.out.println("userD: " + userD.toString(16));
		System.out.println("");

		ECPoint userKey = sm2.ecc_point_g.multiply(userD);
		System.out.println("椭圆曲线点X: " + userKey.getX().toBigInteger().toString(16));
		System.out.println("椭圆曲线点Y: " + userKey.getY().toBigInteger().toString(16));
		System.out.println("");

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		System.out.println("SM3摘要Z: " + Util.getHexString(z));
		System.out.println("");

		System.out.println("M: " + Util.getHexString(sourceData));
		System.out.println("");

		sm3.update(z, 0, z.length);
		sm3.update(sourceData, 0, sourceData.length);
		byte[] md = new byte[32];
		sm3.doFinal(md, 0);

		System.out.println("SM3摘要值: " + Util.getHexString(md));
		System.out.println("");

		SM2Result sm2Result = new SM2Result();
		sm2.sm2Sign(md, userD, userKey, sm2Result);
		System.out.println("r: " + sm2Result.r.toString(16));
		System.out.println("s: " + sm2Result.s.toString(16));
		System.out.println("");

		DERInteger d_r = new DERInteger(sm2Result.r);
		DERInteger d_s = new DERInteger(sm2Result.s);
		ASN1EncodableVector v2 = new ASN1EncodableVector();
		v2.add(d_r);
		v2.add(d_s);
		DERObject sign = new DERSequence(v2);
		byte[] signdata = sign.getDEREncoded();
		return signdata;
	}

	
	public static boolean verifySign(byte[] userId, byte[] publicKey, byte[] sourceData, byte[] signData)
			throws IOException {
		if (publicKey == null || publicKey.length == 0) {
			return false;
		}

		if (sourceData == null || sourceData.length == 0) {
			return false;
		}

		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		sm3.update(z, 0, z.length);
		sm3.update(sourceData, 0, sourceData.length);
		byte[] md = new byte[32];
		sm3.doFinal(md, 0);
		System.out.println("SM3摘要值: " + Util.getHexString(md));
		System.out.println("");

		ByteArrayInputStream bis = new ByteArrayInputStream(signData);
		ASN1InputStream dis = new ASN1InputStream(bis);
		DERObject derObj = dis.readObject();
		Enumeration e = ((ASN1Sequence) derObj).getObjects();
		BigInteger r = ((DERInteger) e.nextElement()).getValue();
		BigInteger s = ((DERInteger) e.nextElement()).getValue();
		SM2Result sm2Result = new SM2Result();
		sm2Result.r = r;
		sm2Result.s = s;
		System.out.println("r: " + sm2Result.r.toString(16));
		System.out.println("s: " + sm2Result.s.toString(16));
		System.out.println("");

		sm2.sm2Verify(md, userKey, sm2Result.r, sm2Result.s, sm2Result);
		return sm2Result.r.equals(sm2Result.R);
	}
}
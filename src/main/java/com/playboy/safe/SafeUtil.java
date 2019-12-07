package com.playboy.safe;


import com.playboy.safe.util.Base64Utils;
import com.playboy.safe.util.RSAUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.playboy.safe.util.XMLUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

public class SafeUtil {
  public static  Map getkeys(){
	    Map keyMap = new HashMap();
		try {
			Map keyObjectMap = RSAUtils.genKeyPair();
			String publicKey = RSAUtils.getPublicKey(keyObjectMap);
			String privateKey = RSAUtils.getPrivateKey(keyObjectMap);
			keyMap.put("publicKey", publicKey);
			keyMap.put("privateKey", privateKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyMap;
  }
  /**
   *签名
 * @throws Exception 
   */
  
  public static String sign(String head,String xml,String privateKey,String encoding) throws Exception{
		 xml = xml.trim().replaceAll("\r\n","").replaceAll("\n", "");//签名前要把字符串里的换行符和末尾的空格去掉，要不然验证不通过
	  	 String sign = RSAUtils.sign(xml.getBytes(encoding), privateKey,encoding);
		 Document document = XMLUtils.fromXML(xml, encoding);
		 Element msgText = document.getRootElement();
		 XMLUtils.appendChild(msgText, "signature", sign);
		 return XMLUtils.asXml(head,msgText, encoding);
  }
  /**
   * 签名验证
   * @param xml
   * @param publicKey
   * @return
   * @throws Exception
   */
  public static boolean verify(String head,String xml, String  publicKey,String encoding) throws Exception{
      Map msg = XMLUtils.xmlTOMap(xml);
      String signature = (String) msg.get("signature");
      Document document = DocumentHelper.parseText(xml);
      Element msgText = document.getRootElement();
      Element caInfoElement = msgText.element("signature");
      msgText.remove(caInfoElement);
      String outXml = XMLUtils.asXml(head,msgText, encoding);
      boolean a = RSAUtils.verify(outXml.getBytes(encoding), publicKey, signature,encoding);
      return a;
  }
  /**
   * 私钥加密
 * @throws Exception 
   */
  public static String encryptByPrivateKey(String xml, String privateKey,String encoding) throws Exception{
	byte[] encodedData = RSAUtils.encryptByPrivateKey(xml.getBytes(encoding),privateKey,encoding);
	return  BASE64encode(encodedData);
  }
  /**
   * 私钥加密 不带参数
 * @throws Exception 
   */
  public static String encryptByPrivateKey(String xml, String privateKey) throws Exception {
		    byte[] encodedData = RSAUtils.encryptByPrivateKey(xml.getBytes(), privateKey);
		    return BASE64encode(encodedData);
 }
  /**
   * 公钥解密
   * @param data
   * @param privateKey
   * @return
   * @throws Exception
   */
    public static String decryptByPublicKey(String data,String publicKey,String encoding) throws Exception{
  	  byte[] encryptedData = BASE64decode(data);
  	  byte[] decryptData = RSAUtils.decryptByPublicKey(encryptedData, publicKey,encoding);
  	  return new String(decryptData, encoding);
    }

  
  /**
   * 公钥加密
 * @throws Exception 
   */
  public static String encryptByPublicKey(String xml, String publickey,String encoding) throws Exception{
	byte[] encodedData = RSAUtils.encryptByPublicKey(xml.getBytes(encoding),publickey,encoding);
	return  BASE64encode(encodedData);
  }
  /**
   * 公钥加密 不带编码
 * @throws Exception 
   */
  public static String encryptByPublicKey(String xml, String publickey)
		    throws Exception
	{
		    byte[] encodedData = RSAUtils.encryptByPublicKey(xml.getBytes(), publickey);
		    return BASE64encode(encodedData);
      }
/**
 * 私钥解密
 * @param data
 * @param privateKey
 * @return
 * @throws Exception
 */
  public static String decryptByPrivateKey(String data,String privateKey,String encoding) throws Exception{
	  byte[] encryptedData = BASE64decode(data);
	  byte[] decryptData = RSAUtils.decryptByPrivateKey(encryptedData, privateKey,encoding);
	return new String(decryptData, encoding);
  }


public static String BASE64encode(byte[] data) {
  BASE64Encoder encoder = new BASE64Encoder();
  return encoder.encode(data);
}

public static byte[] BASE64decode(String data) {
  BASE64Decoder decoder = new BASE64Decoder();
  byte[] r = null;
  try {
    r = decoder.decodeBuffer(data);
  } catch (Exception e) {
    e.printStackTrace();
  }
  return r;
}
/**
 * base64转码
 * @param data
 * @param encoding
 * @return
 * @throws Exception
 */
public static String encode(String data,String encoding) throws Exception{
	String param = Base64Utils.encode(data.getBytes(encoding));//base64转码
	return param;
}
/**
 * base64解码
 * @param data
 * @param encoding
 * @return
 * @throws Exception
 */
public static String decode(String data,String encoding) throws Exception{
	data = new String(Base64Utils.decode(data,encoding), encoding);
	return data;
}

}

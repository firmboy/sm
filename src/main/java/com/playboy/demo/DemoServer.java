package com.playboy.demo;

import com.playboy.safe.SafeUtil;
import com.playboy.sm.SM2Utils;
import com.playboy.sm.SM4Utils;
import com.playboy.sm.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author firmboy
 * @Description 本demo是针对利用sm2和sm4两种加密算法对需要进行大报文交换的webservice的实例(这是测试的服务端)
 */
public class DemoServer {
	
	//对方私钥
	static String privateKey = "37871BFB758814FB5F633F9B2CDA7F8B53C1C9409FBDA986CDEE05CE96144001";

	//本方公钥
	static String publicKey = "04EDA367BF0E95DE06BA7B8A366019813830C1C4EF7B18EFD01E478D41FA2F0283726C61C01513CE2D3438209FB17993D39FC2F6F84C4A4DE58F8564699306D718";
	
	//模拟数据库存储SID和sm4Key
	private Map db = new HashMap();//用来临时存放SID和sm4的密钥，实际应用建议存储在数据库中
	
	//模拟最终发送
	private static String resultMsg = "hello World"; //最终需要返回的报文
	
	/**
	 * @Description 用来模拟服务端第一次交换sm4密钥的方法
	 * @return
	 * @throws Exception 
	 */
	public String getSm4Key(String xml) throws Exception{
		//Base64解码
		String decode = SafeUtil.decode(xml, "utf-8"); 
		//sm2解密
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(privateKey), Util.hexToByte(decode)));//sm2解密
		//解析报文
		String SID = DemoXml.parseXml(plainText); //获取发送来的SID
		System.out.println("服务端解析到的SID:"+SID);
		//生成sm4密钥
		String sm4Key = SM4Utils.generateKey();
		System.out.println("服务端返回的sm4Key:"+sm4Key);
		//保存SID和sm4Key
		db.put(SID, sm4Key);
		
		//解析报文
		String resultXml = DemoXml.getXML(sm4Key);
		//sm2加密
		String cipherText = SM2Utils.encrypt(Util.hexToByte(publicKey), resultXml.getBytes());
		//Base64编码
		String encode = SafeUtil.encode(cipherText, "utf-8"); 
		
		return encode;
	}
	
	/**
	 * @Description 用来模拟服务端第二次发送大报文
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public String getMsg(String xml) throws Exception{
		//Base64解码
		String decode = SafeUtil.decode(xml, "utf-8"); 
		//先获取参数中的SID
		String[] split = decode.split("&");
		String xmlMsg = split[0];
		String SID = split[1];
		System.out.println("服务端解析到的SID:"+SID);
		//通过SID获取sm4Key
		String sm4Key = db.get(SID).toString();
		System.out.println("服务端通过SID查到的sm4Key:"+sm4Key);
		//sm4解密
		String plainText = SM4Utils.decryptData_ECB(xmlMsg, sm4Key);
		//解析报文
		String msg = DemoXml.parseXml(plainText); 
		System.out.println("服务端解析到的客户端发送的参数:"+msg);
		
		//组织报文
		String resultXml = DemoXml.getXML(resultMsg);
		//sm4加密
		String cipherText = SM4Utils.encryptData_ECB(resultXml, sm4Key);
		//Base64编码
		String encode = SafeUtil.encode(cipherText, "utf-8"); 
		
		return encode;
	}
	
	
}

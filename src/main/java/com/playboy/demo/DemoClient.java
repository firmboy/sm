package com.playboy.demo;


import com.playboy.safe.SafeUtil;
import com.playboy.sm.SM2Utils;
import com.playboy.sm.SM4Utils;
import com.playboy.sm.util.Util;

import java.util.UUID;

/**
 * 
 * @author firmboy
 * @Description 本demo是针对利用sm2和sm4两种加密算法对需要进行大报文交换的webservice的实例 (这是测试的客户端)
 */
// 写在前面：两种加密结合使用的思路，每一次的数据交换需要两次webService的交互实现，第一次利用sm2加密交换sm4的密钥，
// 第二次利用sm4的密钥交换加密的大报文，sm4的密钥在使用完一次后立即废除
// 1.双方在线下交换好sm2的公钥和私钥
// 2.客户端生成一个SID，利用sm2的私钥加密，发送给服务端（第一次）
// 3.服务端接收到报文后利用线下交换好的sm2的公钥解密报文，生成sm4的密钥，并将sm4的密钥和SID存储，因为下一次的解密需要这个SID对应的sm4密钥来解密
// 4.服务端利用sm2的公钥对sm4的密钥加密，然后返回给客户端
// 5.客户段收到客户段的响应后将报文用sm2的私钥解密，获取到sm4的密钥（第一次交换结束，获取到加密大报文的sm4密钥）
// 6.客户段利用获取到的sm4密钥加密报文，然后将SID，加密后的报文，用户名，密码发送给服务端（第二次）
// 7.服务段接受到报文后，根据SID查找到对应的sm4的密钥，
// 8，根据查找到的sm4密钥，解密接受到的报文，然后实现业务逻辑，并且组织返回的报文，然后用sm4的密钥加密，返回给客户端
// 9.客户段接受到服务端返回的报文，利用sm4的密钥解密，然后处理业务逻辑
public class DemoClient {

	//本方公钥
	static String publicKey = "0430B8FE9F86E895E03774B79310947BD733BBA0408D92AC59CC2464A91D7F02AB7558CFF28DC233E422EB67630FD4439968FAB9338BC2CCFA75663215C2D91EC0";
	
	//对方私钥
	static String privateKey = "00BADED67C7390F168574EC9190D8BE531775E87D82DC89A38E2581BAD5D932498";
	
	static String SID;
	
	public static void main(String[] args) throws Exception {
		//创建一个模拟的服务端对象
		DemoServer demoServer = new DemoServer();
		//模拟第一次发送请求获取sm4的密钥
		String sm4Key = getSm4Key(demoServer);
		//模拟第二次发送请求获取大报文
		String msg = getMsg(sm4Key,demoServer);
	}

	public static String getSm4Key(DemoServer demoServer) throws Exception {
		
		//生成一个SID，随机数
		SID = UUID.randomUUID().toString().replaceAll("-", "");
		System.err.println("客户端生成的SID:"+SID);
		//组织发送的报文
		String xml = DemoXml.getXML(SID);
		//String xml = "<Root><Head><MsgType>SR01</MsgType><MsgId>A001010011112017111600000173</MsgId><Src>RS</Src><SrcName>人社</SrcName><SrcZone>1300</SrcZone><SrcUserName>test</SrcUserName><Dst>CZ</Dst><DstName>财政</DstName><DstZone>1300</DstZone><MsgTime>2019-12-07 17:01:22</MsgTime><Remark></Remark></Head><Body><ID>A001010011112017111600000173</ID><BatchNo>SR01_001</BatchNo><FileId>02f903ab4f0e41d680cf664ac04fe535</FileId><InsTypeCode>1101</InsTypeCode><DistCode>1300</DistCode><FilePath>/root/SR01_1101_623443_20190621_1255.CSV</FilePath></Body></Root>";
		//sm2加密
		String cipherText = SM2Utils.encrypt(Util.hexToByte(publicKey), xml.getBytes());
		//Base64编码
		String encode = SafeUtil.encode(cipherText, "utf-8");
		System.out.println(encode);
		
		//发送webservice
		String sm4KeyXML = demoServer.getSm4Key(encode); 
		//Base64解码
		String decode = SafeUtil.decode(sm4KeyXML, "utf-8"); 
		//sm2解密
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(privateKey), Util.hexToByte(decode)));//sm2解密
		//解析报文
		String sm4Key = DemoXml.parseXml(plainText); //获取发送来的SID
		System.err.println("客户端解析服务端返回的sm4Key:"+sm4Key);
		
		return sm4Key;
	}

	public static String getMsg(String sm4Key,DemoServer demoServer) throws Exception{
		//组织报文
		String xml = DemoXml.getXML("getMsg");
		//sm4加密
		String cipherText = SM4Utils.encryptData_ECB(xml, sm4Key);
		//Base64编码
		String encode = SafeUtil.encode(cipherText+"&"+SID, "utf-8"); 
		
		//发送webservice
		String resultXml = demoServer.getMsg(encode);
		//Base64解码
		String decode = SafeUtil.decode(resultXml, "utf-8"); //Base64解码
		//sm4解密
		String plainText = SM4Utils.decryptData_ECB(decode, sm4Key);
		//解析报文
		String msg = DemoXml.parseXml(plainText); 
		System.err.println("客户端解析到服务端返回的密文:"+msg);
		return msg;
	}
	
}

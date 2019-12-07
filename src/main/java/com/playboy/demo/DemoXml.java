package com.playboy.demo;

public class DemoXml {
	
	/**
	 * 模拟组织报文,这个只是参考方法，没有借鉴意义
	 * 
	 * @return
	 */
	public static String getXML(String msg) {
		StringBuffer headXml = new StringBuffer();
		headXml = headXml.append("<head><version>v1.0</version><dataType>CB001</dataType>")
				.append("<src>100000</src><des>200000</des>").append("<des>200000</des>").append("<app>CZSB</app>")
				.append("<msgId>CB0011000002017050500000001</msgId>")
				.append("<msgRef>CB0011000002017050500000001</msgRef>").append("<pageCount>1</pageCount>")
				.append("<pageNo>1</pageNo>").append("<recordCount>1</recordCount>")
				.append("<workDate>20170505 12:23:23</workDate>").append("<reserved></reserved></head>");

		StringBuffer bodyXml = new StringBuffer();
		bodyXml = bodyXml.append("<body><record><accNo>123218937123</accNo><accName>财政局</accName>")
				.append("<accBranch>中国银行杭州分行</accBranch>").append("<subAccNo>123218937123-01</subAccNo>")
				.append("<currency>RMB</currency>").append("<reserved1></reserved1>").append("<reserved2></reserved2>")
				.append("</record></body>");

		StringBuffer xml = new StringBuffer("<root>");
		xml = xml.append(headXml).append(bodyXml).append("</root>");
		return xml.toString() +"&"+ msg;
	}
	
	public static String parseXml(String xml){
		String[] split = xml.split("&");
		return split[1];
	}
}

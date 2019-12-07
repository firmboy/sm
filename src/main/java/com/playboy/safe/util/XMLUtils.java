package com.playboy.safe.util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLUtils {
	  public static final String DEFAULT_ENCODING = "UTF-8";

	 public static Document fromXML(Reader in, String encoding)
	    throws Exception
	  {
	    if ((encoding == null) || (encoding.equals(""))) {
	      encoding = "UTF-8";
	    }
	    SAXReader reader = new SAXReader();
	    Document document = reader.read(in, encoding);
	    return document;
	  }

	  public static Document fromXML(InputStream inputSource, String encoding)
	    throws Exception
	  {
	      if ((encoding == null) || (encoding.equals(""))) {
	        encoding = "UTF-8";
	      }
	      SAXReader reader = new SAXReader();
	      Document document = reader.read(inputSource, encoding);
	      return document;
	  }

	  public static Document fromXML(String source, String encoding)
	    throws Exception
	  {
	    return fromXML(new StringReader(source), encoding);
	  }

	  
	  
	  public static Element appendChild(Element parent, String name, String value)
	  {
	    Element element = parent.addElement(new QName(name, parent.getNamespace()));
	    if (value != null) {
	      element.addText(value);
	    }
	    return element;
	  }

	  public static String asXml(String head,Element e, String encoding)
	    throws IOException
	  {
	    OutputFormat format = new OutputFormat();
	    format.setEncoding(encoding);
	    format.setExpandEmptyElements(true);
	    StringWriter out = new StringWriter();
	    XMLWriter writer = new XMLWriter(out, format);
	    writer.write(e);
	    writer.flush();
	    writer.close();
	    out.close();
	    if(null!=head){
	    	  return head + out.toString();
	    }
	    return out.toString();
	  }
	  
	  
	  public static Map xmlTOMap(String xml)
	    throws DocumentException
	  {
	    Element e = DocumentHelper.parseText(xml).getRootElement();
	    return Dom2Map(e);
	  }

	  public static Map Dom2Map(Element e)
	  {
	    Map map = new HashMap();
	    List list = e.elements();
	    if (list.size() > 0)
	      for (int i = 0; i < list.size(); i++) {
	        Element iter = (Element)list.get(i);
	        List mapList = new ArrayList();
	        if (iter.elements().size() > 0) {
	          Map m = Dom2Map(iter);
	          if (map.get(iter.getName()) != null) {
	            Object obj = map.get(iter.getName());

	            if (!obj.getClass().getName()
	              .equals("java.util.ArrayList")) {
	              mapList = new ArrayList();
	              mapList.add(obj);
	              mapList.add(m);
	            }

	            if (obj.getClass().getName()
	              .equals("java.util.ArrayList")) {
	              mapList = (List)obj;
	              mapList.add(m);
	            }
	            map.put(iter.getName(), mapList);
	          } else {
	            map.put(iter.getName(), m);
	          }
	        } else if (map.get(iter.getName()) != null) {
	          Object obj = map.get(iter.getName());

	          if (!obj.getClass().getName()
	            .equals("java.util.ArrayList")) {
	            mapList = new ArrayList();
	            mapList.add(obj);
	            mapList.add(iter.getText());
	          }

	          if (obj.getClass().getName()
	            .equals("java.util.ArrayList")) {
	            mapList = (List)obj;
	            mapList.add(iter.getText());
	          }
	          map.put(iter.getName(), mapList);
	        } else {
	          map.put(iter.getName(), iter.getText());
	        }
	      }
	    else
	      map.put(e.getName(), e.getText());
	    return map;
	  }

}

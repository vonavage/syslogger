package com.naveen.syslog.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.integration.syslog.RFC5424SyslogParser;
import org.springframework.integration.syslog.SyslogHeaders;
import org.springframework.stereotype.Service;
 

@Service("syslogService")
public class SyslogServices {

	private String fileName;
	
	private RFC5424SyslogParser parser = new RFC5424SyslogParser();
	
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static final int NUM_FIELDS = 6;
	
	String accessLogPattern = "^\"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\" \\\"([^\\\"]+)\\\"";
	
	private void printAccessLogEntry(String logEntryLine) {
		System.out.println("Using RE Pattern:");
	    System.out.println(accessLogPattern);

	    System.out.println("Input line is:");
	    System.out.println(logEntryLine);

	    Pattern p = Pattern.compile(accessLogPattern);
	    Matcher matcher = p.matcher(logEntryLine);
	    System.out.println("Group Count:"+matcher.groupCount());
	    if (!matcher.matches() || 
	      NUM_FIELDS != matcher.groupCount()) {
	      System.err.println("Bad log entry (or problem with RE?):");
	      System.err.println(logEntryLine);
	      return;
	    }
	    
	    System.out.println("Request: " + matcher.group(1));
	    System.out.println("Response: " + matcher.group(2));
	    System.out.println("Bytes Sent: " + matcher.group(3));
	    if (!matcher.group(4).equals("-"))
	      System.out.println("Referer: " + matcher.group(4));
	    System.out.println("Browser: " + matcher.group(5));
	}

	private void printStructuredData(List<String> strList) {
		
		strList.stream().forEach(e->System.out.println(e));
		for(String str1: strList) {
		String tag1 = str1.substring(str1.indexOf("tag")+4, str1.length()-1);
		String tag2 = tag1.indexOf("tag")>0?tag1.substring(tag1.indexOf("tag")+4, tag1.length()-1):"";
		String tag3 = tag2.indexOf("tag")>0?tag2.substring(tag2.indexOf("tag")+4, tag2.length()-1):"";
		tag1 = tag1.indexOf("tag=")>0?tag1.substring(0, tag1.indexOf("tag=")):tag1;
		System.out.println("tag1=" + tag1+"\ntag2="+tag2+"\ntag3="+tag3);
		}
	}
	
	
	public void parseFile(String fileName) {
		
		Path path = Paths.get(fileName);
	    try(BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))){

	      
	      String sCurrentLine = null;
	      while((sCurrentLine = reader.readLine()) != null){//while there is content on the current line
	        System.out.println(sCurrentLine); // print the current line
	        System.out.println(sCurrentLine.length());
        	java.util.Map<java.lang.String,?>  objectMap = parser.parse(sCurrentLine, sCurrentLine.length(), false);	
        	//objectMap.keySet().stream().forEach(key->System.out.println(key + "->" + objectMap.get(key)));
        	
        	System.out.println(objectMap.get(SyslogHeaders.STRUCTURED_DATA));
        	if(objectMap.get(SyslogHeaders.STRUCTURED_DATA)!=null)printStructuredData((List)objectMap.get(SyslogHeaders.STRUCTURED_DATA));
        	System.out.println(objectMap.get(SyslogHeaders.MESSAGE));
        	if(objectMap.get(SyslogHeaders.MESSAGE)!=null)printAccessLogEntry((String)objectMap.get(SyslogHeaders.MESSAGE));
        	
	      }
	    }catch(IOException ex){
	      ex.printStackTrace(); //handle an exception here
	    }
	    
		/*try (BufferedReader br = new BufferedReader(new FileReader(fileName))) 
        {
        	String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) 
            {
            	System.out.println(sCurrentLine.length());
            	java.util.Map<java.lang.String,?>  objectMap = parser.parse(sCurrentLine, sCurrentLine.length(), false);	
            	objectMap.keySet().stream().forEach(key->System.out.println(key + "->" + objectMap.get(key)));
            	
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
       */
	}
	
	
	
	 
	public String printFileName() {
		return "File Name:" + fileName;
	}
}
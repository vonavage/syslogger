package com.naveen.syslog.services;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.naveen.syslog.services.SyslogServices;


public class SyslogMain {
 
	@SuppressWarnings("resource")
	public static void main(String[] args) {
 
		// loading the definitions from the given XML file
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
 
		SyslogServices service = (SyslogServices) context
				.getBean("syslogService");
		String message = service.printFileName();
		System.out.println(message);
 
		service.parseFile(service.getFileName());
		
	}
}
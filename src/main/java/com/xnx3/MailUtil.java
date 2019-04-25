package com.xnx3;
import java.security.GeneralSecurityException;
import java.util.Date;  
import java.util.Properties;  
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;  
import javax.mail.Authenticator;  
import javax.mail.PasswordAuthentication;  
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMultipart;
import javax.mail.Transport;  
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 邮件发送
 * @author 管雷鸣
 */
public class MailUtil {  
	private Properties properties;  
	public boolean debug=false;	//调试日志
	public final String BR = "\n";	//内容里的换行符
	
	private String username;	//登录用户名
	private String password;	//登录密码
	private String replayTo;	//邮件发送者的邮箱地址
	
	/**
	 * 邮件发送相关设置
	 * @param host smtp的host，如网易163邮箱是 smtp.163.com
	 * @param username 邮箱账号、邮箱，如 ceshi@163.com
	 * @param password 邮箱登陆密码，如 123456
	 */
	private void setUserPassword(String host, String username, String password){
		properties = new Properties();
		//设置邮件服务器  
		properties.put("mail.smtp.host", host);  
		//验证  
		properties.put("mail.smtp.auth", "true");  
		
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 邮件发送，默认用80端口发送。不过阿里云服务器禁止80端口发送，就要设定25端口来发送邮件
	 * @param host smtp的host，如网易163邮箱是 smtp.163.com
	 * @param username 邮箱账号、邮箱，如 ceshi@163.com
	 * @param password 邮箱登陆密码，如 123456
	 */
	public MailUtil(String host, String username, String password) {
		setUserPassword(host, username, password);
	}
	
	/**
	 * 邮件发送
	 * @param host smtp的host，如网易163邮箱是 smtp.163.com
	 * @param username 邮箱账号、邮箱，如 ceshi@163.com
	 * @param password 邮箱登陆密码，如 123456
	 * @param smtpPort 邮箱发送的端口，如网易的则是 25
	 */
	public MailUtil(String host, String username, String password, String smtpPort) {
		setUserPassword(host, username, password);
		setSmtpPort(smtpPort);
	}
	
	/**
	 * 邮件发送
	 * @param host smtp的host，如网易163邮箱是 smtp.163.com
	 * @param username 邮箱账号、邮箱，如 ceshi@163.com
	 * @param password 邮箱登陆密码，如 123456
	 * @param smtpPort 邮箱发送的端口，如网易的则是 25
	 * @param replayTo 如果对方回复邮件，会发送到这个邮箱中
	 */
	public MailUtil(String host, String username, String password, String smtpPort, String replayTo) {
		setUserPassword(host, username, password);
		setSmtpPort(smtpPort);
		this.replayTo = replayTo;
	}
	
	/**
	 * 设定发送邮件的服务器的端口
	 * @param mailSmtpPort 端口号，如正常使用80端口，阿里云使用 25端口 
	 */
	private void setSmtpPort(String mailSmtpPort){
		properties.put("mail.smtp.port", mailSmtpPort);
	}
	
	/**
	 * 增加 ssl
	 */
	public void addSSL(){
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * 是否开启邮件发送的日志打印
	 * @param debug true：开启
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	

	/**
	 * 发送Txt纯文字邮件
	 * @param targetMail  发送至的邮箱账号
	 * @param title  邮件标题
	 * @param content 邮件发送的内容
	 */
	public void sendMail(String targetMail,String title,String content) {  
		Transport trans = null;
		try {  
			//根据属性新建一个邮件会话  
			Session mailSession = Session.getInstance(properties,  
			new Authenticator() {  
				public PasswordAuthentication getPasswordAuthentication() {  
					  return new PasswordAuthentication(username,password);  
				  }
			});  
			mailSession.setDebug(debug);  
			//建立消息对象  
			MimeMessage mailMessage = new MimeMessage(mailSession);  
			//发件人  
			mailMessage.setFrom(new InternetAddress(username));  	//xnx3_cs@163.com
			//收件人  
			mailMessage.setRecipient(MimeMessage.RecipientType.TO,  
			new InternetAddress(targetMail));  
			//主题  
			mailMessage.setSubject(title);  
			//内容  
			mailMessage.setText(content);  
			//发信时间  
			mailMessage.setSentDate(new Date());  
			
			if(replayTo != null && replayTo.length() > 3){
				Address[] add = {new InternetAddress(replayTo)};
				mailMessage.setReplyTo(add);
			}
			
			//存储信息  
			mailMessage.saveChanges();  
			//  
			trans = mailSession.getTransport("smtp");  
			//发送
			trans.send(mailMessage);  
		} catch (Exception e) {  
			e.printStackTrace();  
		} finally {  
			try {
				trans.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}  
	}  
	
	/**
	 * 发送HTML格式邮件
	 * @param targetMail  发送至的邮箱账号
	 * @param title  邮件标题
	 * @param content 邮件发送的HTML内容，直接写html即可，无需html、body等
	 */
	public void sendHtmlMail(String targetMail,String title,String content) {  
		Transport trans = null;
		try {  
			//根据属性新建一个邮件会话  
			Session mailSession = Session.getInstance(properties,  
			new Authenticator() {  
				public PasswordAuthentication getPasswordAuthentication() {  
					return new PasswordAuthentication(username,password);  
				}
			});  
			mailSession.setDebug(debug);  
			//建立消息对象  
			MimeMessage mailMessage = new MimeMessage(mailSession);  
			//发件人  
			mailMessage.setFrom(new InternetAddress(username));  	//xnx3_cs@163.com
			//收件人  
			mailMessage.setRecipient(MimeMessage.RecipientType.TO,  
			new InternetAddress(targetMail));  
			//主题  
			mailMessage.setSubject(title);  
			
			if(replayTo != null && replayTo.length() > 3){
				Address[] add = {new InternetAddress(replayTo)};
				mailMessage.setReplyTo(add);
			}
			
			//内容  
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
	        Multipart mainPart = new MimeMultipart();
	        // 创建一个包含HTML内容的MimeBodyPart
	        BodyPart html = new MimeBodyPart();
	        // 设置HTML内容
	        html.setContent(content, "text/html; charset=utf-8");
	        mainPart.addBodyPart(html);
	        // 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			//发信时间  
			mailMessage.setSentDate(new Date());  
			//存储信息  
			mailMessage.saveChanges();  
			//  
			trans = mailSession.getTransport("smtp");  
			//发送  
			trans.send(mailMessage);  
		} catch (Exception e) {  
			e.printStackTrace();  
		} finally {  
			try {
				trans.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}  
	}  
	
}
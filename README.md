# Java 一句代码发送邮件
适用于JDK8

# 代码使用示例
1. maven pom.xml 中加入
````
<dependency>
	<groupId>com.xnx3.mail</groupId>
	<artifactId>mail</artifactId>
	<version>1.0</version>
</dependency>
````

2. 代码书写
````
public class Test {
	public static MailUtil mail;	//全局，创建一次，多次使用。
	static{
		/**
		 * host smtp的host，如网易163邮箱是 smtp.163.com
		 * username 邮箱账号、邮箱，如 ceshi@163.com
		 * password 邮箱登陆密码，如 123456
		 * smtpPort 邮箱发送的端口，如网易的则是 25
		 */
		mail = new MailUtil("smtp.163.com", "xnx3_cs@163.com", "cccccc", "25");
	}
	
	public static void main(String[] args) {
		//给 123456@qq.com 发一封邮件
		mail.sendMail("123456@qq.com", "我是邮件的标题", "我是邮件的内容哎");
	}
}
````
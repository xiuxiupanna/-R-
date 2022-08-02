package cn.itcast.travel.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

/**
 * 发送邮件工具类
 */
public final class MailUtil {
	private MailUtil(){}
	/**
	 * 发送邮件
	 * 参数一:发送邮件给谁
	 * 参数二:发送邮件的内容
	 */
	public static void sendMail(String toEmail, String emailMsg) throws Exception {
		try {
			// 1、读取邮件配置
			final Properties props = new Properties();
			// 使用类加载器读取配置文件
			ClassLoader loader = MailUtil.class.getClassLoader();
			props.load(loader.getResourceAsStream("mail.properties"));

			// 2、创建验证器
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					String username = props.getProperty("username");
					String password = props.getProperty("password");
					return new PasswordAuthentication(username, password);
				}
			};

			// 3、创建邮件服务器会话对象Session
			Session session = Session.getInstance(props, auth);

			// 4、邮件信息对象
			Message message = new MimeMessage(session);
			// 发送者
			String from = props.getProperty("from");
			message.setFrom(new InternetAddress(from));

			// 发送方式与接收者
			message.setRecipient(RecipientType.TO, new InternetAddress(toEmail));
			// 主题
			message.setSubject("黑马旅游用户激活");
			// 内容及发送格式
			message.setContent(emailMsg, "text/html;charset=UTF-8");

			// 5、发送邮件
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试类
	 */
	public static void main(String[] args) throws Exception{
		String toEmail = "13720377284@139.com";
		String emailMsg = "测试一下";
		sendMail(toEmail,emailMsg);
		System.out.println("发送成功。。。");
	}
}









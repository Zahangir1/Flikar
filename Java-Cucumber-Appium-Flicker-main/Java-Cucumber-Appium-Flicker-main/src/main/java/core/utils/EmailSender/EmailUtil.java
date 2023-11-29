package core.utils.EmailSender;

import core.utils.ConfigUtil;
import core.utils.PropertyReader.PropertyFileReader;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class EmailUtil {

    static Properties myProp;
    public static Properties setConfig(){ return myProp = ConfigUtil.getConfig("emailutil"); }
    static String replyAddress = setConfig().getProperty("replyAddress");
    static String FromPersonName = setConfig().getProperty("FromPersonName");
    final static String fromEmail = setConfig().getProperty("fromEmail"); //requires valid gmail id
    final static String password = setConfig().getProperty("emailpassword"); // correct password for gmail id
    final static String toAddressesCSVpath = setConfig().getProperty("toAddressesCSVpath");
    static String mail_smtp_socketFactory_class = setConfig().getProperty("mail_smtp_socketFactory_class");
    static String mail_smtp_host = setConfig().getProperty("mail_smtp_host");
    static String mail_smtp_socketFactory_port = setConfig().getProperty("mail_smtp_socketFactory_port");
    static String mail_smtp_auth= setConfig().getProperty("mail_smtp_auth");
    static String filepath;
    static ArrayList<String> toEmailList = new ArrayList<String>();

    public static void Email(){

        toEmailList= PropertyFileReader.ReadCSVFileSingleColumn(toAddressesCSVpath);
        System.out.println("SSLEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", mail_smtp_host); //SMTP Host
        props.put("mail.smtp.socketFactory.port", mail_smtp_socketFactory_port); //SSL Port
        props.put("mail.smtp.socketFactory.class",mail_smtp_socketFactory_class ); //SSL Factory Class
        props.put("mail.smtp.auth", mail_smtp_auth); //Enabling SMTP Authentication
        props.put("mail.smtp.port", mail_smtp_socketFactory_port); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        System.out.println("Session created");

        for (String object: toEmailList) {
            sendAttachmentEmail(session, object, "Bykea Automation Test Results", "Kindly check the attachment for the reports.\n \n \n  Don't reply to this email");
        }
        toEmailList.clear();
    }

    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(replyAddress, FromPersonName));
            msg.setReplyTo(InternetAddress.parse(replyAddress, true)); // addresslist , reply allowed or no
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);
            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAttachmentEmail(Session session, String toEmail, String subject, String body){
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(replyAddress, FromPersonName));
            msg.setReplyTo(InternetAddress.parse(replyAddress, true)); // addresslist , reply allowed or not
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setText(body);
            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Second part is attachment
            messageBodyPart = new MimeBodyPart();
            if(System.getProperty("os.name").contains("Windows")) {
               setReportPath(System.getProperty("user.dir")+"\\test-output\\HtmlReport\\ExtentHtml.html");
            }
            else
            {
                setReportPath(System.getProperty("user.dir")+"/test-output/HtmlReport/ExtentHtml.html");
            }
           // setReportPath(System.getProperty("user.dir")+"\\test-output\\HtmlReport\\ExtentHtml.html");
           // setReportPath(System.getProperty("user.dir")+"/test-output/HtmlReport/ExtentHtml.html");
            DataSource source = new FileDataSource(filepath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("bykea_Automation_Report.html");
          //  messageBodyPart.setFileName(filepath);
            multipart.addBodyPart(messageBodyPart);
            // Send the complete message parts
            msg.setContent(multipart);
            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with attachment!!");
        }catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void sendImageEmail(Session session, String toEmail, String subject, String body){
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(replyAddress, FromPersonName));
            msg.setReplyTo(InternetAddress.parse(replyAddress, true)); // addresslist , reply allowed or not
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Second part is image attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "image.png";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            //Trick is to add the content-id header here
            messageBodyPart.setHeader("Content-ID", "image_id");
            multipart.addBodyPart(messageBodyPart);
            //third part for displaying image in the email body
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<h1>Attached Image</h1>" +
                    "<img src='cid:image_id'>", "text/html");
            multipart.addBodyPart(messageBodyPart);
            //Set the multipart message to the email message
            msg.setContent(multipart);
            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with image!!");
        }catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void setReportPath(String path) {
        filepath = path;
    }

    public String getReportPath(){
        return filepath;
    }

}

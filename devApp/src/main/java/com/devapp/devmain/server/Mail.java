package com.devapp.devmain.server;

import android.content.Context;

import com.devapp.devmain.services.SendEmail;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends javax.mail.Authenticator {
    AmcuConfig amcuConfig;
    private String user;
    private String password;
    private String[] to;
    private String from;
    private String port;
    private String sport;
    private String host;
    private String subject;
    private String body;
    private boolean _auth;
    private boolean _debuggable;
    private Multipart multipart;
    private Context mContext;

    public Mail(Context ctx) {

        host = "smtp.googlemail.com"; // default smtp server
        port = "465"; // default smtp port
        sport = "465"; // default socketfactory port'

        //	host = "smtp.mail.yahoo.com";
////		port = "587";
////		sport = "587";
        this.mContext = ctx;

        amcuConfig = AmcuConfig.getInstance();

        user = ""; // username
        password = ""; // password
        from = ""; // email sent from
        subject = ""; // email subject
        body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a
        // handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap
                .getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String user, String pass, Context ctx) {
        this(ctx);
        this.user = user;
        password = pass;
        this.mContext = ctx;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Multipart getMultipart() {
        return multipart;
    }

    public void setMultipart(Multipart multipart) {
        this.multipart = multipart;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();

        if (!user.equals("") && !password.equals("")
                && !from.equals("") && !subject.equals("") && !body.equals("")) {
            Session session = Session.getInstance(props, this);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));

            try {
                InternetAddress[] addressTo = new InternetAddress[to.length];
                for (int i = 0; i < to.length; i++) {
                    if (to[i] != null && !to[i].replaceAll(" ", "").equalsIgnoreCase("")) {
                        addressTo[i] = new InternetAddress(to[i]);
                    }
                }
                msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            } catch (MessagingException e) {
                SendEmail.isIllegalEmail = true;
                e.printStackTrace();
            } catch (Exception e) {
                SendEmail.isIllegalEmail = true;
                e.printStackTrace();
            }

            //removed smartamcu.care@stellapps.com as the mail BCC, As per new requirement

//			msg.setRecipients(MimeMessage.RecipientType.BCC,
//                    "smartamcu.care@stellapps.com");

            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setHeader("Content-Type",
                    "text/html; charset=ISO-8859-1");
            messageBodyPart.setContent(body, "text/html");
            // messageBodyPart.setText(body);

            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(multipart, "text/html; charset=ISO-8859-1");
            msg.saveChanges();

            // send email
            try {
                Transport.send(msg);
                return true;
            } catch (Exception e) {
                if (e.toString().contains("Daily sending quota exceeded")) {
                    SendEmail.isSendingMailQuotaOver = true;
                }
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(new File(filename).getName());

        multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", host);

        if (_debuggable) {
            props.put("mail.debug", "true");
        }

        if (_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", sport);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");


        return props;
    }

    // the getters and setters
    public String getBody() {
        return body;
    }

    public void setBody(String _body) {
        this.body = _body;
    }
}
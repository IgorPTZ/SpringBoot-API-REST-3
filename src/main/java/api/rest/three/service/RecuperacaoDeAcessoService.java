package api.rest.three.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class RecuperacaoDeAcessoService {
	
	private String usuario = "angular.project1993@gmail.com";
	
	private String senha   = "teste321";
	
	public void enviarEmailDeRecuperacaoDeAcesso(String assunto, String destino, String conteudo) throws Exception {
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.auth", "true"); // Autorização
		
		properties.put("mail.smtp.starttls", "true"); // Autenticação
		
		properties.put("mail.smtp.host", "smtp.gmail.com"); // Servidor da Google
		
		properties.put("mail.smtp.port", "465"); //Porta do servidor da Google
		
		properties.put("mail.smtp.socketFactory.port", "465"); // Especifica porta do socket
		
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Classe de conexão para sockets
	
		Session session = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication(usuario, senha );
			}
		});
		
		Address[] destinatario = InternetAddress.parse(destino);
		
		Message mensagem = new MimeMessage(session);
		
		mensagem.setFrom(new InternetAddress(usuario)); // Remetente do email
		
		mensagem.setRecipients(Message.RecipientType.TO, destinatario); // Destinatario do email
		
		mensagem.setSubject(assunto); // Assunto do email
		
		mensagem.setText(conteudo); // Conteudo do email
		
		Transport.send(mensagem);
	}
}

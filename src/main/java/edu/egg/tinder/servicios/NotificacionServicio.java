/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.egg.tinder.servicios;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author CARMEN
 */
@Service
public class NotificacionServicio {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviar(String cuerpo, String titulo, String mail) /*throws MessagingException*/ {
//        //MENSAJE MAS COMPLEJO
//        MimeMessage mensaje = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
//        helper.setTo(mail);
//        helper.setSubject(titulo);
//        helper.setFrom("info@syshandex.com");
//        // default = text/plain
//        //helper.setText("Check attachment for image!");
//
//        // true = text/html
//        helper.setText("<h1>Check attachment for image!</h1>", true);
//
//        // hardcoded a file path
////        FileSystemResource file = new FileSystemResource(new File("C:/Users\\CARMEN\\Desktop\\ssl4.jpg"));
////
////        helper.addAttachment("ssl4.jpg", file);
//
//        mailSender.send(mensaje);
        
        //MESAJE SIMPLE
        SimpleMailMessage mensaje=new SimpleMailMessage();
        mensaje.setTo(mail);
        mensaje.setFrom("noreply@tinder-mascota.com");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);        
    }
}

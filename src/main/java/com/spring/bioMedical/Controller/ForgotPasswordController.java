package com.spring.bioMedical.Controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.spring.bioMedical.entity.User;
import com.spring.bioMedical.service.UserService;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {
    @Autowired
    private JavaMailSender mailSender;
     
    @Autowired
    private UserService userService;
     
    @RequestMapping(value="/forgotPassword", method = RequestMethod.GET)
    public String showForgotPasswordForm() {
    	return "forgot_password";
    }
 
    @RequestMapping(value="/forgotPassword", method = RequestMethod.POST)
    public String processForgotPassword(HttpServletRequest request, Model model) {
    	String email = request.getParameter("email");
        String token = RandomString.make(30);
         
        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = request.getScheme() + "://" + request.getServerName() + "/resetPassword?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email.");
             
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
             
        return "forgot_password";
    }
     
    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setFrom("no-reply@betech.com", "BeTech Support");
        helper.setTo(recipientEmail);
         
        String subject = "Here's the link to reset your password";
         
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
         
        helper.setSubject(subject);
         
        helper.setText(content, true);
         
        mailSender.send(message);
    }  
     
    @RequestMapping(value="/resetPassword", method = RequestMethod.GET)
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
    	User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
         
        if (user == null) {
            model.addAttribute("message", "Invalid Token!");
            return "reset_password";
        }
         
        return "reset_password";
    }
     
    @RequestMapping(value="/resetPassword", method = RequestMethod.POST)
    public String processResetPassword(HttpServletRequest request, Model model) {
    	String token = request.getParameter("token");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("cnf_password");
        
         
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");
        
        
        Zxcvbn passwordCheck = new Zxcvbn();
		
		Strength strength = passwordCheck.measure(password);

         
        if (user == null) {
            model.addAttribute("message", "Invalid Token!");
            model.addAttribute("token", token);
            return "reset_password";
        }
        else if (strength.getScore() < 3) {
			model.addAttribute("message", "Your password is too weak.");
			model.addAttribute("token", token);
            return "reset_password";
		}
        else if (!passwordConfirm.equals(password)) {
			model.addAttribute("message", "Password and Confirm Password should be same.");
			model.addAttribute("token", token);
            return "reset_password";
		} else {           
            userService.updatePassword(user, password);
            model.addAttribute("password", "You have successfully changed your password.");
        }
         
        return "login";
    }
}

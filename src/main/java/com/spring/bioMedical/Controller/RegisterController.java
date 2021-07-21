package com.spring.bioMedical.Controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.spring.bioMedical.entity.User;
import com.spring.bioMedical.service.EmailService;
import com.spring.bioMedical.service.UserService;

@Controller
public class RegisterController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private UserService userService;
	private EmailService emailService;
	
	public static final String ACCOUNT_SID = "ACdd951a4808a0b0438515ef2e71fe9d00";
	public static final String AUTH_TOKEN = "29eaa0d0691f739484f6abc1c18acf2b";

	
	@Autowired
	public RegisterController(
			UserService userService, EmailService emailService) {
		//this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.emailService = emailService;
	}
	
	// Return registration form template
	@RequestMapping(value="/register", method = RequestMethod.GET)

	public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user) {
		
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	// Process form input data
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request) throws IOException, InterruptedException {
		
		// Lookup user in database by e-mail
		User userExists = userService.findByEmail(user.getEmail());
		
		System.out.println(userExists);
		
		if (userExists != null) {
			modelAndView.addObject("alreadyRegisteredMessage", "Email already in use!");
			modelAndView.setViewName("register");
			bindingResult.reject("email");
		}
		
		if (user.getFirstName().trim().equals("") || user.getLastName().trim().equals("") 
				|| user.getFirstName().length() < 2 || user.getLastName().length() < 2 
				|| user.getPrefName().trim().equals("") || user.getPrefName().length() < 2 ) {
			modelAndView.addObject("nameMessage", "Enter proper first, last and preferred name!");
			modelAndView.setViewName("register");
			bindingResult.reject("name");
		}
		
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	    Boolean valid = user.getEmail().matches(regex);
	    
	    if (!valid) {
	    	modelAndView.addObject("emailMessage", "Enter a valid email address!");
			modelAndView.setViewName("register");
			bindingResult.reject("email");
	    }
	    
	    Pattern pattern = Pattern.compile("^\\d{12}$");
	    Matcher matcher = pattern.matcher(user.getMobile());
	      
	    boolean res = matcher.matches();
	    
	    if (!res) {
	    	modelAndView.addObject("mobileMessage", "Enter a valid mobile number!");
			modelAndView.setViewName("register");
			bindingResult.reject("mobile");
	    }
	    
			
		if (bindingResult.hasErrors()) { 
			modelAndView.setViewName("register");		
		} else { // new user so we create user and send confirmation e-mail
					
			// Disable user until they click on confirmation link in email
		    
			user.setEnabled(false);
			user.setRole("ROLE_USER");
		      
			
		    // Generate random 36-character string token for confirmation link
		    user.setConfirmationToken(UUID.randomUUID().toString());
		        
		    userService.saveUser(user);
				
			String appUrlServer = request.getScheme() + "://" + request.getServerName();
//			
//			HttpRequest smsReq = HttpRequest.newBuilder()
//					.uri(URI.create("https://d7sms.p.rapidapi.com/secure/send"))
//					.header("content-type", "application/json")
//					.header("authorization", "Basic dnR5YjgwOTI6RnNwcFFueTI=")
//					.header("x-rapidapi-key", "5dbf5e3c4dmshfff746f95ba3bfdp1df0d1jsn48a7bc21bb3f")
//					.header("x-rapidapi-host", "d7sms.p.rapidapi.com")
//					.method("POST", HttpRequest.BodyPublishers.ofString("{ \"coding\": \"8\", \"from\": \"BeTech\", \"hex-content\": \"00480065006c006c006f\", \"to\": 447398491741 }")).build();
//			
//			HttpResponse<String> response = HttpClient.newHttpClient().send(smsReq, HttpResponse.BodyHandlers.ofString());
//			System.out.println(response.body() + " <------ sms response");
//		    
		    
			SimpleMailMessage registrationEmail = new SimpleMailMessage();
			registrationEmail.setTo(user.getEmail());
			registrationEmail.setSubject("Registration Confirmation");
			String href = appUrlServer + "/confirm?token=" + user.getConfirmationToken();
			registrationEmail.setText("To confirm your e-mail address, please "
					+ "<a href='"+ href +"'>Click Here!</a>");
			registrationEmail.setFrom("noreply@springApp.com");
			
			emailService.sendEmail(registrationEmail);
			
			modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
			modelAndView.setViewName("register");
		}
			
		return modelAndView;
	}
	
	// Process confirmation link
	@RequestMapping(value="/confirm", method = RequestMethod.GET)
	public ModelAndView confirmRegistration(ModelAndView modelAndView, @RequestParam("token") String token) {
			
		User user = userService.findByConfirmationToken(token);
			
		if (user == null) { // No token found in DB
			modelAndView.addObject("invalidToken", "Oops! This is an invalid confirmation link.");
		} else { // Token found
			modelAndView.addObject("confirmationToken", user.getConfirmationToken());
		}
			
		modelAndView.setViewName("confirm");
		return modelAndView;		
	}
	
	// Process confirmation link
	@RequestMapping(value="/confirm", method = RequestMethod.POST)
	public ModelAndView confirmRegistration(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {
				
		modelAndView.setViewName("confirm");
		
		Zxcvbn passwordCheck = new Zxcvbn();
		
		Strength strength = passwordCheck.measure(requestParams.get("password"));
		
		if (strength.getScore() < 3) {
			//modelAndView.addObject("errorMessage", "Your password is too weak.  Choose a stronger one.");
			bindingResult.reject("password");
			
			redir.addFlashAttribute("errorMessage", "Your password is too weak. Choose a stronger one.");

			modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
			System.out.println(requestParams.get("token"));
			return modelAndView;
		}
		if (!requestParams.get("ConfirmPassword").equals(requestParams.get("password"))) {
			bindingResult.reject("password");
			redir.addFlashAttribute("errorMessage", "Both the passwords should be same.");
			modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
			System.out.println(requestParams.get("token"));
			return modelAndView;
		}
	
		// Find the user associated with the reset token
		User user = userService.findByConfirmationToken(requestParams.get("token"));

		// Set new password
//		user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));
		user.setPassword(passwordEncoder.encode(requestParams.get("password")));
//		user.setPassword(requestParams.get("password"));

		// Set user to enabled
		user.setEnabled(true);
		
		// Save user
		userService.saveUser(user);
		
		modelAndView.addObject("successMessage", "Your password has been set!");
		return modelAndView;		
	}
	
	
	
}
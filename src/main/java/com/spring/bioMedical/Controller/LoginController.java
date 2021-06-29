package com.spring.bioMedical.Controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showHome(HttpServletRequest request, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken || auth == null) {
			return "login";
		}
		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/user-details";
		}
		if (request.isUserInRole("ROLE_DOCTOR")) {
			return "redirect:/doctor/index";
		}
		return "redirect:/user/index";
	}
	
	@RequestMapping(value = "/default", method = RequestMethod.GET)
	public String showDefault(HttpServletRequest request, Model model) {
		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/user-details";
		}
		if (request.isUserInRole("ROLE_DOCTOR")) {
			return "redirect:/doctor/index";
		}
		return "redirect:/user/index";
	}
	
	@RequestMapping(value = "/access-denied", method = RequestMethod.GET)
	public String showAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		response.sendRedirect(request.getContextPath() + "?accessDenied");
		return "redirect:/login";
	}
}

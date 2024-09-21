package com.smartcontact.project.smart_contact_manager.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.project.smart_contact_manager.Message;
import com.smartcontact.project.smart_contact_manager.Userr;
import com.smartcontact.project.smart_contact_manager.configuration.SecurityConfig;
import com.smartcontact.project.smart_contact_manager.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Controller
public class HomeController {
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(ModelMap model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(ModelMap model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	@GetMapping("/signup")
	public String signup(ModelMap model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user",new Userr());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") Userr user,BindingResult result,@RequestParam(value="agreement",defaultValue="false") boolean agreement,
			ModelMap model,HttpSession session) {
		try {
		if(!agreement) {
			System.out.println("you have not agreed terms and conditions");	
			throw new Exception("you have not agreed terms and conditions");
			}
		if(result.hasErrors()) {
			System.out.println("Error is "+" "+result.toString());
			model.addAttribute("user",user);
			return "signup";
		}
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
		userRepository.save(user);
		model.addAttribute("user",new Userr());
		session.setAttribute("message", new Message("Successfully Registered","alert-success"));
	     return "signup";
		}
		catch(Exception e) {
	     e.printStackTrace();
	    session.setAttribute("message", new Message("something went wrong"+e.getMessage(),"alert-danger"));
	     return "signup";
		}	
	}
	@GetMapping("/signin")
	public String customLogin(ModelMap model) {
		
		return "login";
	}
		

}

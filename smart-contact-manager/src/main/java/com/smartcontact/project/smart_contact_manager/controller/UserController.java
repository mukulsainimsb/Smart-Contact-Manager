package com.smartcontact.project.smart_contact_manager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.project.smart_contact_manager.Contact;
import com.smartcontact.project.smart_contact_manager.Userr;
import com.smartcontact.project.smart_contact_manager.repository.ContactRepository;
import com.smartcontact.project.smart_contact_manager.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(ModelMap model,Principal principal) {
		//This method will run for all handlers below
		String username= principal.getName();
		Userr user=userRepository.findByEmail(username);
		model.addAttribute("user",user);
	}
	
	// dashboard home
	@RequestMapping("/index")
	public String dashboard(ModelMap model) {
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(ModelMap model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	}
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,
			Principal principal,ModelMap model) {
	
		try {
		String name=principal.getName();
		Userr user = this.userRepository.findByEmail(name);
		//uploading file
		if(file.isEmpty()) {
			System.out.println("file is not uploaded successfully");
			//setting default image for empty images
			contact.setImage("contact.png");
		}
		else {
			contact.setImage(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/img").getFile();
		 Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		System.out.println("image is uploaded");
		//sending form success message
		model.addAttribute("message","Successfully Registered");
		}
		
		
		contact.setUser(user);
		user.getContacts().add(contact);
		
		userRepository.save(user);
		System.out.println(contact);
		System.out.println("added successfully");
		} catch( Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			//sending form error message
				model.addAttribute("messagee","Something went wrong");
		}
		return "normal/add_contact_form";
	}
	//handler to show contacts
	@GetMapping("/show-contacts")
	public String showContacts(ModelMap model,Principal principal) {
		String username = principal.getName();
		Userr user=userRepository.findByEmail(username);
		int id=user.getId();
		List<Contact> contacts=contactRepository.findByUserId(id);
		model.addAttribute("contacts",contacts);
		return "normal/show_contacts";
	}
	//showing particular user detail by clicking on their email
	
	@RequestMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId,ModelMap model,Principal principal) {
		
		System.out.println("c id is="+cId);
		Optional<Contact> contact= contactRepository.findById(cId);
		Contact contacts= contact.get();
		String username = principal.getName();
		Userr user=userRepository.findByEmail(username);
		if(user.getId()==contacts.getUser().getId()) {
			
			model.addAttribute("contact",contacts);
		}
		
		return "normal/contact_detail";
	}
	//delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,ModelMap model,Principal principal) {
		Optional<Contact> contactOptional=contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		Userr user= userRepository.findByEmail(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);
		return "redirect:/user/show-contacts";
		
	}
	//for update page
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,ModelMap model) {
		Contact contact=contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}
	
	//for real updation get working
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,ModelMap model,Principal principal) {
     try {
    	 //fetch old contact detail
    	 Contact oldContactDetail=contactRepository.findById(contact.getcId()).get();
    	 
			if(!file.isEmpty()) {
				//delete old one
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContactDetail.getImage());
				file1.delete();
				 
				//update new photo
				File saveFile=new ClassPathResource("static/img").getFile();
				 Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContactDetail.getImage());
			}
			Userr user = userRepository.findByEmail(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			
		    
		} catch(Exception e) {
		
			e.printStackTrace();
		}
		
		return "redirect:/user/contact/"+contact.getcId();
	}
}



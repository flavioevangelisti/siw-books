package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.CredentialsValidator;
import it.uniroma3.siw.validator.UserValidator;
import jakarta.validation.Valid;


@Controller
public class AuthenticationController {
	
	@Autowired 
	private CredentialsService credentialsService;
	@Autowired 
	private UserService userService;
	@Autowired
	private UserValidator userValidator;
	@Autowired
	private CredentialsValidator credentialsValidator;
	
	@GetMapping("/formRegistrazione")
	public String getRegistrazione(Model model) {
		model.addAttribute("user",new User());
		model.addAttribute("credentials",new Credentials());
		return "/formRegistrazione.html";
		}
	
	
	@PostMapping("/registrazione")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult userBindingResult,
            @Valid @ModelAttribute("credentials") Credentials credentials,BindingResult credentialsBindingResult,
                 Model model) {
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);
		 if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
        // se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB 
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            userService.saveUser(user);
            model.addAttribute("user", user);
            model.addAttribute("credentials", credentials);
            return "/index.html";}
            else
            	return "/formRegistrazione.html";
    }
	
	@GetMapping("/login") 
	public String showLoginForm (Model model) {
		return "/formLogin";
	}
	
	
	@GetMapping("/success")
	public String defaultAfterLogin(Model model) {

		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("credentials", credentials);
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "admin/indexAdmin.html";
		}
		return "/index.html";
	}

}
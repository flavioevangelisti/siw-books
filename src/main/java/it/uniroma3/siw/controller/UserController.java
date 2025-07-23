package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CredentialsService credentialsService;

	private boolean verifyId(Long id) {
	    return id != null && id.equals(this.userService.getUser().getId());
	}
	
	@GetMapping("/user/{id}")
	public String showUser(@PathVariable("id") Long id, @RequestParam(value = "showPasswordModal", required = false, defaultValue = "false")
							boolean showPasswordModal, Model model) {
		if (!verifyId(id))
			return "error";
		
		User currentUser = userService.getUser();
		Credentials currentCredentials = credentialsService.getCredentials(id);
		model.addAttribute("user", currentUser); 
		model.addAttribute("credentials", currentCredentials);
		model.addAttribute("showPasswordModal", showPasswordModal);
		model.addAttribute("orders", currentUser.getRecensioni());
		return "user/user.html";
	}
	
	@GetMapping("/user/{id}/editProfile")
	public String showFormUpdateUser(@PathVariable("id") Long id, Model model) {
		if (!verifyId(id))
			return "redirect:/login";

		User user = this.userService.getUser();
		Credentials currentCredentials = credentialsService.getCredentials(id);
		model.addAttribute("user", user);
		model.addAttribute("credentials", currentCredentials);
		return "user/formEditUser.html";
	}

	@PostMapping("/user/{id}/editProfile")
	public String updateUser(@PathVariable("id") Long id, @ModelAttribute @Valid User user,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("msgError", "Campi non validi");
			model.addAttribute("user", user);
			return "user/formEditUser.html";
		}
		if (!verifyId(id))
			return "redirect:/login";
		this.userService.saveUser(user);
		return "redirect:/user/" + user.getId();
	}
	
	@PostMapping("/user/{id}/changePassword")
	public String updateUserCredentials(@PathVariable("id") Long id,
	                                    @RequestParam @Valid String confirmPwd,
	                                    @RequestParam @Valid String newPwd,
	                                    Model model) {
	    if (newPwd == null || confirmPwd == null || newPwd.isEmpty() || confirmPwd.isEmpty()) {
	        model.addAttribute("msgError", "La password non può essere vuota");
	    } else if (!newPwd.equals(confirmPwd)) {
	        model.addAttribute("msgError", "Le password non coincidono");
	    } else if (!newPwd.matches("^(?=.*[A-Z])(?=.*[\\W_]).{8,}$")) {
	        model.addAttribute("msgError", "La password deve avere almeno 8 caratteri, una maiuscola e un carattere speciale");
	    } else {
	        User user = this.userService.getUser();
	        if (!verifyId(id)) return "redirect:/login";
	        Credentials credentials = this.credentialsService.getCredentialsByUser(user);
	        credentials.setPassword(newPwd);
	        this.credentialsService.saveCredentials(credentials);
	        return "redirect:/user/" + user.getId();
	    }

	    model.addAttribute("user", this.userService.getUser());
	    model.addAttribute("showPasswordModal", true);
	    return "user/user.html";
	}
}


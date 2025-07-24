package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.AutoreValidator;
import jakarta.validation.Valid;


@Controller
public class AutoreController {

	@Autowired
	private AutoreService autoreService;
	@Autowired
	private LibroService libroService;
	@Autowired
	private AutoreValidator autoreValidator;
	@Autowired
	private UserService userService;
	@Autowired
	private CredentialsService credentialsService;

	@GetMapping("/paginaAutori")
	public String paginaAutori(Model model) {
		model.addAttribute("autori", this.autoreService.findAll());

		return "paginaAutori.html"; 
	}

	@GetMapping("/autore/{id}")
	public String getAutore(@PathVariable("id") Long id, Model model) {
		Autore autore = autoreService.findById(id);
	    model.addAttribute("autore", autore);

	    // Recupera utente loggato
	    User user = userService.getUser();
	    if (user != null) {
	        model.addAttribute("credentials", credentialsService.getCredentials(user.getId()));
	    }

	    return "autore.html";
	}

	@GetMapping(value="/admin/indexAutore")
	public String indexAutore() {
		return "admin/indexAutore.html";
	}

	@GetMapping("/admin/formCreaAutore")
	public String formCreaAutore(Model model) {
		model.addAttribute("autore", new Autore());
		return "admin/formCreaAutore.html";
	}

	@PostMapping("/admin/formCreaAutore")
	public String creaLibro(@Valid @ModelAttribute("autore") Autore autore, 
			BindingResult bindingResult,
			@RequestParam("imageFile") MultipartFile imageFile, 
			Model model) throws IOException {
		this.autoreValidator.validate(autore, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.autoreService.creaAutore(autore, bindingResult, imageFile);
			return "/admin/indexAdmin";
		} else {
			return "admin/formCreaAutore";
		}
	}
	
	@GetMapping(value="/admin/gestisciAutori")
	public String gestisciAutore(Model model) {
		model.addAttribute("autori", this.autoreService.findAll());
		return "admin/gestisciAutori.html";
	}

	@GetMapping(value="/admin/formModificaAutore/{id}")
	public String formModificaAutore(@PathVariable("id") Long id, Model model) {
		model.addAttribute("autore", this.autoreService.findById(id));
		return "admin/formModificaAutore.html";
	}

	@PostMapping("/admin/formModificaAutore/{id}")
	public String modificaautore(@RequestParam("id") Long id,
			@RequestParam("nuovoNome") String nuovoNome,
			@RequestParam("nuovoCognome") String nuovoCognome,
			@RequestParam("nuovaNazionalita") String nuovaNazionalita,
			@RequestParam("nuovaDataNascita") LocalDate nuovaDataNascita,
			@RequestParam("nuovaDataMorte") LocalDate nuovaDataMorte,
			Model model) {
		this.autoreService.modifica(id,nuovoNome,nuovoCognome,nuovaNazionalita,nuovaDataNascita,nuovaDataMorte);
		return "/admin/indexAdmin";
	}


	@GetMapping("/autore/{id}/libri")
	public String getLibriPerAutore(@PathVariable Long id, Model model) {
	    Autore autore = autoreService.findById(id);
	    List<Libro> libri = libroService.findByAutore(autore);
	    model.addAttribute("libri", libri);
	    model.addAttribute("autoreFiltrato", autore); // facoltativo, per mostrare nome autore
	    return "paginaLibri";
	}

	@GetMapping(value = "/admin/cancellaAutore/{id}")
	public String cancellaAutore(@PathVariable("id") Long id, Model model) {
		this.autoreService.delete(id);
		return "admin/indexAdmin.html";
	}
}
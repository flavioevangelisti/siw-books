package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.RecensioneValidator;
import jakarta.validation.Valid;

@Controller
public class RecensioneController {

	@Autowired
	private RecensioneService recensioneService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private GlobalController globalController;

	@Autowired
	private UserService userService;

	@Autowired
	private LibroService libroService;

	@Autowired
	private RecensioneValidator recensioneValidator;

	@GetMapping("/paginaRecensioni")
	public String paginaRecensioni(Model model) {
		model.addAttribute("recensioni", this.recensioneService.findAll());
		return "paginaRecensioni.html";
	}

	@GetMapping("/recensione{id}")
	public String getRecensione(@PathVariable("id") Long id, Model model) {
		model.addAttribute("recensione", this.recensioneService.findById(id));
		return "recensione.html";
	}

	@GetMapping("/formCreaRecensione")
	public String formCreaRecensione(Model model) {
		model.addAttribute("recensione", new Recensione());
		model.addAttribute("listaLibri", libroService.findAll());
		return "formCreaRecensione.html";
	}

	@PostMapping("/formCreaRecensione")
	public String creaRecensione(@Valid @ModelAttribute("recensione") Recensione recensione,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser,
			Model model) throws IOException {
		this.recensioneValidator.validate(recensione, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("listaLibri", libroService.findAll());
			return "formCreaRecensione.html";
		}

		// Recupero utente autenticato passando da Credentials
		Credentials credentials = this.credentialsService.getCredentials(currentUser.getUsername());
		User utente = credentials.getUser();

		// Recupero corretto del libro (per evitare problemi di persistence context)
		Long libroId = recensione.getLibro().getId();
		Libro libro = libroService.findById(libroId);
		recensione.setLibro(libro);

		// Controllo se l'utente ha già recensito questo libro
		boolean recensioneEsistente = recensioneService.existsByUserAndLibro(utente, libro);
		if (recensioneEsistente) {
			bindingResult.rejectValue("libro", "error.recensione", "Hai già recensito questo libro");
			model.addAttribute("listaLibri", libroService.findAll());
			return "formCreaRecensione.html";
		}

		// Salvataggio recensione associando lo user
		recensioneService.creaRecensione(recensione, utente);

		return "index.html";
	}


	@GetMapping("/admin/gestisciRecensioni")
	public String gestisciRecensioni(Model model) {
		model.addAttribute("recensioni", this.recensioneService.findAll());
		return "admin/gestisciRecensioni.html";
	}

	@GetMapping("/admin/indexRecensione")
	public String indexRecensione() {
		return "admin/indexRecensione.html";
	}

	@GetMapping("/admin/cancellaRecensione/{id}")
	public String cancellaRecensione(@PathVariable("id") Long id, Model model) {
		this.recensioneService.delete(id);
		return "admin/indexAdmin.html";
	}
}

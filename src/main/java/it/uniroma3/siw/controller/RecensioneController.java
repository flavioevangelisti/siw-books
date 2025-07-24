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
		User user = userService.getUser();
	    if (user != null) {
	        model.addAttribute("credentials", credentialsService.getCredentials(user.getId()));
	    }
		return "recensione.html";
	}

	@GetMapping("/formCreaRecensione")
	public String formCreaRecensione(Model model) {
		model.addAttribute("recensione", new Recensione());
		model.addAttribute("listaLibri", libroService.findAll());
		User user = userService.getUser();
	    if (user != null) {
	        model.addAttribute("credentials", credentialsService.getCredentials(user.getId()));
	    }
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

	    Credentials credentials = this.credentialsService.getCredentials(currentUser.getUsername());
	    User utente = credentials.getUser();

	    Long libroId = recensione.getLibro().getId();
	    Libro libro = libroService.findById(libroId);
	    recensione.setLibro(libro);

	    boolean recensioneEsistente = recensioneService.existsByUserAndLibro(utente, libro);
	    if (recensioneEsistente) {
	        bindingResult.rejectValue("libro", "error.recensione", "Hai già recensito questo libro");
	        model.addAttribute("listaLibri", libroService.findAll());
	        return "formCreaRecensione.html";
	    }

	    // Non serve mettere recensione.setRecensore(utente) qui, lo fa già il service
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
	
	@GetMapping("/recensioni/{id}/elimina")
	public String eliminaRecensione(@PathVariable("id") Long id,
	                               @AuthenticationPrincipal UserDetails currentUser) {

	    Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
	    User utente = credentials.getUser();

	    Recensione recensione = recensioneService.findById(id);

	    boolean isAdmin = credentials.getRole().equals("ADMIN");
	    boolean isRecensore = recensione.getRecensore().getId().equals(utente.getId());

	    if (isAdmin || isRecensore) {
	        recensioneService.delete(id);
	    }

	    if (isAdmin) {
	        return "redirect:/admin/gestisciRecensioni";
	    } else {
	        return "redirect:/user/" + utente.getId();
	    }
	}
	
	@GetMapping("/recensioni/{id}/modifica")
	public String mostraFormModifica(@PathVariable("id") Long id,
	                                 @AuthenticationPrincipal UserDetails currentUser,
	                                 Model model) {
	    Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
	    User utente = credentials.getUser();

	    Recensione recensione = recensioneService.findById(id);
	    if (!recensione.getRecensore().getId().equals(utente.getId())) {
	        return "redirect:/accessoNegato"; // oppure una pagina personalizzata
	    }

	    model.addAttribute("recensione", recensione);
	    return "formModificaRecensione.html";
	}

	@PostMapping("/recensioni/{id}/modifica")
	public String modificaRecensione(@PathVariable("id") Long id,
	                                 @Valid @ModelAttribute("recensione") Recensione recensioneModificata,
	                                 BindingResult bindingResult,
	                                 @AuthenticationPrincipal UserDetails currentUser,
	                                 Model model) {
	    Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
	    User utente = credentials.getUser();

	    Recensione originale = recensioneService.findById(id);
	    if (!originale.getRecensore().getId().equals(utente.getId())) {
	        return "redirect:/accessoNegato";
	    }

	    recensioneValidator.validate(recensioneModificata, bindingResult);
	    if (bindingResult.hasErrors()) {
	        return "formModificaRecensione.html";
	    }

	    originale.setTesto(recensioneModificata.getTesto());
	    originale.setVoto(recensioneModificata.getVoto());
	    recensioneService.save(originale);

	    return "redirect:/user/" + utente.getId();
	}

}

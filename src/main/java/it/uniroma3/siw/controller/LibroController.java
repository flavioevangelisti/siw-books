
package it.uniroma3.siw.controller;

import java.io.IOException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.validator.LibroValidator;
import jakarta.validation.Valid;

@Controller
public class LibroController {
	
	@Autowired
	private LibroService libroService;
	@Autowired
	private AutoreService autoreService;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private LibroValidator libroValidator;
	@Autowired
	private GlobalController globalController;
	
	 @GetMapping("/paginaLibri")
	    public String paginaLibri(Model model) {
		  model.addAttribute("libri", this.libroService.findAll());
		   
	       return "paginaLibri.html";
	    }

	 @GetMapping("/libro{id}")
		public String getLibro(@PathVariable("id") Long id, Model model) {
			model.addAttribute("libro", this.libroService.findById(id));
			model.addAttribute("listaAutori", this.autoreService.findById(id));
			
			return "libro.html";
		}

	 @GetMapping(value="/admin/indexLibro")
		public String indexLibro() {
			return "admin/indexLibro.html";
		}
	 
	 @GetMapping("/admin/formCreaLibro")
		public String formCreaLibro(Model model) {
			model.addAttribute("libro", new Libro());
			model.addAttribute("listaAutori",autoreService.findAll());
			return "admin/formCreaLibro.html";
		}
	 
	 @PostMapping("/admin/formCreaLibro")
	 public String creaLibro(@Valid @ModelAttribute("libro") Libro libro, 
             BindingResult bindingResult,
             @RequestParam("imageFile") MultipartFile imageFile, 
             Model model) throws IOException {
      this.libroValidator.validate(libro, bindingResult);
      if (!bindingResult.hasErrors()) {
       this.libroService.creaLibro(libro, bindingResult, imageFile);
       return "/admin/indexAdmin";
         } else {
         return "admin/formCreaLibro";
          }
}
	 
	 @GetMapping(value="/admin/formModificaLibro/{id}")
		public String formModificaLibro(@PathVariable("id") Long id, Model model) {
			model.addAttribute("libro", this.libroService.findById(id));
			return "admin/formModificaLibro.html";
		}
	 
	 @GetMapping(value="/admin/gestisciLibri")
		public String gestisciLibri(Model model) {
			model.addAttribute("libri", this.libroService.findAll());
			return "admin/gestisciLibri.html";
		}

	 @PostMapping("/admin/formModificaLibro/{id}")
	    public String modificalibro(@RequestParam("id") Long id,
				@RequestParam("nuovoTitolo") String nuovoTitolo,
				@RequestParam("nuovoAnno") Integer nuovoAnno,
				
			
			 Model model) {
	        this.libroService.modifica(id,nuovoTitolo,nuovoAnno);
	        return "/admin/indexAdmin";
	    }
	 
	 @GetMapping(value = "/admin/cancellaLibro/{id}")
		public String cancellaLibro(@PathVariable("id") Long id, Model model) {
			this.libroService.delete(id);
			return "admin/indexAdmin.html";
		}
}

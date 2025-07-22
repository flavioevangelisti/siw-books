package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.repository.AutoreRepository;



@Service
public class AutoreService {
	
	@Autowired
	private AutoreRepository autoreRepository;
	
	public Iterable<Autore>findAll(){
		return autoreRepository.findAll();
	}
	@Transactional
	public Autore findById(Long id) {
		return autoreRepository.findById(id).get();
	}
	
	public boolean existsByNomeAndCognome(String nome, String cognome) {
		return autoreRepository.existsByNomeAndCognome(nome, cognome);
	}
	
	@Transactional
	public Autore save(Autore autore) {
		return autoreRepository.save(autore);
		
	}
	
	@Transactional
	public void save(Autore autore,MultipartFile file) throws IOException {
		autore.setImmagine(Base64.getEncoder().encodeToString(file.getBytes()));
		autoreRepository.save(autore);		
	}
	
	 @Transactional
		public void creaAutore( Autore autore, BindingResult bindingResult,
				 MultipartFile imageFile) throws IOException {
		 
			this.save(autore, imageFile);	
		}

	
	@Transactional
	public void modifica( Long id,
			 String nuovoNome,  String nuovoCognome,
			 String nuovaNazionalita,
			 Integer nuovaDataNascita,  Integer nuovaDataMorte
			) {
		Autore a = this.findById(id);
		a.setNome(nuovoNome);
		a.setCognome(nuovoCognome);
		a.setNazionalita(nuovaNazionalita);
		a.setDataNascita(nuovaDataNascita);
		a.setDataMorte(nuovaDataMorte);
	
		this.save(a);	
	}
	
	@Transactional
	public void delete(Long id) {
		Autore autore = this.findById(id);
		autoreRepository.delete(autore);
		
	}

}
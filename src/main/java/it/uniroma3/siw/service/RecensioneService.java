package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {

	@Autowired
	private RecensioneRepository recensioneRepository;
	
	
	public Iterable<Recensione>findAll(){
		return recensioneRepository.findAll();
	}

	public boolean existsByTesto( String testo) {
		return  recensioneRepository.existsByTesto(testo);
	}

	public boolean existsByUser(User user) {
		return  recensioneRepository.existsByUser(user);
	}
	
	public boolean existsByUserAndLibro(User user, Libro libro) {
	    return recensioneRepository.existsByUserAndLibro(user, libro);
	}


	@Transactional
	public void save(Recensione recensione) {
		recensioneRepository.save(recensione);
	}

	@Transactional
	public Recensione findById(Long id) {
		return recensioneRepository.findById(id).get();
	}

	@Transactional
	public void creaRecensione(  Recensione recensione,
			User user) {
		{
            
			recensione.setUser(user);
			this.save(recensione);	

		}
	}

	@Transactional
	public void delete(Long id) {
		Recensione recensione = this.findById(id);
		recensioneRepository.delete(recensione);
	}
}
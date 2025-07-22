package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Autore;

import it.uniroma3.siw.service.AutoreService;

@Component
public class AutoreValidator implements Validator {
	
	@Autowired
	private AutoreService autoreService;
	
	
	@Override
	public void validate(Object o, Errors errors) {
	    Autore autore  = (Autore)o;
	    if (autore.getNome() != null && autore.getCognome() != null
	        && autoreService.existsByNomeAndCognome(autore.getNome(), autore.getCognome())) {
	        errors.reject("autore.duplicate");
	    }
	}

	@Override
	public boolean supports(Class<?> aClass) {
	    return Autore.class.equals(aClass);
	}

}
package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import it.uniroma3.siw.model.Recensione;

import it.uniroma3.siw.service.RecensioneService;

@Component
public class RecensioneValidator implements Validator {
	

	@Autowired
	private RecensioneService recensioneService;
	
	@Override
	public void validate(Object o, Errors errors) {
	    Recensione recensione  = (Recensione)o;
	    if ( recensione.getRecensore() != null
	        && recensioneService.existsByUser(recensione.getRecensore())) {
	        errors.reject("recensione.duplicate");
	    }
	}

	@Override
	public boolean supports(Class<?> aClass) {
	    return Recensione.class.equals(aClass);
	}

}
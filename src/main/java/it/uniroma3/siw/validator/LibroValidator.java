package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.service.LibroService;


@Component
public class LibroValidator implements Validator {

	
	@Autowired
	private LibroService libroService;
	
	@Override
	public void validate(Object o, Errors errors) {
	    Libro libro  = (Libro)o;
	    if (libro.getTitolo() != null && libro.getAnno() != null
	        && libroService.existsByTitoloAndAnno(libro.getTitolo(), libro.getAnno())) {
	        errors.reject("libro.duplicate");
	    }
	}

	@Override
	public boolean supports(Class<?> aClass) {
	    return Libro.class.equals(aClass);
	}
}
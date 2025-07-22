package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {


	@Autowired
    protected PasswordEncoder passwordEncoder;


	@Autowired
	protected CredentialsRepository credentialsRepository;
	
	
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		return result.orElse(null);
	}
	@Transactional
	public Credentials getCredentials(UserDetails user) {
		if(user != null) {
			return this.credentialsRepository.findByUsername(user.getUsername()).orElse(null);
		}
		else {
			return null;
		}
	}
    
    public Credentials saveCredentials(Credentials credentials) {
    	credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
      return credentialsRepository.save(credentials);
    }
    public Credentials saveCredentialsAdmin(Credentials credentials) {
    	credentials.setRole(Credentials.ADMIN_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
      return credentialsRepository.save(credentials);
    }
    @Transactional
	public boolean existByUsername(String username) {
		
		return this.credentialsRepository.existsByUsername(username);
	}
}
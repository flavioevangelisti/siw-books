package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

//si occupa della logica degli utenti
@Service
public class UserService {

	@Autowired
	protected UserRepository userRepository;
	
	/**
   * This method retrieves a User from the DB based on its ID.
   * @param id the id of the User to retrieve from the DB
   * @return the retrieved User, or null if no User with the passed ID could be found in the DB
   */
 @Transactional(isolation = Isolation.SERIALIZABLE)
  public User getUser(Long id) {
  	Optional<User> result = this.userRepository.findById(id);
      return result.orElse(null);
  }

  /**
   * This method saves a User in the DB.
   * @param user the User to save into the DB
   * @return the saved User
   * @throws DataIntegrityViolationException if a User with the same username
   *                              as the passed User already exists in the DB
   */
 @Transactional(isolation = Isolation.SERIALIZABLE)
  public User saveUser(User user) {
      return this.userRepository.save(user);
  }

  /**
   * This method retrieves all Users from the DB.
   * @return a List with all the retrieved Users
   */
 @Transactional
	public boolean existByEmail(String email) {

		return this.userRepository.existsByEmail(email);
	}

public User getUserByEmail(String email) {
	// TODO Auto-generated method stub
	return userRepository.findByEmail(email);
}
}
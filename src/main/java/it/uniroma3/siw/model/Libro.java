package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Libro {

	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;
	    
	    @NotBlank
		private String titolo;
	    @NotNull
	    private Integer anno;
	    @Column(length = 1000000)
		private String immagine;
	    
	    @ManyToMany
	    private List<Autore> autori;
	    
	    @OneToMany(mappedBy = "libro", cascade = CascadeType.REMOVE, orphanRemoval = true)
	    private List<Recensione> recensioni;
	    
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getTitolo() {
			return titolo;
		}
		public void setTitolo(String titolo) {
			this.titolo = titolo;
		}
		public Integer getAnno() {
			return anno;
		}
		public void setAnno(Integer anno) {
			this.anno = anno;
		}
		public String getImmagine() {
			return immagine;
		}
		public void setImmagine(String immagine) {
			this.immagine = immagine;
		}
		@Override
		public int hashCode() {
			return Objects.hash(anno, titolo);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Libro other = (Libro) obj;
			return Objects.equals(anno, other.anno) && Objects.equals(titolo, other.titolo);
		}
		public List<Autore> getAutori() {
			return autori;
		}
		public void setAutori(List<Autore> autori) {
			this.autori = autori;
		}
		public List<Recensione> getRecensioni() {
			return recensioni;
		}
		public void setRecensioni(List<Recensione> recensioni) {
			this.recensioni = recensioni;
		}
	    

}

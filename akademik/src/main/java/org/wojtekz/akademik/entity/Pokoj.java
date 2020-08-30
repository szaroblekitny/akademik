package org.wojtekz.akademik.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entity dla pokoju - odzwierciedla pokój jako rekord w bazie danych.
 * Zawiera pola:
 * <ul>
 * <li>long id
 * <li>String numerPokoju
 * <li>int liczbaMiejsc
 * <li>List &lt;Student&gt; lista zakwaterowanych studentów
 * </ul>
 * 
 * @author Wojtek Zaręba
 *
 */
@Entity
public class Pokoj implements Serializable, Plikowalny {
	private static final long serialVersionUID = 3208928506624425725L;

	@Id
	private long id;
	
	@Column(name = "numer_pokoju")
	private String numerPokoju;
	
	@Column(name="liczba_miejsc")
	private int liczbaMiejsc;
	
	@OneToMany(mappedBy="pokoj")
	private List<Student> zakwaterowani = new ArrayList<>();
	
	/**
	 * Przypisywanie studenta do pokoju.
	 * 
	 * @param student kwaterowany student
	 * 
	 */
	public void zakwateruj(Student student) {
		zakwaterowani.add(student);
		student.setPokoj(this);
	}
	
	/**
	 * Usuwanie studenta z pokoju.
	 * 
	 * @param student wypisywany student
	 * 
	 */
	public void wykwateruj(Student student) {
		zakwaterowani.remove(student);
		student.setPokoj(null);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getNumerPokoju() {
		return numerPokoju;
	}
	
	public void setNumerPokoju(String numerPokoju) {
		this.numerPokoju = numerPokoju;
	}
	
	public int getLiczbaMiejsc() {
		return liczbaMiejsc;
	}
	
	public void setLiczbaMiejsc(int liczbaMiejsc) {
		this.liczbaMiejsc = liczbaMiejsc;
	}
	
	public List<Student> getZakwaterowani() {
		return zakwaterowani;
	}

	public void setZakwaterowani(List<Student> zakwaterowani) {
		this.zakwaterowani = zakwaterowani;
	}

	@Override
	public String toString() {
		return "Pokoj [id=" + id + ", numerPokoju=" + numerPokoju
				+ ", liczbaMiejsc=" + liczbaMiejsc + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Pokoj other = (Pokoj) obj;
		return id == other.id;
	}
	
}

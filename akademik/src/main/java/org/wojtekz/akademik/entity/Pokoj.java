package org.wojtekz.akademik.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Pokoj implements Serializable, Plikowalny, Comparable<Pokoj> {
	private static final long serialVersionUID = 3208928506624425725L;

	@Id
	private long id;
	
	@Column(name = "numer_pokoju")
	private String numerPokoju;
	
	@Column(name="liczba_miejsc")
	private int liczbaMiejsc;
	
	@OneToMany(mappedBy="pokoj", fetch=FetchType.EAGER)
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

	/**
	 * Porównuje ten obiekt z określonym obiektem w celu posortowania.
	 * W przypadku klasy Pokoj całkowicie wystarczające jest porównanie
	 * po identyfikatorze.
	 * 
	 * @param innyPokoj porównywany Pokój
	 */
	@Override
	public int compareTo(Pokoj innyPokoj) {
		return (int) (innyPokoj.getId() - this.getId());
	}
	
}

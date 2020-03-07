package org.wojtekz.akademik.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity dla pokoju - odzwierciedla pokój jako rekord w bazie danych.
 * Zawiera trzy pola:
 * <ul>
 * <li>long id
 * <li>String numerPokoju
 * <li>int liczbaMiejsc
 * </ul>
 * 
 * @author Wojtek Zaręba
 *
 */
@Entity
public class Pokoj {
	@Id
	private long id;
	private String numerPokoju;
	private int liczbaMiejsc;
	
	/**
	 * Pusty konstruktor klasy Pokoj.
	 */
	public Pokoj() {
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
	
	
	@Override
	public String toString() {
		return "Pokoj [id=" + id + ", numerPokoju=" + numerPokoju
				+ ", liczbaMiejsc=" + liczbaMiejsc + "]";
	}
	

}

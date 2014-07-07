package org.wojtekz.akademik.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Pokoj {
	@Id
	private int id;
	private String numerPokoju;
	private int liczbaMiejsc;
	
	// -- constructor
	public Pokoj() {
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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

package org.wojtekz.akademik.entity;

/**
 * Wynik kwaterowania: kto został umieszczony w którym pokoju.
 * 
 * @author Wojciech Zareba
 *
 */
public class Zakwaterowani {
	private String nrPokoju;
	private String imie;
	private String nazwisko;
	
	
	public String getNrPokoju() {
		return nrPokoju;
	}
	
	public void setNrPokoju(String nrPokoju) {
		this.nrPokoju = nrPokoju;
	}
	
	public String getImie() {
		return imie;
	}
	
	public void setImie(String imie) {
		this.imie = imie;
	}
	
	public String getNazwisko() {
		return nazwisko;
	}
	
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	
}

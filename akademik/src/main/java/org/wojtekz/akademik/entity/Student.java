package org.wojtekz.akademik.entity;

import java.io.Serializable;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Odzwierciedla studenta jako rekord w bazie danych.
 * Zawiera pola:
 * <ul>
 * <li>long id
 * <li>String imie
 * <li>String nazwisko
 * <li>Plec plec
 * </ul>
 * 
 * @author Wojtek Zaręba
 *
 */
@Entity
public class Student implements Serializable, Plikowalny {
	private static final long serialVersionUID = 6073222260778454842L;
	
	@Id
	private long id;
	private String imie;
	private String nazwisko;
	
	@Convert( converter = KonwerterPlci.class )
	private Plec plec;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Plec getPlec() {
		return plec;
	}

	public void setPlec(Plec plec) {
		this.plec = plec;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", imie=" + imie + ", nazwisko="
				+ nazwisko + ", plec=" + plec + "]";
	}
	
	
} 

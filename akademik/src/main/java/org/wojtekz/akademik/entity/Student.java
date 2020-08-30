package org.wojtekz.akademik.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Odzwierciedla studenta jako rekord w bazie danych.
 * Zawiera pola:
 * <ul>
 * <li>long id
 * <li>String imie
 * <li>String nazwisko
 * <li>Plec plec
 * <li>Pokoj pokoj
 * </ul>
 * 
 * @author Wojtek Zaręba
 *
 */
@Entity
public class Student implements Serializable, Plikowalny {
	private static final long serialVersionUID = 6073222260778454843L;
	
	@Id
	private long id;
	
	/**
	 * Imię studenta.
	 */
	private String imie;
	
	/**
	 * Nazwisko studenta.
	 */
	private String nazwisko;

	/**
	 * Płeć studenta. Jestem skrajnym konserwatystą: sa dwie płcie
	 * i różnopłciowi studenci nie mieszkają razem.
	 */
	@Convert( converter = KonwerterPlci.class )
	private Plec plec;
	
	/**
	 * Pokój przydzielony studentowi podaczas kwaterunku.
	 */
	@ManyToOne
	@JoinColumn(name="pokoj_id", nullable=true)
	private Pokoj pokoj;
	

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
	

	public Pokoj getPokoj() {
		return pokoj;
	}

	public void setPokoj(Pokoj pokoj) {
		this.pokoj = pokoj;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", imie=" + imie + ", nazwisko="
				+ nazwisko + ", plec=" + plec + "]";
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
		
		Student other = (Student) obj;
		return id == other.id;
	}
	
	
} 

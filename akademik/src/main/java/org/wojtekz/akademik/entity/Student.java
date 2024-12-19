package org.wojtekz.akademik.entity;

import java.io.Serializable;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
public class Student implements Serializable, Plikowalny, Comparable<Student> {
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
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="zakwaterowani", nullable=true)
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
				+ nazwisko + ", plec=" + plec + ", nr pokoju="
				+ (pokoj != null ? pokoj.getNumerPokoju() : "niezakw.") + "]";
	}

	/**
	 * Porównuje ten obiekt z określonym obiektem do posortowania.
	 * Zwraca ujemną liczbę całkowitą, zero lub dodatnią liczbę całkowitą,
	 * ponieważ ten obiekt jest mniejszy, równy lub większy niż porównywany obiekt.
	 * Zgłasza ClassCastException, jeśli określony typ obiektu
	 * uniemożliwia porównanie go z obiektem podanym w parametrze.
	 * 
	 * <p>W przypadku klasy Student całkowicie wystarczające jest porównanie
	 * po identyfikatorze.
	 * 
	 * @param innyStudent prównywany student
	 */
	@Override
	public int compareTo(Student innyStudent) {
		return (int) (innyStudent.getId() - this.getId());
	}
	
	
} 

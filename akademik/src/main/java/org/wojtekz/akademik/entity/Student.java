package org.wojtekz.akademik.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Student {
	@Id
	private long id;
	private String imie;
	private String nazwisko;
	private Plec plec;
	
	// costructor
	public Student() {
	}

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

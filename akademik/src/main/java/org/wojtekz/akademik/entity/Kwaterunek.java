package org.wojtekz.akademik.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Ta klasa reprezentuje powiązanie pomiędzy pokojami a studentami.
 * Oprócz swojego identyfikatora ma identyfikator pokoju i studenta
 * mieszkającego w nim.
 * 
 * @author Wojtek
 *
 */
@Entity
public class Kwaterunek {
	
	@Id
	private long id;
	private long student;
	private long pokoj;
	
	/**
	 * Pusty konstruktor klasy.
	 */
	public Kwaterunek() {
		
	}
	
	/**
	 * Kostruktor parametrowy. Tworzy obiekt z wypełnionymi polami.
	 * 
	 * @param id unikalny identyfikator rekordu
	 * @param student identyfikator studenta
	 * @param pokoj identyfikator pokoju
	 */
	/*public Kwaterunek(long id, long student, long pokoj) {
		this.id = id;
		this.student = student;
		this.pokoj = pokoj;
	}*/


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStudent() {
		return student;
	}
	public void setStudent(long student) {
		this.student = student;
	}
	public long getPokoj() {
		return pokoj;
	}
	public void setPokoj(long pokoj) {
		this.pokoj = pokoj;
	}
	
	@Override
	public String toString() {
		return "Kwaterunek [id=" + id + ", student=" + student + ", pokoj=" + pokoj + "]";
	}

}

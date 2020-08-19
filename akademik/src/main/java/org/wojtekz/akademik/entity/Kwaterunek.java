package org.wojtekz.akademik.entity;

import java.io.Serializable;

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
public class Kwaterunek implements Serializable {
	
	/**
	 * Nr wersji serializacji.
	 */
	private static final long serialVersionUID = -8692581793087734924L;
	
	@Id
	private long id;
	
	private long student;
	private long pokoj;
	

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

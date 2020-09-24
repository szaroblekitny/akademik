package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Obsługa studentów od strony aplikacji JSF.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class StudentBean implements Serializable {
	private static final long serialVersionUID = 297092190215801549L;
	private static Logger logg = LogManager.getLogger();

	private transient List<Student> studenci;
	
	// dane dodane w celu dodania studenta do listy
	private transient String imie;
	private transient String nazwisko;
	private transient Plec plec;
	
	private transient Messagesy komunikaty;
	private transient StudentRepository studentRepository;
	
	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}
	
	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepository = studentRepo;
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

	/**
	 * Lista studentów dla PrimeFaces. Brana z bazy <b>tylko</b>
	 * w przypadku, gdy jest nullem. 
	 * 
	 * @return po prostu wszyscy studenci jako lista
	 */
	public List<Student> getStudenci() {
		logg.trace("---------> początek getStudenci");
		if (studenci == null) {
			logg.debug("---------> findAll studentów");
			studenci = studentRepository.findAll();
		}
		return studenci;
	}
		
	/**
	 * Reakcja na zdarzenie edycji rekordu. Wyświetla komunikat,
	 * wykwaterowuje studenta i zapisuje poprawiony rekord do bazy.
	 * 
	 * @param event zdarzenie edycji z komponentu p:cellEditor
	 */
	public void onRowEdit(RowEditEvent<Student> event) {
		Student student = event.getObject();
		student.setPokoj(null);
        studentRepository.save(student);
        komunikaty.addMessage("Edycja studenta", "Zapisany " + student.toString());
    }
    
	/**
	 * Reakcja na zdarzenie anulowania edycji. Tylko wyświetla komunikat.
	 * 
	 * @param event zdarzenie anulowania edycji z komponentu p:cellEditor
	 */
    public void onRowCancel(RowEditEvent<?> event) {
    	komunikaty.addMessage("Edycja anulowana", String.valueOf(((Student) event.getObject()).getId()));
    }

	/**
	 * Nowy rekord. Metoda wywoływana w panelu tworzenia nowego studenta.
	 * Tworzy studenta, nadaje mu ID, dopisuje resztę danych przekazanych
	 * z formatki i zapisuje rekord w bazie.
	 * 
	 */
    public void onAddNew() {
    	logg.debug("--------> dodanie nowego studenta");
    	Student nowy = new Student();
    	nowy.setId(studentRepository.findLastId() + 1L);
    	nowy.setImie(this.imie);
    	nowy.setNazwisko(this.nazwisko);
    	nowy.setPlec(this.plec);
    	studenci.add(nowy);
        komunikaty.addMessage("Tworzenie", "Nowy student " + nowy.toString());
        // zapis do bazy
        studentRepository.save(nowy);
        this.imie = null;
        this.nazwisko = null;
        this.plec = null;
    }
    
}

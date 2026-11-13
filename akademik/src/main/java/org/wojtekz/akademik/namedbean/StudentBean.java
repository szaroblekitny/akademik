package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.List;

import jakarta.faces.application.FacesMessage;


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
			wczytaj();
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
		String blad = brakImieniaLubNazwiska(student.getImie(), student.getNazwisko());
		if (blad != null) {
			komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, "Edycja studenta", blad, null);
			// obiekt w liście ma już pusty wpis z formularza; przy następnym
			// wyświetleniu lista wczyta się na nowo z bazy, gdzie dane są nietknięte
			studenci = null;
			return;
		}
		student.setImie(student.getImie().strip());
		student.setNazwisko(student.getNazwisko().strip());
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
    	// Płeć jest obowiązkowa: bez niej kwaterowanie pomija studenta, a eksport i raporty
    	// nie przechodzą przez schemat. Formularz też ma required="true", ale serwer
    	// sprawdza sam, bo przeglądarce nie można ufać (błąd zgłoszony 11.11.2026).
    	// To samo dotyczy imienia i nazwiska (błąd zgłoszony tego samego dnia).
    	String blad = brakImieniaLubNazwiska(this.imie, this.nazwisko);
    	if (blad != null) {
    		komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, "Tworzenie", blad, null);
    		return;
    	}
    	if (this.plec == null) {
    		komunikaty.addMessage(FacesMessage.SEVERITY_ERROR, "Tworzenie", "Wybierz płeć studenta", null);
    		return;
    	}
    	Student nowy = new Student();
    	nowy.setId(studentRepository.findLastId() + 1L);
    	nowy.setImie(this.imie.strip());
    	nowy.setNazwisko(this.nazwisko.strip());
    	nowy.setPlec(this.plec);
    	studenci.add(nowy);
        komunikaty.addMessage("Tworzenie", "Nowy student " + nowy.toString());
        // zapis do bazy
        studentRepository.save(nowy);
        this.imie = null;
        this.nazwisko = null;
        this.plec = null;
    }
    

	/**
	 * Sprawdza imię i nazwisko. {@code isBlank()} (Java 11) uznaje za puste także
	 * napis z samych spacji; schemat pliku danych ({@code xs:token}) i tak by je usunął
	 * i odrzucił pusty napis, więc taki student psułby potem eksport.
	 *
	 * @param imie imię z formularza
	 * @param nazwisko nazwisko z formularza
	 * @return komunikat dla użytkownika albo {@code null}, gdy oba pola są wypełnione
	 */
	private static String brakImieniaLubNazwiska(String imie, String nazwisko) {
		if (imie == null || imie.isBlank()) {
			return "Podaj imię studenta";
		}
		if (nazwisko == null || nazwisko.isBlank()) {
			return "Podaj nazwisko studenta";
		}
		return null;
	}

	/**
	 * Wczytuje studentów z bazy na nowo (audyt po Etapie 2, punkt A3). Strona woła tę metodę
	 * przez {@code <f:viewAction>} przy każdym wejściu na nią, więc po imporcie, kwaterowaniu
	 * czy zmianach na innym ekranie lista nie jest już nieaktualna. Między kliknięciami
	 * na tej samej stronie (AJAX) lista zostaje w sesji, bo edycja wierszy w tabeli
	 * PrimeFaces potrzebuje tej samej listy między żądaniami.
	 */
	public void wczytaj() {
		logg.debug("---------> wczytanie studentów z bazy");
		studenci = studentRepository.findAll();
	}
}

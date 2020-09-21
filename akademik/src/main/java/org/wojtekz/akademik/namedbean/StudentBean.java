package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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
	
	/**
	 * Lista studentów dla PrimeFaces. Brana z bazy <b>tylko</b>
	 * w przypadku, gdy jest nullem. 
	 * 
	 * @return po prostu wszyscy studenci jako lista
	 */
	public List<Student> getStudenci() {
		logg.trace("---------> początek getStudenci");
		if (studenci == null) {
			logg.debug("---------> zapis studentów");
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
	 * Nowy rekord.Tworzy studenta, nadaje mu ID, dodaje rekord do tabelki
	 * i przekazuje do edycji.
	 * 
	 */
    public void onAddNew() {
    	logg.debug("--------> dodanie nowego studenta");
        // Car car2Add = service.createCars(1).get(0);
        // cars1.add(car2Add);
    	Student nowy = new Student();
    	nowy.setId(studentRepository.findLastId() + 1L);
    	studenci.add(nowy);
        komunikaty.addMessage("Tworzenie", "Nowy student " + nowy.getId());
    }
	

}

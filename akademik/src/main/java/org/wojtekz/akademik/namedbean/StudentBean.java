package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
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

	private List<Student> studenci;
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
	 * Lista studentów dla PrimeFaces (jest deczko prościej).
	 * 
	 * @return po prostu wszyscy studenci jako lista
	 */
	public List<Student> getStudenci() {
		logg.debug("-----------> pobieram studentów do wyświetlenia");
		this.studenci = studentRepository.findAll();
		return studenci;
	}
	
	
	/**
	 * Reakcja na zdarzenie edycji rekordu. Wyświetla komunikat
	 * i zapisuje rekord do bazy.
	 * 
	 * @param event zdarzenie edycji z komponentu p:cellEditor
	 */
	public void onRowEdit(RowEditEvent<?> event) {
		Student student = (Student) event.getObject();
        komunikaty.addMessage("Edycja studenta", "Student o Id " + student.getId());
        studentRepository.save(student);
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
	 * Pobiera listę studentów z bazy i przekształca na Stringi czytelne dla strony web.
	 * 
	 * @return lista studentów
	 */
	public List<String> pobierzStudentow() {
		logg.trace("-----------> pobierzStudentow start");
		List<String> studList = new ArrayList<>();
		List<Student> listaStudentow = studentRepository.findAll();
		
		for (Student ss : listaStudentow) {
			studList.add(ss.toString());
		}
		
		logg.debug("-----------> mamy studentów dla stronki");
		return studList;
	}
	

}

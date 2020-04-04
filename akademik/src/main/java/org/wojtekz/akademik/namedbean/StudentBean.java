package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.StudentService;

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
	private transient final StudentService studentServ;
	
	@Autowired
	public StudentBean(StudentService studentService) {
		this.studentServ = studentService;
		studenci = studentServ.listAll();
	}
	
	
	/**
	 * Lista studentów dla PrimeFaces (jest deczko prościej).
	 * 
	 * @return po prostu wszyscy studenci jako lista
	 */
	public List<Student> getStudenci() {
		return studenci;
	}
	
	
	/**
	 * Reakcja na zdarzenie edycji rekordu. Wyświetla komunikat
	 * i zapisuje rekord do bazy.
	 * 
	 * @param event zdarzenie edycji z komponentu p:cellEditor
	 */
	public void onRowEdit(RowEditEvent event) {
		Student student = (Student) event.getObject();
		// String jest wymagany w FacesMessage
        FacesMessage msg = new FacesMessage("Edycja studenta",
        		"Student o Id " + String.valueOf(student.getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        studentServ.save(student);
    }
    
	/**
	 * Reakcja na zdarzenie anulowania edycji. Tylko wyświetla komunikat.
	 * 
	 * @param event zdarzenie anulowania edycji z komponentu p:cellEditor
	 */
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edycja anulowana", String.valueOf(((Student) event.getObject()).getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

	/**
	 * Pobiera listę studentów z bazy i przekształca na Stringi czytelne dla strony web.
	 * 
	 * @return lista studentów
	 */
	public List<String> pobierzStudentow() {
		logg.trace("-----------> pobierzStudentow start");
		List<String> studenci = new ArrayList<>();
		List<Student> listaStudentow = studentServ.listAll();
		
		for (Student ss : listaStudentow) {
			studenci.add(ss.toString());
		}
		
		logg.debug("-----------> mamy studentów dla stronki");
		return studenci;
	}
	

}

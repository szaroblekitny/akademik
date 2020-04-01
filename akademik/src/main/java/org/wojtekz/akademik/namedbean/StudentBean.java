package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
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
	
	@Autowired
	private transient StudentService studentServ;
	
	@PostConstruct
    public void init() {
		logg.debug("------> PostConstruct StudentBean");
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

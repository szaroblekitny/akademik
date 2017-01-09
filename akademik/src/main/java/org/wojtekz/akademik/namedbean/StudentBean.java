package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.StudentService;

/**
 * Obs³uga studentów od strony aplikacji JSF.
 * 
 * @author wojtek
 *
 */
@Component
@SessionScoped
public class StudentBean implements Serializable {
	private static final long serialVersionUID = 297092190215801549L;

	private static Logger logg = Logger.getLogger(StudentBean.class.getName());
	
	@Autowired
	StudentService studentServ;
	
	/**
	 * Pobiera listê studentów z bazy i przekszta³ca na Stringi czytelne dla strony web.
	 * 
	 * @return lista studentów
	 */
	public List<String> pobierzStudentow() {
		logg.debug("-----------> pobierzStudentow start");
		List<String> studenci = new ArrayList<>();
		List<Student> listaStudentow = new ArrayList<>();
		if (studentServ != null) {
			listaStudentow = studentServ.listAll();
		} else {
			logg.debug("-----------> nulowa lista studentów, coœ nie bangla");
		}
		
		for (Student ss : listaStudentow) {
			studenci.add(ss.toString());
		}
		
		logg.debug("-----------> mamy studentów dla stronki");
		return studenci;
	}
	

}

package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.core.Akademik;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Bean do obsługi kwaterowania - wypełniania tabelki Kwaterunek.
 * Robi to metoda {@link org.wojtekz.akademik.core.Akademik#zakwateruj}
 * wywoływana w tym komponencie.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class ZakwaterujBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();
	
	private transient StudentRepository studentRepository;
	private transient Messagesy komunikaty;
	private transient Akademik akademik;
	
	/**
	 * Wstawia klasę {@link org.wojtekz.akademik.core.Akademik} do tego komponentu.
	 * 
	 * @param akad klasa, która wykona zadanie zakwaterowania studentów
	 */
	@Autowired
	public void setAkademik(Akademik akad) {
		logg.debug("-------> setAkademik {}", akad);
		this.akademik = akad;
	}
	
	/**
	 * Wstawia klasę {@link Messagesy} do tego komponentu. Potem używa do
	 * wyświetlenia komunikatów.
	 * 
	 * @param komunikaty klasa wyswietlająca komunikaty w kontekscie JSF
	 */
	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}
	
	/**
	 * Wstawia klasę repozytorium studentów do tego komponentu.
	 * 
	 * @param studentRepository repozytorium studentów
	 */
	@Autowired
	public void setStudentRepository(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}
	
	/**
	 * Lista studentów dla strony głównej akademika. Bierze studentów z bazy.
	 * 
	 * @return po prostu wszyscy studenci jako lista
	 */
	public List<Student> getStudenci() {
		logg.debug("-----------> pobieram studentów do wyświetlenia");
		return studentRepository.findAll();
	}
	
	/**
	 * Pobiera klasę {@link org.wojtekz.akademik.core.Akademik} z tego komponentu.
	 * 
	 * @return zwracana klasa
	 */
	public Akademik getAkademik() {
		return this.akademik;
	}
	
	/**
	 * Wywołuje metodę {@link org.wojtekz.akademik.core.Akademik#zakwateruj}
	 * i wyświetla stosowny komunikat. Najpierw jednak usuwa znaczniki
	 * kwaterowania w tabeli studentów.
	 * 
	 */
	public void zakwateruj() {
		logg.debug("------> zakwateruj w Beanie");
		// rozkwaterowanie - usunięcie wpisów w polu zakwaterowani tabeli studentów
		akademik.oproznij();
		
		boolean ok = akademik.zakwateruj();
		komunikaty.addMessage("Kwaterowanie", ok ? "OK" : "Nie poszło");
	}

	/**
	 * Wywołuje metodę {@link org.wojtekz.akademik.core.Akademik#oproznij}
	 * i wyświetla stosowny komunikat.
	 */
	public void oproznij() {
		logg.debug("------> oproznij w Beanie");
		akademik.oproznij();
		komunikaty.addMessage("Opróżnianie", "Akademik opróżniony");
	}

}

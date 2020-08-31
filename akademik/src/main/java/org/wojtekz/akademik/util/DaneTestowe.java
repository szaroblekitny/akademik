package org.wojtekz.akademik.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Klasa przygotowująca dane testowe: 3 pokoje i 6 studentów.
 * 
 * @author Wojtek Zaręba
 *
 */
public class DaneTestowe {
	private static Logger logg = LogManager.getLogger();

	private Pokoj pokoj1;
	private Pokoj pokoj2;
	private Pokoj pokoj3;
	private Student student1;
	private Student student2;
	private Student student3;
	private Student student4;
	private Student student5;
	private Student student6;

	private List<Pokoj> pokoje;
	private List<Student> mieszkancy;
	
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;

	/**
	 * Wypełnia pola przykładowymi danymi testowymi zdefiniowanymi w tym konstruktorze.
	 */
	public DaneTestowe() {
		logg.trace("----->>> Dane testowe - konstruktor pełny");

		pokoj1 = new Pokoj();
		pokoj1.setId(1);
		pokoj1.setLiczbaMiejsc(2);
		pokoj1.setNumerPokoju("101");

		pokoj2 = new Pokoj();
		pokoj2.setId(2);
		pokoj2.setLiczbaMiejsc(3);
		pokoj2.setNumerPokoju("102");

		pokoj3 = new Pokoj();
		pokoj3.setId(3);
		pokoj3.setLiczbaMiejsc(4);
		pokoj3.setNumerPokoju("103");

		pokoje = new ArrayList<>();
		pokoje.add(pokoj1);
		pokoje.add(pokoj2);
		pokoje.add(pokoj3);

		// Studenci:
		student1 = new Student();
		student1.setId(1);
		student1.setImie("Jan");
		student1.setNazwisko("Kowalski");
		student1.setPlec(Plec.MEZCZYZNA);

		student2 = new Student();
		student2.setId(2);
		student2.setImie("Janina");
		student2.setNazwisko("Kowalska");
		student2.setPlec(Plec.KOBIETA);

		student3 = new Student();
		student3.setId(3);
		student3.setImie("Adam");
		student3.setNazwisko("Malinowski");
		student3.setPlec(Plec.MEZCZYZNA);

		student4 = new Student();
		student4.setId(4);
		student4.setImie("Mirosław");
		student4.setNazwisko("Nowak");
		student4.setPlec(Plec.MEZCZYZNA);

		student5 = new Student();
		student5.setId(5);
		student5.setImie("Małgorzata");
		student5.setNazwisko("Nowakowska");
		student5.setPlec(Plec.KOBIETA);

		student6 = new Student();
		student6.setId(6);
		student6.setImie("Ignacy");
		student6.setNazwisko("Patafian");
		student6.setPlec(Plec.MEZCZYZNA);

		// przestawiona kolejność
		mieszkancy = new ArrayList<>();
		mieszkancy.add(student2);
		mieszkancy.add(student1);
		mieszkancy.add(student4);
		mieszkancy.add(student3);
		mieszkancy.add(student5);
		mieszkancy.add(student6);
		
		logg.debug("----->>> Dane testowe - koniec konstruktora");
	}

	@Autowired
	public void setStudentRepo(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@Autowired
	public void setPokojRepo(PokojRepository pokojRepo) {
		this.pokojRepo = pokojRepo;
	}


	/**
	 * Wrzuca dane przekazane przez listę do tabeli w bazie określonej przez
	 * typ listy. Obsługiwane typy to Pokoj i Student. Przed wrzuceniem
	 * danych dotychczasowe są kasowane: można używać tylko w celach testowych
	 * lub do migracji danych.
	 * 
	 * @param lista lista obiektów wrzucanych do tabeli określonym przez typ listy
	 */
	public <T> void wrzucTrocheDanychDoBazy(List<T> lista) {
		logg.trace("----->>> wrzucTrocheDanychDoBazy start ");
		
		// jeśli lista nie jest pusta, mamy co wrzucać
		if (!lista.isEmpty()) {
			Class<? extends Object> superClass = lista.get(0).getClass();
			String className = superClass.getSimpleName();

			if (logg.isDebugEnabled()) {
				logg.debug("----->>> wrzucTrocheDanychDoBazy className {}", className);
			}

			// najpierw studenci, zmiana poprzedniej kolejności - ze względu na foreign key
			if (className.equals("Student")) {
				logg.debug("----->>> wrzucam dane studentów");
				studentRepo.deleteAll();
				for (T stud : lista) {
					studentRepo.save((Student) stud);
				}
			}
			
			if (className.equals("Pokoj")) {
				logg.debug("----->>> wrzucam dane pokoi");

				pokojRepo.deleteAll();
				for (T pok : lista) {
					pokojRepo.save((Pokoj) pok);
				}
			}
		}
		logg.trace("----->>> wrzucTrocheDanychDoBazy koniec ");
	}

	/**
	 * Wrzuca do bazy gotowe listy danych o typach Pokoj i Student.
	 * 
	 * @param pokoje - lista z danymi o pokojach
	 * @param studenci - lista z danymi o strudentach
	 * 
	 */
	public void wrzucTrocheDanychDoBazy(List<Pokoj> pokoje,
			List<Student> studenci) {
		logg.debug("----->>> wrzucTrocheDanychDoBazy pokoje i studenci");
		wrzucTrocheDanychDoBazy(pokoje);
		wrzucTrocheDanychDoBazy(studenci);

	}

	/**
	 * Wrzuca do bazy dane o pokojach i studentach przekazane do klasy w polach
	 * pokoje i mieszkancy.
	 * 
	 */
	public void wrzucTrocheDanychDoBazy() {
		wrzucTrocheDanychDoBazy(this.pokoje, this.mieszkancy);
	}

	// -------------------------------------------------------------------------------------
	public Pokoj getPokoj1() {
		return pokoj1;
	}

	public void setPokoj1(Pokoj pokoj1) {
		this.pokoj1 = pokoj1;
	}

	public Pokoj getPokoj2() {
		return pokoj2;
	}

	public void setPokoj2(Pokoj pokoj2) {
		this.pokoj2 = pokoj2;
	}

	public Pokoj getPokoj3() {
		return pokoj3;
	}

	public void setPokoj3(Pokoj pokoj3) {
		this.pokoj3 = pokoj3;
	}

	public Student getStudent1() {
		return student1;
	}

	public void setStudent1(Student student1) {
		this.student1 = student1;
	}

	public Student getStudent2() {
		return student2;
	}

	public void setStudent2(Student student2) {
		this.student2 = student2;
	}

	public Student getStudent3() {
		return student3;
	}

	public void setStudent3(Student student3) {
		this.student3 = student3;
	}

	public Student getStudent4() {
		return student4;
	}

	public void setStudent4(Student student4) {
		this.student4 = student4;
	}

	public Student getStudent5() {
		return student5;
	}

	public void setStudent5(Student student5) {
		this.student5 = student5;
	}

	public Student getStudent6() {
		return student6;
	}

	public void setStudent6(Student student6) {
		this.student6 = student6;
	}

	public List<Pokoj> getPokoje() {
		return pokoje;
	}

	public void setPokoje(List<Pokoj> pokoje) {
		this.pokoje = pokoje;
	}

	public List<Student> getMieszkancy() {
		return mieszkancy;
	}

	public void setMieszkancy(List<Student> mieszkancy) {
		this.mieszkancy = mieszkancy;
	}

}

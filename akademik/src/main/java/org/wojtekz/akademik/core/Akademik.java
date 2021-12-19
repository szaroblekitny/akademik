package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

import com.thoughtworks.xstream.io.StreamException;

/**
 * Centralna klasa realizująca logikę obsługi Akademika. Pobiera dane z plików konfiguracyjnych,
 * wykonuje metodę {@link #zakwateruj()} i wyświetla rezultat. W wersji aplikacji webowej
 * jest wykonywana tylko metoda zakwateruj().
 * 
 * @author wojciech Zaręba
 *
 */
@Component
public class Akademik {
	private static Logger logg = LogManager.getLogger();
	
	private Plikowanie plikowanie;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setPlikowanie(Plikowanie plikowanie) {
		this.plikowanie = plikowanie;
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
	 * Wykonuje zadania dla aplikacji wywoływanej z wiersza poleceń.
	 * Kasuje wszystkie dane z bazy, pobiera dane z plików studentów i pokoi,
	 * wykonuje zakwaterowanie i generuje stan akademika po zakwaterowaniu.
	 * Dla aplikacji webowej całkowicie nieprzydatna :-).
	 * 
	 * @param pokojeReader odczytywacz pokoi
	 * @param studenciReader odczytywacz studentów
	 * @param outputWriter zapisywacz wyniku stanu akademika po zakwaterowaniu
	 * 
	 */
	public void akademik(BufferedReader pokojeReader, BufferedReader studenciReader, BufferedWriter outputWriter) {
		// ot i cała logika naszej tekstowej aplikacji:
		try {
			
			// kasowanie wszystkiego, bo ładujemy wszystkie dane z plików
			pokojRepo.deleteAll();
			studentRepo.deleteAll();
			
			pobierzZPliku(pokojeReader);
			pobierzZPliku(studenciReader);
			String meldunek = zakwateruj();
			podajStanAkademika(outputWriter, meldunek);
			
			pokojeReader.close();
			studenciReader.close();
			outputWriter.close();
			
		} catch (NoSuchFileException nsfe) {
			logg.error("----- ERROR >> Nie ma takiego pliku: ", nsfe);
		} catch (StreamException se) {
			logg.error("----- ERROR >> Błąd parsowania pliku: ", se);
		} catch (Exception ee) {
			logg.error("----- ERROR >> Całkowicie błędny błąd: ", ee);
		}
	}
 	
	
	/**
	 * Pobiera z bufora (pliku XML) listę obiektów i zapisuje do bazy, rozpoznając
	 * typ obiektu.
	 *
	 * @param reader bufor odczytu danych (z pliku)
	 * @throws IOException jeśli jest błąd odczytu
	 */
	public void pobierzZPliku(BufferedReader reader) throws IOException {
		logg.trace("----->>> pobierzZPliku");
		
		Plikowalny pp;
		
		List<Plikowalny> pobrane = plikowanie.loadObjectList(reader);
		
		if (!pobrane.isEmpty()) {
			pp = pobrane.get(0);
		} else {
			logg.warn("----->>> pobierzZPliku brak rekordów");
			return;
		}
		
		if (pp instanceof Pokoj) {
			for (Plikowalny pok : pobrane) {
				pokojRepo.save((Pokoj) pok);
			}
			logg.debug("----->>> pokoje zapisane do bazy danych");
			return;
		}
		
		if (pp instanceof Student) {
			for (Plikowalny stud : pobrane) {
				studentRepo.save((Student) stud);
			}
			logg.debug("----->>> studenci zapisani do bazy danych");
			return;
		}
		
		logg.warn("----->>> pobierzZPliku - nieznany typ obiektu");
	}
	
	
	/**
	 * Kwateruje studentow w podanym pokoju. Dla podanego pokoju lecimy
	 * z pętlą tyle razy, jaka jest pojemność pokoju i robimy akcję
	 * zakwaterowania, czyli metodę zakwateruj() klasy Pokoj.
	 *
	 * @param pokoj pokój do zasiedlenia
	 * @param iterStud iterator do wyłapania kolejnego studenta
	 * 
	 */
	private void zakwaterujCzesc(Pokoj pokoj, Iterator<Student> iterStud) {
		logg.trace("-----*> pokoj {}", pokoj);
		Student kwatStudent;

		for (int ii = 1 ; ii <= pokoj.getLiczbaMiejsc() ; ii++ ) {
			if (iterStud.hasNext()) {
				kwatStudent = iterStud.next();
				logg.trace("-----*> kwaterujemy studenta {}", kwatStudent);
				pokoj.zakwateruj(kwatStudent);
				logg.trace("-----*> student zakwaterowany");

				studentRepo.save(kwatStudent);

				logg.trace("-----*> student zapisany");
			} else {
				return;
			}
		}

	}
	
	/**
	 * Metoda wykonująca właściwe kwaterowanie, opis w metodzie zakwateruj().
	 * 
	 * @param pokoje lista pokoi pobranych z bazy
	 * @param kobiety lista kobiet - studentek
	 * @param mezczyzni lista mężczyzn - studentów
	 * @return komunikat o wyniku kwaterowania
	 * 
	 */
	private String kwaterowanie(List<Pokoj> pokoje,
								  List<Student> kobiety,
								  List<Student> mezczyzni) {
		Iterator<Student> iterKob = kobiety.iterator();
		Iterator<Student> iterMez = mezczyzni.iterator();
		Iterator<Pokoj> iterPok = pokoje.iterator();
		
		// pętla po kobietach - damy mają pierwszeństwo
		while (iterPok.hasNext()) {
		
			if (iterKob.hasNext()) {
				zakwaterujCzesc(iterPok.next(), iterKob);
			} else {
				break;
			}
		}
		// jeśli są jescze kobiety, a nie ma pokoi - alert
		if (!iterPok.hasNext() && iterKob.hasNext()) {
			logg.info("----->>> Brakuje pokoi dla kobiet");
			logg.error("------->>> Przepełnienie Akademika <<<-------");
			return "Brakuje pokoi dla kobiet";
		} else {
			// są pokoje, więc kwaterujemy mężczyzn
			while (iterPok.hasNext()) {
				
				if (iterMez.hasNext()) {
					zakwaterujCzesc(iterPok.next(), iterMez);
				} else {
					break;
				}
			}
		}

		if (!iterPok.hasNext() && iterMez.hasNext()) {
			logg.info("----->>> Brakuje pokoi dla mężczyzn");
			logg.error("------->>> Przepełnienie Akademika <<<-------");
			return "Brakuje pokoi dla mężczyzn";
		}
		
		logg.info("---------------->>> ZAKWATEROWANO <<<-----------------");
		return "Kwaterowanie udane";
	}
	
	/**
	 * Kwaterowanie studentów w pokojach. Praktycznie wykonywany
	 * jest zapis numeru pokoju w rekordzie studenta. Rekord pokoju
	 * nie jest zmieniany.
	 * 
	 * <p>Metoda pobiera pokoje i studentów z bazy jako dwie listy.
	 * Następnie dla każdego pokoju wykonuje pętlę tyle razy, ile jest
	 * miejsc w pokoju, przypisując nr pokoju kolejnemu studentowi.
	 * 
	 * <p>Uwzględniając kobiety będziemy mieli dwie pętle: po kobietach
	 * i po mężczyznach. Stosujemy 2 razy ten sam algorytm:
	 * dla każdego pokoju lecimy z pętlą tyle razy, jaka jest pojemność pokoju
	 * i robimy akcję zakwaterowania, czyli metodę zakwaterujCzesc().
	 * 
	 * <p>Ten algorytm nie zakwateruje wszystkich mężczyzn, jeśli duży
	 * pokój zostanie zajęty przez np. jedną kobietę. Dla takiego przypadku
	 * być może mężczyzni zmieściliby się w tym pokoju, ale już
	 * jest niedostępny.
	 * 
	 * @return true, jeśli wszystkim studentom przypisano numer pokoju
	 * 
	 */
	public String zakwateruj() {
		logg.info("------------------>>> KWATERUNEK <<<-------------------");
		
		// listy studentów i pokoi
		List<Pokoj> pokoje = pokojRepo.findAll();
		logg.debug("----->>> mamy pokoi {}", pokoje.size());
		
		List<Student> kobiety = studentRepo.findByPlec(Plec.KOBIETA);
		logg.debug("----->>> mamy kobiet {}", kobiety.size());
		
		List<Student> mezczyzni = studentRepo.findByPlec(Plec.MEZCZYZNA);
		logg.debug("----->>> mamy mężczyzn {}", mezczyzni.size());
		
		return kwaterowanie(pokoje, kobiety, mezczyzni);
	}
	
	/**
	 * Opróżnienie akademika. Po prostu wymazuje numery pokoi z tebeli studentów.
	 */
	public void oproznij() {
		logg.info("-------- OPRÓŻNIENIE ---------");

		for (Student student : studentRepo.findAll()) {
			student.setPokoj(null);
    		studentRepo.save(student);
    	}
	}


	/**
	 * Wypisuje do bufora stan kwaterunku akademika w formie raportu tekstowego. Podaje listę pokoi z nazwiskami
	 * zakwaterowanych studentów.
	 * 
	 * @param writer BufferedWriter - tam zapiywany jest raport
	 * @param meldunek komunikat o wyniku kwaterowania
	 * @throws IOException błąd zapisu
	 */
	public void podajStanAkademika(BufferedWriter writer, String meldunek) throws IOException {
		List<Pokoj> spisPokoi = pokojRepo.findAll();
		for(Pokoj pokoj : spisPokoi) {
			writer.write(pokoj.toString());
			writer.newLine();
			// Wyświetlamy studentów z pokoju
			List<Student> mieszkancy = pokoj.getZakwaterowani();

			for (Student mieszka : mieszkancy) {
				writer.write(mieszka.toString());
				writer.newLine();
			}

			writer.newLine();
		}
		
		writer.write(meldunek);
		writer.newLine();
		writer.write("===================");
	}
}

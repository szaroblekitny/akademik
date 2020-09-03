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
			boolean udaloSie = zakwateruj();
			podajStanAkademika(outputWriter, udaloSie);
			
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
	 * Kwaterowanie studentów w pokojach. Praktycznie wykonywany
	 * jest zapis numeru pokoju w rekordzie studenta. Rekord pokoju
	 * nie jest zmieniany.
	 * 
	 * <p>Metoda pobiera pokoje i studentów z bazy jako dwie listy.
	 * Następnie dla każdego pokoju wykonuje pętlę tyle razy, ile jest
	 * miejsc w pokoju, przypisując nr pokoju kolejnemu studentowi.
	 * 
	 * <p>Na razie bez uwzględniania płci.
	 * 
	 * @return true, jeśli wszystkim studentom przypisano numer pokoju
	 * 
	 */
	public boolean zakwateruj() {
		logg.info("------------------>>> KWATERUNEK <<<-------------------");
		
		// listy studentów i pokoi
		List<Student> studenci = studentRepo.findAll();
		List<Pokoj> pokoje = pokojRepo.findAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi {}", pokoje.size());
			logg.debug("----->>> mamy studentów {}", studenci.size());
		}
		
		// dla każdego pokoju lecimy z pętlą tyle razy, ile jest pojemność pokoju
		// i robimy akcję zakwaterowania, czyli metodę zakwateruj()
		Iterator<Student> iter = studenci.iterator();
		Student kwatStudent = iter.next();
		for (Pokoj pokoj : pokoje) {
			
			logg.trace("-----*> pokoj {}", pokoj);
			for (int ii = 1 ; ii <= pokoj.getLiczbaMiejsc() ; ii++ ) {
				
				logg.trace("-----*> student {}", kwatStudent);
				pokoj.zakwateruj(kwatStudent);
				logg.trace("-----*> student zakwaterowany {}", kwatStudent);

				studentRepo.save(kwatStudent);
				
				if (logg.isTraceEnabled()) {
					logg.trace("-----*> student zapisany");
				}
				
				if (!iter.hasNext()) {
					// jeśli nie ma już więcej studentów mamy pozytywny koniec kwaterunku
					logg.info("------------------>>> ZAKWATEROWANI <<<-------------------");
					return true;
				} else {
					kwatStudent = iter.next();
				}
			}
		}
		
		// zabrakło miejsc
		logg.info("----->>> Nie można zakwaterować studenta {}", kwatStudent);
		logg.error("------->>> Przepełnienie Akademika <<<-------");
		return false;
		
	}
	
	
	/**
	 * Wypisuje do bufora stan kwaterunku akademika w formie raportu tekstowego. Podaje listę pokoi z nazwiskami
	 * zakwaterowanych studentów.
	 * 
	 * @param writer BufferedWriter - tam zapiywany jest raport
	 * @param udaloSie jeśli false, dodaje komunikat, że nie wszyscy studenci zostali zakwaterowani
	 * @throws IOException błąd zapisu
	 */
	public void podajStanAkademika(BufferedWriter writer, boolean udaloSie) throws IOException {
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
		
		if (!udaloSie) {
			writer.write("Nie wszyscy studenci zostali zakwaterowani");
			writer.newLine();
		}
		writer.write("===================");
	}
}

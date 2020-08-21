package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Plikowalny;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;

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
	private long kwatId;
	
	@Autowired
	private PokojService pokojService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private KwaterunekService kwaterunekService;
	@Autowired
	private Plikowanie plikowanie;
	
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
		// ot i cała logika naszej aplikacji:
		try {
			
			pokojService.deleteAll();
			studentService.deleteAll();
			kwaterunekService.deleteAll();
			
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
				pokojService.save((Pokoj) pok);
			}
			logg.debug("----->>> pokoje zapisane do bazy danych");
			return;
		}
		
		if (pp instanceof Student) {
			for (Plikowalny stud : pobrane) {
				studentService.save((Student) stud);
			}
			logg.debug("----->>> studenci zapisani do bazy danych");
			return;
		}
		
		logg.warn("----->>> pobierzZPliku - nieznany typ obiektu");
	}
	
	
	/**
	 * Tworzy wpsiy w tabeli pośredniej Kwaterunek, która zapisuje łączniki studentów
	 * z pokojami.
	 * 
	 * <p>Tu został zastosowany algorytm oparty na bazie danych: dla każdego studenta sprawdza,
	 * czy student nie ma wpisu w tabeli kwaterunek. Jeśli nie, dla każdego pokoju sprawdza liczbę wpisów
	 * dla tego pokoju. Jeśli liczba jest mniejsza od pojemności, dodaje wpis
	 * łączący pokój ze studentem. Przed kwaterunkiem jest czyszczona tabela kwaterunek.
	 * Algorytm jest kosztowny ze względu na ciągłe manipulacje w bazie danych.</p>
	 * 
	 * <p>Na razie bez uwzględniania płci.
	 * 
	 * @return true, jeśli wszyscy studenci zostali zakwaterowani
	 * 
	 */
	public boolean zakwateruj() {
		logg.info("------------------>>> KWATERUNEK <<<-------------------");
		// czyścimy kwaterunek
		kwaterunekService.deleteAll();
		kwatId = 0L;
		
		// listy studentów i pokoi
		List<Student> studenci = studentService.listAll();
		List<Pokoj> pokoje = pokojService.listAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi {}", pokoje.size());
			logg.debug("----->>> mamy studentów {}", studenci.size());
		}
		
		int iluZakwater;
		
		// dla każdego studenta
		for (Student student : studenci) {
			iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
			if (logg.isDebugEnabled()) {
				logg.debug("----->>> student {} ma przydziałów: {}", student.getId(), iluZakwater);
			}
			
			if (iluZakwater == 0) {
				petlaPoPokojach(pokoje, student);
				
				// na koniec sprawdzamy, czy student został zakwaterowany,
				// jeśli nie, mamy przepełnienie
				iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
				if (iluZakwater == 0) {
					logg.warn("----->>> Nie można zakwaterować studenta {}", student);
					logg.error("------->>> Przepełnienie Akademika <<<-------");
					return false;
				}
				
			}
			
		}  // dla każdego studenta
		
		logg.info("------------------>>> Kwaterunek zakończony <<<-------------------");
		return true;
	}
	

	/**
	 * Kwateruje studenta w pokoju dodając wpis do tabeli kwaterunek.
	 * 
	 * @param student kwaterowany student
	 * @param pokoj pokój dla studenta
	 */
	private void kwaterujStudenta(Student student, Pokoj pokoj) {
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> Nowy kwaterunek {} student {} pokój {}", kwatId, student.getId(), pokoj.getId());
		}

		Kwaterunek nowyKwaterunek = new Kwaterunek();
		nowyKwaterunek.setId(kwatId);
		nowyKwaterunek.setStudent(student.getId());
		nowyKwaterunek.setPokoj(pokoj.getId());
		kwaterunekService.save(nowyKwaterunek);
		
		kwatId++;
	}
	
	
	/**
	 * Przelatuje po pokojach usiłując wepchnąć tam studenta.
	 * 
	 * @param pokoje lista pokoi
	 * @param student delikwent do zakwaterowania
	 * 
	 */
	private void petlaPoPokojach(List<Pokoj> pokoje, Student student) {
		logg.trace("----->>> po pokojach");
		
		for (Pokoj pokoj : pokoje) {
			int zajeteMiejsca = kwaterunekService.findByIdPokoju(pokoj.getId()).size();
			
			logg.debug("----->>> dla pokoju {} miejsc: {}, zajętych {}", pokoj.getId(), pokoj.getLiczbaMiejsc(), zajeteMiejsca);
			
			if (pokoj.getLiczbaMiejsc() > zajeteMiejsca) {
				kwaterujStudenta(student, pokoj);
				// wyskakujemy z pokoi po zakwaterowaniu
				return;
			}
		}
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
		List<Pokoj> spisPokoi = pokojService.listAll();
		for(Pokoj pokoj : spisPokoi) {
			writer.write(pokoj.toString());
			writer.newLine();
			List<Student> mieszkancy = kwaterunekService.findStudenciWPokoju(pokoj.getId());
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

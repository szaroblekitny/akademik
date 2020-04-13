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
	 * Pobiera z bufora (pliku XML) listę obiektów i zapisuje je do bazy, rozpoznając
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
	 * <p>Najprostszy jest algorytm: dla każdego studenta wybiera pokój, dla którego zajętość
	 * jest mniejsza od pojemność, tworzy wpis w tabeli kwaterunku i zwiększa zajętość
	 * pokoju o jeden. Wymaga: znacznika stopnia zajętości pokoju i czyszczenia tych
	 * znaczników przed rozpoczęciem kwaterunku.</p>
	 * 
	 * <p>Tu został zastosowany algorytm oparty na bazie danych: dla każdego studenta sprawdza,
	 * czy student nie ma wpisu w tabeli kwaterunek. Jeśli nie, dla każdego pokoju sprawdza liczbę wpisów
	 * dla tego pokoju. Jeśli liczba jest mniejsza od pojemności, dodaje wpis
	 * łączący pokój ze studentem. Na oko trochę kosztowniejszy od poprzedniego, ale
	 * nie wymaga żadnych dodatkowych pól, a przed kwaterunkiem wystarczy tylko
	 * wyczyszczenie tablicy kwaterunek. Kosztowny ze względu na ciągłe
	 * manipulacje w bazie danych.</p>
	 * 
	 * <p>Na razie bez uwzględniania płci.
	 * 
	 * @return true, jeśli wszyscy studenci zostali zakwaterowani
	 * 
	 */
	public boolean zakwateruj() {
		logg.debug("----->>> zakwateruj begins");
		// czyścimy kwaterunek
		kwaterunekService.deleteAll();
		kwatId = 0L;
		
		// listy studentów i pokoi
		List<Student> studenci = studentService.listAll();
		List<Pokoj> pokoje = pokojService.listAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi {}", pokoje.size());
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
					logg.error("----->>> Przepełnienie!");
					return false;
				}
				
			}
			
		}  // dla każdego studenta
		
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
	 * Wypisuje stan kwaterunku akademika. Podaje listę pokoi z nazwiskami
	 * zakwaterowanych studentów.
	 * 
	 * @param writer BufferedWriter
	 * @param udaloSie true, jeśli wszyscy studenci zostali zakwaterowani
	 * @throws IOException błąd zapisu do pliku
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

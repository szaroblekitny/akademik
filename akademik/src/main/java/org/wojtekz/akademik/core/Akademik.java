package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.services.KwaterunekService;
import org.wojtekz.akademik.services.PokojService;
import org.wojtekz.akademik.services.StudentService;

import com.thoughtworks.xstream.io.StreamException;

@Component
public class Akademik {
	private static Logger logg = LogManager.getLogger();
	
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
			
			pobierzPokoje(pokojeReader);
			pobierzStudentow(studenciReader);
			boolean udaloSie = zakwateruj();
			podajStanAkademika(outputWriter, udaloSie);
			
			pokojeReader.close();
			studenciReader.close();
			outputWriter.close();
			
		} catch (NoSuchFileException nsfe) {
			logg.error("----- ERROR >> Nie ma takiego pliku: ", nsfe);
			System.err.println("Nie ma takiego pliku: " + nsfe.getMessage());
		} catch (StreamException se) {
			logg.error("----- ERROR >> Błąd parsowania pliku: ", se);
			System.err.println("Oczekuję pliku xml o pokojach lub studentach, " + se.getMessage());
		} catch (Exception ee) {
			logg.error("----- ERROR >> Całkowicie błędny błąd: ", ee);
		}
	}
 	
	
	/**
	 * Pobiera listę pokoi z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader BufferedReader
	 * @throws XmlMappingException błąd mapowania pól w pliku na pola klasy Pokoje
	 * @throws IOException błąd plikowy
	 */
	@SuppressWarnings("unchecked")
	public void pobierzPokoje(BufferedReader reader) throws XmlMappingException, IOException {
		logg.debug("----->>> pobierzPokoje begins");
		List<Pokoj> pokoje;
		pokoje = (List<Pokoj>) plikowanie.loadObjectList(reader);
		for (Pokoj pok : pokoje) {
			pokojService.save(pok);
		}
		logg.info("----->>> pokoje zapisane do bazy danych");
	}
	
	/**
	 * Pobiera listę studentów z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader BufferedReader
	 * @throws XmlMappingException błąd mapowania pól w pliku na pola klasy Studenci
	 * @throws IOException błąd plikowy
	 */
	@SuppressWarnings("unchecked")
	public void pobierzStudentow(BufferedReader reader) throws XmlMappingException, IOException {
		logg.debug("----->>> pobierzStudentow begins");
		List<Student> studenci;
		studenci = (List<Student>) plikowanie.loadObjectList(reader);
		
		logg.debug("----->>> studenci pobrani z pliku");
		
		for (Student student: studenci) {
			studentService.save(student);
		}
		logg.info("----->>> studenci zapisani do bazy danych");
	}
	
	
	/**
	 * Tworzy wpsiy w tabeli pośredniej Kwaterunek, która zapisuje łączniki studentów
	 * z pokojami.
	 * <p>Najprostszy algorytm: dla każdego studenta wybiera pokój, dla którego zajętość
	 * jest mniejsza od pojemność, tworzy wpis w tabeli kwaterunku i zwiększa zajętość
	 * pokoju o jeden. Wymaga: znacznika stopnia zajętości pokoju i czyszczenia tych
	 * znaczników przed rozpoczęciem kwaterunku.</p>
	 * 
	 * <p>Algorytm oparty na bazie danych: dla każdego studenta sprawdza, czy student nie
	 * ma wpisu w tabeli kwaterunek. Jeśli nie, dla każdego pokoju sprawdza liczbę wpisów
	 * dla tego pokoju. Jeśli liczba jest mniejsza od pojemności, dodaje wpis
	 * łączący pokój ze studentem. Na oko trochę kosztowniejszy od poprzedniego, ale
	 * nie wymaga żadnych dodatkowych pól, a przed kwaterunkiem wystarczy tylko
	 * wyczyszczenie tablicy kwaterunek.</p>
	 * 
	 * Na razie bez uwzględniania płci.
	 * 
	 * @return true, jeśli wszyscy studenci zostali zakwaterowani
	 * 
	 */
	public boolean zakwateruj() {
		logg.debug("----->>> zakwateruj begins");
		// czyścimy kwaterunek
		kwaterunekService.deleteAll();
		
		// listy studentów i pokoi
		List<Student> studenci = studentService.listAll();
		List<Pokoj> pokoje = pokojService.listAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi " + pokoje.size());
		}
		
		long kolKwaterunek = 1;
		int iluZakwater;
		
		// dla każdego studenta
		for (Student student : studenci) {
			iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
			if (logg.isDebugEnabled()) {
				logg.debug("----->>> dla studenta " + student.getId() + " ilu już jest: " + iluZakwater);
			}
			
			if (iluZakwater == 0) {
				logg.debug("----->>> po pokojach");
				pokojeLab:
				for (Pokoj pokoj : pokoje) {
					int zajeteMiejsca = kwaterunekService.findByIdPokoju(pokoj.getId()).size();
					
					if (logg.isDebugEnabled()) {
						logg.debug("----->>> dla pokoju " + pokoj.getId() + " miejsc: " + pokoj.getLiczbaMiejsc() + " zajętych: " + zajeteMiejsca);
					}
					
					if (pokoj.getLiczbaMiejsc() > zajeteMiejsca) {
						if (logg.isDebugEnabled()) {
							logg.debug("----->>> Nowy kwaterunek " + kolKwaterunek + "" + student.getId() + "" + pokoj.getId());
						}
						// Kwaterunek nowyKwaterunek = new Kwaterunek(kolKwaterunek, student.getId(), pokoj.getId());
						Kwaterunek nowyKwaterunek = new Kwaterunek();
						nowyKwaterunek.setId(kolKwaterunek);
						nowyKwaterunek.setStudent(student.getId());
						nowyKwaterunek.setPokoj(pokoj.getId());
						
						kolKwaterunek++;
						kwaterunekService.save(nowyKwaterunek);
						// wyskakujemy z pokoi
						break pokojeLab;
					}
				}
				
				// na koniec sprawdzamy, czy student został zakwaterowany,
				// jeśli nie, mamy przepełnienie
				iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
				if (iluZakwater == 0) {
					logg.warn("----->>> Nie można zakwaterować studenta " + student.toString());
					logg.warn("----->>> Przepełnienie!");
					return false;
				}
				
			}
			
		}  // dla każdego studenta
		
		return true;
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

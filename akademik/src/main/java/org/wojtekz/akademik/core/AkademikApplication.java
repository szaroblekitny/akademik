package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.oxm.XmlMappingException;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.dao.KwaterunekService;
import org.wojtekz.akademik.dao.PokojService;
import org.wojtekz.akademik.dao.StudentService;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

import com.thoughtworks.xstream.io.StreamException;

/**
 * To jest g³ówna klasa naszej aplikacji pod tytu³em Manager akademika. Zobacz opis aplikacji.
 * 
 * @author Wojtek Zarêba
 *
 */
public class AkademikApplication {
	private static Logger logg = Logger.getLogger(AkademikApplication.class.getName());
	private static Charset charset = StandardCharsets.UTF_8;
	
	@Autowired
	PokojService pokojService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	KwaterunekService kwaterunekService;
	
	@Autowired
	Plikowanie plikowanie;
	
	/**
	 * Wywo³anie naszej aplikacji. Podajemy plik z pokojami, plik ze studentami i plik wyjœciowy,
	 * a resztê ma zrobiæ program, czyli zakwaterowaæ studentów i wypisaæ wynik.
	 * 
	 * @param args argumenty: nazwa pliku z pokojami, nazwa pliku ze strudentami i nazwa pliku wyjœciowego
	 */
	public static void glowna(String[] args) {
		logg.info("----->>> Pocz¹tek aplikacji AkademikApplication");
		
		if (args.length != 3) {
			System.out.println("Wywo³anie z argumentami: nazwa_pliku_z_pokojami nazwa_pliku_ze_studentami nazwa_pliku_wynikowego");
			return;
		}
		
		BufferedReader pokojeReader;
		BufferedReader studenciReader;
		try {
			pokojeReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[0]), charset);
			studenciReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[1]), charset);
		} catch (IOException ie) {
			logg.error("Problemy plikowe", ie);
			System.out.println("Pliki wejœciowe s¹ niew³aœciwe");
			return;
		}
		
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AkademikConfiguration.class);
		
		AkademikApplication akademik = applicationContext.getBean(AkademikApplication.class);
		PokojService pokojService = applicationContext.getBean(PokojService.class);
		StudentService studentService = applicationContext.getBean(StudentService.class);
		KwaterunekService kwaterunekService = applicationContext.getBean(KwaterunekService.class);
		
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		// ot i ca³a logika naszej aplikacji:
		try {
			
			pokojService.deleteAll();
			studentService.deleteAll();
			kwaterunekService.deleteAll();
			
			
			BufferedWriter outputWriter = Files.newBufferedWriter(FileSystems.getDefault().getPath(args[2]), charset);
			
			akademik.pobierzPokoje(pokojeReader);
			akademik.pobierzStudentow(studenciReader);
			boolean udaloSie = akademik.zakwateruj();
			akademik.podajStanAkademika(outputWriter, udaloSie);
			
			pokojeReader.close();
			studenciReader.close();
			outputWriter.close();
			
		} catch (NoSuchFileException nsfe) {
			logg.error("----- ERROR >> Nie ma takiego pliku: ", nsfe);
			System.err.println("Nie ma takiego pliku: " + nsfe.getMessage());
		} catch (StreamException se) {
			logg.error("----- ERROR >> B³¹d parsowania pliku: ", se);
			System.err.println("Oczekujê pliku xml o pokojach lub studentach, " + se.getMessage());
		} catch (Exception ee) {
			logg.error("----- ERROR >> Ca³kowicie b³êdny b³¹d: ", ee);
			ee.printStackTrace();
		} finally {
			logg.debug("----->>> Zamykamy kontekst AkademikApplication");
			((AbstractApplicationContext) applicationContext).close();
			logg.info("----->>> Ca³kowity koniec AkademikApplication");
		
		}
	}
	
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Pobiera listê pokoi z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader BufferedReader
	 * @throws XmlMappingException b³¹d mapowania pól w pliku na pola klasy Pokoje
	 * @throws IOException b³¹d plikowy
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
	 * Pobiera listê studentów z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader BufferedReader
	 * @throws XmlMappingException b³¹d mapowania pól w pliku na pola klasy Studenci
	 * @throws IOException b³¹d plikowy
	 */
	@SuppressWarnings("unchecked")
	public void pobierzStudentow(BufferedReader reader) throws XmlMappingException, IOException {
		logg.debug("----->>> pobierzStudentow begins");
		List<Student> studenci;
		studenci = (List<Student>) plikowanie.loadObjectList(reader);
		for (Student student: studenci) {
			studentService.save(student);
		}
		logg.info("----->>> studenci zapisani do bazy danych");
	}
	
	
	/**
	 * Tworzy wpsiy w tabeli poœredniej Kwaterunek, która zapisuje ³¹czniki studentów
	 * z pokojami.
	 * <p>Najprostszy algorytm: dla ka¿dego studenta wybiera pokój, dla którego zajêtoœæ
	 * jest mniejsza od pojemnoœæ, tworzy wpis w tabeli kwaterunku i zwiêksza zajêtoœæ
	 * pokoju o jeden. Wymaga: znacznika stopnia zajêtoœci pokoju i czyszczenia tych
	 * znaczników przed rozpoczêciem kwaterunku.</p>
	 * 
	 * <p>Algorytm oparty na bazie danych: dla ka¿dego studenta sprawdza, czy student nie
	 * ma wpisu w tabeli kwaterunek. Jeœli nie, dla ka¿dego pokoju sprawdza liczbê wpisów
	 * dla tego pokoju. Jeœli liczba jest mniejsza od pojemnoœci, dodaje wpis
	 * ³¹cz¹cy pokój ze studentem. Na oko trochê kosztowniejszy od poprzedniego, ale
	 * nie wymaga ¿adnych dodatkowych pól, a przed kwaterunkiem wystarczy tylko
	 * wyczyszczenie tablicy kwaterunek.</p>
	 * 
	 * Na razie bez uwzglêdniania p³ci.
	 * 
	 * @return true, jeœli wszyscy studenci zostali zakwaterowani
	 * 
	 */
	public boolean zakwateruj() {
		logg.debug("----->>> zakwateruj begins");
		// czyœcimy kwaterunek
		kwaterunekService.deleteAll();
		
		// listy studentów i pokoi
		List<Student> studenci = studentService.listAll();
		List<Pokoj> pokoje = pokojService.listAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi " + pokoje.size());
		}
		
		long kolKwaterunek = 1;
		int iluZakwater;
		
		// dla ka¿dego studenta
		for (Student student : studenci) {
			iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
			if (logg.isDebugEnabled()) {
				logg.debug("----->>> dla studenta " + student.getId() + " ilu ju¿ jest: " + iluZakwater);
			}
			
			if (iluZakwater == 0) {
				logg.debug("----->>> po pokojach");
				pokojeLab:
				for (Pokoj pokoj : pokoje) {
					int zajeteMiejsca = kwaterunekService.findByIdPokoju(pokoj.getId()).size();
					
					if (logg.isDebugEnabled()) {
						logg.debug("----->>> dla pokoju " + pokoj.getId() + " miejsc: " + pokoj.getLiczbaMiejsc() + " zajêtych: " + zajeteMiejsca);
					}
					
					if (pokoj.getLiczbaMiejsc() > zajeteMiejsca) {
						if (logg.isDebugEnabled()) {
							logg.debug("----->>> Nowy kwaterunek " + kolKwaterunek + "" + student.getId() + "" + pokoj.getId());
						}
						Kwaterunek nowyKwaterunek = new Kwaterunek(kolKwaterunek, student.getId(), pokoj.getId());
						kolKwaterunek++;
						kwaterunekService.save(nowyKwaterunek);
						// wyskakujemy z pokoi
						break pokojeLab;
					}
				}
				
				// na koniec sprawdzamy, czy student zosta³ zakwaterowany,
				// jeœli nie, mamy przepe³nienie
				iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
				if (iluZakwater == 0) {
					logg.warn("----->>> Nie mo¿na zakwaterowaæ studenta " + student.toString());
					logg.warn("----->>> Przepe³nienie!");
					return false;
				}
				
			}
			
		}  // dla ka¿dego studenta
		
		return true;
	}
	
	/**
	 * Wypisuje stan kwaterunku akademika. Podaje listê pokoi z nazwiskami
	 * zakwaterowanych studentów.
	 * 
	 * @param writer BufferedWriter
	 * @param udaloSie true, jeœli wszyscy studenci zostali zakwaterowani
	 * @throws IOException b³¹d zapisu do pliku
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

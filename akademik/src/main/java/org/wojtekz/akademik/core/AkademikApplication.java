package org.wojtekz.akademik.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.oxm.XmlMappingException;
import org.wojtekz.akademik.conf.AkademikConfiguration;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;

/**
 * To jest g��wna klasa naszej aplikacji pod tytu�em Manager akademika. Zobacz opis aplikacji.
 * 
 * @author Wojtek
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
	 * Wywo�anie naszej aplikacji. Podajemy plik z pokojami, plik ze studentami i plik wyj�ciowy,
	 * a reszt� ma zrobi� program, czyli zakwaterowa� student�w i wypisa� wynik.
	 * 
	 * @param args argumenty: nazwa pliku z pokojami, nazwa pliku ze strudentami i nazwa pliku wyj�ciowego
	 */
	public static void main(String[] args) {
		logg.info("----->>> Pocz�tek aplikacji AkademikApplication");
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AkademikConfiguration.class);
		
		AkademikApplication akademik = applicationContext.getBean(AkademikApplication.class);
		PokojService pokojService = applicationContext.getBean(PokojService.class);
		StudentService studentService = applicationContext.getBean(StudentService.class);
		KwaterunekService kwaterunekService = applicationContext.getBean(KwaterunekService.class);
		
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		// ot i ca�a logika naszej aplikacji:
		try {
			
			pokojService.deleteAll();
			studentService.deleteAll();
			kwaterunekService.deleteAll();
			
			BufferedReader pokojeReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[0]), charset);
			BufferedReader studenciReader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[1]), charset);
			BufferedWriter outputWriter = Files.newBufferedWriter(FileSystems.getDefault().getPath(args[2]), charset);
			
			akademik.pobierzPokoje(pokojeReader);
			akademik.pobierzStudentow(studenciReader);
			akademik.zakwateruj();
			akademik.podajStanAkademika(outputWriter);
			
			pokojeReader.close();
			studenciReader.close();
			outputWriter.close();
			
		} catch (Exception ee) {
			logg.error("----- ERROR >> Ca�kowicie b��dny b��d: ", ee);
			ee.printStackTrace();
		}
		
		logg.debug("----->>> Zamykamy kontekst AkademikApplication");
		((AbstractApplicationContext) applicationContext).close();
		logg.info("----->>> Ca�kowity koniec AkademikApplication");
	}
	
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Pobiera list� pokoi z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader BufferedReader
	 * @throws XmlMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void pobierzPokoje(BufferedReader reader) throws XmlMappingException, IOException {
		logg.info("----->>> pobierzPokoje begins");
		List<Pokoj> pokoje;
		pokoje = (List<Pokoj>) plikowanie.loadObjectList(reader);
		for (Pokoj pok : pokoje) {
			pokojService.save(pok);
		}
		logg.info("----->>> pokoje zapisane");
	}
	
	/**
	 * Pobiera list� student�w z pliku XML i zapisuje w bazie danych.
	 * 
	 * @param reader
	 * @throws XmlMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void pobierzStudentow(BufferedReader reader) throws XmlMappingException, IOException {
		logg.info("----->>> pobierzStudentow begins");
		List<Student> studenci;
		studenci = (List<Student>) plikowanie.loadObjectList(reader);
		for (Student student: studenci) {
			studentService.save(student);
		}
		logg.info("----->>> pokoje zapisane");
	}
	
	
	/**
	 * Tworzy wpsiy w tabeli po�redniej Kwaterunek, kt�ra zapisuje ��czniki student�w
	 * z pokojami.
	 * <p>Najprostszy algorytm: dla ka�dego studenta wybiera pok�j, dla kt�rego zaj�to��
	 * jest mniejsza od pojemno��, tworzy wpis w tabeli kwaterunku i zwi�ksza zaj�to��
	 * pokoju o jeden. Wymaga: znacznika stopnia zaj�to�ci pokoju i czyszczenia tych
	 * znacznik�w przed rozpocz�ciem kwaterunku.</p>
	 * 
	 * <p>Alorytm oparty na bazie danych: dla ka�dego studenta sprawdza, czy student nie
	 * ma wpisu w kwaterunek. Je�li nie, dla ka�dego pokoju spradza liczb� wpis�w
	 * dla tego pokoju. Je�li liczba jest mniejsza od pojemno�ci, dodaje wpis
	 * ��cz�cy pok�j ze studentem. Na oko troch� kosztowniejszy od poprzedniego, ale
	 * nie wymaga �adnych dodatokowych p�l, a przed kwaterunkiem wystaczy tylko
	 * wyczyszczenie tablicy kwaterunek.</p>
	 * 
	 * Na razie bez uwzgl�dniania p�ci.
	 */
	public void zakwateruj() {
		logg.info("----->>> zakwateruj begins");
		// czy�cimy kwaterunek
		kwaterunekService.deleteAll();
		
		// listy student�w i pokoi
		List<Student> studenci = studentService.listAll();
		List<Pokoj> pokoje = pokojService.listAll();
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> mamy pokoi " + pokoje.size());
		}
		
		long kolKwaterunek = 1;
		int iluZakwater;
		
		// dla ka�dego studenta
		for (Student student : studenci) {
			iluZakwater = kwaterunekService.findByIdStudenta(student.getId()).size();
			if (logg.isDebugEnabled()) {
				logg.debug("----->>> dla studenta " + student.getId() + " ilu ju� jest: " + iluZakwater);
			}
			
			if (iluZakwater == 0) {
				logg.debug("----->>> po pokojach");
				pokojeLab:
				for (Pokoj pokoj : pokoje) {
					int zajeteMiejsca = kwaterunekService.findByIdPokoju(pokoj.getId()).size();
					
					if (logg.isDebugEnabled()) {
						logg.debug("----->>> dla pokoju " + pokoj.getId() + " miejsc: " + pokoj.getLiczbaMiejsc() + " zaj�tych: " + zajeteMiejsca);
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
			}
			
		}  // dla ka�dego studenta
	}
	
	/**
	 * Wypisuje stan kwaterunku akademika. Podaje list� pokoi z nazwiskami
	 * zakwaterowanych student�w.
	 * 
	 * @param writer BufferedWriter
	 * @throws IOException 
	 */
	public void podajStanAkademika(BufferedWriter writer) throws IOException {
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
		writer.write("===================");
	}
	
	/**
	 * Metoda pomocnicza s�u��ca do zapisania pokoj�w z bazy danych do pliku.
	 * Zapewne zostanie przeniesiona do innej klasy.
	 * 
	 * @param writer BufferedWriter
	 */
	public void zapiszPokojeDoBufora(BufferedWriter writer) {
		// TODO zrzucenie pokoi z bazy do bufora (plikowego)
	}

	/**
	 * Metoda pomocnicza s�u��ca do zapisania student�w z bazy danych do pliku.
	 * Zapewne zostanie przeniesiona do innej klasy.
	 * 
	 * @param writer BufferedWriter
	 */
	public void zapiszStudentowDoBufora(BufferedWriter writer) {
		// TODO zrzucenie studentow z bazy do bufora (plikowego)
	}
}

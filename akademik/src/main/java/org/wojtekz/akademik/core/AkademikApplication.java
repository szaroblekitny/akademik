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
 * To jest g³ówna klasa naszej aplikacji pod tytu³em Manager akademika. Zobacz opis aplikacji.
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
	 * Wywo³anie naszej aplikacji. Podajemy plik z pokojami, plik ze studentami i plik wyjœciowy,
	 * a resztê ma zrobiæ program, czyli zakwaterowaæ studentów i wypisaæ wynik.
	 * 
	 * @param args argumenty: nazwa pliku z pokojami, nazwa pliku ze strudentami i nazwa pliku wyjœciowego
	 */
	public static void main(String[] args) {
		logg.info("----->>> Pocz¹tek aplikacji AkademikApplication");
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
			logg.error("----- ERROR >> Ca³kowicie b³êdny b³¹d: ", ee);
			ee.printStackTrace();
		}
		
		logg.debug("----->>> Zamykamy kontekst AkademikApplication");
		((AbstractApplicationContext) applicationContext).close();
		logg.info("----->>> Ca³kowity koniec AkademikApplication");
	}
	
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Pobiera listê pokoi z pliku XML i zapisuje w bazie danych.
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
	 * Pobiera listê studentów z pliku XML i zapisuje w bazie danych.
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
	 * Tworzy wpsiy w tabeli poœredniej Kwaterunek, która zapisuje ³¹czniki studentów
	 * z pokojami.
	 * <p>Najprostszy algorytm: dla ka¿dego studenta wybiera pokój, dla którego zajêtoœæ
	 * jest mniejsza od pojemnoœæ, tworzy wpis w tabeli kwaterunku i zwiêksza zajêtoœæ
	 * pokoju o jeden. Wymaga: znacznika stopnia zajêtoœci pokoju i czyszczenia tych
	 * znaczników przed rozpoczêciem kwaterunku.</p>
	 * 
	 * <p>Alorytm oparty na bazie danych: dla ka¿dego studenta sprawdza, czy student nie
	 * ma wpisu w kwaterunek. Jeœli nie, dla ka¿dego pokoju spradza liczbê wpisów
	 * dla tego pokoju. Jeœli liczba jest mniejsza od pojemnoœci, dodaje wpis
	 * ³¹cz¹cy pokój ze studentem. Na oko trochê kosztowniejszy od poprzedniego, ale
	 * nie wymaga ¿adnych dodatokowych pól, a przed kwaterunkiem wystaczy tylko
	 * wyczyszczenie tablicy kwaterunek.</p>
	 * 
	 * Na razie bez uwzglêdniania p³ci.
	 */
	public void zakwateruj() {
		logg.info("----->>> zakwateruj begins");
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
			}
			
		}  // dla ka¿dego studenta
	}
	
	/**
	 * Wypisuje stan kwaterunku akademika. Podaje listê pokoi z nazwiskami
	 * zakwaterowanych studentów.
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
	 * Metoda pomocnicza s³u¿¹ca do zapisania pokojów z bazy danych do pliku.
	 * Zapewne zostanie przeniesiona do innej klasy.
	 * 
	 * @param writer BufferedWriter
	 */
	public void zapiszPokojeDoBufora(BufferedWriter writer) {
		// TODO zrzucenie pokoi z bazy do bufora (plikowego)
	}

	/**
	 * Metoda pomocnicza s³u¿¹ca do zapisania studentów z bazy danych do pliku.
	 * Zapewne zostanie przeniesiona do innej klasy.
	 * 
	 * @param writer BufferedWriter
	 */
	public void zapiszStudentowDoBufora(BufferedWriter writer) {
		// TODO zrzucenie studentow z bazy do bufora (plikowego)
	}
}

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
		
		logg.debug("----->>> Mamy kontekst AkademikApplication");
		
		// ot i ca�a logika naszej aplikacji:
		try {
			
			// TODO dorobi� wst�pne czyszczenie tabel
			
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
	 */
	public void zakwateruj() {
		// TODO dorobi� kwaterunek
	}
	
	/**
	 * Wypisuje stan kwaterunku akademika. Podaje list� pokoi z nazwiskami
	 * zakwaterowanych student�w.
	 * 
	 * @param writer BufferedWriter
	 */
	public void podajStanAkademika(BufferedWriter writer) {
		// TODO wypisanie listy pokoi z zakwaterowanymi studentami
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

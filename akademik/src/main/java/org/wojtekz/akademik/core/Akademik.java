package org.wojtekz.akademik.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Plec;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.plik.NieprawidlowyPlikException;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;
import org.wojtekz.akademik.service.DaneService;

/**
 * Logika kwaterunku: {@link #zakwateruj()} przydziela studentów z bazy do pokoi
 * (kobiety i mężczyzn osobno) i zwraca meldunek dla użytkownika. Aplikacja webowa
 * woła tylko tę metodę.
 * <p>
 * Metoda {@link #akademik(InputStream, BufferedWriter)} to pozostałość wersji
 * z wiersza poleceń: importuje plik danych, kwateruje i zapisuje stan akademika.
 * Używają jej już tylko testy.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
public class Akademik {
	private static Logger logg = LogManager.getLogger();
	
	private DaneService daneService;
	private StudentRepository studentRepo;
	private PokojRepository pokojRepo;
	
	@Autowired
	public void setDaneService(DaneService daneService) {
		this.daneService = daneService;
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
	 * Kasuje wszystkie dane z bazy, wczytuje pokoje i studentów z pliku danych,
	 * wykonuje zakwaterowanie i generuje stan akademika po zakwaterowaniu.
	 * Dla aplikacji webowej całkowicie nieprzydatna :-).
	 * <p>
	 * Strumienie zamyka ten, kto je otworzył (od kroku 2.1a, wcześniej robiła to ta metoda).
	 * 
	 * @param dane plik z pokojami i studentami w formacie {@code akademik.xsd}
	 * @param outputWriter zapisywacz wyniku stanu akademika po zakwaterowaniu
	 * 
	 */
	public void akademik(InputStream dane, BufferedWriter outputWriter) {
		// ot i cała logika naszej tekstowej aplikacji:
		try {
			
			// import sam kasuje stare dane (krok 2.3)
			daneService.importuj(dane);
			String meldunek = zakwateruj();
			podajStanAkademika(outputWriter, meldunek);
			outputWriter.flush();
			
		} catch (NieprawidlowyPlikException npe) {
			logg.error("----- ERROR >> Błędny plik z danymi: ", npe);
		} catch (Exception ee) {
			logg.error("----- ERROR >> Całkowicie błędny błąd: ", ee);
		}
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
		
		String meldunek = kwaterowanie(pokoje, kobiety, mezczyzni);

		// Student bez płci nie trafia na żadną z dwóch list, więc kwaterowanie go pomija.
		// Bez tego sprawdzenia meldunek mógł brzmieć „Kwaterowanie udane”, choć ktoś
		// został bez pokoju (błąd zgłoszony 11.11.2026).
		List<Student> bezPlci = studentRepo.findByPlecIsNull();
		if (!bezPlci.isEmpty()) {
			String kto = String.join(", ", bezPlci.stream()
					.map(student -> student.getImie() + " " + student.getNazwisko())
					.toList());
			logg.warn("----->>> studenci bez określonej płci, bez pokoju: {}", kto);
			String brak = "Bez określonej płci: " + kto;
			meldunek = "Kwaterowanie udane".equals(meldunek) ? brak : meldunek + ". " + brak;
		}
		return meldunek;
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

package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repo.PokojRepository;
import org.wojtekz.akademik.repo.StudentRepository;

/**
 * Obsługa strony pokoi w JSF.
 * 
 * @author Wojciech Zaręba
 *
 */
@Component
@Scope("session")
public class PokojBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logg = LogManager.getLogger();
	
	private transient List<Pokoj> pokoje;
	private transient Pokoj wybranyPokoj;
	
	private transient String numerPokoju;
	private transient int liczbaMiejsc;
	
	private transient PokojRepository pokojRepository;
	private transient StudentRepository studentRepository;
	private transient Messagesy komunikaty;
	
	
	public Pokoj getWybranyPokoj() {
		return wybranyPokoj;
	}

	public void setWybranyPokoj(Pokoj wybranyPokoj) {
		this.wybranyPokoj = wybranyPokoj;
	}

	public String getNumerPokoju() {
		return numerPokoju;
	}

	public void setNumerPokoju(String numerPokoju) {
		this.numerPokoju = numerPokoju;
	}

	public int getLiczbaMiejsc() {
		return liczbaMiejsc;
	}

	public void setLiczbaMiejsc(int liczbaMiejsc) {
		this.liczbaMiejsc = liczbaMiejsc;
	}

	@Autowired
	public void setPokojRepository(PokojRepository pokojRepository) {
		this.pokojRepository = pokojRepository;
	}
	
	@Autowired
	public void setStudentRepository(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Autowired
	public void setMessagesy(Messagesy komunikaty) {
		this.komunikaty = komunikaty;
	}

	
	/**
	 * Pobranie pokoi z bazy.
	 * <p><b>Bardzo ważna wiedza tajemna:</b>
	 * nie odtwarzamy pokoi z bazy za każdym razem, gdy zawoła je stronka, a jedynie
	 * wtedy, gdy lista jest nullem.
	 * 
	 * @return pokoje z bazy, jeśli to konieczne
	 */
	public List<Pokoj> getPokoje() {
		
		if (pokoje == null) {
			pokoje = pokojRepository.findAll();
		}
		
		return pokoje;
	}

	/**
	 * Zwraca listę pokoi jako linie metody Pokoj.toString().
	 * 
	 * @return lista pokoi do wyświetlenia bezpośrednio na stronce
	 * 
	 */
	public List<String> pobierzPokoje() {
		logg.trace("-----------> pobierzPokoje start");
		List<String> pobranePok = new ArrayList<>();
		
		for (Pokoj ss : getPokoje()) {
			pobranePok.add(ss.toString());
		}
		
		logg.trace("-----------> mamy pokoje dla stronki");
		return pobranePok;
	}
	
	
	/**
	 * Reakcja na zdarzenie edycji rekordu. Wyświetla komunikat i zapisuje rekord do bazy.
	 * Jeśli istniało powiązanie pomiędzy pokojem a studentami, jest ono odwiązywane.
	 * 
	 * @param event zdarzenie edycji z komponentu p:cellEditor
	 */
	public void onRowEdit(RowEditEvent<Pokoj> event) {
		logg.debug("---> początek onRowEdit dla zdarzenia {}", event);
		Pokoj pokoj = event.getObject();
		
        // rozwiązujemy powiązania pokoju ze studentem, jeśli były
        if (!pokoj.getZakwaterowani().isEmpty()) {
        	List<Student> odwolani = studentRepository.findByPokoj(pokoj);
        	for (Student odwo : odwolani) {
        		pokoj.wykwateruj(odwo);
        		logg.debug("------> rozwiązujemy dla pokoju {}, studenta {}", pokoj.getNumerPokoju(), odwo.getId());
        		studentRepository.save(odwo);
        	}
        }
        
        logg.debug("---> przed zapisaniem pokoju {}", pokoj);
        pokojRepository.save(pokoj);
        komunikaty.addMessage("Edycja", "Zapisany " + pokoj.toString());
    }
    
	/**
	 * Reakcja na zdarzenie anulowania edycji. Tylko wyświetla komunikat.
	 * 
	 * @param event zdarzenie anulowania edycji z komponentu p:cellEditor
	 */
    public void onRowCancel(RowEditEvent<Pokoj> event) {
    	komunikaty.addMessage("Edycja anulowana", String.valueOf(event.getObject().getId()));
    }
    
    
    /**
	 * Nowy rekord. Metoda wywoływana w panelu tworzenia nowego pokoju strony pokoj.xhtml.
	 * Tworzy pokój, nadaje mu ID, dopisuje resztę danych przekazanych
	 * z formatki i zapisuje rekord w bazie.
	 * 
	 */
    public void onAddNew() {
    	logg.debug("-----> dodanie nowego pokoju");
    	// Student nowy = new Student();
    	Pokoj nowyPokoj = new Pokoj();
    	nowyPokoj.setId(pokojRepository.findLastId() + 1L);
    	nowyPokoj.setLiczbaMiejsc(this.liczbaMiejsc);
    	nowyPokoj.setNumerPokoju(this.numerPokoju);
    	pokoje.add(nowyPokoj);
        komunikaty.addMessage("Tworzenie", "Nowy pokój " + nowyPokoj.toString());
        // zapis do bazy
        logg.debug("-----> zapis pokoju {} do bazy", nowyPokoj.getNumerPokoju());
        pokojRepository.save(nowyPokoj);
        this.numerPokoju = null;
        this.liczbaMiejsc = 0;
    }


    /**
     * Usuwa pokój przez kliknięcie przycisku. Usuwa pokój z listy pokoi utworzonej na potrzeby
     * obsługi formatki oraz z bazy danych, z tabeli pokoi. Zadaje pytanie o potwierdzenie
     * i wyświetla komunikat z potwierdzeniem usunięcia pokoju.
     * 
     */
    public void deletePokoj() {
    	logg.debug("-----> usunięcie pokoju");
    	
    	try {
    		pokojRepository.delete(this.wybranyPokoj);
    		komunikaty.addMessage("Usunięcie", "Usunięto pokój " + this.wybranyPokoj.getNumerPokoju());
    		this.pokoje.remove(this.wybranyPokoj);
    		this.wybranyPokoj = null;

    	} catch (UnexpectedRollbackException ue) {
    		komunikaty.addMessage("Usunięcie", "Nie można usunąć pokoju "
    	                         + this.wybranyPokoj.getNumerPokoju()
    		                     + ": " + ue.getLocalizedMessage());
		}
    	logg.debug("-----> Uaktualnienie komunikatów PrimeFaces");
        PrimeFaces.current().ajax().update("form:msgs", "form:pokojTab");
    }

}

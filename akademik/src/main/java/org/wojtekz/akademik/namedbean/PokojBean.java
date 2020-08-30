package org.wojtekz.akademik.namedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.repo.PokojRepository;

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
	
	private PokojRepository pokojRepository;
	
	@Autowired
	public void setPokojRepository(PokojRepository pokojRepository) {
		this.pokojRepository = pokojRepository;
	}


	/**
	 * Zwraca listę pokoi jako linie metody Pokoj.toString().
	 * 
	 * @return lista pokoi do wyświetlenia bezpośrednio na stronce
	 * 
	 */
	public List<String> pobierzPokoje() {
		logg.trace("-----------> pobierzPokoje start");
		List<String> pokoje = new ArrayList<>();
		List<Pokoj> listaPokoi = pokojRepository.findAll();
		
		for (Pokoj ss : listaPokoi) {
			pokoje.add(ss.toString());
		}
		
		logg.debug("-----------> mamy pokoje dla stronki");
		return pokoje;
	}

}

package org.wojtekz.akademik.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Pokoj;
import org.wojtekz.akademik.repos.PokojRepository;

/**
 * Implementacja serwisu pokoi.
 * 
 * @author Wojtek
 *
 */
@Repository
@Transactional(readOnly = true)
public class PokojServiceImpl implements PokojService {
	private static Logger logg = LogManager.getLogger();
	
	@Autowired
	PokojRepository pokojRep;

	@Override
	@Transactional
	public void save(Pokoj pokoj) {
		logg.debug("----->>> save Pokoj");
		pokojRep.save(pokoj);
	}

	@Override
	public List<Pokoj> listAll() {
		logg.debug("----->>> listAll Pokoj");
		
		List<Pokoj> listaPokoi = new ArrayList<>();
		listaPokoi = pokojRep.findAll();
		return listaPokoi;
	}

	@Override
	public Pokoj findByNumber(String numerPokoju) {
		logg.debug("----->>> findByNumber Pokoj");
		
		return pokojRep.findByNumber(numerPokoju);
	}

	@Override
	public long ilePokoi() {
		logg.debug("----->>> ilePokoi");
		
		return pokojRep.count();
	}

	@Override
	@Transactional
	public void deleteAll() {
		logg.debug("----->>> deleteAll Pokoj");
		
		pokojRep.deleteAll();
	}

	@Override
	@Transactional
	public void deleteByNumber(String numerPokoju) {
		logg.debug("----->>> deleteByNumber Pokoj");
		
		pokojRep.deleteByNumber(numerPokoju);
	}

}

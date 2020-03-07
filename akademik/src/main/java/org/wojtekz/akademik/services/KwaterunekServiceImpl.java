package org.wojtekz.akademik.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;
import org.wojtekz.akademik.repos.KwaterunekRepository;

/**
 * Implementacja obsługi tabelki kwaterunek.
 * 
 * @author Wojtek
 *
 */
@Repository
@Transactional(readOnly = true)
public class KwaterunekServiceImpl implements KwaterunekService {
	private static Logger logg = LogManager.getLogger();
	
	@Autowired
	KwaterunekRepository kwaterunekRep;
	
	@Autowired
	StudentService studentServ;

	@Override
	@Transactional
	public void save(Kwaterunek kwaterunek) {
		logg.debug("----->>> save kwaterunek");
		kwaterunekRep.save(kwaterunek);
	}

	@Override
	public List<Kwaterunek> listAll() {
		logg.debug("----->>> listAll");
		List<Kwaterunek> lista;
		lista = kwaterunekRep.findAll();
		return lista;
	}

	@Override
	public Kwaterunek findById(long id) {
		logg.debug("----->>> findById");
		return kwaterunekRep.findOne(id);
	}

	@Override
	public List<Kwaterunek> findByIdStudenta(long idStudenta) {
		logg.debug("----->>> findByIdStudenta");
		return kwaterunekRep.findByIdStudenta(idStudenta);
	}

	@Override
	public List<Kwaterunek> findByIdPokoju(long idPokoju) {
		logg.debug("----->>> findByIdPokoju");
		return kwaterunekRep.findByIdPokoju(idPokoju);
	}

	@Override
	public List<Student> findStudenciWPokoju(long idPokoju) {
		if (logg.isDebugEnabled()) {
			logg.debug("----->>> findStudenciWPokoju " + idPokoju);
		}
		List<Kwaterunek> zakwaterowani;
		List<Student> studenci = new ArrayList<Student>(); 
		zakwaterowani = kwaterunekRep.findByIdPokoju(idPokoju);
		
		logg.debug("----->>> Szukam zakwaterowanych");
		for (Kwaterunek kw: zakwaterowani) {
			Student studZakw = studentServ.findById(kw.getStudent());
			
			if (logg.isDebugEnabled()) {
				logg.debug("----->>> " + kw.toString());
				logg.debug("----->>> " + studZakw.toString());
			}
			studenci.add(studZakw);
		}
		return studenci;
	}

	@Override
	@Transactional
	public void deleteAll() {
		logg.debug("----->>> deleteAll");
		kwaterunekRep.deleteAll();

	}

	@Override
	@Transactional
	public void deleteById(long id) {
		logg.debug("----->>> deleteById");
		kwaterunekRep.delete(id);

	}

}

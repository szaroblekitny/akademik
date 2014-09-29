package org.wojtekz.akademik.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.dao.KwaterunekRepository;
import org.wojtekz.akademik.dao.StudentRepository;
import org.wojtekz.akademik.entity.Kwaterunek;
import org.wojtekz.akademik.entity.Student;

/**
 * Implementacja obs³ugi tabelki kwaterunek.
 * 
 * @author Wojtek
 *
 */
@Transactional(readOnly = true)
public class KwaterunekServiceImpl implements KwaterunekService {
	// private static Logger logg = Logger.getLogger(KwaterunekServiceImpl.class.getName());
	// TODO dorobiæ logowanie
	
	@Autowired
	KwaterunekRepository kwaterunekRep;
	
	@Autowired
	StudentRepository studentRep;

	@Override
	@Transactional
	public void save(Kwaterunek kwaterunek) {
		kwaterunekRep.save(kwaterunek);
	}

	@Override
	public List<Kwaterunek> listAll() {
		List<Kwaterunek> lista;
		lista = kwaterunekRep.findAll();
		return lista;
	}

	@Override
	public Kwaterunek findById(long id) {
		return kwaterunekRep.findOne(id);
	}

	@Override
	public List<Kwaterunek> findByIdStudenta(long idStudenta) {
		return kwaterunekRep.findByIdStudenta(idStudenta);
	}

	@Override
	public List<Kwaterunek> findByIdPokoju(long idPokoju) {
		return kwaterunekRep.findByIdPokoju(idPokoju);
	}

	@Override
	public List<Student> findStudenciWPokoju(long idPokoju) {
		// TODO dorobiæ TEST!
		List<Kwaterunek> zakwaterowani;
		List<Student> studenci = new ArrayList<Student>(); 
		zakwaterowani = kwaterunekRep.findByIdPokoju(idPokoju);
		
		for (Kwaterunek kw: zakwaterowani) {
			studenci.add(studentRep.findOne(kw.getStudent()));
		}
		return studenci;
	}

	@Override
	@Transactional
	public void deleteAll() {
		kwaterunekRep.deleteAll();

	}

	@Override
	@Transactional
	public void deleteById(long id) {
		kwaterunekRep.delete(id);

	}

}

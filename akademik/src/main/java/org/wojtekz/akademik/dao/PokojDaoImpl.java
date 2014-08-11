package org.wojtekz.akademik.dao;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wojtekz.akademik.entity.Pokoj;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

@Repository
@Transactional(readOnly = true)
public class PokojDaoImpl extends SimpleJpaRepository<Pokoj, Long> implements PokojDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private PokojDao repository;
	
	public PokojDaoImpl(Class<Pokoj> domainClass, EntityManager em) {
		super(domainClass, em);
		this.em = em;
	}

}

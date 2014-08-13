package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Pokoj;

public interface PokojRepository extends JpaRepository<Pokoj, Long> {
	
	@Query("select pp from Pokoj pp where pp.numerPokoju = ?1")
	public Pokoj findByNumber(String numerPokoju);
	
	@Query("delete Pokoj pp where pp.numerPokoju = ?1")
	public void deleteByNumber(String numerPokoju);

}

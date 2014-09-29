package org.wojtekz.akademik.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wojtekz.akademik.entity.Kwaterunek;

public interface KwaterunekRepository extends JpaRepository<Kwaterunek, Long> {
	
	@Query("select kw from Kwaterunek kw where kw.student = ?1")
	public List<Kwaterunek> findByIdStudenta(long idStudenta);
	
	@Query("select kw from Kwaterunek kw where kw.pokoj = ?1")
	public List<Kwaterunek> findByIdPokoju(long idPokoju);

}

package org.wojtekz.akademik.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wojtekz.akademik.entity.Pokoj;

public interface PokojDao extends JpaRepository<Pokoj, Long> {

}

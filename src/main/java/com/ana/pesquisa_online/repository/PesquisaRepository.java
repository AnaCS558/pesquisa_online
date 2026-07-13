package com.ana.pesquisa_online.repository;

import com.ana.pesquisa_online.entity.Pesquisa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesquisaRepository  extends JpaRepository<Pesquisa, Long> {
}

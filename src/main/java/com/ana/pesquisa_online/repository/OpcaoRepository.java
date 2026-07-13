package com.ana.pesquisa_online.repository;

import com.ana.pesquisa_online.entity.Opcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpcaoRepository extends JpaRepository<Opcao, Long> {
}

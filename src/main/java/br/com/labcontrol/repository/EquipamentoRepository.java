package br.com.labcontrol.repository;

import br.com.labcontrol.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data JPA para Equipamento.
 * Substitui EquipamentoDAO/EquipamentoDAOJdbc da Etapa 6.
 */
public interface EquipamentoRepository extends JpaRepository<Equipamento, Integer> {

    List<Equipamento> findByNomeContainingIgnoreCase(String nome);
}

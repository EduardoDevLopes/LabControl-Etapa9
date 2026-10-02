package br.com.labcontrol.service;

import br.com.labcontrol.exception.RegistroNaoEncontradoException;
import br.com.labcontrol.exception.ValidacaoException;
import br.com.labcontrol.model.Equipamento;
import br.com.labcontrol.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negócio relacionadas a Equipamento.
 *
 * Migrada da Etapa 6/7 preservando literalmente as mesmas validações
 * e os dois métodos de cálculo puro criados na Etapa 7
 * (calcularNivelEstoque, calcularQuantidadeAposBaixa), cobertos por
 * 16 testes JUnit em EquipamentoServiceTest. A única mudança
 * estrutural é a troca de EquipamentoDAO por EquipamentoRepository.
 */
@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    public Equipamento cadastrar(Equipamento equipamento) {
        validar(equipamento);
        return equipamentoRepository.save(equipamento);
    }

    public Equipamento atualizar(Equipamento equipamento) {
        validar(equipamento);
        Equipamento existente = buscarPorIdOuFalhar(equipamento.getId());

        existente.setNome(equipamento.getNome());
        existente.setDescricao(equipamento.getDescricao());
        existente.setQuantidade(equipamento.getQuantidade());
        existente.setStatus(equipamento.getStatus());

        return equipamentoRepository.save(existente);
    }

    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        equipamentoRepository.deleteById(id);
    }

    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAll();
    }

    public List<Equipamento> buscarPorNome(String nomeBusca) {
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            return equipamentoRepository.findAll();
        }
        return equipamentoRepository.findByNomeContainingIgnoreCase(nomeBusca.trim());
    }

    public Equipamento buscarPorIdOuFalhar(int id) {
        return equipamentoRepository.findById(id)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Equipamento não encontrado (id=" + id + ")."));
    }

    private void validar(Equipamento equipamento) {
        if (equipamento == null) {
            throw new ValidacaoException("Equipamento não pode ser nulo.");
        }
        if (isVazio(equipamento.getNome())) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (isVazio(equipamento.getDescricao())) {
            throw new ValidacaoException("A descrição é obrigatória.");
        }
        if (equipamento.getStatus() == null) {
            throw new ValidacaoException("O status é obrigatório.");
        }
        if (equipamento.getQuantidade() < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Classifica o nível de estoque de um equipamento a partir da
     * quantidade cadastrada. Migrada sem nenhuma alteração da Etapa 7
     * — os 9 casos de teste (UT-01 a UT-09) continuam válidos aqui.
     *
     *  - quantidade == 0                -> ESGOTADO
     *  - quantidade entre 1 e 2         -> CRITICO
     *  - quantidade entre 3 e 5         -> BAIXO
     *  - quantidade a partir de 6       -> NORMAL
     */
    public NivelEstoque calcularNivelEstoque(int quantidade) {
        if (quantidade < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
        if (quantidade == 0) {
            return NivelEstoque.ESGOTADO;
        }
        if (quantidade <= 2) {
            return NivelEstoque.CRITICO;
        }
        if (quantidade <= 5) {
            return NivelEstoque.BAIXO;
        }
        return NivelEstoque.NORMAL;
    }

    /**
     * Calcula a quantidade restante após a baixa (retirada) de um
     * número de unidades de um equipamento. Migrada sem alterações
     * da Etapa 7.
     */
    public int calcularQuantidadeAposBaixa(int quantidadeAtual, int quantidadeRetirada) {
        if (quantidadeRetirada <= 0) {
            throw new ValidacaoException("A quantidade a retirar deve ser maior que zero.");
        }
        if (quantidadeRetirada > quantidadeAtual) {
            throw new ValidacaoException("Quantidade insuficiente em estoque para essa retirada.");
        }
        return quantidadeAtual - quantidadeRetirada;
    }

    public enum NivelEstoque {
        ESGOTADO,
        CRITICO,
        BAIXO,
        NORMAL
    }
}

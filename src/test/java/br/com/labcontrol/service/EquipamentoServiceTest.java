package br.com.labcontrol.service;

import br.com.labcontrol.exception.ValidacaoException;
import br.com.labcontrol.model.Equipamento;
import br.com.labcontrol.model.StatusEquipamento;
import br.com.labcontrol.repository.EquipamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários (JUnit 5) da camada de serviço de Equipamento.
 *
 * Migrados da Etapa 7 para a Etapa 9: o EquipamentoDAOFake (feito à
 * mão, implementando a interface EquipamentoDAO) foi aposentado
 * junto com o próprio DAO manual, já que agora a persistência é
 * feita via Spring Data JPA (EquipamentoRepository). No lugar,
 * usamos Mockito para simular o repository — mesma ideia de
 * "isolar o service de qualquer banco real", só que com uma
 * ferramenta padrão do ecossistema Spring em vez de uma
 * implementação própria.
 *
 * Os 9 casos de calcularNivelEstoque/calcularQuantidadeAposBaixa
 * (UT-01 a UT-09, Etapa 7) foram preservados sem nenhuma alteração
 * de expectativa — são métodos de cálculo puro que nem chegam a
 * usar o repository, então nem precisam de stub no Mockito.
 */
@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository equipamentoRepository;

    private EquipamentoService service;

    @BeforeEach
    void setUp() {
        service = new EquipamentoService(equipamentoRepository);
    }

    // ---------- calcularNivelEstoque(int) ----------

    @Test
    @DisplayName("Quantidade zero deve resultar em nível ESGOTADO")
    void quantidadeZeroDeveSerEsgotado() {
        assertEquals(EquipamentoService.NivelEstoque.ESGOTADO, service.calcularNivelEstoque(0));
    }

    @ParameterizedTest(name = "quantidade={0} -> CRITICO")
    @CsvSource({"1", "2"})
    @DisplayName("Quantidades de 1 a 2 devem resultar em nível CRITICO")
    void quantidadeBaixaDeveSerCritico(int quantidade) {
        assertEquals(EquipamentoService.NivelEstoque.CRITICO, service.calcularNivelEstoque(quantidade));
    }

    @ParameterizedTest(name = "quantidade={0} -> BAIXO")
    @CsvSource({"3", "4", "5"})
    @DisplayName("Quantidades de 3 a 5 devem resultar em nível BAIXO")
    void quantidadeIntermediariaDeveSerBaixo(int quantidade) {
        assertEquals(EquipamentoService.NivelEstoque.BAIXO, service.calcularNivelEstoque(quantidade));
    }

    @ParameterizedTest(name = "quantidade={0} -> NORMAL")
    @CsvSource({"6", "10", "100"})
    @DisplayName("Quantidades a partir de 6 devem resultar em nível NORMAL")
    void quantidadeAltaDeveSerNormal(int quantidade) {
        assertEquals(EquipamentoService.NivelEstoque.NORMAL, service.calcularNivelEstoque(quantidade));
    }

    @Test
    @DisplayName("Quantidade negativa deve lançar ValidacaoException")
    void quantidadeNegativaDeveLancarExcecao() {
        ValidacaoException excecao = assertThrows(ValidacaoException.class,
                () -> service.calcularNivelEstoque(-1));
        assertTrue(excecao.getMessage().toLowerCase().contains("negativa"));
    }

    // ---------- calcularQuantidadeAposBaixa(int, int) ----------

    @Test
    @DisplayName("Baixa parcial deve subtrair corretamente do estoque atual")
    void baixaParcialDeveSubtrairCorretamente() {
        int resultado = service.calcularQuantidadeAposBaixa(10, 4);
        assertEquals(6, resultado);
    }

    @Test
    @DisplayName("Baixa que zera o estoque deve retornar zero")
    void baixaQueEsgotaEstoqueDeveRetornarZero() {
        int resultado = service.calcularQuantidadeAposBaixa(5, 5);
        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("Baixa maior que o estoque disponível deve lançar ValidacaoException")
    void baixaMaiorQueEstoqueDeveLancarExcecao() {
        assertThrows(ValidacaoException.class,
                () -> service.calcularQuantidadeAposBaixa(3, 4));
    }

    @Test
    @DisplayName("Baixa com quantidade retirada zero ou negativa deve lançar ValidacaoException")
    void baixaComQuantidadeInvalidaDeveLancarExcecao() {
        assertThrows(ValidacaoException.class,
                () -> service.calcularQuantidadeAposBaixa(10, 0));
        assertThrows(ValidacaoException.class,
                () -> service.calcularQuantidadeAposBaixa(10, -2));
    }

    // ---------- Testes complementares de regras já existentes ----------

    @Test
    @DisplayName("Cadastro de equipamento válido deve receber id gerado")
    void cadastroDeEquipamentoValidoDeveGerarId() {
        Equipamento entrada = new Equipamento("Microscópio", "Equipamento óptico", 5, StatusEquipamento.DISPONIVEL);
        Equipamento salvoComId = new Equipamento("Microscópio", "Equipamento óptico", 5, StatusEquipamento.DISPONIVEL);
        salvoComId.setId(1);

        when(equipamentoRepository.save(any(Equipamento.class))).thenReturn(salvoComId);

        Equipamento resultado = service.cadastrar(entrada);
        assertTrue(resultado.getId() > 0);
    }

    @Test
    @DisplayName("Cadastro de equipamento com quantidade negativa deve falhar")
    void cadastroComQuantidadeNegativaDeveFalhar() {
        Equipamento equipamento = new Equipamento("Balança", "Balança digital", -1, StatusEquipamento.DISPONIVEL);
        assertThrows(ValidacaoException.class, () -> service.cadastrar(equipamento));
    }
}

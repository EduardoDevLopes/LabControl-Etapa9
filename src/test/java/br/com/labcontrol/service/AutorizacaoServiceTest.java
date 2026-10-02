package br.com.labcontrol.service;

import br.com.labcontrol.model.PerfilUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários (JUnit 5) das regras de autorização por perfil.
 * Assim como os cálculos de EquipamentoService, esta é uma regra de
 * negócio pura (sem acesso a banco), ideal para teste unitário.
 */
class AutorizacaoServiceTest {

    private final AutorizacaoService autorizacao = new AutorizacaoService();

    @Test
    @DisplayName("Administrador deve poder gerenciar usuários")
    void administradorPodeGerenciarUsuarios() {
        assertTrue(autorizacao.podeGerenciarUsuarios(PerfilUsuario.ADMINISTRADOR));
    }

    @Test
    @DisplayName("Operador não deve poder gerenciar usuários")
    void operadorNaoPodeGerenciarUsuarios() {
        assertFalse(autorizacao.podeGerenciarUsuarios(PerfilUsuario.OPERADOR));
    }

    @Test
    @DisplayName("Técnico de laboratório deve poder gerenciar equipamentos")
    void tecnicoPodeGerenciarEquipamentos() {
        assertTrue(autorizacao.podeGerenciarEquipamentos(PerfilUsuario.TECNICO_LABORATORIO));
    }

    @Test
    @DisplayName("Operador não deve poder gerenciar equipamentos")
    void operadorNaoPodeGerenciarEquipamentos() {
        assertFalse(autorizacao.podeGerenciarEquipamentos(PerfilUsuario.OPERADOR));
    }

    @Test
    @DisplayName("Qualquer perfil cadastrado deve poder consultar equipamentos")
    void qualquerPerfilPodeConsultarEquipamentos() {
        assertTrue(autorizacao.podeConsultarEquipamentos(PerfilUsuario.OPERADOR));
        assertTrue(autorizacao.podeConsultarEquipamentos(PerfilUsuario.ADMINISTRADOR));
        assertTrue(autorizacao.podeConsultarEquipamentos(PerfilUsuario.TECNICO_LABORATORIO));
    }

    @Test
    @DisplayName("Perfil nulo não deve poder consultar equipamentos")
    void perfilNuloNaoPodeConsultarEquipamentos() {
        assertFalse(autorizacao.podeConsultarEquipamentos(null));
    }
}

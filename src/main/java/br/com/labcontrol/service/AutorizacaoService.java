package br.com.labcontrol.service;

import br.com.labcontrol.model.PerfilUsuario;
import org.springframework.stereotype.Service;

/**
 * Regras de autorização por perfil de usuário.
 *
 * Migrada sem alterações da Etapa 6/7 (coberta por
 * AutorizacaoServiceTest, 6 testes JUnit). Aqui, além de continuar
 * servindo a checagem de UI, passa a ser usada também por
 * SessaoInterceptor para bloquear chamadas de API feitas por um
 * perfil sem permissão (ver config/SessaoInterceptor.java).
 */
@Service
public class AutorizacaoService {

    public boolean podeGerenciarUsuarios(PerfilUsuario perfil) {
        return perfil == PerfilUsuario.ADMINISTRADOR;
    }

    public boolean podeGerenciarEquipamentos(PerfilUsuario perfil) {
        return perfil == PerfilUsuario.ADMINISTRADOR || perfil == PerfilUsuario.TECNICO_LABORATORIO;
    }

    public boolean podeConsultarEquipamentos(PerfilUsuario perfil) {
        return perfil != null;
    }
}

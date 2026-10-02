package br.com.labcontrol.model;

/**
 * Perfis de acesso do sistema.
 *
 * Migrado sem alterações de comportamento a partir da Etapa 6/7 — o
 * enum já existia como camada de domínio isolada de Swing, então a
 * migração para JPA foi só anotar Usuario.perfil com
 * @Enumerated(EnumType.STRING) (ver Usuario.java), sem tocar aqui.
 */
public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    TECNICO_LABORATORIO("Técnico de Laboratório"),
    OPERADOR("Operador");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static PerfilUsuario fromDescricao(String descricao) {
        for (PerfilUsuario p : values()) {
            if (p.descricao.equalsIgnoreCase(descricao)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Perfil inválido: " + descricao);
    }

    @Override
    public String toString() {
        return descricao;
    }
}

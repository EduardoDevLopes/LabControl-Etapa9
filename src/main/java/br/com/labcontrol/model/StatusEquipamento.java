package br.com.labcontrol.model;

/**
 * Status possíveis de um equipamento.
 *
 * Nesta etapa foi adicionado o valor MANUTENCAO: o front-end da
 * Etapa 8 já previa essa terceira opção no formulário de equipamento
 * (dropdown Disponível / Em uso / Manutenção), então o back-end é
 * ajustado aqui para acompanhar — nenhuma migração de schema é
 * necessária, já que a coluna 'status' já era VARCHAR.
 */
public enum StatusEquipamento {
    DISPONIVEL("Disponível"),
    EM_USO("Em uso"),
    MANUTENCAO("Em manutenção");

    private final String descricao;

    StatusEquipamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusEquipamento fromDescricao(String descricao) {
        for (StatusEquipamento s : values()) {
            if (s.descricao.equalsIgnoreCase(descricao)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + descricao);
    }

    @Override
    public String toString() {
        return descricao;
    }
}

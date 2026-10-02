package br.com.labcontrol.dto;

import br.com.labcontrol.model.Equipamento;
import br.com.labcontrol.model.StatusEquipamento;
import br.com.labcontrol.service.EquipamentoService;

/**
 * DTO de Equipamento para a API REST.
 *
 * Inclui o campo 'nivelEstoque', calculado no momento da conversão
 * (ver deEntidade) a partir de EquipamentoService.calcularNivelEstoque().
 * Isso elimina a necessidade de recalcular o nível em JavaScript no
 * front-end (como acontecia em mock-data.js na Etapa 8) — a partir
 * desta etapa, o front-end apenas exibe o valor que a API já entrega
 * pronto, e a lógica de cálculo vive em um único lugar.
 */
public class EquipamentoDTO {

    private Integer id;
    private String nome;
    private String descricao;
    private int quantidade;
    private StatusEquipamento status;
    private EquipamentoService.NivelEstoque nivelEstoque;

    public EquipamentoDTO() {
    }

    public static EquipamentoDTO deEntidade(Equipamento equipamento, EquipamentoService.NivelEstoque nivelEstoque) {
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.id = equipamento.getId();
        dto.nome = equipamento.getNome();
        dto.descricao = equipamento.getDescricao();
        dto.quantidade = equipamento.getQuantidade();
        dto.status = equipamento.getStatus();
        dto.nivelEstoque = nivelEstoque;
        return dto;
    }

    public Equipamento paraEntidade() {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(this.id);
        equipamento.setNome(this.nome);
        equipamento.setDescricao(this.descricao);
        equipamento.setQuantidade(this.quantidade);
        equipamento.setStatus(this.status);
        return equipamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public StatusEquipamento getStatus() {
        return status;
    }

    public void setStatus(StatusEquipamento status) {
        this.status = status;
    }

    public EquipamentoService.NivelEstoque getNivelEstoque() {
        return nivelEstoque;
    }

    public void setNivelEstoque(EquipamentoService.NivelEstoque nivelEstoque) {
        this.nivelEstoque = nivelEstoque;
    }
}

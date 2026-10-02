package br.com.labcontrol.controller;

import br.com.labcontrol.dto.EquipamentoDTO;
import br.com.labcontrol.model.Equipamento;
import br.com.labcontrol.service.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de CRUD de equipamentos, consumida por equipamentos.js e
 * equipamento-form.js (adaptados da Etapa 8) via fetch(). Protegida
 * por SessaoInterceptor (ver WebConfig).
 *
 * Cada equipamento devolvido já traz o campo nivelEstoque calculado
 * (ver EquipamentoDTO), então o front-end não precisa mais recalcular
 * isso em JavaScript como fazia na Etapa 8 (mock-data.js).
 */
@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoApiController {

    private final EquipamentoService equipamentoService;

    public EquipamentoApiController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    public List<EquipamentoDTO> listar(@RequestParam(required = false) String busca) {
        List<Equipamento> equipamentos = equipamentoService.buscarPorNome(busca);
        return equipamentos.stream()
            .map(this::comNivelEstoque)
            .toList();
    }

    @GetMapping("/{id}")
    public EquipamentoDTO buscarPorId(@PathVariable int id) {
        return comNivelEstoque(equipamentoService.buscarPorIdOuFalhar(id));
    }

    @PostMapping
    public ResponseEntity<EquipamentoDTO> cadastrar(@RequestBody EquipamentoDTO dto) {
        Equipamento salvo = equipamentoService.cadastrar(dto.paraEntidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(comNivelEstoque(salvo));
    }

    @PutMapping("/{id}")
    public EquipamentoDTO atualizar(@PathVariable int id, @RequestBody EquipamentoDTO dto) {
        dto.setId(id);
        Equipamento atualizado = equipamentoService.atualizar(dto.paraEntidade());
        return comNivelEstoque(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        equipamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint auxiliar usado pela prévia dinâmica de nível de
     * estoque no formulário (equipamento-form.js), evitando recalcular
     * a regra em JavaScript no cliente.
     */
    @GetMapping("/nivel-estoque")
    public EquipamentoService.NivelEstoque calcularNivelEstoque(@RequestParam int quantidade) {
        return equipamentoService.calcularNivelEstoque(quantidade);
    }

    private EquipamentoDTO comNivelEstoque(Equipamento equipamento) {
        EquipamentoService.NivelEstoque nivel = equipamentoService.calcularNivelEstoque(equipamento.getQuantidade());
        return EquipamentoDTO.deEntidade(equipamento, nivel);
    }
}

package br.com.labcontrol.controller;

import br.com.labcontrol.dto.UsuarioDTO;
import br.com.labcontrol.exception.ValidacaoException;
import br.com.labcontrol.model.Usuario;
import br.com.labcontrol.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de CRUD de usuários, consumida por usuarios.js e
 * usuario-form.js (adaptados da Etapa 8) via fetch(). Protegida por
 * SessaoInterceptor (ver WebConfig) — exige login ativo.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.listarTodos().stream()
            .map(UsuarioDTO::deEntidade)
            .toList();
    }

    @GetMapping("/{id}")
    public UsuarioDTO buscarPorId(@PathVariable int id) {
        return UsuarioDTO.deEntidade(usuarioService.buscarPorIdOuFalhar(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody UsuarioDTO dto) {
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new ValidacaoException("Informe uma senha para o novo usuário.");
        }
        Usuario salvo = usuarioService.cadastrar(dto.paraEntidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.deEntidade(salvo));
    }

    @PutMapping("/{id}")
    public UsuarioDTO atualizar(@PathVariable int id, @RequestBody UsuarioDTO dto) {
        dto.setId(id);
        Usuario atualizado = usuarioService.atualizar(dto.paraEntidade());
        return UsuarioDTO.deEntidade(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

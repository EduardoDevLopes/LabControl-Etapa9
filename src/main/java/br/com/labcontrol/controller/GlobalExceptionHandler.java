package br.com.labcontrol.controller;

import br.com.labcontrol.dto.ErroRespostaDTO;
import br.com.labcontrol.exception.AutenticacaoException;
import br.com.labcontrol.exception.RegistroNaoEncontradoException;
import br.com.labcontrol.exception.ValidacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Traduz as exceções de domínio (ValidacaoException,
 * AutenticacaoException, RegistroNaoEncontradoException) em respostas
 * HTTP com corpo JSON no formato ErroRespostaDTO, para todos os
 * controllers REST da aplicação.
 *
 * Sem esta classe, uma ValidacaoException lançada dentro de um
 * service apareceria para o cliente como um HTTP 500 genérico (erro
 * interno do servidor) com stack trace exposto — o que é tanto uma
 * má experiência quanto um risco de segurança (vazamento de detalhes
 * internos). Com o handler, o front-end recebe um 400/401/404 com uma
 * mensagem clara para exibir ao usuário.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(ValidacaoException ex) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de validação",
            List.of(ex.getMessage())
        );
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAutenticacao(AutenticacaoException ex) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(
            HttpStatus.UNAUTHORIZED.value(),
            "Falha na autenticação",
            List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corpo);
    }

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(RegistroNaoEncontradoException ex) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(
            HttpStatus.NOT_FOUND.value(),
            "Registro não encontrado",
            List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(Exception ex) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno",
            List.of("Ocorreu um erro inesperado. Tente novamente.")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(corpo);
    }
}

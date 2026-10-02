package br.com.labcontrol.exception;

/**
 * Lançada quando dados de entrada não atendem às regras de negócio
 * (campo obrigatório vazio, quantidade negativa, login duplicado etc.).
 * Migrada sem alterações da Etapa 6/7 — GlobalExceptionHandler traduz
 * esta exceção em uma resposta HTTP 400 com corpo JSON padronizado.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}

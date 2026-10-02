package br.com.labcontrol.exception;

/**
 * Lançada quando login/senha não conferem. Traduzida pelo
 * GlobalExceptionHandler em HTTP 401.
 */
public class AutenticacaoException extends RuntimeException {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}

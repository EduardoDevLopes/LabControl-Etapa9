package br.com.labcontrol.exception;

/**
 * Lançada quando um registro buscado por id não existe. Traduzida
 * pelo GlobalExceptionHandler em HTTP 404.
 */
public class RegistroNaoEncontradoException extends RuntimeException {

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

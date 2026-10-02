package br.com.labcontrol.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrão de resposta de erro da API, devolvido pelo
 * GlobalExceptionHandler. O front-end (api.js) sempre pode contar com
 * esta mesma forma para qualquer erro 4xx/5xx.
 */
public class ErroRespostaDTO {

    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private List<String> mensagens;

    public ErroRespostaDTO(int status, String erro, List<String> mensagens) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagens = mensagens;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public List<String> getMensagens() {
        return mensagens;
    }
}

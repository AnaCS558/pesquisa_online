package com.ana.pesquisa_online.exception;

import java.util.Map;

public class ErrorResponse {

    private int status;
    private String mensagem;
    private Map<String, String> erros;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public ErrorResponse(int status, String mensagem, Map<String, String> erros) {
        this.status = status;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Map<String, String> getErros() {
        return erros;
    }

    public void setErros(Map<String, String> erros) {
        this.erros = erros;
    }
}

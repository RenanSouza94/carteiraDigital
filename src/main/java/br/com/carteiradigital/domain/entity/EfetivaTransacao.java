package br.com.carteiradigital.domain.entity;

public class EfetivaTransacao  {

    public EfetivaTransacao() {
    }

    public EfetivaTransacao(String identificacao,
                            boolean cancelar) {
        this.identificacao = identificacao;
        this.cancelar = cancelar;
    }

    private String identificacao;
    private boolean cancelar;

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public boolean isCancelar() {
        return cancelar;
    }

    public void setCancelar(boolean cancelar) {
        this.cancelar = cancelar;
    }
}

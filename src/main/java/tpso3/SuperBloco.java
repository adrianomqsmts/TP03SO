package tpso3;

public class SuperBloco {

    private int numeroMagico;
    private int tamanhoBloco;
    private int qtdeBlocos;
    private int qtdeBlocosLivres;

    public SuperBloco(int tamanhoBloco, int qtdeBlocos) {
        this.numeroMagico = 12345;
        this.tamanhoBloco = tamanhoBloco;
        this.qtdeBlocos = qtdeBlocos;
        this.qtdeBlocosLivres = qtdeBlocos;
    }

    public int getNumeroMagico() {
        return numeroMagico;
    }

    public void setNumeroMagico(int numeroMagico) {
        this.numeroMagico = numeroMagico;
    }

    public int getTamanhoBloco() {
        return tamanhoBloco;
    }

    public void setTamanhoBloco(int tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
    }

    public int getQtdeBlocos() {
        return qtdeBlocos;
    }

    public void setQtdeBlocos(int qtdeBlocos) {
        this.qtdeBlocos = qtdeBlocos;
    }

    public int getQtdeBlocosLivres() {
        return qtdeBlocosLivres;
    }

    public void setQtdeBlocosLivres(int qtdeBlocosLivres) {
        this.qtdeBlocosLivres = qtdeBlocosLivres;
    }
}

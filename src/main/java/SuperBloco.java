public class SuperBloco {

    private int numeroMagico;
    private int tamanhoBloco;
    private int qtdeBlocosTotais;
    private int qtdeBlocosLivres;

    public SuperBloco(int tamanhoBloco, int qtdeBlocosTotais) {
        this.numeroMagico = 12345;
        this.tamanhoBloco = tamanhoBloco;
        this.qtdeBlocosTotais = qtdeBlocosTotais;
        this.qtdeBlocosLivres = qtdeBlocosTotais;
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

    public int getQtdeBlocosTotais() {
        return qtdeBlocosTotais;
    }

    public void setQtdeBlocosTotais(int qtdeBlocosTotais) {
        this.qtdeBlocosTotais = qtdeBlocosTotais;
    }

    public int getQtdeBlocosLivres() {
        return qtdeBlocosLivres;
    }

    public void setQtdeBlocosLivres(int qtdeBlocosLivres) {
        this.qtdeBlocosLivres = qtdeBlocosLivres;
    }
}

package tpso3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Inode implements Serializable {
    private final Date criado;
    private Date modificado;
    private Date acessado;
    private int tamanhoArquivo;
    private List<Integer> listaEnderecos;

    public Inode(int tamanhoArquivo, List<Integer> listaEnderecos) {
        this.criado = new Date(System.currentTimeMillis());
        this.modificado = new Date(System.currentTimeMillis());
        this.acessado = new Date(System.currentTimeMillis());
        this.tamanhoArquivo = tamanhoArquivo;
        this.listaEnderecos = listaEnderecos;
    }

    public boolean inserirEndereco(int endereco){
        return listaEnderecos.add(endereco);
    }

    public void removerEndereco(int endereco){
        listaEnderecos.remove(endereco);
    }

    public List<Integer> getListaEnderecos() {
        return listaEnderecos;
    }

    public void setListaEnderecos(List<Integer> listaEnderecos) {
        this.listaEnderecos = listaEnderecos;
    }

    public Date getCriado() {
        return criado;
    }

    public Date getModificado() {
        return modificado;
    }

    public void setModificado(Date Modificado) {
        this.modificado = Modificado;
    }

    public Date getAcessado() {
        return acessado;
    }

    public void setAcessado(Date Acessado) {
        this.acessado = Acessado;
    }

    public int getTamanhoArquivo() {
        return tamanhoArquivo;
    }

    public void setTamanhoArquivo(int tamanhoArquivo) {
        this.tamanhoArquivo = tamanhoArquivo;
    }

}

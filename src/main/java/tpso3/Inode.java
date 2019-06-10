package tpso3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Inode implements Serializable {
    private final Date criado;
    private Date modificado;
    private Date acessado;
    private int tamanho;
    private List<Integer> listaEnderecos;

    public Inode(int tamanho) {
        this.criado = new Date(System.currentTimeMillis());
        this.modificado = new Date(System.currentTimeMillis());
        this.acessado = new Date(System.currentTimeMillis());
        this.tamanho = tamanho;
        this.listaEnderecos = new ArrayList<Integer>();
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

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

}

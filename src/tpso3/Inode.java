package tpso3;

import java.io.Serializable;
import java.util.Date;

public class Inode implements Serializable {

    private final Date Criado;
    private Date Modificado;
    private Date Acessado;
    private int tamanho;

    public Inode(int tamanho, String nome) {
        this.Criado = new Date(System.currentTimeMillis());
        this.Modificado = new Date(System.currentTimeMillis());
        this.Acessado = new Date(System.currentTimeMillis());
        this.tamanho = tamanho;
    }

    public Date getCriado() {
        return Criado;
    }

    public Date getModificado() {
        return Modificado;
    }

    public void setModificado(Date Modificado) {
        this.Modificado = Modificado;
    }

    public Date getAcessado() {
        return Acessado;
    }

    public void setAcessado(Date Acessado) {
        this.Acessado = Acessado;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

}

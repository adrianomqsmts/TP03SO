package tpso3;


import java.io.Serializable;

public class Info implements Serializable {
    private String nomeArquivo;
    private Inode inode;

    public Info(String nomeArquivo, Inode inode) {
        this.nomeArquivo = nomeArquivo;
        this.inode = inode;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Inode getInode() {
        return inode;
    }

    public void setInode(Inode inode) {
        this.inode = inode;
    }
}

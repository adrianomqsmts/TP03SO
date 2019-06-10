package tpso3;

import java.io.Serializable;

public class Diretorio implements Serializable{
    
    private String nomeDiretorio;

    public Diretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
    }

    private class Info implements Serializable{
        String nomeArquivo;
        Inode inode;

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

    public String getNomeDiretorio() {
        return nomeDiretorio;
    }

    public void setNomeDiretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
    }
}

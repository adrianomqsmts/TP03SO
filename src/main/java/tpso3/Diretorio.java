package tpso3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Serializable{
    
    private String nomeDiretorio;
    private List<Info> listaInfos;

    public Diretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
        this.listaInfos = new ArrayList<Info>();
    }

    public static class Info implements Serializable{
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

    public void addInfo(Info info){
        listaInfos.add(info);
    }

    public List<Info> getListaInfos() {
        return listaInfos;
    }

    public void setListaInfos(List<Info> listaInfos) {
        this.listaInfos = listaInfos;
    }

    public String getNomeDiretorio() {
        return nomeDiretorio;
    }

    public void setNomeDiretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
    }



}

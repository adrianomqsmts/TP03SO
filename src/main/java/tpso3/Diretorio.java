package tpso3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Serializable{
    
    private String nomeDiretorio;
    private List<Info> listaInfos;
    private List<Diretorio> listaDiretoriosFilho;

    public Diretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
        this.listaInfos = new ArrayList<Info>();
        this.listaDiretoriosFilho = new ArrayList<Diretorio>();
    }


    public Info encontrarInfo(String nomeArquivo){
        for (Info info : listaInfos) {
            if(info.getNomeArquivo().equals(nomeArquivo)){
                return info;
            }
        }
        return null;
    }

    public void removerInfo(String nomeArquivo){
        Info info = encontrarInfo(nomeArquivo);
        if(info != null)
            listaInfos.remove(info);
    }

    public List<Diretorio> getListaDiretoriosFilho() {
        return listaDiretoriosFilho;
    }

    public void setListaDiretoriosFilho(List<Diretorio> listaDiretoriosFilho) {
        this.listaDiretoriosFilho = listaDiretoriosFilho;
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

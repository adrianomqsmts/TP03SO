package tpso3;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Disco implements Serializable {

    private final int tamanhoDisco;
    private final int tamanhoBloco;
    private List<Bloco> listaBlocos;
    private List<Diretorio> listaDiretorios;
    private List<Inode> listaInodes;
    private List<Arquivo> listaArquivos;
    private MapaDeBits mapaDeBits;
    private SuperBloco superBloco;
    private Diretorio diretorioRaiz;

    public Disco(int tamanhoDisco, int tamanhoBloco) {
        this.tamanhoBloco = tamanhoDisco;
        this.tamanhoDisco = tamanhoDisco;
        this.listaBlocos = new ArrayList<Bloco>();
        this.listaDiretorios = new ArrayList<Diretorio>();
        this.listaInodes = new ArrayList<Inode>();
        this.listaArquivos = new ArrayList<Arquivo>();
        this.mapaDeBits = new MapaDeBits((tamanhoDisco/tamanhoBloco));
        this.superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco/tamanhoBloco));
        this.diretorioRaiz = new Diretorio("/");
        inserNoBloco(0, SerializationUtils.serialize(diretorioRaiz));
        criarBlocos(tamanhoBloco, (tamanhoDisco/tamanhoBloco));
    }

    private class Bloco implements Serializable{

        byte[] vetor;

        Bloco(int tamanho) {
            this.vetor = new byte[tamanho];
        }

        public byte[] getVetor() {
            return vetor;
        }

        public void setVetor(byte[] vetor) {
            this.vetor = vetor;
        }

        //inseir no listaBlocos
        // remover do listaBlocos
        // copiar listaBlocos
        // limpar listaBlocos

    }

    public void armazenarArquivo(Arquivo arquivo){
        listaArquivos.add(arquivo);
        inserNoBloco(0, SerializationUtils.serialize(arquivo.getFile()));
    }

    public void criarBlocos(int tamanhoBloco, int qtdeBlocos){
        for (int i = 0; i < qtdeBlocos; i++)
            listaBlocos.add(new Bloco(tamanhoBloco));
    }

    //retorna se o listaBlocos foi todo preenchido ou não.
    public boolean inserNoBloco(int endereco, byte[] dado) {
        int j = 0;
        for (int i = 0; i < dado.length; i++) {
            listaBlocos.get(endereco).vetor[j++] = dado[i];
            if (j == tamanhoBloco) {
                superBloco.setQtdeBlocosLivres(superBloco.getQtdeBlocos() - 1);
                return true;
            }
        }
        listaBlocos.get(endereco).vetor[j] = -126; //Final do dado
        return false;
    }

    public byte[] buscarBloco(int endereco) {
        byte [] saida;
        int i;
        for (i = 0; i < tamanhoBloco; i++) {
            if(listaBlocos.get(endereco).vetor[i] == -126){
                i--;
                break;
            }
        }
        System.out.println(i);
        saida = new byte[i];
        for (i = 0; i < saida.length; i++) {
            saida[i] = listaBlocos.get(endereco).vetor[i];

        }
        return listaBlocos.get(endereco).vetor;
    }

    public int getTamanhoDisco() {
        return tamanhoDisco;
    }

    public int getTamanhoBloco() {
        return tamanhoBloco;
    }

    public List<Bloco> getListaBlocos() {
        return listaBlocos;
    }

    public void setListaBlocos(List<Bloco> listaBlocos) {
        this.listaBlocos = listaBlocos;
    }

    public List<Diretorio> getListaDiretorios() {
        return listaDiretorios;
    }

    public void setListaDiretorios(List<Diretorio> listaDiretorios) {
        this.listaDiretorios = listaDiretorios;
    }

    public List<Inode> getListaInodes() {
        return listaInodes;
    }

    public void setListaInodes(List<Inode> listaInodes) {
        this.listaInodes = listaInodes;
    }

    public List<Arquivo> getListaArquivos() {
        return listaArquivos;
    }

    public void setListaArquivos(List<Arquivo> listaArquivos) {
        this.listaArquivos = listaArquivos;
    }

    public MapaDeBits getMapaDeBits() {
        return mapaDeBits;
    }

    public void setMapaDeBits(MapaDeBits mapaDeBits) {
        this.mapaDeBits = mapaDeBits;
    }

    public SuperBloco getSuperBloco() {
        return superBloco;
    }

    public void setSuperBloco(SuperBloco superBloco) {
        this.superBloco = superBloco;
    }

    public Diretorio getDiretorioRaiz() {
        return diretorioRaiz;
    }

    public void setDiretorioRaiz(Diretorio diretorioRaiz) {
        this.diretorioRaiz = diretorioRaiz;
    }
}

package tpso3;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
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
        this.tamanhoBloco = tamanhoBloco;
        this.tamanhoDisco = tamanhoDisco;
        this.listaBlocos = new ArrayList<Bloco>();
        this.listaDiretorios = new ArrayList<Diretorio>();
        this.listaInodes = new ArrayList<Inode>();
        this.listaArquivos = new ArrayList<Arquivo>();
        this.mapaDeBits = new MapaDeBits((tamanhoDisco/tamanhoBloco));
        this.superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco/tamanhoBloco));
        //this.diretorioRaiz = new Diretorio("/");
        this.mapaDeBits.criarMapa((tamanhoDisco/tamanhoBloco));
        criarBlocos(tamanhoBloco, (tamanhoDisco/tamanhoBloco));
        //inserNoBloco(0, SerializationUtils.serialize(diretorioRaiz));
    }

    private class Bloco implements Serializable{

        byte[] vetor;

        Bloco(int tamanhoBloco) {
            this.vetor = new byte[tamanhoBloco];
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
        arquivo.imprimirArquivo();
        byte[] bytes = SerializationUtils.serialize(arquivo.getFile());
        listaArquivos.add(arquivo);
        inserNoBloco(0, bytes);
    }

    public void criarBlocos(int tamanhoBloco, int qtdeBlocos){
        for (int i = 0; i < qtdeBlocos; i++)
            listaBlocos.add(new Bloco(tamanhoBloco));
    }


    public List<Integer> inserirBytesBloco(byte[] bytes, int posicaoVetorBytes, List<Integer> listaEnderecos){
        if((bytes.length/tamanhoBloco) > mapaDeBits.qtdeBlocosLivres()){
            System.err.println("Não existe blocos suficientes para armazenar o arquivo, por favor apague algum arquivo!");
            return null;
        }
        int posicaoLivre = mapaDeBits.pegarPosicaoLivre();
        if(posicaoLivre == -1){
            System.err.println("Não existe blocos livres, por favor apague algum arquivo!");
            return null;
        }
        for(int i = 0; i < tamanhoBloco; i++){
            /*System.out.println("Tamanho Bloco: " + tamanhoBloco);
            System.out.println("Posição livre: " + posicaoLivre);
            System.out.println("Posição Vetor Bytes: " + posicaoVetorBytes);
            System.out.println("Tamanho bytes: " + bytes.length);
            System.out.println("i: " + i);*/
            listaBlocos.get(posicaoLivre).vetor[i] = bytes[posicaoVetorBytes];
            posicaoVetorBytes++;
            if(posicaoVetorBytes == bytes.length){
                mapaDeBits.inserir(posicaoLivre);
                listaEnderecos.add(posicaoLivre);
//                for(Integer integer : listaEnderecos){
//                    System.out.println(integer);
//                }
                return listaEnderecos;
            }
        }
        mapaDeBits.inserir(posicaoLivre);
        listaEnderecos.add(posicaoLivre);
        inserirBytesBloco(bytes, posicaoVetorBytes, listaEnderecos);
        return listaEnderecos;
    }

    public File buscarBytesBloco(String nomeDiretorio, String nomeArquivo){

        Diretorio diretorio = buscarDiretorio(nomeDiretorio);
        Diretorio.Info info = buscarInfo(diretorio, nomeArquivo);

        byte[] bytes = new byte[info.getInode().getTamanhoArquivo()];

        for(int i = 0; i < bytes.length ; i++){
            for(int k = 0; k < info.getInode().getListaEnderecos().size(); k++){
                for(int j = 0; j < tamanhoBloco; j++){ // pegar o tamanho correto
                    bytes[i] = listaBlocos.get(info.getInode().getListaEnderecos().get(k)).vetor[j];
                }
            }
        }
        return (File) SerializationUtils.deserialize(bytes);
    }

    public Diretorio buscarDiretorio(String nomeDiretorio){
        for(Diretorio diretorio : listaDiretorios){
            if(diretorio.getNomeDiretorio().equals(nomeDiretorio)){
                return diretorio;
            }
        }
        System.out.println("nao encontrou diretorio");
        return null;
    }

    public Diretorio.Info buscarInfo(Diretorio diretorio, String nomeArquivo){
        System.out.println(nomeArquivo);
        for(Diretorio.Info info : diretorio.getListaInfos()){
            System.out.println(info.getNomeArquivo());
            if(info.getNomeArquivo().equals(nomeArquivo)){
                return info;
            }
        }
        System.out.println("Nao encontrou info");
        return null;
    }

    public void addInode(Inode inode){
        listaInodes.add(inode);
    }

    public void addDiretorio(Diretorio diretorio){
        listaDiretorios.add(diretorio);
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

    public void imprimirListaInodes(){
        for(Inode inode : listaInodes){
            System.out.println("Data Criado: " + inode.getCriado());
            System.out.println("Data Modificado: " + inode.getModificado());
            System.out.println("Data Acessado: " + inode.getAcessado());
            System.out.println("Tamanho Arquivo: " + inode.getTamanhoArquivo());
            System.out.println("Lista de Endereços: ");
            for(Integer integer : inode.getListaEnderecos()){
                System.out.print(integer + " ");
            }
            System.out.println();
        }
    }

    /*public void impirmirListaDiretorios(){
        for(Diretorio diretorio : listaDiretorios){
            System.out.println("Nome diretorio: " + diretorio.getNomeDiretorio());
            System.out.println("Nome arquivo: " + diretorio.getNomeArquivo());
            System.out.println("Informações I-Node: ");
            System.out.println("Data Criado: " + diretorio.getInode().getCriado());
            System.out.println("Data Modificado: " + diretorio.getInode().getModificado());
            System.out.println("Data Acessado: " + diretorio.getInode().getAcessado());
            System.out.println("Tamanho Arquivo: " + diretorio.getInode().getTamanhoArquivo());
            System.out.println("Lista de Endereços: ");
            for(Integer integer : diretorio.getInode().getListaEnderecos()){
                System.out.print(integer + " ");
            }
            System.out.println();
        }
    }*/

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

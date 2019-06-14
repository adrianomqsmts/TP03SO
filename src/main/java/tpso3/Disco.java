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
        this.mapaDeBits = new MapaDeBits((tamanhoDisco / tamanhoBloco));
        this.superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco / tamanhoBloco));
        //this.diretorioRaiz = new Diretorio("/");
        this.mapaDeBits.criarMapa((tamanhoDisco / tamanhoBloco));
        criarBlocos(tamanhoBloco, (tamanhoDisco / tamanhoBloco));
        //inserNoBloco(0, SerializationUtils.serialize(diretorioRaiz));
    }

    private class Bloco implements Serializable {

        List<Byte> vetor;

        Bloco(int tamanhoBloco) {
            this.vetor = new ArrayList<Byte>(tamanhoBloco);
        }

        public List<Byte> getVetor() {
            return vetor;
        }

        public void setVetor(List<Byte> vetor) {
            this.vetor = vetor;
        }

    }


    public void criarBlocos(int tamanhoBloco, int qtdeBlocos) {
        for (int i = 0; i < qtdeBlocos; i++)
            listaBlocos.add(new Bloco(tamanhoBloco));
    }


    public List<Integer> inserirBytesArquivoBloco(byte[] bytes, int posicaoVetorBytes, List<Integer> listaEnderecos) {
        if ((bytes.length / tamanhoBloco) > mapaDeBits.qtdeBlocosLivres()) {
            System.err.println("Não existe blocos suficientes para armazenar o arquivo, por favor apague algum arquivo!");
            return null;
        }
        int posicaoLivre = mapaDeBits.pegarPosicaoLivre();
        if (posicaoLivre == -1) {
            System.err.println("Não existe blocos livres, por favor apague algum arquivo!");
            return null;
        }
        for (int i = 0; i < tamanhoBloco; i++) {
            /*System.out.println("Tamanho Bloco: " + tamanhoBloco);
            System.out.println("Posição livre: " + posicaoLivre);
            System.out.println("Posição Vetor Bytes: " + posicaoVetorBytes);
            System.out.println("Tamanho bytes: " + bytes.length);
            System.out.println("i: " + i);*/
            listaBlocos.get(posicaoLivre).vetor.add(bytes[posicaoVetorBytes]);
            posicaoVetorBytes++;
            if (posicaoVetorBytes == bytes.length) {
                mapaDeBits.inserir(posicaoLivre);
                listaEnderecos.add(posicaoLivre);
                return listaEnderecos;

            }
        }
        mapaDeBits.inserir(posicaoLivre);
        listaEnderecos.add(posicaoLivre);
        inserirBytesArquivoBloco(bytes, posicaoVetorBytes, listaEnderecos);
        return listaEnderecos;
    }

    public File buscarBytesArquivoBloco(String nomeDiretorio, String nomeArquivo) {

        Diretorio diretorio = buscarDiretorio(nomeDiretorio);
        Info info = buscarInfo(diretorio, nomeArquivo);

        if(info == null){
            return null;
        }

        byte[] bytes = new byte[info.getInode().getTamanhoArquivo()];

        int i = 0;
        for (int k = 0; k < info.getInode().getListaEnderecos().size(); k++) {
            for (int j = 0; j < listaBlocos.get(info.getInode().getListaEnderecos().get(k)).vetor.size(); j++) {
                bytes[i] = listaBlocos.get(info.getInode().getListaEnderecos().get(k)).vetor.get(j);
                i++;
            }
        }
        return (File) SerializationUtils.deserialize(bytes);
    }

    public Diretorio buscarDiretorio(String nomeDiretorio) {
        for (Diretorio diretorio : listaDiretorios) {
            if (diretorio.getNomeDiretorio().equals(nomeDiretorio)) {
                return diretorio;
            }
        }
        return null;
    }

    public Integer buscarIndiceDiretorio(String nomeDiretorio) {
        int indice = 0;
        for (Diretorio diretorio : listaDiretorios) {
            if (diretorio.getNomeDiretorio().equals(nomeDiretorio)) {
                return indice;
            }
            indice++;
        }
        return null;
    }

    public Info buscarInfo(Diretorio diretorio, String nomeArquivo) {
        for (Info info : diretorio.getListaInfos()) {
            if (info.getNomeArquivo().equals(nomeArquivo)) {
                return info;
            }
        }
        return null;
    }

    /* Não remove os bytes em si, somente seta para 0 no mapa de bits os blocos do arquivo. */
    public void removerArquivoBloco(String nomeDiretorio, String nomeArquivo){

        Integer indice;

        Diretorio diretorio = buscarDiretorio(nomeDiretorio);

        if(diretorio != null){
            Info info = buscarInfo(diretorio, nomeArquivo);
            if(info != null){
                for(Integer integer : info.getInode().getListaEnderecos()){
                    System.out.println(integer);
                    System.out.println(mapaDeBits.getMapa().get(integer));
                    mapaDeBits.remover(integer);
                    System.out.println(mapaDeBits.getMapa().get(integer));
                }

                indice = buscarIndiceDiretorio(nomeDiretorio);

                if(indice != null){
                    listaDiretorios.get(indice).removerInfo(nomeArquivo);
                }
            }
        }
    }


    public void addInode(Inode inode) {
        listaInodes.add(inode);
    }

    public void addDiretorio(Diretorio diretorio) {
        listaDiretorios.add(diretorio);
    }

    public void imprimirListaInodes() {
        for (Inode inode : listaInodes) {
            System.out.println("Data Criado: " + inode.getCriado());
            System.out.println("Data Modificado: " + inode.getModificado());
            System.out.println("Data Acessado: " + inode.getAcessado());
            System.out.println("Tamanho Arquivo: " + inode.getTamanhoArquivo());
            System.out.println("Lista de Endereços: ");
            for (Integer integer : inode.getListaEnderecos()) {
                System.out.print(integer + " ");
            }
            System.out.println();
        }
    }

    public void impirmirListaDiretorios(){
        for(Diretorio diretorio : listaDiretorios){
            System.out.println("Nome diretorio: " + diretorio.getNomeDiretorio());
            for (Info info : diretorio.getListaInfos()
                 ) {
                System.out.println("Nome arquivo: " + info.getNomeArquivo());
            }
            System.out.println();
        }
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

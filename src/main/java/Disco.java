import java.util.ArrayList;
import java.util.List;

public class Disco {

    private final int tamanhoDisco;
    private final int tamanhoBloco;
    private List<Bloco> listaBlocos;
    private List<Bloco> listaBlocosInode;
    private MapaBits mapaBits;
    private MapaBits mapaBitsInode;
    private SuperBloco superBloco;

    public Disco(int tamanhoDisco, int tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
        this.tamanhoDisco = tamanhoDisco;
        this.listaBlocos = new ArrayList<Bloco>();
        this.listaBlocosInode = new ArrayList<Bloco>();
        this.mapaBits = new MapaBits((tamanhoDisco / tamanhoBloco) / 2);
        this.mapaBitsInode = new MapaBits((tamanhoDisco / tamanhoBloco) / 2);
        this.superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco / tamanhoBloco));
        this.mapaBits.criarMapa((tamanhoDisco / tamanhoBloco) / 2);
        this.mapaBitsInode.criarMapa((tamanhoDisco / tamanhoBloco) / 2);
        criarBlocos(tamanhoBloco, (tamanhoDisco / tamanhoBloco) / 2);
        criarBlocosInode((tamanhoDisco / tamanhoBloco) / 2);
    }

    private class Bloco {

        private List<Byte> vetor;

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

    public void criarBlocosInode(int qtdeBlocos) {
        for (int i = 0; i < qtdeBlocos; i++)
            listaBlocosInode.add(new Bloco(1000));
    }


    public List<Integer> inserirBytesBloco(byte[] bytes, int posicaoVetorBytes, List<Integer> listaEnderecos) {
        int posicaoLivre = mapaBits.pegarPosicaoLivre();
        if (posicaoLivre == -1) {
            System.err.println("Não existe blocos livres, por favor apague algum arquivo!");
            return null;
        }
        listaBlocos.get(posicaoLivre).getVetor().clear();
        for (int i = 0; i < tamanhoBloco; i++) {
            listaBlocos.get(posicaoLivre).getVetor().add(bytes[posicaoVetorBytes]);
            posicaoVetorBytes++;
            if (posicaoVetorBytes == bytes.length) {
                mapaBits.inserir(posicaoLivre);
                listaEnderecos.add(posicaoLivre);
                return listaEnderecos;
            }
        }
        mapaBits.inserir(posicaoLivre);
        listaEnderecos.add(posicaoLivre);
        inserirBytesBloco(bytes, posicaoVetorBytes, listaEnderecos);
        return listaEnderecos;
    }

    public List<Integer> inserirBytesBlocoInode(byte[] bytes, int posicaoVetorBytes, List<Integer> listaEnderecos) {
        int posicaoLivre = mapaBitsInode.pegarPosicaoLivre();
        if (posicaoLivre == -1) {
            System.err.println("Não existe blocos de inode livres, por favor apague algum arquivo!");
            return null;
        }
        listaBlocosInode.get(posicaoLivre).getVetor().clear();
        for (int i = 0; i < 1000; i++) {
            listaBlocosInode.get(posicaoLivre).getVetor().add(bytes[posicaoVetorBytes]);
            posicaoVetorBytes++;
            if (posicaoVetorBytes == bytes.length) {
                mapaBitsInode.inserir(posicaoLivre);
                listaEnderecos.add(posicaoLivre);
                return listaEnderecos;

            }
        }
        return listaEnderecos;
    }

    public byte[] buscarBytesBloco(Inode inode){
        byte[] bytes = new byte[inode.getTamanho()];
        int i = 0;
        for (int k = 0; k < inode.getListaEnderecos().size(); k++) {
            for (int j = 0; j < listaBlocos.get(inode.getListaEnderecos().get(k)).getVetor().size(); j++) {
                bytes[i] = listaBlocos.get(inode.getListaEnderecos().get(k)).getVetor().get(j);
                i++;
            }
        }
        return bytes;
    }

    public byte[] buscarBytesInodeBloco(int posicao){
        byte[] bytes = new byte[1000];
        int i = 0;
            for (int j = 0; j < listaBlocosInode.get(posicao).getVetor().size(); j++) {
                bytes[i] = listaBlocosInode.get(posicao).getVetor().get(j);
                i++;
            }
        return bytes;
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

    public MapaBits getMapaBits() {
        return mapaBits;
    }

    public void setMapaBits(MapaBits mapaBits) {
        this.mapaBits = mapaBits;
    }

    public SuperBloco getSuperBloco() {
        return superBloco;
    }

    public void setSuperBloco(SuperBloco superBloco) {
        this.superBloco = superBloco;
    }

    public List<Bloco> getListaBlocosInode() {
        return listaBlocosInode;
    }

    public void setListaBlocosInode(List<Bloco> listaBlocosInode) {
        this.listaBlocosInode = listaBlocosInode;
    }

    public MapaBits getMapaBitsInode() {
        return mapaBitsInode;
    }

    public void setMapaBitsInode(MapaBits mapaBitsInode) {
        this.mapaBitsInode = mapaBitsInode;
    }
}

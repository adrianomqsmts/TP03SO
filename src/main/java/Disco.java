import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class Disco {

    private final int tamanhoDisco;
    private final int tamanhoBloco;
    private List<Bloco> listaBlocos;
    private MapaBits mapaBits;
    private SuperBloco superBloco;

    public Disco(int tamanhoDisco, int tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
        this.tamanhoDisco = tamanhoDisco;
        this.listaBlocos = new ArrayList<>();
        this.mapaBits = new MapaBits((tamanhoDisco / tamanhoBloco));
        this.superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco / tamanhoBloco));
        this.mapaBits.criarMapa((tamanhoDisco / tamanhoBloco));
        criarBlocos(tamanhoBloco, (tamanhoDisco / tamanhoBloco));
    }

    private class Bloco {

        private List<Byte> vetor;

        Bloco(int tamanhoBloco) {
            this.vetor = new ArrayList<>(tamanhoBloco);
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

    public List<Integer> inserirBytesBloco(byte[] bytes, int posicaoVetorBytes, List<Integer> listaEnderecos) {
        int posicaoLivre = mapaBits.pegarPosicaoLivre();
        if (posicaoLivre == -1) {
            System.out.println("Não existe blocos livres, por favor apague algum arquivo!");
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

    public Integer inserirBytesInodeBloco(byte[] bytes) {
        int posicaoLivre = mapaBits.pegarPosicaoLivre();
        if (posicaoLivre == -1) {
            System.out.println("Não existe blocos de inode livres, por favor apague algum arquivo!");
            return null;
        }
        listaBlocos.get(posicaoLivre).getVetor().clear();
        for (byte aByte : bytes) {
            listaBlocos.get(posicaoLivre).getVetor().add(aByte);
        }
        mapaBits.inserir(posicaoLivre);
        return posicaoLivre;
    }

    public byte[] buscarBytesBloco(Inode inode) {
        byte[] bytes = new byte[inode.getTamanho()];
        List<Integer> listaEnderecosRestantes = new ArrayList<>();
        int i = 0;
        for (int k = 0; k < inode.getListaEnderecos().size(); k++) {
            for (int j = 0; j < listaBlocos.get(inode.getListaEnderecos().get(k)).getVetor().size(); j++) {
                bytes[i] = listaBlocos.get(inode.getListaEnderecos().get(k)).getVetor().get(j);
                i++;
            }
        }
        if (inode.getEnderecoBlocoDemaisEnderecos() != -1) {
            listaEnderecosRestantes = buscarBytesRestantes(inode.getEnderecoBlocoDemaisEnderecos());
            for (int k = 0; k < listaEnderecosRestantes.size(); k++) {
                for (int j = 0; j < listaBlocos.get(listaEnderecosRestantes.get(k)).getVetor().size(); j++) {
                    bytes[i] = listaBlocos.get(listaEnderecosRestantes.get(k)).getVetor().get(j);
                    i++;
                }
            }
        }
        return bytes;
    }

    public List<Integer> buscarBytesRestantes(int posicao) {
        byte[] bytes = new byte[tamanhoBloco];
        List<Integer> listaEnderecosRestantes = new ArrayList<>();
        int i = 0;
        for (int j = 0; j < listaBlocos.get(posicao).getVetor().size(); j++) {
            bytes[i] = listaBlocos.get(posicao).getVetor().get(j);
            i++;
        }
        listaEnderecosRestantes = (List<Integer>) SerializationUtils.deserialize(bytes);

        return listaEnderecosRestantes;

    }

    public byte[] buscarBytesInodeBloco(int posicao) {
        byte[] bytes = new byte[tamanhoBloco];
        int i = 0;
        for (int j = 0; j < listaBlocos.get(posicao).getVetor().size(); j++) {
            bytes[i] = listaBlocos.get(posicao).getVetor().get(j);
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

}

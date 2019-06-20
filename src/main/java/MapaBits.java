import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapaBits implements Serializable {

    private List<Boolean> mapa;

    public MapaBits(int tamanho) {
        this.mapa = new ArrayList<Boolean>(tamanho);
        criarMapa(tamanho);
    }

    public void criarMapa(int tamanho) {
        mapa.clear();
        for (int i = 0; i < tamanho; i++)
            mapa.add(false);
    }

    public void inserir(int posicao) {
        mapa.remove(posicao);
        mapa.add(posicao, true);
    }

    public int pegarPosicaoLivre() {
        for (int i = 0; i < mapa.size(); i++) {
            if (isLivre(i)) {
                return i;
            }

        }
        return -1;
    }

    public void remover(int posicao) {
        mapa.remove(posicao);
        mapa.add(posicao, false);
    }

    public boolean isLivre(int posicao) {
        return !mapa.get(posicao);
    }

    public int qtdeBlocosLivres() {
        int count = 0;
        for (Boolean bool : mapa) {
            if (!bool)
                count++;
        }
        return count;
    }

    public void imprimirMapa() {
        int i = 0;
        for (Boolean booleano : mapa) {
            System.out.println("Bloco " + i + ": " + booleano);
            i++;
        }
    }

    public List<Boolean> getMapa() {
        return mapa;
    }

    public void setMapa(List<Boolean> mapa) {
        this.mapa = mapa;
    }
}

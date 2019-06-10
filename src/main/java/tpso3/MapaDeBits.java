package tpso3;

import java.util.ArrayList;
import java.util.List;

public class MapaDeBits {

    private List<Boolean> mapa;

    public MapaDeBits(int tamanho) {
        this.mapa = new ArrayList<Boolean>(tamanho);
        criarMapa(tamanho);
    }

    public void criarMapa(int tamanho){
        for(int i = 0; i < tamanho; i++)
            mapa.add(false);
    }

    public void inserir(int posicao){
        mapa.add(posicao, true);
    }

    public void remover(int posicao){
        mapa.add(posicao, false);
    }

    public boolean isLivre(int posicao){
        return mapa.get(posicao);
    }

    public List<Boolean> getMapa() {
        return mapa;
    }

    public void setMapa(List<Boolean> mapa) {
        this.mapa = mapa;
    }
}

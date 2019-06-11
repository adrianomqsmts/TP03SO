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

    public int pegarPosicaoLivre(){
        for (int i = 0; i < mapa.size();i++){
            if(isLivre(i)){
//                System.out.println("Retornando posição: " + i);
                return i;
            }

        }
        return -1;
    }

    public void remover(int posicao){
        mapa.add(posicao, false);
    }

    public boolean isLivre(int posicao){

        return !mapa.get(posicao);
    }

    public int qtdeBlocosLivres(){
        int count  = 0;
        for(Boolean bool : mapa){
            if(!bool)
                count++;
        }
        return count;
    }

    public List<Boolean> getMapa() {
        return mapa;
    }

    public void setMapa(List<Boolean> mapa) {
        this.mapa = mapa;
    }
}
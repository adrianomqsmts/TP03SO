package tpso3;

import java.io.Serializable;
import org.apache.commons.lang.SerializationUtils;

public class SistemasDeArquivos implements Serializable{
    
    private class mapa {

        boolean[] mapa;
        
        public mapa(int tamanho) {
            this.mapa = new boolean[tamanho];
        }
        
    }
    
    Disco HD;

    public SistemasDeArquivos(int disco, int bloco) {
        this.HD = new Disco(disco, bloco);
        Diretorio raiz = new Diretorio("teste", 10);
        HD.inserNoBloco(0, SerializationUtils.serialize(raiz));
    }
    
    public void teste(){
       Object yourObject = SerializationUtils.deserialize(HD.buscarBloco(0));
       System.out.println(((Diretorio)yourObject).getNome());
    }
    
    
    
}

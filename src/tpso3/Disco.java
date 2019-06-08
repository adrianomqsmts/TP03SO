package tpso3;

import java.io.Serializable;

public class Disco implements Serializable {

    Bloco[] bloco;
    private final int tamanhoDisco;
    private final int tamanhoBloco;

    public Disco(int disco, int bloco) {
        this.tamanhoBloco = bloco;
        this.tamanhoDisco = disco;
        this.bloco = new Bloco[disco];
        for (int i = 0; i < disco; i++) {
            this.bloco[i] = new Bloco(bloco);
        }
    }

    //retorna se o bloco foi todo preenchido ou não. 
    public boolean inserNoBloco(int endereco, byte[] dado) {
        int j = 0;
        for (int i = 0; i < dado.length; i++) {
            bloco[endereco].vetor[j++] = dado[i];
            if (j == tamanhoBloco) {
                return true;
            }

        }
        bloco[endereco].vetor[j] = -126; //Final do dado
        return false;

    }

    public byte[] buscarBloco(int endereco) {
        byte [] saida;
        int i;
        for (i = 0; i < tamanhoBloco; i++) {
            if(bloco[endereco].vetor[i] == -126){
                i--;
                break;
            }

        }
        System.out.println(i);
        saida = new byte[i];
        for (i = 0; i < saida.length; i++) {
            saida[i] = bloco[endereco].vetor[i];

        }
        return bloco[endereco].vetor;
    }
    
    

}

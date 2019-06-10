package tpso3;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

public class SistemaDeArquivos implements Serializable{

    private Disco hd;

    public SistemaDeArquivos(int tamanhoDisco, int tamanhoBloco) {
        this.hd = new Disco(tamanhoDisco, tamanhoBloco);
    }

    public void criarArquivo(String caminhoArquivo){

        File file = new File(caminhoArquivo);

        boolean arqExiste = file.exists();

        if(arqExiste){
            Arquivo arquivo = new Arquivo(file);
            hd.armazenarArquivo(arquivo);
        } else{
            System.out.println("Erro ao criar arquivo!");
        }
    }

    public Disco getHd() {
        return hd;
    }

    public void setHd(Disco hd) {
        this.hd = hd;
    }
}

package tpso3;

import java.io.Serializable;

public class Diretorio implements Serializable{
    
    private String nome;
    

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    private class Info implements Serializable{
        String nome;
        int endereco;
    }
    
    private Info [] tabela;

    public Diretorio(String nome, int tamanho) {
        this.nome = nome;
        tabela = new Info[tamanho];
        for (int i = 0; i < 10; i++) {
            this.tabela[i] = new Info();
        }
    }
      
    
}

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Serializable {

    private String nomeDiretorio;
    private List<Info> tabela;
    private int enderecoInode;
    private String caminho;

    public Diretorio(String nomeDiretorio, int enderecoInode, String caminho) {
        this.nomeDiretorio = nomeDiretorio;
        this.tabela = new ArrayList<Info>();
        this.enderecoInode = enderecoInode;
        this.caminho = caminho;
    }

    public static class Info implements Serializable {
        private String nome;
        private boolean arquivo;
        private int enderecoBlocoInode;

        public Info(String nome, boolean arquivo, int enderecoBlocoInode) {
            this.nome = nome;
            this.arquivo = arquivo;
            this.enderecoBlocoInode = enderecoBlocoInode;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public boolean isArquivo() {
            return arquivo;
        }

        public void setArquivo(boolean arquivo) {
            this.arquivo = arquivo;
        }

        public int getEnderecoBlocoInode() {
            return enderecoBlocoInode;
        }

        public void setEnderecoBlocoInode(int enderecoBlocoInode) {
            this.enderecoBlocoInode = enderecoBlocoInode;
        }
    }

    public String getNomeDiretorio() {
        return nomeDiretorio;
    }

    public void setNomeDiretorio(String nomeDiretorio) {
        this.nomeDiretorio = nomeDiretorio;
    }

    public List<Info> getTabela() {
        return tabela;
    }

    public void setTabela(List<Info> tabela) {
        this.tabela = tabela;
    }

    public int getEnderecoInode() {
        return enderecoInode;
    }

    public void setEnderecoInode(int enderecoInode) {
        this.enderecoInode = enderecoInode;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}

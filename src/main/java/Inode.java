import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Inode implements Serializable {
    private Date criado;
    private Date modificado;
    private Date acessado;

    private List<Integer> listaEnderecos;
    private int tamanho;
    private int enderecoBlocoDemaisEnderecos;
    private int tamanhoMaximoLista;

    public Inode(int tamanho) {
        this.criado = new Date(System.currentTimeMillis());
        this.modificado = new Date(System.currentTimeMillis());
        this.acessado = new Date(System.currentTimeMillis());
        this.tamanho = tamanho;
        this.tamanhoMaximoLista = 2;
//        this.listaEnderecos = listaEnderecos;
        this.enderecoBlocoDemaisEnderecos = -1;
    }

    public List<Integer> verificarLista(List<Integer> listaEnderecos) {
        List<Integer> novaLista = new ArrayList<>();
        List<Integer> restanteLista = new ArrayList<>();
        if (listaEnderecos.size() > tamanhoMaximoLista) {
            for (int i = 0; i < tamanhoMaximoLista; i++) {
                novaLista.add(listaEnderecos.get(i));
            }
            for (int i = tamanhoMaximoLista; i < listaEnderecos.size(); i++) {
                restanteLista.add(listaEnderecos.get(i));
            }
            this.listaEnderecos = novaLista;
            return restanteLista;
        } else {
            this.listaEnderecos = listaEnderecos;
            return null;
        }
    }

    public boolean inserirEndereco(int endereco) {
        return listaEnderecos.add(endereco);
    }

    public void removerEndereco(int endereco) {
        listaEnderecos.remove(endereco);
    }

    public List<Integer> getListaEnderecos() {
        return listaEnderecos;
    }

    public void setListaEnderecos(List<Integer> listaEnderecos) {
        this.listaEnderecos = listaEnderecos;
    }

    public void setCriado(Date criado) {
        this.criado = criado;
    }

    public Date getCriado() {
        return criado;
    }

    public Date getModificado() {
        return modificado;
    }

    public void setModificado(Date Modificado) {
        this.modificado = Modificado;
    }

    public Date getAcessado() {
        return acessado;
    }

    public void setAcessado(Date Acessado) {
        this.acessado = Acessado;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getEnderecoBlocoDemaisEnderecos() {
        return enderecoBlocoDemaisEnderecos;
    }

    public void setEnderecoBlocoDemaisEnderecos(int enderecoBlocoDemaisEnderecos) {
        this.enderecoBlocoDemaisEnderecos = enderecoBlocoDemaisEnderecos;
    }
}

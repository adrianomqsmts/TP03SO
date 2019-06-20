import org.apache.commons.lang3.SerializationUtils;

import java.io.*;

public class Arquivo implements Serializable {

    private String texto;
    private String nome;

    public void lerFile(File file) {
        texto = "";
        nome = file.getName();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                String linha = br.readLine();
                texto = texto.concat(linha);
                texto = texto.concat("\n");
            }
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void imprimirArquivo() {
        System.out.println(texto);
    }

    public byte[] serializar() {
        return SerializationUtils.serialize(texto);
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int tamanhoDisco = 10000; // em bytes
        int tamanhoBloco = 500; // em bytes

        SistemaArquivos sistemaArquivos = new SistemaArquivos(tamanhoDisco, tamanhoBloco);

        sistemaArquivos.executarArquivoComandos();

    }
    
}

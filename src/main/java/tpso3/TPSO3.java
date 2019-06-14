package tpso3;

import java.io.IOException;

public class TPSO3 {

    public static void main(String[] args) throws IOException {
        int tamanhoDisco = 1024 * 1024; // em bytes
        int tamanhoBloco = 10; // em bytes

        SistemaDeArquivos sistemaDeArquivos = new SistemaDeArquivos(tamanhoDisco, tamanhoBloco);
        sistemaDeArquivos.criarArquivo("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\java\\arquivos\\teste.txt");
        sistemaDeArquivos.criarArquivo("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\java\\arquivos\\arquivo.txt");
        sistemaDeArquivos.abrirArquivo("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\java\\arquivos\\", "arquivo.txt");
    }
    
}

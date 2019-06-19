import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int tamanhoDisco = 5000; // em bytes
        int tamanhoBloco = 10; // em bytes

        SistemaArquivos sistemaArquivos = new SistemaArquivos(tamanhoDisco, tamanhoBloco);

        sistemaArquivos.executarArquivoComandos();

//        sistemaArquivos.criarDiretorio("novo");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo");
//
//        sistemaArquivos.criarDiretorio("teste");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste");
//
//        sistemaArquivos.criarDiretorio("cassio");
//
//        sistemaArquivos.renomearDiretorio("cassio", "cas");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste/cass");
//
//        sistemaArquivos.criarArquivo("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\arquivo.txt");
//
//        sistemaArquivos.criarArquivo("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\teste.txt");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste/arquivo.txt");
//
//        sistemaArquivos.imprimirDiretorioAtual();
//
//        sistemaArquivos.renomearArquivo("arquivo.txt", "cassio.txt");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste/cassio.txt");
//
//        sistemaArquivos.removerArquivo("cassio.txt");
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste/teste.txt");
//
//        sistemaArquivos.imprimirDiretorioAtual();
//
//        sistemaArquivos.abrirDiretorioArquivo("/novo/teste/cas");

    }
    
}

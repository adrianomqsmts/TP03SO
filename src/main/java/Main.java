import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws IOException {
        int tamanhoDisco = 0; // em bytes
        int tamanhoBloco = 0; // em bytes

        SistemaArquivos sistemaArquivos = new SistemaArquivos();
        Scanner teclado = new Scanner(System.in);
        int opcao;
        String linha;

        System.out.println("Digite sua opção: \n (1) - Arquivo Comandos\n (2) - Digitar Comandos");

        opcao = teclado.nextInt();

        if (opcao == 1) {
            sistemaArquivos.executarArquivoComandos();
        } else if (opcao == 2) {
            do {
                System.out.println("Informe o Tamanho do Disco: ");
                tamanhoDisco = teclado.nextInt();
                System.out.println("Informe o Tamanho do Bloco: ");
                tamanhoBloco = teclado.nextInt();
                if (tamanhoDisco < tamanhoBloco || tamanhoBloco < 500) {
                    System.out.println("ERRO! Tamanho do disco deve ser maior que o do bloco e o bloco deve ter mais de 500 bytes!");
                }
            } while (tamanhoDisco < tamanhoBloco || tamanhoBloco < 500);

            sistemaArquivos.criarSuperBloco(tamanhoDisco, tamanhoBloco);
            sistemaArquivos.criarHd(tamanhoDisco, tamanhoBloco);

            teclado.nextLine();

            while (true) {
                System.out.println("Informe um comando válido: ");
                linha = teclado.nextLine();
                List<String> comando = Arrays.asList(linha.split(" "));
                switch (comando.get(0)) {
                    case "CD":
                        sistemaArquivos.criarDiretorio(comando.get(1));
                        break;
                    case "CA":
                        sistemaArquivos.criarArquivo(comando.get(1));
                        break;
                    case "O":
                        sistemaArquivos.abrirDiretorioArquivo(comando.get(1));
                        break;
                    case "RD":
                        sistemaArquivos.renomearDiretorio(comando.get(1), comando.get(2));
                        break;
                    case "RA":
                        sistemaArquivos.renomearArquivo(comando.get(1), comando.get(2));
                        break;
                    case "DA":
                        sistemaArquivos.removerArquivo(comando.get(1));
                        break;
                    case "MA":
                        sistemaArquivos.moverArquivo(comando.get(1), comando.get(2));
                        break;
                    case "E":
                        System.out.println("Encerrando....");
                        exit(1);
                        break;
                }

            }
        }


    }

}

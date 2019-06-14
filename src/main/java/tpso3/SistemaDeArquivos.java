package tpso3;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class SistemaDeArquivos implements Serializable{

    private Disco hd;

    public SistemaDeArquivos(int tamanhoDisco, int tamanhoBloco) {
        this.hd = new Disco(tamanhoDisco, tamanhoBloco);
    }

    public Arquivo criarArquivo(String caminhoArquivo) throws IOException {

        Inode inode;
        Diretorio diretorio;
        List<Integer> listaEnderecos = new ArrayList<Integer>();

        File file = new File(caminhoArquivo);

        boolean arqExiste = file.exists();

        if(arqExiste){
            Arquivo arquivo = new Arquivo(file);
            Info info;
            inode = new Inode(arquivo.serializar().length, hd.inserirBytesArquivoBloco(arquivo.serializar(), 0, listaEnderecos));
            if(hd.buscarDiretorio(file.getPath().replace(file.getName(),"")) == null){
                diretorio = new Diretorio(file.getPath().replace(file.getName(),""));
                info = new Info(file.getName(), inode);
                diretorio.addInfo(info);
                hd.addInode(inode);
                hd.addDiretorio(diretorio);
            }else{
                info = new Info(file.getName(), inode);
                hd.buscarDiretorio(file.getPath().replace(file.getName(),"")).getListaInfos().add(info);
            }

//            hd.imprimirListaInodes();

            hd.impirmirListaDiretorios();

            return arquivo;
        } else{
            System.out.println("Erro ao criar arquivo!");
            return null;
        }
    }

    public void abrirArquivo(String caminhoDiretorio, String nomeArquivo){

        Arquivo arquivo;
        File file;

        file = hd.buscarBytesArquivoBloco(caminhoDiretorio, nomeArquivo);

        if(file == null){
            exit(0);
        }
        arquivo = new Arquivo(file);

        arquivo.imprimirArquivo();

    }

    public void renomearArquivo(String nomeDiretorio, String nomeArquivo, String novoNomeArquivo){

        Arquivo arquivo;
        File file;

        file = hd.buscarBytesArquivoBloco(nomeDiretorio, nomeArquivo);

        arquivo = new Arquivo(file);

        System.out.println("Antigo nome: " + arquivo.getFile().getName());

        System.out.println(arquivo.getFile().renameTo(new File(nomeDiretorio, novoNomeArquivo)));

        System.out.println("Novo nome: " + arquivo.getFile().getName());

    }

    public void removerArquivo(String nomeDiretorio, String nomeArquivo){
        hd.getMapaDeBits().imprimirMapa();
        hd.impirmirListaDiretorios();
        hd.removerArquivoBloco(nomeDiretorio, nomeArquivo);
        hd.getMapaDeBits().imprimirMapa();
        hd.impirmirListaDiretorios();
    }


    public Disco getHd() {
        return hd;
    }

    public void setHd(Disco hd) {
        this.hd = hd;
    }
}

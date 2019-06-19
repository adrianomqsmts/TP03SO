import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SistemaArquivos implements Serializable {

    private Disco hd;
    private List<Integer> listaEnderecosDiretorioRaiz;
    private Inode inodeDiretorioAtual;
    private Inode inodeDiretorioRaiz;
    private Inode inodeArquivoAtual;

    public SistemaArquivos(int tamanhoDisco, int tamanhoBloco) {
        this.hd = new Disco(tamanhoDisco, tamanhoBloco);
        this.listaEnderecosDiretorioRaiz = new ArrayList<Integer>();
        this.inodeDiretorioRaiz = criarDiretorioRaiz("/");
        this.inodeDiretorioAtual = this.inodeDiretorioRaiz;
    }

    public void executarArquivoComandos(){

        File arqComandos = new File("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\comandos.txt");

        if(arqComandos.exists()){
            try{
                BufferedReader br = new BufferedReader(new FileReader(arqComandos));
                while(br.ready()){
                    String linha = br.readLine();
                    List<String> comando = Arrays.asList(linha.split(" "));
                    switch (comando.get(0)){
                        case "CD":
                            criarDiretorio(comando.get(1));
                            break;
                        case "CA":
                            criarArquivo(comando.get(1));
                            break;
                        case "O":
                            abrirDiretorioArquivo(comando.get(1));
                            break;
                        case "RD":
                            renomearDiretorio(comando.get(1), comando.get(2));
                            break;
                        case "RA":
                            renomearArquivo(comando.get(1), comando.get(2));
                            break;
                        case "DD":
                            break;
                        case "DA":
                            removerArquivo(comando.get(1));
                            break;
                        case "MA":
                            moverArquivo(comando.get(1), comando.get(2));
                            break;
                    }

                }
                br.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    public void moverArquivo(String nomeArquivo, String caminho) throws IOException {

        Diretorio diretorioAtual;
        String caminhoDiretorioAtual;

        System.out.println("Movendo arquivo!");

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

        caminhoDiretorioAtual = diretorioAtual.getCaminho();

        removerArquivo(nomeArquivo);

        abrirDiretorioArquivo(caminho);

        criarArquivo(nomeArquivo);

        abrirDiretorioArquivo(caminhoDiretorioAtual);

    }

    public Inode criarDiretorioRaiz(String nomeDiretorio) {

        Diretorio diretorio = new Diretorio(nomeDiretorio, hd.getMapaBitsInode().pegarPosicaoLivre(), nomeDiretorio);

        List<Integer> listaEnderecosDiretorio = new ArrayList<Integer>();
        List<Integer> listaEnderecosInode = new ArrayList<Integer>();

        /* Inserindo Diretorio no bloco. */
        listaEnderecosDiretorio = hd.inserirBytesBloco(SerializationUtils.serialize(diretorio), 0, listaEnderecosDiretorio);

        Inode inode = new Inode(SerializationUtils.serialize(diretorio).length, listaEnderecosDiretorio);

        /* Inserindo I-node no bloco. */
        listaEnderecosInode = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inode), 0, listaEnderecosInode);

        return inode;

    }

    public void abrirDiretorioArquivo(String caminho) {

        Diretorio diretorioRaiz;
        Inode inode;
        boolean encontrou = false;
        int indice = 1;

        if (caminho.equals("/")) {
            abrirDiretorioRaiz();
        } else {
            List<String> lista = Arrays.asList(caminho.split("/"));

            if (lista.isEmpty()) {
                System.out.println("Lista de diretorios vazia");
                return;
            }

            diretorioRaiz = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioRaiz));

            for (Diretorio.Info info : diretorioRaiz.getTabela()) {
                if (info.getNome().equals(lista.get(indice))) {
                    inode = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
                    abrirDiretorioArquivoAux(inode, lista, ++indice);
                }
            }
        }

        /*if(!encontrou){
            System.out.println("ERRO! Caminho '" + caminho + "' inválido!");
        }*/

    }

    public void abrirDiretorioArquivoAux(Inode inode, List<String> lista, int indice) {

        Inode inode1;
        Diretorio diretorio = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));

        /* Não esquecer de usar return */
        if (indice == lista.size()) {
            inodeDiretorioAtual = inode;
            imprimirDiretorioAtual();
            return;
        }

        for (Diretorio.Info info : diretorio.getTabela()) {
            if (indice == lista.size()) {
                return;
            }
            if (info.getNome().equals(lista.get(indice))) {
                inode1 = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
                /* Não esquecer de usar return */
                if (info.isArquivo()) {
                    inodeArquivoAtual = inode1;
                    inodeDiretorioAtual = inode;
                    abrirArquivoPorInode(inode1);
                    return;
                } else{
                    abrirDiretorioArquivoAux(inode1, lista, ++indice);
                }
            }
        }
    }

    public void imprimirDiretorioAtual() {
        Diretorio diretorioAtual;

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

        System.out.println("Diretório '" + diretorioAtual.getNomeDiretorio() + "' esta aberto!!!\n");

        System.out.println("Nome diretório: '" + diretorioAtual.getNomeDiretorio() + "'");
        System.out.println("Caminho do diretório: " + diretorioAtual.getCaminho());
        System.out.println("Arquivos no diretório: ");
        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (info.isArquivo())
                System.out.println("'" + info.getNome() + "'");
        }
        System.out.println("Diretórios filhos: ");
        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (!info.isArquivo())
                System.out.println("'" + info.getNome() + "'");
        }

        System.out.println();
    }

    public void abrirDiretorioRaiz() {

        Diretorio diretorio;

        diretorio = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioRaiz));

        System.out.println("Diretorio '" + diretorio.getNomeDiretorio() + "' esta aberto!!!\n");

        System.out.println("Nome diretório: '" + diretorio.getNomeDiretorio() + "'");
        System.out.println("Caminho do diretório: " + diretorio.getCaminho());
        System.out.println("Arquivos no diretório: ");
        for (Diretorio.Info info : diretorio.getTabela()) {
            if (info.isArquivo())
                System.out.println("'" + info.getNome() + "'");
        }
        System.out.println("Diretórios filhos: ");
        for (Diretorio.Info info : diretorio.getTabela()) {
            if (!info.isArquivo())
                System.out.println("'" + info.getNome() + "'");
        }

        System.out.println();

        inodeDiretorioAtual = inodeDiretorioRaiz;

    }

    public void criarDiretorio(String nomeNovoDiretorio) {

        Diretorio diretorioAtual, novoDiretorio;
        Diretorio.Info infoNovoDiretorio;

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (!info.isArquivo()) {
                if (info.getNome().equals(nomeNovoDiretorio)) {
                    System.out.println("ERRO! Diretorio ja existe!");
                    return;
                }
            }
        }

        if (diretorioAtual.getNomeDiretorio().equals("/")) {
            novoDiretorio = new Diretorio(nomeNovoDiretorio, hd.getMapaBitsInode().pegarPosicaoLivre(), diretorioAtual.getCaminho() + nomeNovoDiretorio);
        } else {
            novoDiretorio = new Diretorio(nomeNovoDiretorio, hd.getMapaBitsInode().pegarPosicaoLivre(), diretorioAtual.getCaminho() + "/" + nomeNovoDiretorio);
        }

        List<Integer> listaEnderecosNovoDiretorio = new ArrayList<Integer>();
        List<Integer> listaEnderecosNovoInode = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novalistaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        /* Inserindo Diretorio no bloco. */
        listaEnderecosNovoDiretorio = hd.inserirBytesBloco(SerializationUtils.serialize(novoDiretorio), 0, listaEnderecosNovoDiretorio);

        Inode inodeNovoDiretorio = new Inode(SerializationUtils.serialize(novoDiretorio).length, listaEnderecosNovoDiretorio);

        /* Inserindo I-node no bloco. */
        listaEnderecosNovoInode = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeNovoDiretorio), 0, listaEnderecosNovoInode);

        infoNovoDiretorio = new Diretorio.Info(nomeNovoDiretorio, false, listaEnderecosNovoInode.get(0));

        diretorioAtual.getTabela().add(infoNovoDiretorio);

        /* Atualiza o atual diretorio / diretorio pai */
        for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
            hd.getMapaBits().remover(integer);
        }

        novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

        inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
        inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

        /* Atualiza o inode do atual diretorio / diretorio pai */
        hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());

        novalistaEnderecosInodeDiretorioAtual = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioAtual), 0, novalistaEnderecosInodeDiretorioAtual);

        System.out.println("Diretório '" + novoDiretorio.getNomeDiretorio() + "' criado com sucesso no caminho '" + diretorioAtual.getCaminho() + "'!\n");
    }

    public void renomearDiretorio(String nomeDiretorio, String novoNomeDiretorio) {

        Diretorio diretorioAtual, diretorioARenomear;
        Inode inodeDiretorioARenomear;
        List<Integer> novaListaEnderecosDiretorioRenomeado = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosInodeDiretorioRenomeado = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        boolean encontrou = false;
        int posicaoInfo = 0;

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (!info.isArquivo() & info.getNome().equals(nomeDiretorio)) {
                encontrou = true;

                inodeDiretorioARenomear = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));

                diretorioARenomear = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioARenomear));

                diretorioARenomear.setNomeDiretorio(novoNomeDiretorio);

                if (diretorioAtual.getNomeDiretorio().equals("/")) {
                    diretorioARenomear.setCaminho(diretorioAtual.getCaminho() + novoNomeDiretorio);
                } else {
                    diretorioARenomear.setCaminho(diretorioAtual.getCaminho() + "/" + novoNomeDiretorio);
                }

                /* Atualizando Diretorio Renomeado no disco */
                for (Integer integer : inodeDiretorioARenomear.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosDiretorioRenomeado = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioARenomear), 0, novaListaEnderecosDiretorioRenomeado);

                inodeDiretorioARenomear.setListaEnderecos(novaListaEnderecosDiretorioRenomeado);
                inodeDiretorioARenomear.setTamanho(SerializationUtils.serialize(diretorioARenomear).length);

                /* Atualizando I-node do Diretorio Renomeado no disco */
                hd.getMapaBitsInode().remover(info.getEnderecoBlocoInode());

                novaListaEnderecosInodeDiretorioRenomeado = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioARenomear), 0, novaListaEnderecosInodeDiretorioRenomeado);

                diretorioAtual.getTabela().get(posicaoInfo).setNome(novoNomeDiretorio);
                diretorioAtual.getTabela().get(posicaoInfo).setEnderecoBlocoInode(novaListaEnderecosInodeDiretorioRenomeado.get(0));

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

                inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                /* Atualizando I-node do Diretorio Atual no disco */
                hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());

                novaListaEnderecosInodeDiretorioAtual = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioAtual), 0, novaListaEnderecosInodeDiretorioAtual);

                diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

                for (Diretorio.Info info1 : diretorioAtual.getTabela()) {
                    if (!info1.isArquivo() & info1.getNome().equals(novoNomeDiretorio)) {
                        System.out.println("Diretório '" + nomeDiretorio + "' renomeado com sucesso para '" + info1.getNome() + "' no caminho '" + diretorioAtual.getCaminho() + "'! \n");
                    }
                }

            }
            posicaoInfo++;
        }
        if (!encontrou)
            System.out.println("ERRO AO RENOMEAR DIRETÓRIO! Não existe diretorio com este nome!");
    }

    public void criarArquivo(String nomeArquivo) throws IOException {

        String caminhoArquivo;

        caminhoArquivo = "C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\" + nomeArquivo;

        Inode inodeArquivo;
        Diretorio diretorioAtual;
        List<Integer> listaEnderecosNovoArquivo = new ArrayList<Integer>();
        List<Integer> listaEnderecosInodeNovoArquivo = new ArrayList<Integer>();
        List<Integer> novalistaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novalistaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        File file = new File(caminhoArquivo);

        boolean arqExiste = file.exists();
        boolean arquivoExiste = false;

        if (arqExiste) {

            diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

            for (Diretorio.Info info : diretorioAtual.getTabela()) {
                if (info.isArquivo() & info.getNome().equals(file.getName())) {
                    arquivoExiste = true;
                }
            }

            if (!arquivoExiste) {

                Arquivo arquivo = new Arquivo(file);

                listaEnderecosNovoArquivo = hd.inserirBytesBloco(arquivo.serializar(), 0, listaEnderecosNovoArquivo);

                inodeArquivo = new Inode(arquivo.serializar().length, listaEnderecosNovoArquivo);

                listaEnderecosInodeNovoArquivo = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeArquivo), 0, listaEnderecosInodeNovoArquivo);

                Diretorio.Info info = new Diretorio.Info(arquivo.getFile().getName(), true, listaEnderecosInodeNovoArquivo.get(0));

                diretorioAtual.getTabela().add(info);

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novalistaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novalistaEnderecosDiretorioAtual);

                inodeDiretorioAtual.setListaEnderecos(novalistaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                /* Atualizando I-node Diretorio Atual no disco */
                hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());

                novalistaEnderecosInodeDiretorioAtual = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioAtual), 0, novalistaEnderecosInodeDiretorioAtual);

                System.out.println("Arquivo '" + file.getName() + "' criado com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' \n");
            }

        } else {
            System.out.println("Erro ao criar arquivo!");
        }
    }

    public void abrirArquivo(String nomeArquivo) {

        Diretorio diretorioAtual;
        Inode inodeArquivo;
        File file;
        Arquivo arquivo;

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
        boolean encontrou = false;

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (info.isArquivo() & info.getNome().equals(nomeArquivo)) {
                encontrou = true;

                inodeArquivo = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));

                file = (File) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeArquivo));

                arquivo = new Arquivo(file);

                arquivo.imprimirArquivo();
            }
        }

        if (!encontrou)
            System.out.println("ERRO AO ABRIR ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());

    }

    public void abrirArquivoPorInode(Inode inode) {

        File file;
        Arquivo arquivo;
        file = (File) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));
        arquivo = new Arquivo(file);
        System.out.println("Abrindo arquivo '" + file.getName() + "'...!\n");
        arquivo.imprimirArquivo();
        System.out.println("\nFim do arquivo '" + file.getName() + "'...!\n");

    }

    public void renomearArquivo(String nomeArquivo, String novoNomeArquivo) {

        Diretorio diretorioAtual;
        Inode inodeArquivoARenomear;
        File file;
        List<Integer> novaListaEnderecosArquivoRenomeado = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosInodeArquivoRenomeado = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novaListaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
        boolean encontrou = false;
        int posicaoInfo = 0;

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (info.isArquivo() & info.getNome().equals(nomeArquivo)) {
                encontrou = true;

                inodeArquivoARenomear = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));

                file = (File) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeArquivoARenomear));

                file.getName().replace(file.getName(), novoNomeArquivo);

                /* File não muda de nome, logo não é necesário atualiza-lo no disco. */
                /* Atualizar Arquivo renomeado no disco */
                for (Integer integer : inodeArquivoARenomear.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosArquivoRenomeado = hd.inserirBytesBloco(SerializationUtils.serialize(file), 0, novaListaEnderecosArquivoRenomeado);

                inodeArquivoARenomear.setListaEnderecos(novaListaEnderecosArquivoRenomeado);
                inodeArquivoARenomear.setTamanho(SerializationUtils.serialize(file).length);

                /* Atualizando I-node Arquivo renomeado no disco */
                hd.getMapaBitsInode().remover(diretorioAtual.getTabela().get(posicaoInfo).getEnderecoBlocoInode());

                novaListaEnderecosInodeArquivoRenomeado = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeArquivoARenomear), 0, novaListaEnderecosInodeArquivoRenomeado);

                diretorioAtual.getTabela().get(posicaoInfo).setNome(novoNomeArquivo);
                diretorioAtual.getTabela().get(posicaoInfo).setEnderecoBlocoInode(novaListaEnderecosInodeArquivoRenomeado.get(0));

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

                inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                /* Atualizando I-node Diretorio Atual no disco */
                hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());

                novaListaEnderecosInodeDiretorioAtual = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioAtual), 0, novaListaEnderecosInodeDiretorioAtual);

                diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

                for (Diretorio.Info info1 : diretorioAtual.getTabela()) {
                    if (info1.isArquivo() & info1.getNome().equals(novoNomeArquivo)) {
                        System.out.println("Arquivo '" + nomeArquivo + "' renomeado com sucesso para '" + info1.getNome() +"' no diretório '" + diretorioAtual.getNomeDiretorio() + "' \n");
                    }
                }


            }
            posicaoInfo++;
        }

        if (!encontrou)
            System.out.println("ERRO AO RENOMEAR ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());


    }

    public void removerArquivo(String nomeArquivo) {

        Inode inodeArquivoARemover;
        Diretorio diretorioAtual;
        List<Integer> novalistaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novalistaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
        boolean encontrou = false;
        int posicaoInfo = 0, posicaoInfoFinal = 0;

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (info.isArquivo() & info.getNome().equals(nomeArquivo)) {
                posicaoInfoFinal = posicaoInfo;
                encontrou = true;

                inodeArquivoARemover = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));

                /* Removendo arquivo do mapa de bits */
                for (Integer integer : inodeArquivoARemover.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                /* Removendo I-node do mapa de bits */
                hd.getMapaBitsInode().remover(info.getEnderecoBlocoInode());

            }

            posicaoInfo++;
        }
        if (encontrou) {
            /* Removendo Informações (Info) do arquivo no diretorio atual */
            diretorioAtual.getTabela().remove(diretorioAtual.getTabela().get(posicaoInfoFinal));

            /* Atualizando Diretorio Atual no disco */
            for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                hd.getMapaBits().remover(integer);
            }

            novalistaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novalistaEnderecosDiretorioAtual);

            inodeDiretorioAtual.setListaEnderecos(novalistaEnderecosDiretorioAtual);
            inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

            /* Atualizando I-node Diretorio Atual no disco */
            hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());

            novalistaEnderecosInodeDiretorioAtual = hd.inserirBytesBlocoInode(SerializationUtils.serialize(inodeDiretorioAtual), 0, novalistaEnderecosInodeDiretorioAtual);

            System.out.println("Arquivo '" + nomeArquivo + "' removido com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' de caminho '" + diretorioAtual.getCaminho()+ "'! \n");
        } else
            System.out.println("ERRO AO REMOVER ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());


    }


    public Disco getHd() {
        return hd;
    }

    public void setHd(Disco hd) {
        this.hd = hd;
    }

    public List<Integer> getListaEnderecosDiretorioRaiz() {
        return listaEnderecosDiretorioRaiz;
    }

    public void setListaEnderecosDiretorioRaiz(List<Integer> listaEnderecosDiretorioRaiz) {
        this.listaEnderecosDiretorioRaiz = listaEnderecosDiretorioRaiz;
    }

    public Inode getInodeDiretorioAtual() {
        return inodeDiretorioAtual;
    }

    public void setInodeDiretorioAtual(Inode inodeDiretorioAtual) {
        this.inodeDiretorioAtual = inodeDiretorioAtual;
    }

    public Inode getInodeArquivoAtual() {
        return inodeArquivoAtual;
    }

    public void setInodeArquivoAtual(Inode inodeArquivoAtual) {
        this.inodeArquivoAtual = inodeArquivoAtual;
    }
}

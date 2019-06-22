import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.System.exit;

public class SistemaArquivos implements Serializable {

    private Disco hd;
    private Inode inodeDiretorioAtual;
    private Inode inodeDiretorioRaiz;
    private Inode inodeArquivoAtual;
    private SuperBloco superBloco;

    public SistemaArquivos() {

    }

    public void executarArquivoComandos() {

        File arqComandos = new File("C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\comandos.txt");

        int tamanhoDisco = 0;
        int tamanhoBloco = 0;
        int i = 0;

        if (arqComandos.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(arqComandos));
                while (br.ready()) {
                    String linha = br.readLine();
                    List<String> comando = Arrays.asList(linha.split(" "));
                    switch (comando.get(0)) {
                        case "TD":
                            tamanhoDisco = Integer.parseInt(comando.get(1));
                            break;
                        case "TB":
                            tamanhoBloco = Integer.parseInt(comando.get(1));
                            break;
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
                        case "DA":
                            removerArquivo(comando.get(1));
                            break;
                        case "MA":
                            moverArquivo(comando.get(1), comando.get(2));
                            break;
                        case "I":
                            imprimirSuperBloco();
                            break;
                    }
                    i++;
                    if(i == 2){
                        if(tamanhoDisco < tamanhoBloco || tamanhoBloco < 500){
                            System.out.println("Tamanho do disco deve ser maior que o do bloco e o bloco deve ter mais de 500 bytes!");
                            exit(1);
                        }else{
                            criarSuperBloco(tamanhoDisco, tamanhoBloco);
                            criarHd(tamanhoDisco, tamanhoBloco);
                        }
                    }

                }
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void criarSuperBloco(int tamanhoDisco, int tamanhoBloco){
        superBloco = new SuperBloco(tamanhoBloco, (tamanhoDisco/tamanhoBloco));
    }

    public void imprimirSuperBloco(){
        System.out.println("Número Mágico: " + superBloco.getNumeroMagico());
        System.out.println("Tamanho dos Blocos: " + superBloco.getTamanhoBloco());
        System.out.println("Quantidade Total de Blocos: " + superBloco.getQtdeBlocosTotais());
        System.out.println("Quantidade Total de Blocos Livres: " + superBloco.getQtdeBlocosLivres());
        System.out.println();
    }

    public void criarHd(int tamanhoDisco, int tamanhoBloco){
        this.hd = new Disco(tamanhoDisco, tamanhoBloco);
        this.inodeDiretorioRaiz = criarDiretorioRaiz("/");
        this.inodeDiretorioAtual = this.inodeDiretorioRaiz;
    }

    public void moverArquivo(String nomeArquivo, String caminho) throws IOException {

        Diretorio diretorioAtual;
        String caminhoDiretorioAtual;
        Inode inode;

        System.out.println("Movendo arquivo!");

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

        caminhoDiretorioAtual = diretorioAtual.getCaminho();

        inode = removerArquivo(nomeArquivo);

        abrirDiretorioArquivo(caminho);

        criarArquivoMover(nomeArquivo, inode);

        abrirDiretorioArquivo(caminhoDiretorioAtual);

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());

    }

    public Inode criarDiretorioRaiz(String nomeDiretorio) {

        Diretorio diretorio = new Diretorio(nomeDiretorio, hd.getMapaBits().pegarPosicaoLivre() + 1, nomeDiretorio);

        List<Integer> listaEnderecosDiretorio = new ArrayList<>();

        /* Inserindo Diretorio no bloco. */
        listaEnderecosDiretorio = hd.inserirBytesBloco(SerializationUtils.serialize(diretorio), 0, listaEnderecosDiretorio);

        Inode inode = new Inode(SerializationUtils.serialize(diretorio).length);

        List<Integer> listaRestante = new ArrayList<>();
        List<Integer> listaRestanteAux = new ArrayList<>();
        listaRestante = inode.verificarLista(listaEnderecosDiretorio);

        if (listaRestante != null) {
            listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
            inode.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
        }

        /* Inserindo I-node no bloco. */
        hd.inserirBytesInodeBloco(SerializationUtils.serialize(inode));

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());

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
                if (indice == lista.size()) {
                    return;
                }

                if (info.getNome().equals(lista.get(indice))) {
                    inode = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
                    if (info.isArquivo()) {
                        abrirArquivoPorInode(inode, info.getNome());
                        return;
                    }else{
                        abrirDiretorioArquivoAux(inode, lista, ++indice);
                    }
                }
            }
        }


        /*if(!encontrou){
            System.out.println("ERRO! Caminho '" + caminho + "' inválido!");
        }*/

    }

    public void abrirDiretorioArquivoAux(Inode inode, List<String> lista, int indice) {

        Inode inode1;

        /* Não esquecer de usar return */
        if (indice == lista.size()) {
            inodeDiretorioAtual = inode;
            imprimirDiretorioAtual();
            return;
        }

        Diretorio diretorio = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));

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
                    abrirArquivoPorInode(inode1, info.getNome());
                    return;
                } else {
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
            if (!info.isArquivo() & info.getNome().equals(nomeNovoDiretorio)) {
                System.out.println("ERRO AO CRIAR DIRETÓRIO! Diretório já existe!");
                return;
            }
        }

        if (diretorioAtual.getNomeDiretorio().equals("/")) {
            novoDiretorio = new Diretorio(nomeNovoDiretorio, hd.getMapaBits().pegarPosicaoLivre() + 1, diretorioAtual.getCaminho() + nomeNovoDiretorio);
        } else {
            novoDiretorio = new Diretorio(nomeNovoDiretorio, hd.getMapaBits().pegarPosicaoLivre() + 1, diretorioAtual.getCaminho() + "/" + nomeNovoDiretorio);
        }

        List<Integer> listaEnderecosNovoDiretorio = new ArrayList<Integer>();
        Integer enderecoInodeNovoDiretorio;
        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<Integer>();

        /* Inserindo Diretorio no bloco. */
        listaEnderecosNovoDiretorio = hd.inserirBytesBloco(SerializationUtils.serialize(novoDiretorio), 0, listaEnderecosNovoDiretorio);

        Inode inodeNovoDiretorio = new Inode(SerializationUtils.serialize(novoDiretorio).length);

        List<Integer> listaRestante = new ArrayList<>();
        List<Integer> listaRestanteAux = new ArrayList<>();
        listaRestante = inodeNovoDiretorio.verificarLista(listaEnderecosNovoDiretorio);

        if (listaRestante != null) {
            listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
            inodeNovoDiretorio.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
        }

        /* Inserindo I-node no bloco. */
        enderecoInodeNovoDiretorio = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeNovoDiretorio));

        infoNovoDiretorio = new Diretorio.Info(nomeNovoDiretorio, false, enderecoInodeNovoDiretorio);

        diretorioAtual.getTabela().add(infoNovoDiretorio);

        /* Atualiza o atual diretorio / diretorio pai */
        for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
            hd.getMapaBits().remover(integer);
        }

        novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

//        inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
        inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

        listaRestante = inodeDiretorioAtual.verificarLista(novaListaEnderecosDiretorioAtual);

        if (listaRestante != null) {
            listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
            inodeDiretorioAtual.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
        }

        /* Atualiza o inode do atual diretorio / diretorio pai */
        hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());
        hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());

        System.out.println("Diretório '" + novoDiretorio.getNomeDiretorio() + "' criado com sucesso no caminho '" + diretorioAtual.getCaminho() + "'!\n");
    }

    public void renomearDiretorio(String nomeDiretorio, String novoNomeDiretorio) {

        Diretorio diretorioAtual, diretorioARenomear;
        Inode inodeDiretorioARenomear;
        List<Integer> novaListaEnderecosDiretorioRenomeado = new ArrayList<Integer>();
        Integer novoEnderecoInodeDiretorioRenomeado;
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

//                inodeDiretorioARenomear.setListaEnderecos(novaListaEnderecosDiretorioRenomeado);
                inodeDiretorioARenomear.setTamanho(SerializationUtils.serialize(diretorioARenomear).length);
                inodeDiretorioARenomear.setModificado(new Date(System.currentTimeMillis()));

                List<Integer> listaRestante = new ArrayList<>();
                List<Integer> listaRestanteAux = new ArrayList<>();

                listaRestante = inodeDiretorioARenomear.verificarLista(novaListaEnderecosDiretorioRenomeado);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeDiretorioARenomear.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                /* Atualizando I-node do Diretorio Renomeado no disco */
                hd.getMapaBits().remover(info.getEnderecoBlocoInode());

                novoEnderecoInodeDiretorioRenomeado = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioARenomear));

                diretorioAtual.getTabela().get(posicaoInfo).setNome(novoNomeDiretorio);
                diretorioAtual.getTabela().get(posicaoInfo).setEnderecoBlocoInode(novoEnderecoInodeDiretorioRenomeado);

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

//                inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                listaRestante = inodeDiretorioAtual.verificarLista(novaListaEnderecosDiretorioAtual);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeDiretorioAtual.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                /* Atualizando I-node do Diretorio Atual no disco */
                hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());

                hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

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

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());
    }

//    public void removerDiretorio(String nomeDiretorio){
//
//        Diretorio diretorioAtual;
//        Inode inodeDiretorioRemover;
//        boolean encontrou = false;
//        int posicaoInfo = 0;
//        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<>();
//        List<Integer> novaListaEnderecosInodeDiretorioAtual = new ArrayList<>();
//
//        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
//
//        for(Diretorio.Info info : diretorioAtual.getTabela()){
//            if(!info.isArquivo() & info.getNome().equals(nomeDiretorio)){
//
//                encontrou = true;
//
//                inodeDiretorioRemover = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
//
//                if(removerDiretorioVazio(inodeDiretorioRemover)){
//                    diretorioAtual.getTabela().remove(posicaoInfo);
//
//                    *//* Atualizando Diretorio Atual no disco *//*
//                    for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
//                        hd.getMapaBits().remover(integer);
//                    }
//
//                    novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);
//
//                    inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
//                    inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);
//
//                    *//* Atualizando I-node Diretorio Atual no disco *//*
//                    hd.getMapaBitsInode().remover(diretorioAtual.getEnderecoInode());
//
//                    novaListaEnderecosInodeDiretorioAtual = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual), 0, novaListaEnderecosInodeDiretorioAtual);
//
//                } else{
//                    *//* Chamada metodo auxiliar recursivo *//*
//                    removerDiretorioAux(inodeDiretorioRemover, inodeDiretorioAtual);
//                }
//
//            }
//            posicaoInfo++;
//        }
//
//        if(!encontrou){
//            System.out.println("ERRO AO REMOVER DIRETÓRIO! Diretório '" + nomeDiretorio + "' não existe no diretório atual '" + diretorioAtual.getCaminho() +"'!\n");
//        }
//
//    }
//
//    public void removerDiretorioAux(Inode inode, Inode inodePai){
//
//        Inode inode1;
//        Diretorio diretorio;
//        int posicaoInfo = 0, posicaoInfoRemover = 0;
//        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<>();
//        List<Integer> novaListaEnderecosInodeDiretorioAtual = new ArrayList<>();
//        boolean removeuArquivo = false;
//
//        diretorio = (Diretorio)  SerializationUtils.deserialize(hd.buscarBytesBloco(inode));
//
//        for(Diretorio.Info info : diretorio.getTabela()){
//            if(info.isArquivo()){
//                removerArquivoDiretorio(info.getNome(), inode);
//                removeuArquivo = removerDiretorioVazio(inode);
//                posicaoInfoRemover = posicaoInfo;
//            } else{
//                inode1 = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
//                if(removerDiretorioVazio(inode1)){
//                    diretorio.getTabela().remove(posicaoInfo);
//
//                    *//* Atualizando Diretorio Atual no disco *//*
//                    for (Integer integer : inode.getListaEnderecos()) {
//                        hd.getMapaBits().remover(integer);
//                    }
//
//                    novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorio), 0, novaListaEnderecosDiretorioAtual);
//
//                    inode.setListaEnderecos(novaListaEnderecosDiretorioAtual);
//                    inode.setTamanho(SerializationUtils.serialize(diretorio).length);
//
//                    *//* Atualizando I-node Diretorio Atual no disco *//*
//                    hd.getMapaBitsInode().remover(diretorio.getEnderecoInode());
//
//                    novaListaEnderecosInodeDiretorioAtual = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inode), 0, novaListaEnderecosInodeDiretorioAtual);
//
//                }else{
//                    removerDiretorioAux(inode1);
//                }
//            }
//            posicaoInfo++;
//        }
//
//        if(removeuArquivo){
//            diretorio.getTabela().remove(posicaoInfoRemover);
//            /* Atualizando Diretorio Atual no disco */
//            for (Integer integer : inode.getListaEnderecos()) {
//                hd.getMapaBits().remover(integer);
//            }
//
//            novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorio), 0, novaListaEnderecosDiretorioAtual);
//
//            inode.setListaEnderecos(novaListaEnderecosDiretorioAtual);
//            inode.setTamanho(SerializationUtils.serialize(diretorio).length);
//
//            /* Atualizando I-node Diretorio Atual no disco */
//            hd.getMapaBitsInode().remover(diretorio.getEnderecoInode());
//
//            novaListaEnderecosInodeDiretorioAtual = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inode), 0, novaListaEnderecosInodeDiretorioAtual);
//        }
//
//    }

    public boolean removerDiretorioVazio(Inode inode, Inode inodePai) {

        Diretorio diretorio = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));

        if (diretorio.getTabela().size() == 0) {
            /* Remove o diretorio do disco */
            for (Integer integer : inode.getListaEnderecos()) {
                hd.getMapaBits().remover(integer);
            }
            /* Remove o i-node desse diretorio do disco */
            hd.getMapaBits().remover(diretorio.getEnderecoInode());
            return true;
        }

        return false;

    }

    public void criarArquivo(String nomeArquivo) throws IOException {

        String caminhoArquivo;

        caminhoArquivo = "C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\" + nomeArquivo;

        Inode inodeArquivo;
        Diretorio diretorioAtual;
        List<Integer> listaEnderecosNovoArquivo = new ArrayList<Integer>();
        Integer enderecoInodeNovoArquivo;
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

                Arquivo arquivo = new Arquivo();
                arquivo.lerFile(file);

                listaEnderecosNovoArquivo = hd.inserirBytesBloco(arquivo.serializar(), 0, listaEnderecosNovoArquivo);

                inodeArquivo = new Inode(arquivo.serializar().length);

                List<Integer> listaRestante = new ArrayList<>();
                List<Integer> listaRestanteAux = new ArrayList<>();
                listaRestante = inodeArquivo.verificarLista(listaEnderecosNovoArquivo);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeArquivo.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                enderecoInodeNovoArquivo = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeArquivo));

                Diretorio.Info info = new Diretorio.Info(arquivo.getNome(), true, enderecoInodeNovoArquivo);

                diretorioAtual.getTabela().add(info);

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novalistaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novalistaEnderecosDiretorioAtual);

//                inodeDiretorioAtual.setListaEnderecos(novalistaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                listaRestante = inodeDiretorioAtual.verificarLista(novalistaEnderecosDiretorioAtual);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeDiretorioAtual.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                /* Atualizando I-node Diretorio Atual no disco */
                hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());
                hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

                System.out.println("Arquivo '" + file.getName() + "' criado com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' de caminho '" + diretorioAtual.getCaminho() + "' !\n");
            }

        } else {
            System.out.println("Erro ao criar arquivo!");
        }
        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());
    }


    public void criarArquivoMover(String nomeArquivo, Inode inodeArquivoRemovido) throws IOException {

        String caminhoArquivo;

        caminhoArquivo = "C:\\Users\\cassi\\OneDrive\\Documentos\\GitHub\\TP03SO\\src\\main\\resources\\" + nomeArquivo;

        Inode inodeArquivo;
        Diretorio diretorioAtual;
        List<Integer> listaEnderecosNovoArquivo = new ArrayList<Integer>();
        Integer enderecoInodeNovoArquivo;
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

                Arquivo arquivo = new Arquivo();
                arquivo.lerFile(file);

                listaEnderecosNovoArquivo = hd.inserirBytesBloco(arquivo.serializar(), 0, listaEnderecosNovoArquivo);

                inodeArquivo = new Inode(arquivo.serializar().length);
                inodeArquivo.setAcessado(inodeArquivoRemovido.getAcessado());
                inodeArquivo.setModificado(new Date(System.currentTimeMillis()));
                inodeArquivo.setCriado(inodeArquivoRemovido.getCriado());

                List<Integer> listaRestante = new ArrayList<>();
                List<Integer> listaRestanteAux = new ArrayList<>();
                listaRestante = inodeArquivo.verificarLista(listaEnderecosNovoArquivo);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeArquivo.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                enderecoInodeNovoArquivo = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeArquivo));

                Diretorio.Info info = new Diretorio.Info(arquivo.getNome(), true, enderecoInodeNovoArquivo);

                diretorioAtual.getTabela().add(info);

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novalistaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novalistaEnderecosDiretorioAtual);

//                inodeDiretorioAtual.setListaEnderecos(novalistaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

                listaRestante = inodeDiretorioAtual.verificarLista(novalistaEnderecosDiretorioAtual);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeDiretorioAtual.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                /* Atualizando I-node Diretorio Atual no disco */
                hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());
                hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

                System.out.println("Arquivo '" + file.getName() + "' criado com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' de caminho '" + diretorioAtual.getCaminho() + "' !\n");
            }

        } else {
            System.out.println("Erro ao criar arquivo!");
        }
        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());
    }

//    public void abrirArquivo(String nomeArquivo) {
//
//        Diretorio diretorioAtual;
//        Inode inodeArquivo;
//        File file;
//        Arquivo arquivo;
//
//        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
//        boolean encontrou = false;
//
//        for (Diretorio.Info info : diretorioAtual.getTabela()) {
//            if (info.isArquivo() & info.getNome().equals(nomeArquivo)) {
//                encontrou = true;
//
//                inodeArquivo = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));
//
//                file = (File) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeArquivo));
//
//                arquivo = new Arquivo(file);
//
//                arquivo.imprimirArquivo();
//            }
//        }
//
//        if (!encontrou)
//            System.out.println("ERRO AO ABRIR ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());
//
//    }

    public void abrirArquivoPorInode(Inode inode, String nomeArquivo) {

        String texto;
        Arquivo arquivo;
        texto = (String) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));
        arquivo = new Arquivo();
        inode.setAcessado(new Date(System.currentTimeMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        arquivo.setTexto(texto);
        System.out.println("Atributos do Arquivo " + nomeArquivo + " : ");
        System.out.println("Data de criação: " + sdf.format(inode.getCriado()) );
        System.out.println("Data de modificação: " + sdf.format(inode.getModificado()));
        System.out.println("Data de acesso: " + sdf.format(inode.getAcessado()));
        System.out.println();
        System.out.println("Abrindo arquivo '" + nomeArquivo + "'...!\n");
        arquivo.imprimirArquivo();
        System.out.println("Fim do arquivo '" + nomeArquivo + "'...!\n");
    }

    public void renomearArquivo(String nomeArquivo, String novoNomeArquivo) {

        Diretorio diretorioAtual;
        Inode inodeArquivoARenomear;
        File file;
        List<Integer> novaListaEnderecosArquivoRenomeado = new ArrayList<Integer>();
        Integer novoEnderecoInodeArquivoRenomeado;
        List<Integer> novaListaEnderecosDiretorioAtual = new ArrayList<Integer>();
        Integer novoEnderecoInodeDiretorioAtual;

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));
        boolean encontrou = false;
        int posicaoInfo = 0;

        for (Diretorio.Info info : diretorioAtual.getTabela()) {
            if (info.isArquivo() & info.getNome().equals(nomeArquivo)) {
                encontrou = true;

                /*inodeArquivoARenomear = (Inode) SerializationUtils.deserialize(hd.buscarBytesInodeBloco(info.getEnderecoBlocoInode()));

                *//*file = (File) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeArquivoARenomear));

                file.getName().replace(file.getName(), novoNomeArquivo);

                *//**//* File não muda de nome, logo não é necesário atualiza-lo no disco. *//**//*
                *//**//* Atualizar Arquivo renomeado no disco *//**//*
                for (Integer integer : inodeArquivoARenomear.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }*//*

                *//*novaListaEnderecosArquivoRenomeado = hd.inserirBytesBloco(SerializationUtils.serialize(file), 0, novaListaEnderecosArquivoRenomeado);

//                inodeArquivoARenomear.setListaEnderecos(novaListaEnderecosArquivoRenomeado);
                inodeArquivoARenomear.setTamanho(SerializationUtils.serialize(file).length);

                List<Integer> listaRestante = new ArrayList<>();
                List<Integer> listaRestanteAux = new ArrayList<>();
                listaRestante = inodeArquivoARenomear.verificarLista(novaListaEnderecosArquivoRenomeado);

                if (listaRestante != null) {
                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                    inodeArquivoARenomear.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
                }

                *//**//* Atualizando I-node Arquivo renomeado no disco *//**//*
                hd.getMapaBits().remover(diretorioAtual.getTabela().get(posicaoInfo).getEnderecoBlocoInode());
*//*
                novoEnderecoInodeArquivoRenomeado = hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeArquivoARenomear));
*/
                diretorioAtual.getTabela().get(posicaoInfo).setNome(novoNomeArquivo);
//                diretorioAtual.getTabela().get(posicaoInfo).setEnderecoBlocoInode(novoEnderecoInodeArquivoRenomeado);

                /* Atualizando Diretorio Atual no disco */
                for (Integer integer : inodeDiretorioAtual.getListaEnderecos()) {
                    hd.getMapaBits().remover(integer);
                }

                novaListaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novaListaEnderecosDiretorioAtual);

//                inodeDiretorioAtual.setListaEnderecos(novaListaEnderecosDiretorioAtual);
                inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);
                inodeDiretorioAtual.setModificado(new Date(System.currentTimeMillis()));

//                listaRestante = inodeArquivoARenomear.verificarLista(novaListaEnderecosArquivoRenomeado);
//
//                if (listaRestante != null) {
//                    listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
//                    inodeArquivoARenomear.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
//                }

                /* Atualizando I-node Diretorio Atual no disco */
                hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());

                hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

                diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inodeDiretorioAtual));

                for (Diretorio.Info info1 : diretorioAtual.getTabela()) {
                    if (info1.isArquivo() & info1.getNome().equals(novoNomeArquivo)) {
                        System.out.println("Arquivo '" + nomeArquivo + "' renomeado com sucesso para '" + info1.getNome() + "' no diretório '" + diretorioAtual.getNomeDiretorio() + "' \n");
                    }
                }


            }
            posicaoInfo++;
        }

        if (!encontrou)
            System.out.println("ERRO AO RENOMEAR ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());

    }

    public Inode removerArquivo(String nomeArquivo) {

        Inode inodeArquivoARemover = null;
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
                hd.getMapaBits().remover(info.getEnderecoBlocoInode());

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

//            inodeDiretorioAtual.setListaEnderecos(novalistaEnderecosDiretorioAtual);
            inodeDiretorioAtual.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

            List<Integer> listaRestante = new ArrayList<>();
            List<Integer> listaRestanteAux = new ArrayList<>();
            listaRestante = inodeDiretorioAtual.verificarLista(novalistaEnderecosDiretorioAtual);

            if (listaRestante != null) {
                listaRestanteAux = hd.inserirBytesBloco(SerializationUtils.serialize((Serializable) listaRestante), 0, listaRestanteAux);
                inodeDiretorioAtual.setEnderecoBlocoDemaisEnderecos(listaRestanteAux.get(0));
            }

            /* Atualizando I-node Diretorio Atual no disco */
            hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());
            hd.inserirBytesInodeBloco(SerializationUtils.serialize(inodeDiretorioAtual));

            System.out.println("Arquivo '" + nomeArquivo + "' removido com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' de caminho '" + diretorioAtual.getCaminho() + "'! \n");
        } else
            System.out.println("ERRO AO REMOVER ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());

        return inodeArquivoARemover;

    }

    public void removerArquivoDiretorio(String nomeArquivo, Inode inode) {

        Inode inodeArquivoARemover;
        Diretorio diretorioAtual;
        List<Integer> novalistaEnderecosDiretorioAtual = new ArrayList<Integer>();
        List<Integer> novalistaEnderecosInodeDiretorioAtual = new ArrayList<Integer>();

        diretorioAtual = (Diretorio) SerializationUtils.deserialize(hd.buscarBytesBloco(inode));
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
                hd.getMapaBits().remover(info.getEnderecoBlocoInode());

            }

            posicaoInfo++;
        }
        if (encontrou) {
            /* Removendo Informações (Info) do arquivo no diretorio atual */
            diretorioAtual.getTabela().remove(diretorioAtual.getTabela().get(posicaoInfoFinal));

            /* Atualizando Diretorio Atual no disco */
            for (Integer integer : inode.getListaEnderecos()) {
                hd.getMapaBits().remover(integer);
            }

            novalistaEnderecosDiretorioAtual = hd.inserirBytesBloco(SerializationUtils.serialize(diretorioAtual), 0, novalistaEnderecosDiretorioAtual);

            inode.setListaEnderecos(novalistaEnderecosDiretorioAtual);
            inode.setTamanho(SerializationUtils.serialize(diretorioAtual).length);

            /* Atualizando I-node Diretorio Atual no disco */
            hd.getMapaBits().remover(diretorioAtual.getEnderecoInode());

            hd.inserirBytesInodeBloco(SerializationUtils.serialize(inode));

            System.out.println("Arquivo '" + nomeArquivo + "' removido com sucesso no diretório '" + diretorioAtual.getNomeDiretorio() + "' de caminho '" + diretorioAtual.getCaminho() + "'! \n");
        } else
            System.out.println("ERRO AO REMOVER ARQUIVO! Arquivo " + nomeArquivo + " não existe no diretório " + diretorioAtual.getNomeDiretorio());

        superBloco.setQtdeBlocosLivres(hd.getMapaBits().qtdeBlocosLivres());
    }


    public Disco getHd() {
        return hd;
    }

    public void setHd(Disco hd) {
        this.hd = hd;
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

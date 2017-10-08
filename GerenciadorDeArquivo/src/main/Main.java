package main;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.GerenciadorBuffer;
import entidades.SorteadorDeRowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.RowId;
import exceptions.ContainerNoExistent;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.stream.Collectors;

public class Main {
    private static String absolutePathProject() {
        return System.getProperty("user.dir") + "\\";
    }
    private static String inputPath() {
        return absolutePathProject() + GlobalVariables.LOCAL_ARQUIVO_ENTRADA;
    }
    private static GerenciadorArquivo ga;
    private static GerenciadorArquivoService gaService;
    private static GerenciadorBuffer gb;

    public static void main(String[] args) {
        System.out.println("Pasta onde está localizado os arquivos input's: ");
        System.out.println(inputPath());

        System.out.println();

        File[] arquivos = selecionarArquivosParaProcessar();
        ga = new GerenciadorArquivo();
        gaService = new GerenciadorArquivoService(ga);

        ArrayList<BlocoContainer> containers = processarArquivos(arquivos);
        ArrayList<RowId> rowIds = sortearRowId(containers);

        gb = new GerenciadorBuffer(GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);
        processarRowIds(rowIds);

        System.out.println("\n------------------------------------------------------------------------------------------------------\n");

        System.out.println("Tamanho dos blocos usado: " + GlobalVariables.TAMANHO_BLOCO);
        System.out.println("Tamanho do Gerenciado de Buffer usado: " + GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);

        System.out.println("Quantidade de Hits: " + gb.getHit());
        System.out.println("Quantidade de Miss: " + gb.getMiss());

        System.out.println("Taxa de Hits: " + gb.taxaAcerto());

    }

    private static File[] selecionarArquivosParaProcessar() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Arquivos localizados: ");
        System.out.println("0. Começar");

        File[] directores = todosOsArquivosDisponiveis();
        printarTodosOsArquivos(directores);

        int quantidadeSelecionados = 0;
        File[] arquivosSelecionados = new File[directores.length];
        int indexSelecionado;

        do {
            System.out.println("\nEscolha os arquivos: ");
            indexSelecionado = scanner.nextInt();

            if (indexSelecionado > 0)
                arquivosSelecionados[quantidadeSelecionados] = directores[indexSelecionado - 1];

            quantidadeSelecionados++;
        } while (indexSelecionado != 0);

        System.out.println("\nArquivos selecionados:");
        printarTodosOsArquivos(arquivosSelecionados);

        return arquivosSelecionados;
    }

    private static ArrayList<BlocoContainer> processarArquivos(File[] arquivos) {
        ArrayList<BlocoContainer> containers = new ArrayList<BlocoContainer>();

        for (File file: arquivos) {
            if (file != null)
            {
                System.out.println("\n------------------------------------------------------------------------------------------------------\n");
                System.out.println(" **Iniciando** o Processo de Criação da Tabela *" + file.getName() + "*");

                long tempoInicio = System.currentTimeMillis();

                try {
                    containers.add(gaService.gerarContainerByInput(file.getAbsolutePath()));
                } catch (FileNotFoundException e) {
                    System.out.println("\nErro ao Gerar o Arquivo Binario da tabela " + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ContainerNoExistent containerNoExistent) {
                    containerNoExistent.printStackTrace();
                }
                System.out.println(" **Finalizado** o Processo de Criação da Tabela *" + file.getName() + "*");
                System.out.println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");
            }
        }

        return containers;
    }

    private static ArrayList<RowId> sortearRowId(ArrayList<BlocoContainer> containers) {
        System.out.println("\n------------------------------------------------------------------------------------------------------\n");
        System.out.println(" **Iniciando** o Sorteamento dos RowIds");

        ArrayList<RowId> allRowIds = new ArrayList<RowId>();
        long tempoInicio = System.currentTimeMillis();

        containers.stream().forEach(container -> {
             allRowIds.addAll(container.getBlocosDados().stream().map(b -> new RowId(b.getHeader().getContainerId(), b.getHeader().getBlocoId())).collect(Collectors.toList()));
        });

        ArrayList<RowId> rowIds = SorteadorDeRowId.Sortear(allRowIds);

        System.out.println(" **Finalizado** o Sorteamento dos RowIds");
        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");

        System.out.println("\n------------------------------------------------------------------------------------------------------\n");
        System.out.println(" **Inicializando** o Gravamento do RowIds Sorteados");

        tempoInicio = System.currentTimeMillis();

        try {
            SorteadorDeRowId.gravarSorteados(rowIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" **Finalizado** o Gravamento do RowIds Sorteados");
        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");

        return rowIds;
    }

    private static void processarRowIds(ArrayList<RowId> rowIds) {
        System.out.println("\n------------------------------------------------------------------------------------------------------\n");
        System.out.println(" **Inicializando** as Requisições dos RowIds Sorteados");
        long tempoInicio = System.currentTimeMillis();

        rowIds.stream().forEach(rowId -> {

            BlocoDado blocoCache = gb.existRowId(rowId);
            System.out.print("     Request: *"+rowId.toString() );
            System.out.println(blocoCache == null ? "       - Miss" : " - Hit");

            if (blocoCache == null) {
                try {
                    long tempoInicioBuscarEmDisco = System.currentTimeMillis();

                    blocoCache = ga.lerBloco(rowId);
                    gb.addBlock(blocoCache);

                    System.out.println("         Tempo para buscar em disco: "+ (System.currentTimeMillis() - tempoInicioBuscarEmDisco) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");
        System.out.println(" **Finalizado** as Requisições dos RowIds Sorteados");

    }

    private static File[] todosOsArquivosDisponiveis(){
        File file = new File(inputPath());
        File[] directores = file.listFiles();

        return directores;
    }

    private static void printarTodosOsArquivos(File[] directores){
        for (int i = 0; i < directores.length; i++) {
            if (directores[i] != null)
                System.out.println(String.format("%s. %s", i+1, directores[i].getName()));
        }
    }
}

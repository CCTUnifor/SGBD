package main;

import entidades.*;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.RowId;
import exceptions.ContainerNoExistent;
import utils.GlobalVariables;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
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
    private static Date date = new Date();

    public static void main(String[] args) throws IOException {
        println("Pasta onde está localizado os arquivos input's: ");
        println(inputPath());
        println("");

        File[] arquivos = selecionarArquivosParaProcessar();
        ga = new GerenciadorArquivo();
        gaService = new GerenciadorArquivoService(ga);

        ArrayList<BlocoContainer> containers = processarArquivos(arquivos);
        ArrayList<RowId> rowIds = sortearRowId(containers);

        gb = new GerenciadorBuffer(GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);
        processarRowIds(rowIds);

        println("\n------------------------------------------------------------------------------------------------------\n");

        println("Tamanho dos blocos usado: " + GlobalVariables.TAMANHO_BLOCO);
        println("Tamanho do Gerenciador de Buffer usado: " + GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);

        println("Quantidade de Hits: " + gb.getHit());
        println("Quantidade de Miss: " + gb.getMiss());

        println("Taxa de Hits: " + gb.taxaAcerto() + "%");

    }

    private static File[] selecionarArquivosParaProcessar() {
        Scanner scanner = new Scanner(System.in);

        println("Arquivos localizados: ");
        println("0. Começar");

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

        println("\nArquivos selecionados:");
        printarTodosOsArquivos(arquivosSelecionados);

        return arquivosSelecionados;
    }

    private static ArrayList<BlocoContainer> processarArquivos(File[] arquivos)  {
        ArrayList<BlocoContainer> containers = new ArrayList<BlocoContainer>();

        for (File file: arquivos) {
            if (file != null)
            {
                println("\n------------------------------------------------------------------------------------------------------\n");
                println(" **Iniciando** o Processo de Criação da Tabela *" + file.getName() + "*");

                long tempoInicio = System.currentTimeMillis();

                try {
                    containers.add(gaService.gerarContainerByInput(file.getAbsolutePath()));
                } catch (FileNotFoundException e) {
                    println("\nErro ao Gerar o Arquivo Binario da tabela " + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ContainerNoExistent containerNoExistent) {
                    containerNoExistent.printStackTrace();
                }
                println(" **Finalizado** o Processo de Criação da Tabela *" + file.getName() + "*");
                println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio));
            }
        }

        return containers;
    }

    private static ArrayList<RowId> sortearRowId(ArrayList<BlocoContainer> containers) {
        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Iniciando** o Sorteamento dos RowIds");

        ArrayList<RowId> allRowIds = new ArrayList<RowId>();
        long tempoInicio = System.currentTimeMillis();

        containers.stream().forEach(container -> {
            allRowIds.addAll(container.getBlocosDados().stream().map(b -> new RowId(b.getHeader().getContainerId(), b.getHeader().getBlocoId())).collect(Collectors.toList()));
        });

        ArrayList<RowId> rowIds = SorteadorDeRowId.Sortear(allRowIds);

        println(" **Finalizado** o Sorteamento dos RowIds");
        println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");

        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Inicializando** o Gravamento do RowIds Sorteados");

        tempoInicio = System.currentTimeMillis();

        try {
            SorteadorDeRowId.gravarSorteados(rowIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        println(" **Finalizado** o Gravamento do RowIds Sorteados");
        println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");

        return rowIds;
    }

    private static void processarRowIds(ArrayList<RowId> rowIds) {
        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Inicializando** as Requisições dos RowIds Sorteados");
        long tempoInicio = System.currentTimeMillis();

        rowIds.stream().forEach(rowId -> {

            BlocoDado blocoCache = gb.existRowId(rowId);
            print("\n     Request: *"+rowId.toString() );
            println(blocoCache == null ? "       - Miss" : "    - << Hit >>");

            if (blocoCache == null) {
                try {
                    blocoCache = ga.lerBloco(rowId);
                    gb.addBlock(blocoCache);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio) + "\n");
        println(" **Finalizado** as Requisições dos RowIds Sorteados");

    }

    private static File[] todosOsArquivosDisponiveis(){
        File file = new File(inputPath());
        File[] directores = file.listFiles();

        return directores;
    }

    private static void printarTodosOsArquivos(File[] directores){
        for (int i = 0; i < directores.length; i++) {
            if (directores[i] != null)
                println(String.format("%s. %s", i+1, directores[i].getName()));
        }
    }

    private static void print(String mensagem) {
        System.out.print(mensagem);
        File file = new File(GlobalVariables.LOCAL_ARQUIVO_FINAL_RESULTADOS + "RESULTADO_"+ identificador() + ".txt");

        try(  FileWriter out = new FileWriter(file, true)  ){
            out.append(mensagem);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void println(String mensagem) {
        System.out.println(mensagem);
        File file = new File(GlobalVariables.LOCAL_ARQUIVO_FINAL_RESULTADOS + "RESULTADO_"+ identificador() + ".txt");

        try(  FileWriter out = new FileWriter(file, true)  ){
            out.append(mensagem + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String identificador() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        return dateFormat.format(date);
    }
}
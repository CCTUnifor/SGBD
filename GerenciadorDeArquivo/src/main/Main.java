package main;

import entidades.*;
import entidades.blocos.BlocoDado;
import entidades.blocos.BlocoDadoHeader;
import entidades.blocos.RowId;
import exceptions.ContainerNoExistentException;
import utils.GlobalVariables;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

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
        long tempoInicioTotal = System.currentTimeMillis();

        println("Pasta onde está localizado os arquivos input's: ");
        println(inputPath());
        println("");

        File[] arquivos = selecionarArquivosParaProcessar();
        ga = new GerenciadorArquivo();
        gaService = new GerenciadorArquivoService(ga);

        if (arquivos != null) {
            ArrayList<RowId> todosRowIds = processarArquivos(arquivos);
            ArrayList<RowId> rowIds = sortearRowId(todosRowIds);

            gb = new GerenciadorBuffer(GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);
            processarRowIds(rowIds);

            println("\n------------------------------------------------------------------------------------------------------\n");

            println("Tamanho dos blocos usado: " + BlocoDadoHeader.TABLE_BLOCK_LENGTH);
            println("Tamanho do Gerenciador de Buffer usado: " + GlobalVariables.TAMANHO_GERENCIADOR_BUFFER);

            println("Quantidade de Hits: " + gb.getHit());
            println("Quantidade de Miss: " + gb.getMiss());

            println("Taxa de Hits: " + gb.taxaAcerto() + "%");

            println("");
            printTempoExecucao(tempoInicioTotal);
        }

        println("\n**Iniciando interface gráfica**");

        front.Main front = new front.Main();
        front.caller(args);
    }

    private static void printTempoExecucao(long tempoInicio) {
        println("Tempo de execução " + new SimpleDateFormat("mm:ss").format(new Date((System.currentTimeMillis() - tempoInicio))) + " minutos");
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

        if (quantidadeSelecionados == 1)
            return null;

        println("\nArquivos selecionados:");
        printarTodosOsArquivos(arquivosSelecionados);

        return arquivosSelecionados;
    }

    private static ArrayList<RowId> processarArquivos(File[] arquivos) {
        ArrayList<RowId> rowIds = new ArrayList<RowId>();

        for (File file : arquivos) {
            if (file != null) {
                println("\n------------------------------------------------------------------------------------------------------\n");
                println(" **Iniciando** o Processo de Criação da Tabela *" + file.getName() + "*");

                long tempoInicio = System.currentTimeMillis();

                try {
                    rowIds.addAll(gaService.gerarContainerByInput(file.getAbsolutePath()));
                } catch (FileNotFoundException e) {
                    println("\nErro ao Gerar o Arquivo Binario da tabela " + file.getName());
                }
                println(" **Finalizado** o Processo de Criação da Tabela *" + file.getName() + "*");
                printTempoExecucao(tempoInicio);
            }
        }

        return rowIds;
    }

    private static ArrayList<RowId> sortearRowId(ArrayList<RowId> allRowIds) {
        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Iniciando** o Sorteamento dos RowIds");
        long tempoInicio = System.currentTimeMillis();

        ArrayList<RowId> rowIds = SorteadorDeRowId.Sortear(allRowIds);

        println(" **Finalizado** o Sorteamento dos RowIds");
        printTempoExecucao(tempoInicio);

        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Inicializando** o Gravamento do RowIds Sorteados");

        tempoInicio = System.currentTimeMillis();

        try {
            SorteadorDeRowId.gravarSorteados(rowIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        println(" **Finalizado** o Gravamento do RowIds Sorteados");
        printTempoExecucao(tempoInicio);

        return rowIds;
    }

    private static void processarRowIds(ArrayList<RowId> rowIds) {
        println("\n------------------------------------------------------------------------------------------------------\n");
        println(" **Inicializando** as Requisições dos RowIds Sorteados");
        long tempoInicio = System.currentTimeMillis();

        rowIds.stream().forEach(rowId -> {
            BlocoDado blocoCache = gb.existRowId(rowId);
            print("\n     Request: *" + rowId.toString());
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
        printTempoExecucao(tempoInicio);
        println(" **Finalizado** as Requisições dos RowIds Sorteados");
    }

    private static File[] todosOsArquivosDisponiveis() {
        File file = new File(inputPath());
        File[] directores = file.listFiles();

        return directores;
    }

    private static void printarTodosOsArquivos(File[] directores) {
        for (int i = 0; i < directores.length; i++) {
            if (directores[i] != null)
                println(String.format("%s. %s", i + 1, directores[i].getName()));
        }
    }

    private static void print(String mensagem) {
        System.out.print(mensagem);
        String _path = GlobalVariables.LOCAL_ARQUIVO_FINAL_RESULTADOS + "RESULTADO_" + identificador() + ".txt";
        GerenciadorDeIO.makeFiles(_path);
        File file = new File(_path);

        try (FileWriter out = new FileWriter(file, true)) {
            out.append(mensagem).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void println(String mensagem) {
        System.out.println(mensagem);
        String _path = GlobalVariables.LOCAL_ARQUIVO_FINAL_RESULTADOS + "RESULTADO_" + identificador() + ".txt";

        GerenciadorDeIO.makeFiles(_path);
        File file = new File(_path);

        try (FileWriter out = new FileWriter(file, true)) {
            out.append(mensagem).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String identificador() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        return dateFormat.format(date);
    }

}

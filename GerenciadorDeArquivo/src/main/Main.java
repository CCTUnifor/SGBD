package main;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import exceptions.ContainerNoExistent;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

public class Main {
    private static String absolutePathProject() {
        return System.getProperty("user.dir") + "\\";
    }
    private static String inputPath() {
        return absolutePathProject() + GlobalVariables.LOCAL_ARQUIVO_ENTRADA;
    }
    private static GerenciadorArquivo ga;
    private static GerenciadorArquivoService gaService;

    public static void main(String[] args) {
        System.out.println("Pasta onde está localizado os arquivos input's: ");
        System.out.println(inputPath());

        System.out.println();

        File[] arquivos = selecionarArquivosParaProcessar();
        ga = new GerenciadorArquivo();
        gaService = new GerenciadorArquivoService(ga);

        processarArquivos(arquivos);
        


    }

    private  static File[] selecionarArquivosParaProcessar() {
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

    private static void processarArquivos(File[] arquivos) {
        for (File file: arquivos) {
            if (file == null)
                return;

            System.out.println("\n------------------------------------------------------------------------------------------------------\n");
            System.out.println(" **Iniciando** o Processo de Criação da Tabela *" + file.getName() + "*");

            long tempoInicio = System.currentTimeMillis();

            try {
                gaService.gerarContainerByInput(file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                System.out.println("\nErro ao Gerar o Arquivo Binario da tabela " + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ContainerNoExistent containerNoExistent) {
                containerNoExistent.printStackTrace();
            }
            System.out.println(" **Finalizado** o Processo de Criação da Tabela *" + file.getName() + "*");
            System.out.println("Tempo de execução: " + (System.currentTimeMillis() - tempoInicio));
        }
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

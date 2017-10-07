package main;

import entidades.GerenciadorArquivo;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Main {
    private static String absolutePathProject() {
        return System.getProperty("user.dir") + "\\";
    }

    private static String inputPath() {
        return absolutePathProject() + GlobalVariables.LOCAL_ARQUIVO_ENTRADA;
    }

    public static void main(String[] args) {
        System.out.println("Pasta onde está localizado os arquivos input's: ");
        System.out.println(inputPath());

        System.out.println();

        File[] arquivos = selecionarArquivosParaProcessar();
        GerenciadorArquivo ga = new GerenciadorArquivo();

        processarArquivos(arquivos, ga);

    }

    private  static File[] selecionarArquivosParaProcessar() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Arquivos localizados: ");
        System.out.println("0. Cancelar");
        System.out.println("1. Começar");

        File[] directores = todosOsArquivosDisponiveis();
        printarTodosOsArquivos(directores);

        int quantidadeSelecionados = 0;
        File[] arquivosSelecionados = new File[directores.length];
        int indexSelecionado;

        do {
            System.out.println("\nEscolha os arquivos: ");
            indexSelecionado = scanner.nextInt();
            if (indexSelecionado == 0)
                System.exit(0);

            if (indexSelecionado > 1)
                arquivosSelecionados[quantidadeSelecionados] = directores[indexSelecionado - 2];

            quantidadeSelecionados++;
        } while (indexSelecionado != 1);

        System.out.println("\nArquivos selecionados:");
        printarTodosOsArquivos(arquivosSelecionados);

        return arquivosSelecionados;
    }

    private static void processarArquivos(File[] arquivos, GerenciadorArquivo ga) {
        for (File file: arquivos) {
            System.out.println("\n Iniciando o Processo de Criação da Tabela " + file.getName());

            try {
                ga.gerarContainerByInput(file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                System.out.println("\nErro ao Gerar o Arquivo Binario da tabela " + file.getName());
            }
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
                System.out.println(String.format("%s. %s", i+2, directores[i].getName()));
        }
    }
}

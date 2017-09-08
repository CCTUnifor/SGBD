package entidades;

import entidades.blocos.*;
import exceptions.BlocoSemEspacoException;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCount = 0;
    private int blocoIdCount = 1;
    private List<BlocoContainer> containers;

    public GerenciadorArquivo() {
        this.containers = new ArrayList<BlocoContainer>();
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();
        BlocoContainer container = this.containers.get(containerId - 1);
        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++);
        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Linha> dados) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++, dados);

        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(bytes);
        if (bloco.getHeader().getContainerId() < this.containerIdCount)
            throw new ContainerNoExistent();

        return bloco;
    }

    @Override
    public BlocoContainer criarBlocoContainer() {
        BlocoContainer container = new BlocoContainer(++this.containerIdCount);
        return container;
    }

    @Override
    public BlocoContainer criarBlocoContainer(String linha) throws BlocoSemEspacoException {
        BlocoContainer container = new BlocoContainer(++this.containerIdCount);

        return container;
    }

    @Override
    public void gravarArquivo(byte[] bytes, String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "rw");
        try {
            randomAccessFile.write(bytes);
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public BlocoContainer lerArquivo(String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");

        try {
            String[] linhas = this.popularLinhas(randomAccessFile);
            BlocoContainer container = new BlocoContainer(++this.containerIdCount);
            container.getBlocoControle().adicionarDescritores(this.popularDescritores(container, linhas[0]));

            ArrayList<Linha> tuplas = this.popularLinhas(linhas);
            ArrayList<BlocoDado> blocos = this.popularBlocos(container, tuplas);

            container.adicionarBlocos(blocos);

            randomAccessFile.close();
            return container;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private String[] popularLinhas(RandomAccessFile randomAccessFile) throws IOException {
        int tamanhoArquivo = (int) randomAccessFile.length();
        String[] linhas = new String[tamanhoArquivo];

        for (int i = 0; i < tamanhoArquivo; i++){
            linhas[i] = randomAccessFile.readLine();
        }

        return linhas;
    }

    private ArrayList<Descritor> popularDescritores(BlocoContainer container, String descritor) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        String[] colunas = descritor.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        for (int i = 0; i < colunas.length; i++) {
            String coluna = colunas[i];
            descritores.add(new Descritor(coluna));
        }

        return descritores;
    }

    private ArrayList<Linha> popularLinhas(String[] linhas) {
        ArrayList<Linha> tuplas = new ArrayList<Linha>();

        for (int i = 1; i < linhas.length; i++) {
            String[] colunas = linhas[i].split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

            Linha tuple = new Linha();
            tuple.adicionarColunas(colunas);
            tuplas.add(tuple);
        }
        return tuplas;
    }

    private ArrayList<BlocoDado> popularBlocos(BlocoContainer container, ArrayList<Linha> tuples) {
        ArrayList<BlocoDado> blocos = new ArrayList<BlocoDado>();
        blocos.add(new BlocoDado(container.getContainerId(), ++this.blocoIdCount)); // primeiro bloco

        int blocoIndex = 0;
        for (Linha linha: tuples) {
            BlocoDado bloco = blocos.get(blocoIndex);

            if (!bloco.adicionarTupla(linha)) {
                BlocoDado blocoAux = new BlocoDado(container.getContainerId(), ++this.blocoIdCount);
                blocoAux.adicionarTupla(linha);
                blocos.add(blocoAux);

                blocoIndex++;
            }
        }

        return blocos;
    }
}
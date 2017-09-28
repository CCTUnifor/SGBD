package entidades;

import entidades.blocos.*;
import exceptions.BlocoSemEspacoException;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import sun.nio.cs.StandardCharsets;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCount = 0;
    private int blocoIdCount = 1;

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();
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
    public void gravarArquivo(BlocoContainer container, String diretorio) throws IOException {
        String diretorioCompleto = diretorio + "Tabela" + container.getContainerId() + ".txt";

        File file = new File(diretorioCompleto);
        if (!file.exists())
            file.createNewFile();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            randomAccessFile.write(container.toByteArray());
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public BlocoContainer criarContainerPeloArquivoEntrada(String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");

        try {
            ArrayList<String> linhas = this.popularLinhas(randomAccessFile);
            BlocoContainer container = new BlocoContainer(++this.containerIdCount);
            container.getBlocoControle().adicionarDescritores(this.popularDescritores(container, linhas.get(0)));

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

    @Override
    public BlocoContainer lerArquivo(String diretorio) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(diretorio, "r");
        BlocoContainer container = this.criarBlocoContainer();

        byte[] bytes = null;

        try {
            int tamanho = (int) randomAccessFile.length();
            if (tamanho == 0)
                return null;

            bytes = new byte[tamanho];
            for (int i = 0; i < tamanho; i++){
                bytes[i] = randomAccessFile.readByte();
            }

        }catch (IOException e){
            this.containerIdCount--;
            System.out.println(e.getMessage());
        }

        container.fromByteArray(bytes);
        return container;
    }

    private ArrayList<String> popularLinhas(RandomAccessFile randomAccessFile) throws IOException {
        ArrayList<String> linhas = new ArrayList<String>();

        String linhaAux = null;
        do {
            linhaAux = randomAccessFile.readLine();
            if (linhaAux != null) {
                linhas.add(linhaAux);
            }
        } while (linhaAux != null);

        return linhas;
    }

    private ArrayList<Descritor> popularDescritores(BlocoContainer container, String descritor) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        String[] colunas = descritor.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        for (int i = 0; i < colunas.length; i++) {
            String coluna = colunas[i];
            Descritor _descritor = new Descritor(coluna);
            descritores.add(_descritor);
            container.getBlocoControle().getHeader().atualizarTamanhoDescritor(_descritor.toByteArray().length);
        }

        return descritores;
    }

    private ArrayList<Linha> popularLinhas(ArrayList<String> linhas) {
        ArrayList<Linha> tuplas = new ArrayList<Linha>();

        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

            Linha tuple = new Linha();
            tuple.adicionarColunas(colunas);
            tuplas.add(tuple);
        }

        return tuplas;
    }

    private ArrayList<BlocoDado> popularBlocos(BlocoContainer container, ArrayList<Linha> tuples) {
        ArrayList<BlocoDado> blocos = new ArrayList<BlocoDado>();
        blocos.add(new BlocoDado(container.getContainerId(), this.blocoIdCount++)); // primeiro bloco

        for (Linha linha: tuples) {
            int blocoIndex = container.getBlocoControle().getHeader().getProximoBlocoLivre();
            BlocoDado bloco = blocos.get(blocoIndex);

            if (podeAdicionarMaisTuple(bloco, linha, container))
                bloco.adicionarTupla(linha);
            else {
                BlocoDado blocoAux = new BlocoDado(container.getContainerId(), this.blocoIdCount++);
                blocoAux.adicionarTupla(linha);
                blocos.add(blocoAux);

                container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();
            }

        }

        return blocos;
    }
    private boolean podeAdicionarMaisTuple(BlocoDado bloco, Linha linha, BlocoContainer container) {
        return bloco.getHeader().getTamanhoUsado() + linha.getTamanho() <= container.getBlocoControle().getHeader().getTamanhoDosBlocos();
    }
}
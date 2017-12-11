package entidades;

import entidades.blocos.*;
import entidades.index.IndexFileManager;
import exceptions.ContainerNoExistent;
import factories.ContainerId;
import interfaces.IFileManager;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GerenciadorArquivo implements IFileManager {
    private static final String PATH = GlobalVariables.LOCAL_ARQUIVO_FINAL + "TABLES/";
    private static final String PREFIX = "db.";
    private static final String EXTENSION = ".bin";

    private int containerIdCount = 0;
    private int blocoIdCount = 1;

    public GerenciadorArquivo() {
        try {
            File file = new File(PATH);
            File[] directores = file.listFiles();

            if (directores != null && directores.length > 0) {
                byte[] idBytes = GerenciadorDeIO.getBytes(directores[directores.length - 1].getAbsolutePath(), 0, 1);
                this.containerIdCount = ByteArrayUtils.byteArrayToInt(idBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();
        return new BlocoDado(containerId, this.blocoIdCount++);
    }

    @Override
    public BlocoContainer selectAllFrom(int containerId) throws IOException {
        BlocoContainer container = this.criarBlocoContainer();
        String diretorio = getDiretorio(ContainerId.create(containerId));

        byte[] bytes = GerenciadorDeIO.getBytes(diretorio);
        container.fromByteArray(bytes);
        return container;
    }

    @Override
    public BlocoContainer criarBlocoContainer() {
        return new BlocoContainer(++this.containerIdCount);
    }

    private BlocoContainer criarBlocoContainer(byte[] bytes) {
        BlocoContainer container = this.criarBlocoContainer();
        container.fromByteArray(bytes);

        return container;
    }

    @Override
    public HashMap<ContainerId, String> getContainers() throws IOException {
        File file = new File(PATH);
        File[] directores = file.listFiles();
        HashMap<ContainerId, String> containers = new HashMap<ContainerId, String>();

        if (directores != null && directores.length > 0) {
            for (File containerFile : directores) {
                byte[] idBytes = GerenciadorDeIO.getBytes(containerFile.getAbsolutePath(), 0, 1);
                int idInt = ByteArrayUtils.byteArrayToInt(idBytes);

                ContainerId id = ContainerId.create(idInt);
                containers.put(id, containerFile.getName());
            }
        }

        return containers;
    }

    private ArrayList<Descritor> processarDescritores(BlocoContainer container, String descritor) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        String[] colunas = descritor.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        for (String coluna : colunas) {
            Descritor _descritor = new Descritor(coluna);
            descritores.add(_descritor);
            container.getBlocoControle().getHeader().atualizarTamanhoDescritor(_descritor.toByteArray().length);
        }

        return descritores;
    }


    @Override
    public BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistent {
        BlocoContainer container = this.criarBlocoContainer();
        container.getBlocoControle().adicionarDescritores(this.processarDescritores(container, containerString));

        GerenciadorDeIO.gravarBytes(getDiretorio(ContainerId.create(container.getContainerId())), container.toByteArray());
        return container;
    }

    @Override
    public BlocoDado lerBloco(RowId rowId) throws IOException {
        String diretorio = getDiretorio(ContainerId.create(rowId.getContainerId()));
        byte[] containerBytes = GerenciadorDeIO.getBytes(diretorio);
        BlocoContainer container = this.criarBlocoContainer(containerBytes);

        int indexOndeComecaOBlocoDeDados = container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int tamanhoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        byte[] blocoBytes = GerenciadorDeIO.getBytes(diretorio, indexOndeComecaOBlocoDeDados + (tamanhoBloco * (rowId.getBlocoId() - 1)), tamanhoBloco);
        if (blocoBytes == null)
            return null;

        return new BlocoDado(blocoBytes);
    }

    @Override
    public void gravarBloco(BlocoContainer container, BlocoDado bloco) throws IOException {
        int offset = container.getBlocoControle().getHeader().getTamanhoDosBlocos() + ((bloco.getHeader().getBlocoId() - 1) * container.getBlocoControle().getHeader().getTamanhoDosBlocos());
        int length = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        container.atualizarProximoBlocoLivre();
        bloco.atualizar(offset, length);
    }

    @Override
    public BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistent {
        String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        Linha tuple = new Linha();
        tuple.adicionarColunas(colunas);

        int proximoBlocoLivre = container.getBlocoControle().getHeader().getProximoBlocoLivre();
        int blocoId = (proximoBlocoLivre / container.getBlocoControle().getHeader().getTamanhoDosBlocos()) + 1;
        BlocoDado bloco = this.lerBloco(new RowId(container.getBlocoControle().getHeader().getContainerId(), blocoId));

        if (bloco == null) {
            bloco = this.criarBlocoDeDado(container.getContainerId());
            container.adicionarBloco(bloco);
            container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();
        }

        if (podeAdicionarMaisTuple(bloco, tuple, container))
            bloco.adicionarTupla(tuple);
        else {
            BlocoDado blocoAux = new BlocoDado(container.getContainerId(), this.blocoIdCount++);
            blocoAux.adicionarTupla(tuple);

            container.adicionarBloco(blocoAux);
            container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();

            return blocoAux;
        }

        return bloco;
    }

    private boolean podeAdicionarMaisTuple(BlocoDado bloco, Linha linha, BlocoContainer container) {
        int tamanhoHeader = 8;
        int tamanhoMaximoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int tamanhoUsadoDoBloco = bloco.getHeader().getTamanhoUsado();
        int tamanhoTupla = linha.getTamanhoCompleto();

        return tamanhoUsadoDoBloco + tamanhoTupla + tamanhoHeader <= tamanhoMaximoBloco;
    }

    @Override
    public List<String> getColumns(ContainerId containerId) throws IOException {
        BlocoControle controle = new BlocoControle(containerId.getValue());
        String diretorio = getDiretorio(containerId);


        try {
            controle.fromByteArray(GerenciadorDeIO.getBytes(diretorio, 0, controle.getHeader().getTamanhoDosBlocos()));
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerId.getValue());
            return null;
        }
        return controle.getColumnsName();
    }

    public static List<String> getColumnsStatic(ContainerId containerId) throws IOException {
        BlocoControle controle = new BlocoControle(containerId.getValue());
        String diretorio = getDiretorio(containerId);

        try {
            controle.fromByteArray(GerenciadorDeIO.getBytes(diretorio, 0, controle.getHeader().getTamanhoDosBlocos()));
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerId.getValue());
            return null;
        }
        return controle.getColumnsName();
    }

    public static String getDiretorio(ContainerId containerId) throws IOException {
        String _path = PATH + "/" + PREFIX + containerId.getValue() + EXTENSION;
        GerenciadorDeIO.makeDirs(_path);
        return _path;
    }
}
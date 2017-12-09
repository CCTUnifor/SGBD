package entidades;

import entidades.blocos.*;
import entidades.index.IndexFileManager;
import exceptions.ContainerNoExistentException;
import exceptions.IndexNoExistentException;
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
    private static final String EXTENSION = ".table";

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
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistentException {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistentException("");
        return new BlocoDado(containerId, this.blocoIdCount++);
    }

    @Override
    public BlocoContainer selectAllFrom(int containerId) throws ContainerNoExistentException {
        BlocoContainer container = this.criarBlocoContainer();

        byte[] bytes;
        try {
            bytes = GerenciadorDeIO.getBytes(getDiretorio(containerId));
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
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
    public BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistentException {
        BlocoContainer container = this.criarBlocoContainer();
        container.getBlocoControle().adicionarDescritores(this.processarDescritores(container, containerString));

        GerenciadorDeIO.gravarBytes(getDiretorio(container.getContainerId()), container.toByteArray());
        return container;
    }

    @Override
    public BlocoContainerHeader getContainerHeader(ContainerId containerId) throws ContainerNoExistentException {
        byte[] containerBytes;
        try {
            containerBytes = GerenciadorDeIO.getBytes(getDiretorio(containerId.getValue()), 0, 11);
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
        
        return new BlocoContainerHeader(containerBytes);
    }

    @Override
    public BlocoDado lerBloco(RowId rowId) throws IOException {
        String diretorio = getDiretorio(rowId.getContainerId());
        byte[] containerBytes = GerenciadorDeIO.getBytes(diretorio, 0, 11);
        BlocoContainer container = this.criarBlocoContainer(containerBytes);

        int indexOndeComecaOBlocoDeDados = 11 + container.getBlocoControle().getHeader().getTamanhoDescritor();
        int tamanhoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        byte[] blocoBytes = GerenciadorDeIO.getBytes(diretorio, indexOndeComecaOBlocoDeDados + (tamanhoBloco * (rowId.getBlocoId() - 1)), tamanhoBloco);
        if (blocoBytes == null)
            return null;

        return new BlocoDado(blocoBytes);
    }

    @Override
    public void gravarBloco(BlocoContainer container, BlocoDado bloco) throws ContainerNoExistentException {
        int offset = 11 + container.getBlocoControle().getHeader().getTamanhoDescritor() + ((bloco.getHeader().getBlocoId() - 1) * container.getBlocoControle().getHeader().getTamanhoDosBlocos());
        int length = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        try {
            container.atualizarProximoBlocoLivre();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bloco.atualizar(offset, length);
    }

    @Override
    public BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistentException {
        String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        Tuple tuple = new Tuple();
        tuple.adicionarColunas(colunas);

        int proximoBlocoLivre = container.getBlocoControle().getHeader().getProximoBlocoLivre();
        int blocoId = (proximoBlocoLivre / container.getBlocoControle().getHeader().getTamanhoDosBlocos()) + 1;
        BlocoDado bloco = this.lerBloco(new RowId(container.getBlocoControle().getHeader().getContainerId(), blocoId));

        if (bloco == null) {
            bloco = this.criarBlocoDeDado(container.getContainerId());
            container.adicionarBloco(bloco);
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

    private boolean podeAdicionarMaisTuple(BlocoDado bloco, Tuple linha, BlocoContainer container) {
        int tamanhoHeader = 8;
        int tamanhoMaximoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int tamanhoUsadoDoBloco = bloco.getHeader().getTamanhoUsado();
        int tamanhoTupla = linha.getTamanhoCompleto();

        return tamanhoUsadoDoBloco + tamanhoTupla + tamanhoHeader <= tamanhoMaximoBloco;
    }

    @Override
    public List<String> getColumns(ContainerId containerId) throws ContainerNoExistentException {
        BlocoControle controle = new BlocoControle(containerId.getValue());
        String diretorio = getDiretorio(containerId.getValue());

        try {
            controle.fromByteArray(GerenciadorDeIO.getBytes(diretorio, 0, 11));
            controle.fromByteArray(GerenciadorDeIO.getBytes(diretorio, 0, 11 + controle.getHeader().getTamanhoDescritor()));
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerId.getValue());
            return null;
        }

        return controle.getColumnsName();
    }

    @Override
    public void adicionarIndiceAoContainerId(ContainerId containerId, String indexName) throws IndexNoExistentException, ContainerNoExistentException {
        String indexPath = IndexFileManager.getDiretorio(containerId, indexName);
        String tablePath = getDiretorio(containerId.getValue());

        BlocoContainer container;
        try {
            container = new BlocoContainer(GerenciadorDeIO.getBytes(tablePath, 0, 11));
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
        String descString = indexPath + "[P(101)];";
        Descritor desc = new Descritor(descString);
        int tamanhoNovoDescritor = container.getBlocoControle().getHeader().getTamanhoDescritor() + desc.toByteArray().length;

        container.getBlocoControle().adicionarDescritor(desc);
        try {
            GerenciadorDeIO.atualizarBytes(tablePath, 9, ByteArrayUtils.intTo2Bytes(tamanhoNovoDescritor));
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
    }

    public static String getDiretorio(int containerId) throws ContainerNoExistentException {
        String _path = PATH + "/" + PREFIX + containerId + EXTENSION;
        try {
            GerenciadorDeIO.makeDirs(_path);
        } catch (IOException e) {
            throw new ContainerNoExistentException("");
        }
        return _path;
    }
}
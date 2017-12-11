package entidades.index;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoControle;
import entidades.blocos.Descritor;
import entidades.blocos.RowId;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.CollumnValue;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.IndexBlockNotFoundException;
import exceptions.innerBlock.IndexLeafBlockCannotPushPointerChildException;
import exceptions.innerBlock.InnerIndexBlockFullCollumnValueException;
import exceptions.innerBlock.InnerIndexBlockPointerToChildIsFullException;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IIndexFileManager;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexFileManager implements IIndexFileManager {
    private static final String PATH = GlobalVariables.LOCAL_ARQUIVO_FINAL + "INDICES/";
    private static final String PREFIX = "db.";
    private static final String EXTENSION = ".index";


    @Override
    public IndexContainer createIndex(ContainerId containerId, String indexName, ArrayList<Integer> columns) throws IOException, ContainerNoExistent {
        IndexContainer controller = new IndexContainer(containerId.getValue());
        String indexPath = getDiretorio(containerId, indexName);

        GerenciadorDeIO.gravarBytes(indexPath, controller.toByteArray());
        this.putIndexToContainer(containerId, indexName);

        for (int i : columns) {
            Descritor col = new Descritor(i + "[C(4)]|");
            controller.getBlocoControle().adicionarDescritor(col, indexPath);
        }

        return controller;
    }

    private void putIndexToContainer(ContainerId containerId, String indexName) throws IOException, ContainerNoExistent {
        String indexPath = getDiretorio(containerId, indexName);
        String tablePath = GerenciadorArquivo.getDiretorio(containerId);

        BlocoContainer container = new BlocoContainer(GerenciadorDeIO.getBytes(tablePath, 0, BlocoControle.getBlockLengthFile(tablePath)));
        String descString = indexPath + "[P(101)]|";
        Descritor desc = new Descritor(descString);

        container.getBlocoControle().adicionarDescritor(desc, tablePath);
    }

    public static String getDiretorio(ContainerId containerId, String indexName) throws IOException {
        String _path = PATH + "/" + containerId.getValue() + "/" + PREFIX + indexName + EXTENSION;
        GerenciadorDeIO.makeDirs(_path);
        return _path;
    }

    public static String getDiretorio(int containerId) throws IOException, ContainerNoExistent {
        String diretorio = PATH + "/" + containerId + "/";

        File file = new File(diretorio);
        File[] indices = file.listFiles();
        for (File index : indices) {
            if (index.isDirectory())
                continue;

            int blockLength = BlocoControle.getBlockLengthFile(index.getAbsolutePath());

            byte[] containerBytes = GerenciadorDeIO.getBytes(index.getAbsolutePath(), 0, blockLength);
            IndexContainer ic = new IndexContainer(containerBytes);

            if (ic.getBlocoControle().getContainerId() == containerId)
                return index.getAbsolutePath();
        }

        throw new ContainerNoExistent();
    }

    @Override
    public boolean existIndice(ContainerId containerId, String indexName) {
        File file = new File(PATH + "/" + containerId.getValue());
        File[] directores = file.listFiles();

        if (directores != null && directores.length > 0) {
            for (File index : directores) {
                if (index.getName().equals(PREFIX + indexName + EXTENSION))
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException {
        BlocoControle controle = new BlocoControle(containerIdSelecionado.getValue());
        String tablePath = GerenciadorArquivo.getDiretorio(containerIdSelecionado);

        try {
            controle.fromByteArray(GerenciadorDeIO.getBytes(tablePath, 0, controle.getHeader().getTamanhoDosBlocos()));
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerIdSelecionado.getValue());
            return null;
        }
        return controle.getIndicesName();
    }

    @Override
    public void createBlock(ContainerId indexContainerId, IndexBlock block) throws IOException, ContainerNoExistent {
        IndexContainer ic = IndexContainer.getJustContainer(indexContainerId);
        block.getHeader().setContainerId(indexContainerId.getValue());

        String indexPath = getDiretorio(indexContainerId.getValue());
        int offset =  ic.getBlocoControle().getHeader().getTamanhoDosBlocos() + ic.getBlocoControle().getHeader().getProximoBlocoLivre();
        block.getHeader().setBlockId(ic.incrementNextFreeBlock());

        GerenciadorDeIO.atualizarBytes(indexPath, offset, block.toByteArray());
    }

    public void createRoot(ContainerId indexContainerId, InnerIndexBlock root, CollumnValue col, RowId rowId) throws IOException, ContainerNoExistent, IndexBlockNotFoundException, InnerIndexBlockPointerToChildIsFullException, IndexLeafBlockCannotPushPointerChildException, IncorrectTypeToPushPointerException, InnerIndexBlockFullCollumnValueException {
        Descritor rootDescritor = new Descritor("1[R(4)]|");

        String path = IndexFileManager.getDiretorio(indexContainerId.getValue());
        IndexContainer.getJustContainer(indexContainerId).getBlocoControle().adicionarDescritor(rootDescritor, path);

        LeafIndexBlock leaf = new LeafIndexBlock(rowId);

        this.createBlock(indexContainerId, root);
        this.createBlock(indexContainerId, leaf);

        root.pushPointerToChild(BlocoId.create(2));
        root.pushColumnValue(col.getCollumnValue());
    }

    public void split(IndexBlock node, IndexBlock nodeLeft, IndexBlock nodeRigth, CollumnValue col, RowId rowId) {
        // TODO
    }
}

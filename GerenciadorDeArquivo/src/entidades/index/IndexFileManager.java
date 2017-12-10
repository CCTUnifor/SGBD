package entidades.index;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoControle;
import entidades.blocos.Descritor;
import entidades.index.inner.InnerIndexBlock;
import exceptions.ContainerNoExistent;
import factories.ContainerId;
import interfaces.IIndexFileManager;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class IndexFileManager implements IIndexFileManager {
    private static final String PATH = GlobalVariables.LOCAL_ARQUIVO_FINAL + "INDICES/";
    private static final String PREFIX = "db.";
    private static final String EXTENSION = ".index";


    @Override
    public IndexContainer createIndex(ContainerId containerId, String indexName) throws IOException, ContainerNoExistent {
        IndexContainer controller = new IndexContainer(containerId.getValue());
        GerenciadorDeIO.gravarBytes(getDiretorio(containerId, indexName), controller.toByteArray());
        this.putIndexToContainer(containerId, indexName);

        return controller;
    }

    private void putIndexToContainer(ContainerId containerId, String indexName) throws IOException, ContainerNoExistent {
        String indexPath = getDiretorio(containerId, indexName);
        String tablePath = GerenciadorArquivo.getDiretorio(containerId);

        BlocoContainer container = new BlocoContainer(GerenciadorDeIO.getBytes(tablePath, 0, 11));
        String descString = indexPath + "[P(101)];";
        Descritor desc = new Descritor(descString);
        int tamanhoNovoDescritor = container.getBlocoControle().getHeader().getTamanhoDescritor() + desc.toByteArray().length;

        container.getBlocoControle().adicionarDescritor(desc);
        GerenciadorDeIO.atualizarBytes(tablePath, 9, ByteArrayUtils.intTo2Bytes(tamanhoNovoDescritor));
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
            byte[] containerBytes = GerenciadorDeIO.getBytes(index.getAbsolutePath(), 0, 11);
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
            controle.fromByteArray(GerenciadorDeIO.getBytes(tablePath, 0, 11));
            controle.fromByteArray(GerenciadorDeIO.getBytes(tablePath, 0, 11 + controle.getHeader().getTamanhoDescritor()));
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerIdSelecionado.getValue());
            return null;
        }
        return controle.getIndicesName();
    }

    @Override
    public void createBlock(ContainerId indexContainerId, InnerIndexBlock block) throws IOException, ContainerNoExistent {
        IndexContainer ic = IndexContainer.getJustContainer(indexContainerId);
        block.getHeader().setContainerId(indexContainerId.getValue());

        String indexPath = getDiretorio(indexContainerId.getValue());
        int offset = BlocoControle.CONTROLLER_BLOCK_LENGTH + ic.getBlocoControle().getHeader().getProximoBlocoLivre();
        block.getHeader().setBlockId(ic.incrementNextFreeBlock());

        GerenciadorDeIO.atualizarBytes(indexPath, offset, block.toByteArray());
    }
}

package entidades.index;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoControle;
import exceptions.ContainerNoExistentException;
import exceptions.IndexNoExistentException;
import factories.ContainerId;
import interfaces.IIndexFileManager;
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
    public void createIndex(ContainerId containerId, String indexName) throws IndexNoExistentException {
        BlocoContainer controller = new BlocoContainer(containerId.getValue());
        try {
            GerenciadorDeIO.gravarBytes(getDiretorio(containerId, indexName), controller.toByteArray());
        } catch (FileNotFoundException ignored) {
        }

    }

    public static String getDiretorio(ContainerId containerId, String indexName) throws IndexNoExistentException {
        String _path = PATH + "/" + containerId.getValue() + "/" + PREFIX + indexName + EXTENSION;
        try {
            GerenciadorDeIO.makeDirs(_path);
        } catch (IOException e) {
            throw new IndexNoExistentException(indexName);
        }
        return _path;
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
    public List<String> getIndicesPath(ContainerId containerIdSelecionado) throws ContainerNoExistentException {
        BlocoControle controle = new BlocoControle(containerIdSelecionado.getValue());
        String tablePath = GerenciadorArquivo.getDiretorio(containerIdSelecionado.getValue());

        try {
            controle.fromByteArray(GerenciadorDeIO.getBytes(tablePath, 0, 11));
            controle.fromByteArray(GerenciadorDeIO.getBytes(tablePath, 0, 11 + controle.getHeader().getTamanhoDescritor()));
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
        return controle.getIndexName();
    }


}

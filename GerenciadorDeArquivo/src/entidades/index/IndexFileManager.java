package entidades.index;

import entidades.GerenciadorDeIO;
import entidades.blocos.BlocoContainer;
import entidades.blocos.Descritor;
import factories.ContainerId;
import interfaces.IIndexFileManager;
import utils.GlobalVariables;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IndexFileManager implements IIndexFileManager {

    private static final String PATH = GlobalVariables.LOCAL_ARQUIVO_FINAL + "INDICES/";
    private static final String PREFIX = "db.";
    private static final String EXTENSION = ".index";

    @Override
    public BlocoContainer createIndex(ContainerId containerId, String indexName) throws IOException {
        BlocoContainer controller = new BlocoContainer(containerId.getValue());
        GerenciadorDeIO.gravarBytes(getDiretorio(containerId, indexName), controller.toByteArray());
        return controller;
    }

    private void adicionarIndexNameToIndexContainer(BlocoContainer controller, String indexName) {
        ArrayList<Descritor> descritors = new ArrayList<>();
        descritors.add(new Descritor(indexName));

        controller.getBlocoControle().adicionarDescritores(descritors);
    }

    public static String getDiretorio(ContainerId containerId, String indexName) throws IOException {
        String _path = PATH + "/" + containerId.getValue() + "/" + PREFIX + indexName + EXTENSION;
        GerenciadorDeIO.makeDirs(_path);
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
}

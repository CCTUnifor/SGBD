package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.RowId;
import exceptions.ContainerNoExistentException;
import factories.ContainerId;
import interfaces.IFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GerenciadorArquivoService {
    private IFileManager ga;

    public GerenciadorArquivoService(IFileManager _ga) {
        ga = _ga;
    }

    public ArrayList<RowId> gerarContainerByInput(String diretorio) throws ContainerNoExistentException {
        ArrayList<String> linhas;
        try {
            linhas = GerenciadorDeIO.getStrings(diretorio);

            BlocoContainer container = ga.criarArquivo(linhas.get(0));
            linhas.remove(0);

            ArrayList<RowId> rowIds = new ArrayList<RowId>();
            BlocoDado blocoAnterior = new BlocoDado(container.getContainerId(), 1);

            for (String linha : linhas) {
                BlocoDado bloco = ga.adicionarLinha(container, linha);
                if (!blocoAnterior.getRowId().equals(bloco.getRowId())) {
                    rowIds.add(blocoAnterior.getRowId());
                }

                ga.gravarBloco(container, bloco);
                blocoAnterior = bloco;
            }
            return rowIds;
        } catch (IOException e) {
            throw new ContainerNoExistentException("");

        }
    }

    public HashMap<ContainerId, String> getTables() {
        try {
            return ga.getContainers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.RowId;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GerenciadorArquivoService {
    private IFileManager ga;
    public GerenciadorArquivoService(IFileManager _ga) {
        ga = _ga;
    }

    public ArrayList<RowId> gerarContainerByInput(String diretorio) throws IOException, ContainerNoExistent {
        ArrayList<String> linhas = GerenciadorDeIO.getStrings(diretorio);

        BlocoContainer container = ga.criarArquivo(linhas.get(0));
        linhas.remove(0);

        ArrayList<RowId> rowIds = new ArrayList<RowId>();
        BlocoDado blocoAnterior = new BlocoDado(1, 1);

        for (String linha : linhas) {
            BlocoDado bloco = ga.adicionarLinha(container, linha);
            if (!blocoAnterior.getRowId().equals(bloco.getRowId())){
                rowIds.add(blocoAnterior.getRowId());
            }

            ga.gravarBloco(container, bloco);
            blocoAnterior = bloco;
        }

        return rowIds;
    }
}

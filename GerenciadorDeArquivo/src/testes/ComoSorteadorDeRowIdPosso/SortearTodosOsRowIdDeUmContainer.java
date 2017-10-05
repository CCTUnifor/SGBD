package testes.ComoSorteadorDeRowIdPosso;

import entidades.GerenciadorArquivo;
import entidades.RowId;
import entidades.SorteadorDeRowId;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class SortearTodosOsRowIdDeUmContainer {
    @Test
    public void Sortear() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoBinario(GlobalVariables.LOCAL_ARQUIVO_FINAL + "Tabela1.bin");

        List<RowId> sorter = SorteadorDeRowId.Sortear(container);
        SorteadorDeRowId.gravarSorteados(sorter);
    }
}

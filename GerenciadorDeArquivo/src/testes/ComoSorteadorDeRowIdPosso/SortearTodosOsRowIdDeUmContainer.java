package testes.ComoSorteadorDeRowIdPosso;

import entidades.GerenciadorArquivo;
import entidades.blocos.RowId;
import entidades.SorteadorDeRowId;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SortearTodosOsRowIdDeUmContainer {
    @Test
    public void Sortear() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerContainer(1);

        List<RowId> sorter = SorteadorDeRowId.Sortear(container);
        SorteadorDeRowId.gravarSorteados(sorter);
    }

    @Test
    public void Sortear2() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerContainer(1);
        BlocoContainer container2 = gerenciadorArquivo.lerContainer(2);

        List<RowId> x = new ArrayList<RowId>();
        //container.getBlocosDados().stream().map(x -> new RowId(x.getHeader().getContainerId(), x.getHeader().getBlocoId())); 
/*
        for (:
             ) {
            
        }
        */
        List<RowId> sorter = SorteadorDeRowId.Sortear(container);
        sorter.addAll(SorteadorDeRowId.Sortear(container2));

        SorteadorDeRowId.gravarSorteados(sorter);
    }
}

package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.RowId;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static utils.GlobalVariables.LOCAL_ARQUIVO_FINAL;

public class SorteadorDeRowId {
    public static ArrayList<RowId> Sortear(BlocoContainer container){
        ArrayList<RowId> rows = (ArrayList<RowId>) container.getBlocosDados().stream().map(b -> new RowId(b.getHeader().getContainerId(), b.getHeader().getBlocoId())).collect(Collectors.toList());
        ArrayList<RowId> sorted = SorteadorDeRowId.shuffleWithRepetition(rows);

        return sorted;
    }

    public static ArrayList<RowId> Sortear(ArrayList<RowId> rowIds){
        ArrayList<RowId> sorted = SorteadorDeRowId.shuffleWithRepetition(rowIds);

        return sorted;
    }

    private static ArrayList<RowId> shuffleWithRepetition(ArrayList<RowId> rows) {
        ArrayList<RowId> sorted = new ArrayList<RowId>();

        int length = rows.size();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int min = i  - 2;
            int max = i + 2;
            if (min < 0)
                min = 0;
            if (max > length - 1)
                max = length - 1;

            int random2 = rand.nextInt((max - min) + 1) + min;

            sorted.add(rows.get(random2));
        }
        return sorted;
    }

    public static void gravarSorteados(List<RowId> sorteados) throws IOException {
        String diretorioCompleto = LOCAL_ARQUIVO_FINAL + "Sorteados.txt";
        ArrayList<String> x = (ArrayList<String>) sorteados.stream().map(rowId -> rowId.print().get(0)).collect(Collectors.toList());

        GerenciadorDeIO.gravarString(diretorioCompleto, x);
    }
}

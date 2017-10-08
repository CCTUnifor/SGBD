package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.RowId;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

import static utils.GlobalVariables.LOCAL_ARQUIVO_FINAL;

public class SorteadorDeRowId {
    public static List<RowId> Sortear(BlocoContainer container){
        List<RowId> rows = container.getBlocosDados().stream().map(b -> new RowId(b.getHeader().getContainerId(), b.getHeader().getBlocoId())).collect(Collectors.toList());
        List<RowId> sorted = SorteadorDeRowId.shuffleWithRepetition(rows);

        return sorted;
    }

    private static List<RowId> shuffleWithRepetition(List<RowId> rows) {
        List<RowId> sorted = new ArrayList<RowId>();

        int length = rows.size();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sorted.add(rows.get(random.nextInt(length)));
        }
        return sorted;
    }

    public static void gravarSorteados(List<RowId> sorteados) throws IOException {
        String diretorioCompleto = LOCAL_ARQUIVO_FINAL + "Sorteados.txt";

        File file = new File(diretorioCompleto);
        if (file.exists())
            file.delete();
        file.createNewFile();

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            for (RowId row : sorteados) {
                randomAccessFile.writeUTF(row.print().get(0));
            }
            randomAccessFile.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

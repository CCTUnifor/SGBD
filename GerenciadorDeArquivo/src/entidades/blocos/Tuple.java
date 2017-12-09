package entidades.blocos;

import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.util.ArrayList;

public class Tuple implements IBinary, IPrint {
    private int tamanho;
    private ArrayList<Coluna> colunas;

    public Tuple() {
        this.colunas = new ArrayList<Coluna>();
    }

    public Tuple(byte[] linhaBytes) {
        this.colunas = new ArrayList<Coluna>();
        this.fromByteArray(linhaBytes);
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public int getTamanhoCompleto() {
        int soma = 4;

        for (Coluna col : colunas) {
            soma += col.getTamanho() + 2;
        }

        return soma;
    }

    public ArrayList<Coluna> getColunas() {
        return colunas;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(ByteArrayUtils.intToBytes(getTamanhoCompleto()))
          .concat(this.bytesColunas());
        return bc.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();
        for (Coluna col : colunas) {
            parse.addAll(col.print());
        }
        return parse;
    }

    private byte[] bytesColunas() {
        ByteArrayConcater bc = new ByteArrayConcater();
        this.colunas.stream().forEach(x -> bc.concat(x.toByteArray()));
        return bc.getFinalByteArray();
    }

    @Override
    public Tuple fromByteArray(byte[] byteArray) {
        this.tamanho = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 0, 4));

        // TODO
        //
        int indexInicio = 4;
        while (indexInicio < byteArray.length){
            int tamanhoColuna = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, indexInicio, 2));
            byte[] bytesColuna = ByteArrayUtils.subArray(byteArray, indexInicio + 2, tamanhoColuna);

            this.colunas.add(new Coluna(bytesColuna));
            indexInicio += tamanhoColuna + 2;
        }

        return this;
    }

    public void adicionarColuna(String coluna) {
        Coluna column = new Coluna(ByteArrayUtils.stringToByteArray(coluna));
        this.colunas.add(column);
        this.tamanho += column.getTamanho();
    }

    public void adicionarColunas(String[] colunas) {
        for (int i = 0; i < colunas.length; i++) {
            this.adicionarColuna(colunas[i]);
        }
    }

    @Override
    public String toString() {
        String inf = "Tamanho: " + this.getTamanho() + " | Informacao: ";
        for (Coluna coluna: colunas) {
            inf += coluna.getInformacao() + " | ";
        }
        return inf;
    }
}

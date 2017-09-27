package entidades.blocos;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Linha implements IBinary{
    private int tamanho;
    private ArrayList<Coluna> colunas;

    public Linha() {
        this.colunas = new ArrayList<Coluna>();
    }

    public Linha(byte[] linhaBytes) {
        this.colunas = new ArrayList<Coluna>();
        this.fromByteArray(linhaBytes);
    }

    public int getTamanho() {
        return this.tamanho + 4;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(ByteArrayUtils.intToBytes(getTamanho()))
          .concat(this.bytesColunas());
        return bc.getFinalByteArray();
    }

    private byte[] bytesColunas() {
        ByteArrayConcater bc = new ByteArrayConcater();

        for (Coluna collumn : this.colunas) {
            bc.concat(collumn.toByteArray());
        }
        return bc.getFinalByteArray();
    }

    @Override
    public Linha fromByteArray(byte[] byteArray) {
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
        String inf = "";
        for (Coluna coluna: colunas) {
            inf += coluna.getInformacao() + " | ";
        }
        return inf;
    }
}

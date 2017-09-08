package entidades.blocos;

import interfaces.IBinary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Linha implements IBinary{
    private int tamanho;
    private ArrayList<Coluna> colunas;

    public Linha() {
        this.colunas = new ArrayList<Coluna>();
    }

    public int getTamanho() {
        return this.tamanho;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }

    public void adicionarColuna(String coluna) {
        Coluna column = new Coluna(coluna.getBytes(StandardCharsets.UTF_8));
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

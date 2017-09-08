package entidades.blocos;

import interfaces.IBinary;

public class Coluna implements IBinary {
    private int tamanho;
    private byte[] dados;

    public Coluna(byte[] bytes) {
        this.dados = bytes;
        this.tamanho = bytes.length;
    }

    public int getTamanho() {
        return  this.tamanho;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }
}

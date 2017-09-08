package entidades.blocos;

import interfaces.IBinary;

public class Descritor implements IBinary {
    private TipoDado tipoDado;
    private String nome;
    private int tamanho;

    public Descritor(String coluna) {

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

package entidades.index.leaf;

import interfaces.IBinary;

public class LeafIndexBlock implements IBinary {
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }
}

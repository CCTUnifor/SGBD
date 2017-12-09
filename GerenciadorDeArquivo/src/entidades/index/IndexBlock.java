package entidades.index;

import interfaces.IBinary;

public class IndexBlock implements IBinary{
    public static final int INDEX_BLOCK_LENGTH = 100;
    private IndexHeaderBlock header;


    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }
}

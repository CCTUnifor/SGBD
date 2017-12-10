package factories;

import interfaces.IBinary;
import utils.ByteArrayUtils;

public class BlocoId implements IBinary {

    public static final int LENGTH = 4;
    private int id;

    public BlocoId() {}

    private BlocoId(int id) {
        this.id = id;
    }

    public static BlocoId create() {
        return new BlocoId();
    }

    public static BlocoId create(int id) {
        return new BlocoId(id);
    }

    public int getValue() {
        return this.id;
    }

    @Override
    public byte[] toByteArray() {
        return ByteArrayUtils.intTo3Bytes(this.id);
    }

    @Override
    public BlocoId fromByteArray(byte[] byteArray) {
        int _blocoId = ByteArrayUtils.byteArrayToInt(byteArray);
        return this.create(_blocoId);
    }

}

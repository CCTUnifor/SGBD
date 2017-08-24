package factories;

import interfaces.IBinary;
import utils.ByteArrayUtils;

public class BlocoId implements IBinary {

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

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public BlocoId fromByteArray(byte[] byteArray) {
        int _blocoId = ByteArrayUtils.byteArrayToInt(byteArray);
        return this.create(_blocoId);
    }

}

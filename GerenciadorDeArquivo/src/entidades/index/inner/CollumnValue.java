package entidades.index.inner;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class CollumnValue implements IBinary {
    public static final int LENGTH = 4;

    private int lengthOfCollumnValue;
    private byte[] collumnNameInBytes;

    private String collumnValue;

    public CollumnValue(String collumnValue) {
        this.collumnValue = collumnValue;
        this.collumnNameInBytes = ByteArrayUtils.stringToByteArray(collumnValue);
        lengthOfCollumnValue = this.collumnNameInBytes.length;
    }

    public CollumnValue(byte[] bytes) {
        this.fromByteArray(bytes);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater(this.getFullLength());
        bc.concat(ByteArrayUtils.intToBytes(this.lengthOfCollumnValue))
                .concat(this.collumnNameInBytes);

        return bc.getFinalByteArray();
    }

    @Override
    public CollumnValue fromByteArray(byte[] byteArray) {
        this.lengthOfCollumnValue = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 0, LENGTH));
        this.collumnValue = ByteArrayUtils.byteArrayToString(ByteArrayUtils.subArray(byteArray, LENGTH, lengthOfCollumnValue));
        return this;
    }

    public int getFullLength() {
        return LENGTH + this.lengthOfCollumnValue;
    }

    public String getCollumnValue() {
        return this.collumnValue;
    }
}
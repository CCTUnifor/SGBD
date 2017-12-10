package entidades.index.inner;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class ValueColumn implements IBinary {
    private int lengthOfCollumnValue;
    private byte[] collumnNameInBytes;

    private String collumnValue;

    public ValueColumn(String collumnValue) {
        this.collumnValue = collumnValue;
        this.collumnNameInBytes = ByteArrayUtils.stringToByteArray(collumnValue);
        lengthOfCollumnValue = this.collumnNameInBytes.length;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater(this.getFullLength());
        bc.concat(ByteArrayUtils.intToBytes(this.lengthOfCollumnValue))
                .concat(this.collumnNameInBytes);

        return bc.getFinalByteArray();
    }

    @Override
    public ValueColumn fromByteArray(byte[] byteArray) {
        this.lengthOfCollumnValue = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 0, 4));
        this.collumnValue = ByteArrayUtils.byteArrayToString(ByteArrayUtils.subArray(byteArray, 5, lengthOfCollumnValue));
        return this;
    }

    public int getFullLength() {
        return 4 + this.lengthOfCollumnValue;
    }
}
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
    public String getCollumnValue(int i) { return this.collumnValue.split(";")[i]; }

    public int compareTo(CollumnValue key) {
        String[] valuesColumns = this.collumnValue.split(";");

        for (int i = 0; i < valuesColumns.length; i++) {
            if (isNumeric(valuesColumns[i])) {
                Double value1 = Double.parseDouble(valuesColumns[i]);
                Double value2 = Double.parseDouble(key.getCollumnValue(i));
                if (value1.compareTo(value2) == 0) {
                    continue;
                } else {
                    if (value1.compareTo(value2) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            } else {
                if (this.getCollumnValue(i).compareToIgnoreCase(key.getCollumnValue(i)) == 0) {
                    continue;
                } else {
                    if (this.getCollumnValue(i).compareToIgnoreCase(key.getCollumnValue(i)) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
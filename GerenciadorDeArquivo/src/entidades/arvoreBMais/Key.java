package entidades.arvoreBMais;

import entidades.blocos.RowId;

public class Key {

    private String value;
    private String[] valuesColumns;
    private RowId rowId;

    public Key(String value) {
        this.value = value;
        this.setValuesColumns();
    }

    public Key(String value, RowId rowId) {
        this.value = value;
        this.setValuesColumns();
        this.rowId = rowId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getValuesColumns() {
        return valuesColumns;
    }

    public int compareToKey(Key key) {
        for (int i = 0; i < this.valuesColumns.length; i++) {
            if (this.valuesColumns[i].compareToIgnoreCase(key.getValueColumn(i)) < 0) {
                return -1;
            } else {
                if (this.valuesColumns[i].compareToIgnoreCase(key.getValueColumn(i)) > 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public String getValueColumn(int index) {
        if (index > -1 && index < this.getValuesColumns().length) {
            return this.valuesColumns[index];
        }
        return null;
    }

    private void setValuesColumns() {
        this.valuesColumns = this.value.split(";");
    }

    public RowId getRowId() {
        return rowId;
    }
}

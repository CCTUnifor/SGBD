package entidades.arvoreBMais;


public class Key {

    private String value;
    private String[] valuesColumns;

    public Key(String value) {
        this.value = value;
        this.setValuesColumns();
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

    public int compareToKey(Key key)
    {
        for (int i = 0; i < this.valuesColumns.length; i++) {
            if(this.valuesColumns[i].compareToIgnoreCase(key.getValueColumn(i)) < 0)
            {
                return -1;
            }
            else
            {
                if(this.valuesColumns[i].compareToIgnoreCase(key.getValueColumn(i)) > 0)
                {
                    return 1;
                }
            }
        }
        return 0;
    }

    public String getValueColumn(int index)
    {
        if(index > -1 && index < this.getValuesColumns().length) {
            return this.valuesColumns[index];
        }
        return null;
    }
    private void setValuesColumns() {
        this.valuesColumns = this.value.split(";");
    }

}

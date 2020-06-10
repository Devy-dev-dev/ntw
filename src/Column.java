import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Column {
    public String name;

    public Column(String inputName){
        name = inputName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

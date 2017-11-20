package fullcompiler;

/**
 * Created by chenchuk on 11/18/17.
 */
public class Var {
    private String name;
    private String type;
    private String kind;
    private Integer index;

    public Var(String name, String type, String kind, Integer index) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.index = index;
    }
    public Var() {
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getKind() {
        return kind;
    }
    public Integer getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return name+ "::" +type+ "," +kind+index;
        //return name+ "::" +type+ "," +kind+ "-" +index+ "\n";
        //return name+ "::" +type+ "," +kind+ "-" +index;
    }
}

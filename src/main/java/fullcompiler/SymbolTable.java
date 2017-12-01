package fullcompiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchuk on 10/14/17.
 */
public class SymbolTable {

    private Integer staticIndex;
    private Integer fieldIndex;
    private Integer argIndex;
    private Integer varIndex;
    private String subName;        // just for debugging

    Map<String, Var> vars;



    public SymbolTable(){
        // init once for the class
        vars = new HashMap<>();
        staticIndex = 0;
        fieldIndex = 0;
        argIndex = 0;
        varIndex = 0;
    }

    public void startSubroutine(){
        // init table for each function
        //subroutineSymbolsTable = new HashMap<>();

    }
//    public void setSubName(String subName){
//        this.subName = subName;
//        // init table for each function
//        //subroutineSymbolsTable = new HashMap<>();
//
//    }

    // define a new var in the ST.
    public void define(String name ,String type, String kind){
        // build Var with the next free index of that kind
        Var var = new Var(name, type, kind, incCounter(kind));
            vars.put(name, var);
    }

    // direct insert a complete Var, indexing by this class
    public void define(Var var){
        define(var.getName(), var.getType(), var.getKind());
    }

    // return number of vars of a given kind
    public int varCount(String kind){
            return (int) vars.values()
                    .stream()
                    .filter(item -> item.getKind().equals(kind))
                    .count();

    }

    // lookup a specific var by its name
    public Var getVar(String name){
        if (vars.containsKey(name)){
            return vars.get(name);
        } else return null;

    }

    // returns the kind by looking at both ST's
    public String KindOf (String name){
        // first search in subroutine ST
        if (vars.containsKey(name)){
            return vars.get(name).getKind();
        }
        return null;
    }

    // returns the kind of a given identifier
    public String TypeOf (String name){
        if (vars.containsKey(name)){
            return vars.get(name).getType();
        }
        return null;
    }

    // returns the index of this identifier (of this kind)
    public Integer IndexOf (String name){
        if (vars.containsKey(name)){
            return vars.get(name).getIndex();
        }
        return null;
    }

    // increment and returns the appropriate counter
    private int incCounter(String kind){
        if (kind.equals("static"))   return staticIndex++;
        if (kind.equals("field"))    return fieldIndex++;
        if (kind.equals("argument")) return argIndex++;
        if (kind.equals("local"))      return varIndex++;
        return 0;
    }

    @Override
    public String toString() {
        return "" +vars;
    }
//
//    public SymbolTable() {
//        // generate predefined symbols
//        symbolsMap = new TreeMap<>();
//        addPredefinedSymbols();
//    }
//    private void addPredefinedSymbols(){
//        symbolsMap.put("SP",0);
//        symbolsMap.put("LCL",1);
//        symbolsMap.put("ARG",2);
//        symbolsMap.put("THIS",3);
//        symbolsMap.put("THAT",4);
//        symbolsMap.put("SCREEN",16384);
//        symbolsMap.put("KBD",24576);
//        for (int i=0; i<=15; i++){
//            symbolsMap.put("R" + i ,i);
//        }
//    }
//
//    public void addEntry(String symbol, Integer address){
//        symbolsMap.put(symbol,address);
//    }
//
//    public Boolean contains(String symbol){
//        return symbolsMap.containsKey(symbol);
//    }
//    public Integer getAddress(String symbol){
//        return (Integer) symbolsMap.get(symbol);
//    }
//
//    @Override
//    public String toString() {
//        return "SymbolTable{" +
//                "symbolsMap=" + symbolsMap +
//                '}';
//    }
//
//    public void show (){
//        System.out.println("*** vars : " + vars.size() + " elements."+);
//        vars.forEach((name, var)-> System.out.println(name +": "+ var));
//    }

}

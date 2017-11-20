package fullcompiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchuk on 10/14/17.
 */
public class DoubleSymbolTable {

//    private Integer staticIndex;
//    private Integer fieldIndex;
//    private Integer argIndex;
//    private Integer varIndex;
//
//    Map<String, Var> classSymbolsTable;
//    Map<String, Var> subroutineSymbolsTable;
//
//    Map<String, Integer> symbolsMap;
//
//
//    public DoubleSymbolTable(){
//        // init once for the class
//        classSymbolsTable = new HashMap<>();
//    }
//
//    public void startSubroutine(){
//        // init table for each function
//        subroutineSymbolsTable = new HashMap<>();
//
//    }
//
//    // define a new var in the ST.
//    // field/static - are class vars
//    // arg/var - are subroutine vars
//    public void define(String name ,String type, Kind kind){
//        // build Var with the next free index of that kind
//        Var var = new Var(name, type, kind, incCounter(kind));
//
//        // class variables : TODO: check temp/pointer/other vars
//        if (kind == Kind.FIELD || kind == Kind.STATIC){
//            classSymbolsTable.put(name, var);
//        }
//        if (kind == Kind.ARG || kind == Kind.VAR){
//            subroutineSymbolsTable.put(name, var);
//        }
//
//
//    }
//
//    // return number of vars of a given kind
//    public int varCounrt(Kind kind){
//        if (kind == Kind.FIELD || kind == Kind.STATIC) {
//            return (int) classSymbolsTable.values()
//                    .stream()
//                    .filter(item -> item.getKind() == kind)
//                    .count();
//        }
//        if (kind == Kind.ARG || kind == Kind.VAR) {
//            return (int) subroutineSymbolsTable.values()
//                    .stream()
//                    .filter(item -> item.getKind() == kind)
//                    .count();
//
//        }
//
//    }
//
//    // returns the kind by looking at both ST's
//    public Kind KindOf (String name){
//        // first search in subroutine ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return subroutineSymbolsTable.get(name).getKind();
//        }
//        // then search in global clas ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return classSymbolsTable.get(name).getKind();
//        }
//        return null;
//    }
//
//    // returns the kind by looking at both ST's
//    public String TypeOf (String name){
//        // first search in subroutine ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return subroutineSymbolsTable.get(name).getType();
//        }
//        // then search in global clas ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return classSymbolsTable.get(name).getType();
//        }
//        return null;
//    }
//
//    // returns the index (of this kind) by looking at both ST's
//    public Integer IndexOf (String name){
//        // first search in subroutine ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return subroutineSymbolsTable.get(name).getIndex();
//        }
//        // then search in global clas ST
//        if (subroutineSymbolsTable.containsKey(name)){
//            return classSymbolsTable.get(name).getIndex();
//        }
//        return null;
//    }
//
//    // increment and returns the appropriate counter
//    private int incCounter(Kind kind){
//        if (kind == Kind.STATIC) return ++staticIndex;
//        if (kind == Kind.FIELD)  return ++fieldIndex;
//        if (kind == Kind.ARG)    return ++argIndex;
//        if (kind == Kind.VAR)    return ++varIndex;
//    }
//
////
////    public SymbolTable() {
////        // generate predefined symbols
////        symbolsMap = new TreeMap<>();
////        addPredefinedSymbols();
////    }
////    private void addPredefinedSymbols(){
////        symbolsMap.put("SP",0);
////        symbolsMap.put("LCL",1);
////        symbolsMap.put("ARG",2);
////        symbolsMap.put("THIS",3);
////        symbolsMap.put("THAT",4);
////        symbolsMap.put("SCREEN",16384);
////        symbolsMap.put("KBD",24576);
////        for (int i=0; i<=15; i++){
////            symbolsMap.put("R" + i ,i);
////        }
////    }
////
////    public void addEntry(String symbol, Integer address){
////        symbolsMap.put(symbol,address);
////    }
////
////    public Boolean contains(String symbol){
////        return symbolsMap.containsKey(symbol);
////    }
////    public Integer getAddress(String symbol){
////        return (Integer) symbolsMap.get(symbol);
////    }
////
////    @Override
////    public String toString() {
////        return "SymbolTable{" +
////                "symbolsMap=" + symbolsMap +
////                '}';
////    }
////
//    public void show (){
//        System.out.println("*** class ST : " + classSymbolsTable.size() + " elements."+);
//        classSymbolsTable.forEach((name, var)-> System.out.println(name +": "+ var));
//
//        System.out.println("*** subr ST : " + subroutineSymbolsTable.size() + " elements."+);
//        subroutineSymbolsTable.forEach((name, var)-> System.out.println(name +": "+ var));
//
//
//    }

}

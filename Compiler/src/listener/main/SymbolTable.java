package listener.main;

import java.util.HashMap;
import java.util.Map;

import listener.main.MiniCParser.Fun_declContext;
import listener.main.MiniCParser.Local_declContext;
import listener.main.MiniCParser.Var_declContext;


public class SymbolTable {
    enum Type {
        INT, INTARRAY, VOID, ERROR
    }

    static public class VarInfo {
        Type type;
        int id;
        int initVal;

        public VarInfo(Type type, int id, int initVal) {
            this.type = type;
            this.id = id;
            this.initVal = initVal;
        }

        public VarInfo(Type type, int id) {
            this.type = type;
            this.id = id;
            this.initVal = 0;
        }
    }

    static public class FInfo {
        public String sigStr;
    }

    private Map<String, VarInfo> _lsymtable = new HashMap<>();    // local v.
    private Map<String, VarInfo> _gsymtable = new HashMap<>();    // global v.
    private Map<String, FInfo> _fsymtable = new HashMap<>();    // function


    private int _globalVarID = 0;
    private int _localVarID = 0;
    private int _labelID = 0;
    private int _tempVarID = 0;

    SymbolTable() {
        initFunDecl();
        initFunTable();
    }

    void initFunDecl() {        // at each func decl
        _localVarID = 0;
        _labelID = 0;
        _tempVarID = 32;
    }

    void putLocalVar(String varname, Type type) {
        //<Fill here>
        _lsymtable.put(varname, new VarInfo(type, MiniCParser.INT));    //have to modify
    }

    void putGlobalVar(String varname, Type type) {
        //<Fill here>
        //_gsymtable.put(varname, new VarInfo(type,Integer.parseInt(getVarId(varname))));
        _gsymtable.put(varname, new VarInfo(type, MiniCParser.INT));
    }

    void putLocalVarWithInitVal(String varname, Type type, int initVar) {
        //<Fill here>
        //_lsymtable.put(varname, new VarInfo(type,Integer.parseInt(getVarId(varname)),initVar));
        _lsymtable.put(varname, new VarInfo(type, MiniCParser.INT, initVar));
    }

    void putGlobalVarWithInitVal(String varname, Type type, int initVar) {
        //<Fill here>
        //_gsymtable.put(varname, new VarInfo(type,Integer.parseInt(getVarId(varname)),initVar));
        _gsymtable.put(varname, new VarInfo(type, MiniCParser.INT, initVar));
    }

    void putParams(MiniCParser.ParamsContext params) {
        for (int i = 0; i < params.param().size(); i++) {
            //System.out.println("params(" + i + ") : " + params.param(i));

            putLocalVar(params.param(i).getChild(1).getText(), Type.INT);
        }
    }

    private void initFunTable() {
        FInfo printlninfo = new FInfo();
        printlninfo.sigStr = "java/io/PrintStream/println(I)V";

        FInfo maininfo = new FInfo();
        maininfo.sigStr = "main([Ljava/lang/String;)V";
        _fsymtable.put("_print", printlninfo);
        _fsymtable.put("main", maininfo);
    }

    public String getFunSpecStr(String fname) {
        // <Fill here>
        //BytecodeGenListenerHelper.
        return _fsymtable.get(fname).sigStr;
        //return null;	//have to modify
    }

    public String getFunSpecStr(Fun_declContext ctx) {
        // <Fill here>

        return getFunSpecStr(ctx.getText());
        //return null;	//have to modify
    }

    public String putFunSpecStr(Fun_declContext ctx) {
        String fname = BytecodeGenListenerHelper.getFunName(ctx);
        String argtype = "";
        String rtype = "";
        String res = "";

        // <Fill here>


        res = fname + "(" + argtype + ")" + rtype;

        FInfo finfo = new FInfo();
        finfo.sigStr = res;
        _fsymtable.put(fname, finfo);

        return res;
    }

    String getVarId(String name) {
        // <Fill here>
        VarInfo lvarId = (VarInfo) _lsymtable.get(name);
        if (lvarId != null) return Integer.toString(lvarId.id);
        VarInfo gvarID = (VarInfo) _gsymtable.get(name);
        if (gvarID != null) {
            return Integer.toString(gvarID.id);
        }

        return null; //have to modify
    }

    Type getVarType(String name) {
        VarInfo lvar = (VarInfo) _lsymtable.get(name);
        if (lvar != null) {
            return lvar.type;
        }

        VarInfo gvar = (VarInfo) _gsymtable.get(name);
        if (gvar != null) {
            return gvar.type;
        }

        return Type.ERROR;
    }

    String newLabel() {
        return "label" + _labelID++;
    }

    String newTempVar() {
        String id = "";
        return id + _tempVarID--;
    }

    // global
    public String getVarId(Var_declContext ctx) {
        // <Fill here>
        //return _gsymtable.get(ctx.IDENT().getText());
        return ctx.IDENT().getSymbol().getText();
        //return null;//have to modify
    }

    // local
    public String getVarId(Local_declContext ctx) {
        String sname = "";
        sname += getVarId(ctx.IDENT().getText());
        return sname;
    }

}

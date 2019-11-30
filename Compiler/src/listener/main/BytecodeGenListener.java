package listener.main;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import listener.main.MiniCParser.ParamsContext;

import static listener.main.BytecodeGenListenerHelper.*;
import static listener.main.SymbolTable.*;

public class BytecodeGenListener extends MiniCBaseListener implements ParseTreeListener {
    ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
    SymbolTable symbolTable = new SymbolTable();

    int tab = 0;
    int label = 0;

    // program	: decl+

    @Override
    public void enterFun_decl(MiniCParser.Fun_declContext ctx) {
        symbolTable.initFunDecl();

        String fname = getFunName(ctx);
        ParamsContext params;

        if (fname.equals("main")) {
            symbolTable.putLocalVar("args", Type.INTARRAY);
        } else {
            symbolTable.putFunSpecStr(ctx);
            params = (MiniCParser.ParamsContext) ctx.getChild(3);
            symbolTable.putParams(params);
        }
    }


    // var_decl	: type_spec IDENT ';' | type_spec IDENT '=' LITERAL ';'|type_spec IDENT '[' LITERAL ']' ';'
    @Override
    public void enterVar_decl(MiniCParser.Var_declContext ctx) {
        String varName = ctx.IDENT().getText();

        if (isArrayDecl(ctx)) {
            symbolTable.putGlobalVar(varName, Type.INTARRAY);
        } else if (isDeclWithInit(ctx)) {
            symbolTable.putGlobalVarWithInitVal(varName, Type.INT, initVal(ctx));
        } else { // simple decl
            symbolTable.putGlobalVar(varName, Type.INT);
        }
    }


    @Override
    public void enterLocal_decl(MiniCParser.Local_declContext ctx) {
        if (isArrayDecl(ctx)) {
            symbolTable.putLocalVar(getLocalVarName(ctx), Type.INTARRAY);
        } else if (isDeclWithInit(ctx)) {
            symbolTable.putLocalVarWithInitVal(getLocalVarName(ctx), Type.INT, initVal(ctx));
        } else { // simple decl
            symbolTable.putLocalVar(getLocalVarName(ctx), Type.INT);
        }
    }


    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) {
//        String classProlog = getFunProlog();

        String fun_decl = "", var_decl = "";

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (isFunDecl(ctx, i))
                fun_decl += newTexts.get(ctx.decl(i));
            else
                var_decl += newTexts.get(ctx.decl(i));
        }

//        newTexts.put(ctx, classProlog + var_decl + fun_decl);
        newTexts.put(ctx, var_decl + fun_decl);
        System.out.println(newTexts.get(ctx));
    }


    // decl	: var_decl | fun_decl
    @Override
    public void exitDecl(MiniCParser.DeclContext ctx) {
        String decl = "";
        if (ctx.getChildCount() == 1) {
            if (ctx.var_decl() != null)                //var_decl
                decl += newTexts.get(ctx.var_decl());
            else                            //fun_decl
                decl += newTexts.get(ctx.fun_decl());
        }
        newTexts.put(ctx, decl);
    }

    // stmt	: expr_stmt | compound_stmt | if_stmt | while_stmt | return_stmt
    @Override
    public void exitStmt(MiniCParser.StmtContext ctx) {
        String stmt = "";
        if (ctx.getChildCount() > 0) {
            if (ctx.expr_stmt() != null)                // expr_stmt
                stmt += newTexts.get(ctx.expr_stmt());
            else if (ctx.compound_stmt() != null)    // compound_stmt
                stmt += newTexts.get(ctx.compound_stmt());
            else if (ctx.if_stmt() != null)    // if_stmt
                stmt += newTexts.get(ctx.if_stmt());
            else if (ctx.while_stmt() != null)    // while_stmt
                stmt += newTexts.get(ctx.while_stmt());
            else if (ctx.return_stmt() != null)    // return_stmt
                stmt += newTexts.get(ctx.return_stmt());
            // <(0) Fill here> maybe modified
        }
        newTexts.put(ctx, stmt);
    }

    // expr_stmt	: expr ';'
    @Override
    public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
        String stmt = "";
        if (ctx.getChildCount() == 2) {
            stmt += newTexts.get(ctx.expr());    // expr
        }
        newTexts.put(ctx, stmt);
    }


    // while_stmt	: WHILE '(' expr ')' stmt
    @Override
    public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
        // <(1) Fill here!>
        String stmt = "";

        String condExpr = newTexts.get(ctx.expr());
        String thenStmt = newTexts.get(ctx.stmt());

        newTexts.put(ctx, stmt);
    }


    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
        // <(2) Fill here!>
        String fun_decl = "def " + ctx.IDENT().getText() + "(";
        fun_decl += newTexts.get(ctx.params()) + "):";
        fun_decl += newTexts.get(ctx.compound_stmt());
        newTexts.put(ctx, fun_decl);
    }

    @Override
    public void exitParams(ParamsContext ctx) {
        /**
         * params == param (, param)*
         * param == newTexts.get(ctx.child)
         **/
        if (ctx.VOID() != null) {   //except void
            return;
        }

        int childCnt = ctx.getChildCount();
        String ret = "";
        for (int i = 0; i < childCnt; i += 2) {   //odd(i) == param and even(i) == ','
            ret += newTexts.get(ctx.getChild(i));
            if (i + 1 < childCnt) ret += ", "; //if param is last break
        }
        newTexts.put(ctx, ret);
    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) {
        // for except type
        // type x => newTexts.put(x)
        newTexts.put(ctx, ctx.IDENT().getText());
    }


    private String funcHeader(MiniCParser.Fun_declContext ctx, String fname) {
        return null;

    }


    @Override
    public void exitVar_decl(MiniCParser.Var_declContext ctx) {
        String varName = ctx.IDENT().getText();
        String varDecl = "";

        if (isDeclWithInit(ctx)) {
            varDecl += "putfield " + varName + "\n";
            // v. initialization => Later! skip now..:
        }
        newTexts.put(ctx, varDecl);
    }

    @Override
    public void exitType_spec(MiniCParser.Type_specContext ctx) {
        return;
    }


    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
        /**
         * name : name of variable
         * value : value of variable
         **/

        String name = ctx.IDENT().getText();
        String value = "";
        if (ctx.LITERAL() != null) {
            value = ctx.LITERAL().getText();
        }

        // int x;    -->  x = ""
        // int x=10; --> x = 10
        newTexts.put(ctx, "\n" + name + " = " + value);
    }


    // compound_stmt	: '{' local_decl* stmt* '}'
    @Override
    public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
        String compound_stmt = "";
        // 0 : {
        // -1 : }
        for (int i = 1; i < ctx.getChildCount() - 2; i++) {
            // 1 ~ -3
            compound_stmt += newTexts.get(ctx.getChild(i)) + "\n";
        }
        compound_stmt += newTexts.get(ctx.getChild(ctx.getChildCount() - 2));
        compound_stmt = compound_stmt.replace("\n", "\n\t");
        compound_stmt += "\n";
        // -2
        newTexts.put(ctx, compound_stmt);
    }

    // if_stmt	: IF '(' expr ')' stmt | IF '(' expr ')' stmt ELSE stmt;
    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
        String stmt = "";
        String condExpr = newTexts.get(ctx.expr());
        String thenStmt = newTexts.get(ctx.stmt(0));


        if (noElse(ctx)) {

        } else {

        }

        newTexts.put(ctx, stmt);
    }


    // return_stmt	: RETURN ';' | RETURN expr ';'
    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
        // <(4) Fill here>
        String stmt = "return "; // 리턴문을 미리 넣어놓고
        tab++;
        if (ctx.expr() != null) { // expr에 값이 있다면.
            stmt += newTexts.get(ctx.expr()); // 거기의 구문을 가져와서 다시 stmt에 스트링으로 저장한다.
        }
        tab--;
        newTexts.put(ctx, stmt); // 저
    }


    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String expr = "";
        tab++;

        if (ctx.getChildCount() == 1) { // 자식을 변수를 가져옴
            expr += ctx.getChild(0).getText();
        } else if (ctx.getChildCount() == 2) { // 단항 연산자를 쓴 변수
            expr += ctx.expr(0).getText() + " " + ctx.getChild(0).getText() + " " + ctx.expr(1).getText();
        } else if (ctx.getChildCount() == 3) { // 연산자 집합
            if (ctx.expr(1) == null)
                expr += ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " " + newTexts.get(ctx.expr(0)) + "\n";
            else
                expr += newTexts.get(ctx.expr(0)) + " " + ctx.getChild(1).getText() + " " + newTexts.get(ctx.expr(1)) + "\n";
        } else if (ctx.getChildCount() == 4) { // ????

        }

        newTexts.put(ctx, expr);
        tab--;
    }


    private String handleUnaryExpr(MiniCParser.ExprContext ctx, String expr) {

        tab++;
        expr += /*getIndent(tab) +*/ newTexts.get(ctx.expr(0));
        switch (ctx.getChild(0).getText()) {
            case "-":
                break;
            case "--":
                break;
            case "++":
                break;
            case "!":
                break;
        }
        tab--;
        return expr;
    }


    private String handleBinExpr(MiniCParser.ExprContext ctx, String expr) {


        expr += newTexts.get(ctx.expr(0));
        expr += newTexts.get(ctx.expr(1));


        switch (ctx.getChild(1).getText()) {
            case "*":

                break;
            case "/":
                break;
            case "%":
                break;
            case "+":        // expr(0) expr(1) iadd
                break;
            case "-":
                break;

            case "==":

                break;
            case "!=":

                break;
            case "<=":

                break;
            case "<":

                // <(6) Fill here>
                break;

            case ">=":

                // <(7) Fill here>

                break;

            case ">":

                // <(8) Fill here>
                break;

            case "and":

                break;
            case "or":

                break;
        }

        return expr;
    }

    private String handleFunCall(MiniCParser.ExprContext ctx, String expr) {
        String fname = getFunName(ctx);

        if (fname.equals("_print")) {        // System.out.println

        } else {

        }

        return null;

    }

    // args	: expr (',' expr)* | ;
    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {

        String argsStr = "\n";

        for (int i = 0; i < ctx.expr().size(); i++) {
            argsStr += newTexts.get(ctx.expr(i));
        }
        newTexts.put(ctx, argsStr);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

}

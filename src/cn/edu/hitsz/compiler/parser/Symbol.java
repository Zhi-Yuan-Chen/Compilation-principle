package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

class Symbol{
    Token token;
    NonTerminal nonTerminal;
    IRValue value;
    SourceCodeType type;

    private Symbol(Token token, NonTerminal nonTerminal, IRValue value,SourceCodeType type){
        this.token = token;
        this.nonTerminal = nonTerminal;
        this.value=value;
        this.type=type;
    }

    public Symbol(Token token){
        this.token=token;
    }

    public Symbol(NonTerminal nonTerminal){
        this.nonTerminal=nonTerminal;
    }

    public Symbol(IRValue value){
        this.value=value;
    }

    public Symbol(SourceCodeType type){
        this.type=type;
    }

    public boolean isToken(){
        return this.token != null;
    }

    public boolean isNonterminal(){
        return this.nonTerminal != null;
    }
}


package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.Stack;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {
    public SymbolTable symbolTable;
    public Stack<Symbol>symbolStack=new Stack<>();
    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
//        throw new NotImplementedException();
        return;
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
//        throw new NotImplementedException();
        //只需要处理声明语句相关的就够了
        switch (production.index()){
            case 4:
                /*S -> D id*/
                Symbol symbol=symbolStack.pop();
                SourceCodeType sourceCodeType=symbolStack.pop().type;
//                System.out.println(symbol.value);
                symbolTable.get(symbol.token.getText()).setType(sourceCodeType);
                symbolStack.push(new Symbol(production.head()));
                break;
            case 5:
                /*D -> int*/
                SourceCodeType type=symbolStack.pop().type;
                symbolStack.push(new Symbol(type));
                break;
            default:
                for(var item:production.body()){
                    symbolStack.pop();
                }
                symbolStack.push(new Symbol(production.head()));
                break;
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
//        throw new NotImplementedException();
        if(currentToken.getKind().getCode()==1){
            symbolStack.push(new Symbol(SourceCodeType.Int));
        }
        else {
            symbolStack.push(new Symbol(currentToken));
        }
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
//        throw new NotImplementedException();
        this.symbolTable=table;
    }
}


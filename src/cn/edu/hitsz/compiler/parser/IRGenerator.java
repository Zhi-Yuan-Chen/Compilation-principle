package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {
    private List<Instruction>instructionList=new ArrayList<>();
    private static Stack<Symbol>symbolTable=new Stack<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
//        throw new NotImplementedException();
        switch (currentToken.getKind().getCode()){
            case 52:
                symbolTable.push(new Symbol(IRImmediate.of(Integer.parseInt(currentToken.getText()))));
                break;
            case 51:
                symbolTable.push(new Symbol(IRVariable.named(currentToken.getText())));
                break;
            default:
                symbolTable.push(new Symbol(IRVariable.temp()));
                break;
        }
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO
//        throw new NotImplementedException();
        IRValue irValue1,irValue2;
        IRVariable temp;
        switch (production.index()){
            case 6:
                /*S -> id = E*/
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                temp=IRVariable.named(symbolTable.pop().value.toString());
                instructionList.add(Instruction.createMov(temp,irValue1));
                temp=IRVariable.temp();
                symbolTable.push(new Symbol(temp));
                break;
            case 7:
                /*S -> return E*/
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                instructionList.add(Instruction.createRet(irValue1));
                temp=IRVariable.temp();
                symbolTable.push(new Symbol(temp));
                break;
            case 8:
                /*E -> E + A*/
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                irValue2=symbolTable.pop().value;
                temp=IRVariable.temp();
                instructionList.add(Instruction.createAdd(temp,irValue2,irValue1));
                symbolTable.push(new Symbol(temp));
                break;
            case 9:
                //E -> E - A
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                irValue2=symbolTable.pop().value;
                temp=IRVariable.temp();
                instructionList.add(Instruction.createSub(temp,irValue2,irValue1));
                symbolTable.push(new Symbol(temp));
                break;
            case 11:
                //A -> A * B
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                irValue2=symbolTable.pop().value;
                temp=IRVariable.temp();
                instructionList.add(Instruction.createMul(temp,irValue2,irValue1));
                symbolTable.push(new Symbol(temp));
                break;
            case 13:
                //E -> ( B )
                symbolTable.pop();
                irValue1=symbolTable.pop().value;
                symbolTable.pop();
                symbolTable.push(new Symbol(irValue1));
                break;
            case 1,10,12,14,15:
                break;
            default:
                for(var item:production.body()){
                    symbolTable.pop();
                }
                temp=IRVariable.temp();
                symbolTable.push(new Symbol(temp));
                break;
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
//        throw new NotImplementedException();
        return;
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
//        throw new NotImplementedException();
        return;
    }

    public List<Instruction> getIR() {
        // TODO
//        throw new NotImplementedException();
        return this.instructionList;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}


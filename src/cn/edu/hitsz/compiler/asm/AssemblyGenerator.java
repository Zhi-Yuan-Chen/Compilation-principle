package cn.edu.hitsz.compiler.asm;
import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.*;
import cn.edu.hitsz.compiler.parser.table.BMap;
import cn.edu.hitsz.compiler.parser.table.RegList;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {
    List<String>assemble_code=new ArrayList<>();
    List<Instruction>instructionList=new ArrayList<>();
    BMap bMap=new BMap();
    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
        //该模块主要将add和sub指令的立即数都移动到最后一位
//        throw new NotImplementedException();
        RegList.initialReg();
        IRVariable temp;
        for(var i:originInstructions){
            switch (i.getKind()){
                case ADD :
                    //把add类型的立即数都移动到最后一位去，方便run函数编写
                    //对于add指令，a+b和b+a效果一样，直接移动到最后一位即可
                    if(i.getLHS() instanceof IRImmediate){
                        instructionList.add(Instruction.createAdd(i.getResult(),i.getRHS(),i.getLHS()));
                    }
                    else {
                        instructionList.add(i);
                    }
                    break;
                case SUB:
                    //对于sub指令，b-a和a-b效果不一样，多一个mov的中间步骤
                    if(i.getLHS() instanceof IRImmediate){
                        temp=IRVariable.temp();
                        instructionList.add(Instruction.createMov(temp,i.getLHS()));
                        instructionList.add(Instruction.createSub(i.getResult(),temp,i.getRHS()));
                    }
                    else if(i.getRHS() instanceof IRImmediate){
                        temp=IRVariable.temp();
                        instructionList.add(Instruction.createMov(temp,i.getRHS()));
                        instructionList.add(Instruction.createSub(i.getResult(),i.getLHS(),temp));
                    }
                    else {
                        instructionList.add(i);
                    }
                    break;
                default:
                    instructionList.add(i);
                    break;
            }
        }
        System.out.println(instructionList);
    }


    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        IRValue rs1;
        IRValue rs2;
        IRVariable rd;
        IRValue imm;
        RegList.Reg reg;
        // TODO: 执行寄存器分配与代码生成
//        throw new NotImplementedException();
        for (int i = 0; i < instructionList.size(); i++) {
            Instruction instr=instructionList.get(i);
            switch (instructionList.get(i).getKind()){
                case ADD :
                    //add类型
                    if(instr.getOperands().get(1) instanceof IRVariable){
//                        System.out.println(instr.getResult());
//                        System.out.println(instr.getOperands().get(0));
//                        System.out.println(instr.getOperands().get(1));
                        rs1=instr.getOperands().get(0);
                        rs2=instr.getOperands().get(1);
                        rd=instr.getResult();
                        reg=RegList.getReg();
                        if(reg!=null){
                            bMap.replace(rd,reg);
                        }
                        else {
                            for(var j: RegList.Reg.values()){
                                if(RegList.foundIRVariable(i,instructionList,bMap.getByValue(j))){
                                    bMap.replace(rd,j);
                                    break;
                                }
                            }
                        }
                        assemble_code.add("add "+bMap.getByKey(rd)+", "+
                                bMap.getByKey((IRVariable) rs1)+" ,"+ bMap.getByKey((IRVariable) rs2));
                    }
                    else {
                        //addi类型
                        rs1=instr.getOperands().get(0);
                        imm=instr.getOperands().get(1);
                        rd=instr.getResult();
                        reg=RegList.getReg();
                        if(reg!=null){
                            bMap.replace(rd,reg);
                        }
                        else {
                            for(var j:RegList.Reg.values()){
                                if(RegList.foundIRVariable(i,instructionList,bMap.getByValue(j))){
                                    bMap.replace(rd,j);
                                    break;
                                }
                            }
                        }
                        assemble_code.add("addi "+bMap.getByKey(rd)+", "+
                                bMap.getByKey((IRVariable) rs1)+", "+imm);
                    }
                    break;
                case MUL:
                    rs1=instr.getOperands().get(0);
                    rs2=instr.getOperands().get(1);
                    rd=instr.getResult();
                    reg=RegList.getReg();
                    if(reg!=null){
                        bMap.replace(rd,reg);
                    }
                    else {
                        for (var j: RegList.Reg.values()){
                            if(RegList.foundIRVariable(i,instructionList,bMap.getByValue(j))){
                                bMap.replace(rd,j);
                                break;
                            }
                        }
                    }
                    assemble_code.add("mul "+bMap.getByKey(rd)+", "+bMap.getByKey((IRVariable) rs1)
                    +", "+bMap.getByKey((IRVariable) rs2));
                    break;
                case SUB:
                    rs1=instr.getOperands().get(0);
                    rs2=instr.getOperands().get(1);
                    rd=instr.getResult();
                    reg=RegList.getReg();
                    if(reg!=null){
                        bMap.replace(rd,reg);
                    }
                    else {
                        for(var j:RegList.Reg.values()){
                            if(RegList.foundIRVariable(i,instructionList,bMap.getByValue(j))){
                                bMap.replace(rd,j);
                                break;
                            }
                        }
                    }
                    assemble_code.add("sub "+bMap.getByKey(rd)+", "+bMap.getByKey((IRVariable) rs1)+", "
                    +bMap.getByKey((IRVariable) rs2));
                    break;
                case MOV:
                    rs1=instr.getOperands().get(0);
                    rd=instr.getResult();
                    reg=RegList.getReg();
                    if(reg!=null){
                        bMap.replace(rd,reg);
                    }
                    else {
                        for(var j:RegList.Reg.values()){
                            if(RegList.foundIRVariable(i,instructionList,bMap.getByValue(j))){
                                bMap.replace(rd,j);
                            }
                        }
                    }
                    if(rs1 instanceof IRImmediate){
                        assemble_code.add("li "+bMap.getByKey(rd)+", "+rs1);
                    }
                    else {
                        assemble_code.add("mv "+bMap.getByKey(rd)+", "+bMap.getByKey((IRVariable) rs1));
                    }
                    break;
                case RET:
                    rs1=instr.getOperands().get(0);
                    assemble_code.add("mv "+"a0, "+bMap.getByKey((IRVariable) rs1));
                    break;
                default:
                    break;
            }
        }
        for (int i = 0; i < assemble_code.size(); i++) {
            System.out.println(assemble_code.get(i));
        }
    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // TODO: 输出汇编代码到文件
//        throw new NotImplementedException();
        FileUtils.writeLines(path,assemble_code);
    }
}


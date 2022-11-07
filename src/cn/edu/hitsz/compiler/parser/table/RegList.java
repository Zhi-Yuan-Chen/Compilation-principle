package cn.edu.hitsz.compiler.parser.table;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class RegList{
    public enum Reg{
        t0,t1,t2,t3,t4,t5,t6;
    }
    public static List<Reg>regs=new ArrayList<>();

    public static void initialReg(){
        for(var i:Reg.values()){
            regs.add(i);
        }
    }

    public static Reg getReg(){
        if(regs.size()!=0){
            Reg a=regs.get(0);
            regs.remove(0);
            return a;
        }
        return null;
    }

    public static boolean foundIRVariable(int a, List<Instruction> instructions, IRVariable irVariable)
            //查找irVariable在后面是否出现
    {
        if(a<instructions.size()-1){
            for (int i = a; i < instructions.size(); i++) {
                for(IRValue irValue:instructions.get(i).getOperands()){
                    if(irValue.equals(irVariable)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

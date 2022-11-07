package cn.edu.hitsz.compiler.parser.table;

import cn.edu.hitsz.compiler.ir.IRVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
public class BMap {
    private final Map<IRVariable, RegList.Reg> KVmap = new HashMap<>();
    private final Map<RegList.Reg, IRVariable> VKmap = new HashMap<>();

    public void removeByKey(IRVariable key) {
        VKmap.remove(KVmap.remove(key));
    }

    public void removeByValue(RegList.Reg value) {
        KVmap.remove(VKmap.remove(value));
    }

    public boolean containsKey(IRVariable key) {
        return KVmap.containsKey(key);
    }

    public boolean containsValue(RegList.Reg value) {
        return VKmap.containsKey(value);
    }

    public void replace(IRVariable key, RegList.Reg value) {
        // 对于双射关系, 将会删除交叉项
        removeByKey(key);
        removeByValue(value);
        KVmap.put(key, value);
        VKmap.put(value, key);
    }

    public RegList.Reg getByKey(IRVariable key) {
        return KVmap.get(key);
    }

    public IRVariable getByValue(RegList.Reg value) {
        return VKmap.get(value);
    }
}

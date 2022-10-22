package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    public List<Token>tokenList=new ArrayList<>();
    public List<String>codeList;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        if(path==null){
            throw new RuntimeException();
            //路径不存在直接抛异常
        }
        codeList=FileUtils.readLines(path);
        //读取文件每一行数据
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
        for(String s:codeList){
            deal(s);
        }
        tokenList.add(Token.simple(String.valueOf('$')));
//        throw new NotImplementedException();
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        if(tokenList!=null){
            return tokenList;
        }
        return null;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }

    public boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    public boolean isLetter(char c){
        return Character.isLetter(c);
    }

    public boolean isSymbol(char c){
        if(!isDigit(c) && !isLetter(c)){
            return true;
        }
        return false;
    }

    public void deal(String line){
        int num=0;
        //位置指针，进行扫描用
        int length=line.length();
        char ch=line.charAt(num);
        while (num<length){
            if(isLetter(ch)){
                char []temp_ch=new char[10];
                int temp=0;
                int word_length=0;
                while (!isSymbol(ch)){
                    temp_ch[temp]=ch;
                    temp++;
                    word_length++;
                    num++;
                    ch=line.charAt(num);
                }
                String s=new String(Arrays.copyOf(temp_ch,word_length));
                if(TokenKind.isAllowed(s)){
                    tokenList.add(Token.simple(s));
                }
                else {
                    tokenList.add(Token.normal("id",s));
                    if(!symbolTable.has(s)){
                        symbolTable.add(s);
                    }
                }
            }
            else if(isDigit(ch)){
                char []temp_ch=new char[10];
                int temp=0;
                int word_length=0;
                while (isDigit(ch)){
                    temp_ch[temp]=ch;
                    temp++;
                    word_length++;
                    num++;
                    ch=line.charAt(num);
                }
                String s=new String(Arrays.copyOf(temp_ch,word_length));
                tokenList.add(Token.normal("IntConst",s));
            }
            else{
                switch (ch){
                    case '=':
                        tokenList.add(Token.simple(String.valueOf('=')));
                        break;
                    case ',':
                        tokenList.add(Token.simple(String.valueOf(',')));
                        break;
                    case ';':
                        tokenList.add(Token.simple("Semicolon"));
                        break;
                    case '+':
                        tokenList.add(Token.simple(String.valueOf('+')));
                        break;
                    case '-':
                        tokenList.add(Token.simple(String.valueOf('-')));
                        break;
                    case '*':
                        tokenList.add(Token.simple(String.valueOf('*')));
                        break;
                    case '/':
                        tokenList.add(Token.simple(String.valueOf('/')));
                        break;
                    case '(':
                        tokenList.add(Token.simple(String.valueOf('(')));
                        break;
                    case ')':
                        tokenList.add(Token.simple(String.valueOf(')')));
                        break;
                }
                if(ch!=';') {
                    num++;
                    ch = line.charAt(num);
                }
                else {
                    break;
                }
            }
        }

    }
}

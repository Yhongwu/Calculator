package com.yhw.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author yaohongwu
 * @date 2023/4/26
 */
public class Calculator {
    /**
     * 最终操作结果
     */
    private int result = 0;
    /**
     * 存放每一次操作结果
     */
    private Stack<Integer> leftStack = new Stack<>();
    /**
     * 上一次操作
     */
    private Character lastOpt = null;
    /**
     * 上一次操作的数
     */
    private int lastOptVal = 0;


    public void execute(String command) {
        switch (command) {
            case "undo":
                undo();
                break;
            case "redo":
                redo();
                break;
            case "clear":
                clear();
                break;
            default: {
                execCommand(command);
            }
        }
    }

    /**
     * 解析输入的表达式
     * @param command
     */
    public void execCommand(String command) {
        int num = 0;
        char opt = '+';
        for (int i = 0; i < command.length(); i++) {
            char ch = command.charAt(i);
            if (Character.isDigit(ch)) {
                if (i == 0) {
                    clear();
                }
                num = num * 10 + (ch - '0');
            }
            if ((!Character.isDigit(ch) && ch != ' ') || i == command.length() - 1) {
                execute(opt, num);
                num = 0;
                opt = ch;
            }

        }
    }

    /**
     * 撤销上一步的操作
     */
    public void undo() {
        if (!leftStack.isEmpty()) {
            leftStack.pop();
        }
        result = leftStack.isEmpty() ? 0 : leftStack.peek();
    }

    /**
     * 重复执行最后一步的操作
     */
    public void redo() {
        if (lastOpt != null) {
            execute(lastOpt, lastOptVal);
        }
    }

    /**
     * 重置
     */
    public void clear() {
        result = 0;
        lastOpt = null;
        lastOptVal = 0;
        leftStack.clear();
    }

    /**
     * 执行加减乘除命令
     * @param opt
     * @param value
     * @return
     */
    public Integer execute(Character opt, Integer value) {
        lastOpt = opt;
        lastOptVal = value;
        Command command = CommandFactory.getCommand(opt);
        if (command == null) {
            System.out.println("输入的命令有误");
            clear();
            return result;
        }
        result = command.exec(result, value);
        leftStack.push(result);
        return result;
    }

    public int getResult() {
        return result;
    }


    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("exit")) {
            calculator.execute(input);
            System.out.println("执行结果：" + calculator.getResult());
            input = scanner.nextLine();
        }
        scanner.close();
    }
}

class CommandFactory {
    private static Map<Character, Command> commandMap = new HashMap<>();
    static {
        commandMap.put('+', new AddCommand());
        commandMap.put('-', new SubCommand());
        commandMap.put('*', new MultiplyCommand());
        commandMap.put('/', new DivideCommand());
    }

    public static Command getCommand(Character opt) {
        return commandMap.get(opt);
    }
}

abstract class Command {
    public abstract Integer exec(Integer value1, Integer value2);
}

class AddCommand extends Command {
    public Integer exec(Integer value1, Integer value2) {
        return value1 + value2;
    }
}

class SubCommand extends Command {
    public Integer exec(Integer value1, Integer value2) {
        return value1 - value2;
    }
}

class MultiplyCommand extends Command {
    public Integer exec(Integer value1, Integer value2) {
        return value1 * value2;
    }
}

class DivideCommand extends Command {
    public Integer exec(Integer value1, Integer value2) {
        return value1 / value2;
    }
}
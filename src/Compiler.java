import java.util.ArrayList;

public class Compiler {
	public ArrayList<Code> codeList;
	public boolean mainCode = true;
	private Code dammy = new Code(0);
	private Function currentFunc;
	private ArrayList<Variable> varlist = new ArrayList<Variable>();
	private ArrayList<Function> funclist = new ArrayList<Function>();

	public int compile(Cons begin, Executer executer) {
		ArrayList<Code> newCodeList = new ArrayList<Code>();
		//main関数か否か
		if (mainCode) {
			mainCode = false;
			reCompile(newCodeList, begin, executer);
			newCodeList.add(new Code(Executer.command.PRINT));		//main関数の最後で結果を出力
			//長さが確定したArrayListを配列に変換
			Code[] releaseCodeLsit = new Code[newCodeList.size()];
			for (int i = 0; i < releaseCodeLsit.length; i++) {
				releaseCodeLsit[i] = newCodeList.get(i);
			}
			executer.sourceList.set(0, releaseCodeLsit);			//main関数はsourceListの"0"番目
		} else {
			reCompile(newCodeList, begin, executer);
			newCodeList.add(new Code(Executer.command.RET));
			//長さが確定したArrayListを配列に変換
			Code[] releaseCodeLsit = new Code[newCodeList.size()];
			for (int i = 0; i < releaseCodeLsit.length; i++) {
				releaseCodeLsit[i] = newCodeList.get(i);
			}
			executer.sourceList.add(releaseCodeLsit);
		}
		return executer.sourceList.size() - 1;
	}

	private void reCompile(ArrayList<Code> newCodeList, Cons begin, Executer executer) {
		switch (begin.kind) {
		case OPERATOR:
			compileOp(newCodeList, begin, executer);
			break;
		case NUMBER:
			newCodeList.add(new Code(Executer.command.PUSH));
			newCodeList.add(new Code(Integer.parseInt(begin.getValue())));
			break;
		case IF:
			compileIf(newCodeList, begin, executer);
			break;
		case SETQ:
			compileSetq(newCodeList, begin, executer);
			break;
		case DEFUN:
			compileDefun(newCodeList, begin, executer);
			break;
		case NAME:
			compileName(newCodeList, begin, executer);
			break;
		case NODE:
			reCompile(newCodeList, begin.car, executer);
			break;
		}
	}

	private void compileOp(ArrayList<Code> newCodeList, Cons token, Executer executer) {
		Cons current = token.cdr;
		while (current != null) {
			if (current.kind == Cons.ConsIdentifier.NODE) {
				reCompile(newCodeList, current.car, executer);
			} else if (current.kind == Cons.ConsIdentifier.NUMBER) {
				newCodeList.add(new Code(Executer.command.PUSH));
				newCodeList.add(new Code(Integer.parseInt(current.getValue())));
			} else if (current.kind == Cons.ConsIdentifier.NAME) {
				compileName(newCodeList, current, executer);
			}
			current = current.cdr;
		}
		switch (token.getValue()) {
		case "+":
			newCodeList.add(new Code(Executer.command.ADD));
			break;
		case "-":
			newCodeList.add(new Code(Executer.command.SUB));
			break;
		case "*":
			newCodeList.add(new Code(Executer.command.MUL));
			break;
		case "/":
			newCodeList.add(new Code(Executer.command.DIV));
			break;
		case ">":
			newCodeList.add(new Code(Executer.command.GT));
			break;
		case "<":
			newCodeList.add(new Code(Executer.command.LT));
			break;
		case ">=":
			newCodeList.add(new Code(Executer.command.GTEQ));
			break;
		case "<=":
			newCodeList.add(new Code(Executer.command.LTEQ));
			break;
		case "=":
			newCodeList.add(new Code(Executer.command.EQ));
			break;
		}
	}

	private void compileIf(ArrayList<Code> newCodeList, Cons token, Executer executer) {
		int falseNum, exitNum;
		compileOp(newCodeList, token.cdr.car, executer);	//条件文をコンパイル
		newCodeList.add(new Code(Executer.command.IF));		//IFを追加
		newCodeList.add(dammy);
		falseNum = newCodeList.size() - 1;					//faulse文の先頭番号を保持するCode(確定後に代入
		reCompile(newCodeList, token.cdr.cdr, executer);	//true文をコンパイル
		newCodeList.add(new Code(Executer.command.JUMP));	//true文の最後にJUMPを追加
		newCodeList.add(dammy);
		exitNum = newCodeList.size() - 1;					//if文の終了番号を保持するCode(確定後に代入
		newCodeList.set(falseNum, new Code(newCodeList.size()));
		reCompile(newCodeList, token.cdr.cdr.cdr, executer);//false文をコンパイル
		newCodeList.set(exitNum, new Code(newCodeList.size()));
	}

	private void compileSetq(ArrayList<Code> newCodeList, Cons token, Executer executer) {
		if (token.cdr.car == null) {
		}
		if (currentFunc == null) {
			varlist.add(new Variable(token.cdr.getValue(), varlist.size() + 1));
		} else {
			currentFunc.localvar.add(new Variable(token.cdr.getValue(), currentFunc.localvar.size() + 1));
		}
	}

	private void compileDefun(ArrayList<Code> newCodeList, Cons token, Executer executer) {
		int count = 0;
		Cons varToken = token;
		Function newFunc = new Function(token.cdr.getValue());
		varToken = token.cdr.cdr.car;
		while (varToken != null) {
			newFunc.parameter.add(new Variable(varToken.getValue(), count));
			count++;
			varToken = varToken.cdr;
		}
		funclist.add(newFunc);
		currentFunc = funclist.get(funclist.size() - 1);
		newFunc.number = compile(token.cdr.cdr.cdr.car, executer);
	}

	private void compileName(ArrayList<Code> newCodeList, Cons token, Executer executer) {
		int func_i = 0, var_i = 0;
		Cons currentToken = token.cdr;
		boolean finish = false;
		//関数に該当するNameがあれば、関数の呼び出し
		while (func_i < funclist.size() && finish == false) {
			if (funclist.get(func_i).name.equals(token.getValue())) {
				for (int num = 0; num < funclist.get(func_i).parameter.size(); num++) {
					reCompile(newCodeList, currentToken, executer);					//引数をpush
					currentToken = currentToken.cdr;
				}
				newCodeList.add(new Code(Executer.command.PUSH));					//引数の個数をpush
				newCodeList.add(new Code(funclist.get(func_i).parameter.size()));
				newCodeList.add(new Code(Executer.command.CALL));					//CALLと関数の識別番号をpush
				if (currentFunc == funclist.get(func_i)) {
					newCodeList.add(new Code(func_i + 1));
				} else {
					newCodeList.add(new Code(funclist.get(func_i).number));
				}

				finish = true;
			}
			func_i++;
		}
		//引数に該当するNameがあれば、引数の値をpush
		while (var_i < currentFunc.parameter.size() && finish == false) {
			if (currentFunc.parameter.get(var_i).name.equals(token.getValue())) {
				newCodeList.add(new Code(Executer.command.LOADA));
				newCodeList.add(new Code(var_i + 1));
				finish = true;
			}
			var_i++;
		}
	}
}

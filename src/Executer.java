import java.util.ArrayList;
import java.util.Stack;

public class Executer {
	public enum command {
		/**関数を終了して、結果をpush*/
		RET,
		/**指定した値をpush*/
		PUSH,
		/**スタックからpop*/
		POP,
		/**加算。スタックの先頭から値を二つpopし、それらの加算した結果をスタックにpushする*/
		ADD,
		/**減算。スタックの先頭から値を二つpopし、それらの減算した結果をスタックにpushする*/
		SUB,
		/**乗算。スタックの先頭から値を二つpopし、それらの乗算した結果をスタックにpushする*/
		MUL,
		/**除算。スタックの先頭から値を二つpopし、それらの除算した結果をスタックにpushする*/
		DIV,
		/**">"の比較演算。TRUEなら"1", FALSEなら"0"*/
		GT,
		/**"<"の比較演算。TRUEなら"1", FALSEなら"0"*/
		LT,
		/**">="の比較演算。TRUEなら"1", FALSEなら"0"*/
		GTEQ,
		/**"<="の比較演算。TRUEなら"1", FALSEなら"0"*/
		LTEQ,
		/**"="の比較演算。TRUEなら"1", FALSEなら"0"*/
		EQ,
		/**関数の呼び出し*/
		CALL,
		/**n番目の引数をスタックにpushする*/
		LOADA,
		/**n番目のローカル変数をスタックにpushする*/
		LOADL,
		/**n番目の引数にスタックの先頭の値をpopする*/
		STOREA,
		/**n番目のローカル変数にスタックの先頭の値をpopする*/
		STOREL,
		/**スタックの先頭が"1"ならTRUE, それ以外ならFALSEで条件分岐する*/
		IF,
		/**指定したアドレスにprogramCounterを更新*/
		JUMP,
		/**結果を出力*/
		PRINT;
	}

	/**関数ごとにcodeListに分割し、それらのArrayList*/
	public ArrayList<ArrayList<Code>> sourceList = new ArrayList<ArrayList<Code>>();

	public int execute(VirtualMachine vm, int sourceId) {
		Stack<Integer> returnSourceId = new Stack<Integer>();
		int counter, result, number, source = 0;
		int arg, arg1, arg2;
		ArrayList<Code> codeList = sourceList.get(sourceId);
		while (codeList.size() > vm.pc) {
			if (codeList.get(vm.pc).code == null) {
				return -1;
			} else {
				switch (codeList.get(vm.pc).code) {
				case RET:
					result = vm.pop();
					//スタックの先頭からfuncPointerまでpop
					for (counter = vm.stackTop; counter > vm.funcPointer; counter--) {
						vm.pop();
					}
					vm.funcPointer = vm.pop(); 	//戻り先のfuncPointerに更新
					vm.pc = vm.pop() - 1; 		//programCounterを戻りアドレスに更新
					number = vm.pop(); 			//終了した関数の引数の個数をnumberに代入
					//終了した関数の引数をすべてpop
					for (counter = 0; counter < number; counter++) {
						vm.pop();
					}
					vm.push(result);
					codeList = sourceList.get(returnSourceId.pop());
					break;
				case PUSH:
					vm.push(codeList.get(++vm.pc).value);
					break;
				case POP:
					vm.pop();
					break;
				case ADD:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 + arg2);
					break;
				case SUB:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 - arg2);
					break;
				case MUL:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 * arg2);
					break;
				case DIV:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 / arg2);
					break;
				case GT:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 > arg2 ? 1 : 0);
					break;
				case LT:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 < arg2 ? 1 : 0);
					break;
				case GTEQ:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 >= arg2 ? 1 : 0);
					break;
				case LTEQ:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 <= arg2 ? 1 : 0);
					break;
				case EQ:
					arg2 = vm.pop();
					arg1 = vm.pop();
					vm.push(arg1 == arg2 ? 1 : 0);
					break;
				case CALL:
					vm.push(vm.pc + 2);							//戻りアドレスをpush
					vm.push(vm.funcPointer); 					//現在のfuncPointerをpush
					vm.funcPointer = vm.stackTop; 				//funcPointerを呼び出した関数に更新
					returnSourceId.push(source);
					source = codeList.get(vm.pc + 1).value; //呼び出す関数のcodeListの識別番号をsourceに代入
					vm.pc = -1; 									//呼び出す関数のcodeListの先頭にprogramCounterを更新
					//execute(vm, sourceList.get(source));
					codeList = sourceList.get(source);
					break;

				// 関数の引数  ←[funcPointer/一階層上のfuncPointer][戻りアドレス][引数の個数][引数n][引数n-1]...[引数1]
				// 関数の局所  ←[局所n][局所n-1]...[局所1][funcPointer/一階層上のfuncPointer]

				case LOADA:
					arg = vm.stack[vm.funcPointer - 3 - (vm.stack[vm.funcPointer - 2] - codeList.get(++vm.pc).value)];
					vm.push(arg);
					break;
				case LOADL:
					arg = vm.stack[vm.funcPointer + codeList.get(++vm.pc).value];
					vm.push(arg);
					break;
				case STOREA:
					vm.stack[vm.funcPointer - 3 - (vm.stack[vm.funcPointer - 2] - codeList.get(++vm.pc).value)] = vm
							.pop();
					break;
				case STOREL:
					vm.stack[vm.funcPointer + codeList.get(++vm.pc).value] = vm.pop();
					break;
				case IF:
					if (vm.pop() != 1) {
						vm.pc = codeList.get(vm.pc + 1).value - 1;
					} else {
						vm.pc++;
					}
					break;
				case JUMP:
					vm.pc = codeList.get(vm.pc + 1).value - 1;
					break;
				case PRINT:
					System.out.println(vm.stack[vm.stackTop]);
					break;
				}
			}
			vm.pc++;
		}
		return 0;
	}
}

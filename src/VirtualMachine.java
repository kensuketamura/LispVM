public class VirtualMachine {
	int[] stack = new int[4096];
	/**現在実行している関数のデータ（引数,ローカル変数など）を呼び出すための起点となるスタック番号*/
	int funcPointer;
	/**ProgramCounter。codeList内で注目している行番号*/
	int pc;
	/**スタックの先頭番号*/
	int stackTop;
	/**グローバル変数や関数を呼び出すための起点となるスタック番号*/
	int globalPointer;

	public VirtualMachine() {
		this.funcPointer = 0;
		this.globalPointer = 0;
		this.pc = 0;
		this.stackTop = 0;
	}

	public void push(int input) {
		if (++stackTop >= stack.length) {
			int[] copy = new int[stack.length * 2];
			System.arraycopy(stack, 0, copy, 0, stack.length);
			stack = copy;
		}
		stack[stackTop] = input;
	}

	public int pop() {
		int value = stack[stackTop];
		--stackTop;
		return value;
	}
}

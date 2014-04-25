import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.printf("\n");
		System.out.println("88     0000   0000    000000");
		System.out.println("88      00   00   00  00   00");
		System.out.println("88      00    00      00   00");
		System.out.println("88      00      00    000000");
		System.out.println("88      00   00   00  00");
		System.out.println("888888 0000    0000   00");
		System.out.printf("\n    Welcome to LISP!!\n\n");

		InputKey inputKey = new InputKey();
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser();
		LoadFile loadFile;
		Compiler compiler = new Compiler();
		Executer executer = new Executer();
		VirtualMachine vm = new VirtualMachine();

		executer.sourceList.add(new ArrayList<Code>());
		if (args.length != 0) {
			loadFile = new LoadFile(args[0]);
			long s = System.currentTimeMillis();
			tokenizer.tokenize(loadFile.getFileInString());
			parser.parse(tokenizer.getToken());
			while (!parser.startcons.isEmpty()) {
				compiler.mainCode = true;
				compiler.compile(parser.getStart(), executer);
				vm.stackTop = 0;
				vm.pc = 0;
				executer.execute(vm, 0);
			}
			long e = System.currentTimeMillis();
			System.out.println(e - s);
		} else {
			while (!inputKey.input().equals("END")) {
				tokenizer.tokenize(inputKey.getInput());
				parser.parse(tokenizer.getToken());
				while (!parser.startcons.isEmpty()) {
					compiler.mainCode = true;
					compiler.compile(parser.getStart(), executer);
					vm.stackTop = 0;
					vm.pc = 0;
					executer.execute(vm, 0);
				}
			}
		}
		/*
				ArrayList<Code> fib = new ArrayList<Code>();
				fib.add(new Code(Executer.command.LOADA));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(3));
				fib.add(new Code(Executer.command.LT));
				fib.add(new Code(Executer.command.IF));
				fib.add(new Code(11));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.JUMP));
				fib.add(new Code(30));
				fib.add(new Code(Executer.command.LOADA));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.SUB));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.CALL));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.LOADA));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(2));
				fib.add(new Code(Executer.command.SUB));
				fib.add(new Code(Executer.command.PUSH));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.CALL));
				fib.add(new Code(1));
				fib.add(new Code(Executer.command.ADD));
				fib.add(new Code(Executer.command.RET));

				ArrayList<Code> mainCode = new ArrayList<Code>();
				mainCode.add(new Code(Executer.command.PUSH));
				mainCode.add(new Code(1));
				mainCode.add(new Code(Executer.command.PUSH));
				mainCode.add(new Code(36));
				mainCode.add(new Code(Executer.command.PUSH));
				mainCode.add(new Code(1));
				mainCode.add(new Code(Executer.command.CALL));
				mainCode.add(new Code(1));

				executer.sourceList.add(mainCode);
				executer.sourceList.add(fib);
				executer.execute(vm, mainCode);

				System.out.println(vm.stack[vm.stackTop]);
		*/
		System.out.println("-----END-----");
	}

}

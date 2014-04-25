import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.printf("\n");
		System.out.println("88     8888   8888    888888");
		System.out.println("88      88   88   88  88   88");
		System.out.println("88      88    88      88   88");
		System.out.println("88      88      88    888888");
		System.out.println("88      88   88   88  88");
		System.out.println("888888 8888    8888   88");
		System.out.printf("\n    Welcome to LISP!!\n\n");

		InputKey inputKey = new InputKey();
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser();
		LoadFile loadFile;
		Compiler compiler = new Compiler();
		Executer executer = new Executer();
		VirtualMachine vm = new VirtualMachine();

		executer.sourceList.add(new Code[2]);
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
				executer.execute(vm, executer.sourceList.get(0));
			}
			long e = System.currentTimeMillis();
			System.out.println(e - s + "ms");
		} else {
			while (!inputKey.input().equals("END")) {
				tokenizer.tokenize(inputKey.getInput());
				parser.parse(tokenizer.getToken());
				while (!parser.startcons.isEmpty()) {
					compiler.mainCode = true;
					compiler.compile(parser.getStart(), executer);
					vm.stackTop = 0;
					vm.pc = 0;
					executer.execute(vm, executer.sourceList.get(0));
				}
			}
		}
		System.out.println("------END------");
	}

}

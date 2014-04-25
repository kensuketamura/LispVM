public class Cons {
	public enum ConsIdentifier {
		OPERATOR,
		NUMBER,
		IF,
		SETQ,
		DEFUN,
		NAME,
		NODE;
	}

	private String value;
	public ConsIdentifier kind;
	public Cons car;
	public Cons cdr;

	public Cons(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}

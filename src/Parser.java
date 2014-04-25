import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
	private Cons currentToken, prevToken, start;
	public Queue<Cons> startcons = new LinkedList<Cons>();

	public Cons parse(ArrayList<String> token) {
		Stack<Cons> node = new Stack<Cons>();
		int i;
		Stack<Boolean> change = new Stack<Boolean>();
		for (i = 0; i < token.size() - 1; i++) {
			currentToken = new Cons(token.get(i));

			switch (currentToken.getValue()) {
			case "+":
			case "-":
			case "*":
			case "/":
			case "<":
			case ">":
			case "<=":
			case ">=":
			case "=":
				currentToken.kind = Cons.ConsIdentifier.OPERATOR;
				break;
			case "(":
				currentToken.kind = Cons.ConsIdentifier.NODE;
				break;
			case "defun":
				currentToken.kind = Cons.ConsIdentifier.DEFUN;
				break;
			case "setq":
				currentToken.kind = Cons.ConsIdentifier.SETQ;
				break;
			case "if":
				currentToken.kind = Cons.ConsIdentifier.IF;
				break;
			default:
				if (Character.isDigit(currentToken.getValue().charAt(0))) {
					currentToken.kind = Cons.ConsIdentifier.NUMBER;
				} else {
					currentToken.kind = Cons.ConsIdentifier.NAME;
				}
			}

			if (node.isEmpty()) {
				startcons.add(currentToken);
			}
			switch (currentToken.getValue()) {
			case "(":
				if (!change.isEmpty()) {
					if (change.pop()) {
						if (prevToken != null) {
							prevToken.car = currentToken;
						}
						change.push(false);
					} else {
						if (prevToken != null) {
							prevToken.cdr = currentToken;
						}
					}
				} else {
					if (prevToken != null) {
						prevToken.cdr = currentToken;
					}
				}
				node.push(currentToken);
				change.push(true);
				break;
			case ")":
				currentToken = node.pop();
				break;
			default:
				if (!change.isEmpty()) {
					if (change.pop()) {
						if (prevToken != null) {
							prevToken.car = currentToken;
						}
						change.push(false);
					} else {
						if (prevToken != null) {
							prevToken.cdr = currentToken;
						}
					}
				} else {
					if (prevToken != null) {
						prevToken.cdr = currentToken;
					}
				}
				break;
			}
			prevToken = currentToken;
			if (i == 1) {
				start = currentToken;
			}
		}
		return start;
	}

	public Cons getStart() {
		return this.startcons.poll();
	}
}

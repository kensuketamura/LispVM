import java.util.ArrayList;

//関数クラス
public class Function {
	public String name; // 関数名
	public ArrayList<Variable> parameter = new ArrayList<Variable>();
	public int number;

	public Function(String name) {
		this.name = name;
	}
}

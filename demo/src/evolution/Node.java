package evolution;

public class Node {

	private String oper;
	private Node left;
	private Node right;
	private Node parent;

	public Node(Node parent, String oper) {
		this.parent = parent;
		this.oper = oper;
	}

	public Node clone(Node parent) {
		Node n = new Node(parent, oper);
		if (!isLeaf()) {
			n.left = left.clone(n);
			n.right = right.clone(n);
		}
		return n;
	}

	private boolean isLeaf() {
		return left == null;
	}

	public int evaluate() {
		switch (oper) {
		case "*":
			return left.evaluate() * right.evaluate();
		case "+":
			return left.evaluate() + right.evaluate();

		default:
			return Integer.parseInt(oper);
		}
	}

	/**
	 * Number of nodes
	 * 
	 * @return
	 */
	public int weight() {
		if (isLeaf()) {
			return 1;
		} else {
			return 1 + left.weight() + right.weight();
		}
	}

	public Node get(int index) {
		if (index == 0) {
			return this;
		} else {
			index--;
			int wLeft = left.weight();
			if (index <= wLeft) {
				return left.get(index);
			} else {
				return right.get(index - wLeft);
			}
		}
	}
}

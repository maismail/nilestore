package eg.nileu.cis.nilestore.utils.hashtree;

import java.util.ArrayList;
import java.util.List;

public class CompleteBinaryTree<T> extends ArrayList<T> {

	private static final long serialVersionUID = 4299163059202889768L;

	public CompleteBinaryTree() {

	}

	public int lchild(int i) {
		if (i < 0 && i >= size())
			throw new IndexOutOfBoundsException("index is out of range");
		int ans = (2 * i) + 1;
		return ans;
	}

	public int parent(int i) {
		if (i < 0 && i >= size())
			throw new IndexOutOfBoundsException("index is out of range");
		return (i - 1) / 2;
	}

	public int rchild(int i) {
		if (i < 0 && i >= size())
			throw new IndexOutOfBoundsException("index is out of range");
		int ans = (2 * i) + 2;
		return ans;
	}

	public int sibling(int i) {
		int parent = parent(i);
		if (lchild(parent) == i) {
			return rchild(parent);
		} else {
			return lchild(parent);
		}
	}

	public List<Integer> neededFor(int i) {
		if (i < 0 && i >= size())
			throw new IndexOutOfBoundsException("index is out of range");

		List<Integer> needed = new ArrayList<Integer>();

		int here = i;
		while (here != 0) {
			needed.add(sibling(here));
			here = parent(here);
		}

		return needed;
	}
}

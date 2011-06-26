package eg.nileu.cis.nilestore.utils.hashtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

@SuppressWarnings("serial")
public class IncompleteHashTree extends CompleteBinaryTree<ByteArray> {

	private final int first_leaf_num;

	public IncompleteHashTree(int num_leaves) {
		int start = num_leaves;
		int end = MathUtils.round_pow2(start);

		first_leaf_num = end - 1;

		List<ByteArray> LL = new ArrayList<ByteArray>();
		for (int i = 0; i < end; i++) {
			LL.add(null);
		}
		List<List<ByteArray>> rows = new ArrayList<List<ByteArray>>();
		rows.add(LL);

		while (rows.get(rows.size() - 1).size() != 1) {
			List<ByteArray> last = rows.get(rows.size() - 1);

			rows.add(new ArrayList<ByteArray>());

			for (int i = 0; i < (last.size() / 2); i++) {
				rows.get(rows.size() - 1).add(null);
			}
		}

		// hashtree = new ArrayList<byte[]>();
		for (int i = rows.size() - 1; i >= 0; i--) {
			// hashtree.addAll(rows.get(i));
			this.addAll(rows.get(i));
		}
	}

	public List<Integer> neededHashes(int leaf_num, boolean include_leaf) {
		List<Integer> needed = neededFor(first_leaf_num + leaf_num);
		if (include_leaf) {
			needed.add(first_leaf_num + leaf_num);
		}

		Iterator<Integer> neededIter = needed.iterator();
		while (neededIter.hasNext()) {
			int i = neededIter.next();
			if (this.get(i) != null) {
				neededIter.remove();
			}
		}

		return needed;
	}

	public void setHashes(Map<Integer, ByteArray> hashes) throws BadHashError,
			NotEnoughHashesError {
		setHashes(hashes, new HashMap<Integer, ByteArray>());
	}

	public void setLeaveHashes(Map<Integer, ByteArray> hashes)
			throws BadHashError, NotEnoughHashesError {
		setHashes(new HashMap<Integer, ByteArray>(), hashes);
	}

	public void setLeafHash(int id, byte[] hash) throws BadHashError,
			NotEnoughHashesError {
		Map<Integer, ByteArray> hashes = new HashMap<Integer, ByteArray>();
		hashes.put(id, new ByteArray(hash));
		setLeaveHashes(hashes);
	}

	public void setHashes(Map<Integer, ByteArray> hashes,
			Map<Integer, ByteArray> leaves) throws BadHashError,
			NotEnoughHashesError {
		Map<Integer, ByteArray> newHashes = new HashMap<Integer, ByteArray>(
				hashes);
		for (Map.Entry<Integer, ByteArray> leaf : leaves.entrySet()) {
			int hashnum = first_leaf_num + leaf.getKey();
			if (newHashes.containsKey(hashnum)) {
				if (!leaf.getValue().equals(newHashes.get(hashnum))) {
					throw new BadHashError(String.format(
							"got conflicting hashes leaves[%d] != hashes[%d]",
							leaf.getKey(), hashnum));
				}
			}
			newHashes.put(hashnum, leaf.getValue());
		}

		Set<Integer> remove_upon_failure = new HashSet<Integer>();

		try {
			int num_levels = depthOf(this.size() - 1);
			List<Set<Integer>> hashes_to_check = new ArrayList<Set<Integer>>();
			for (int _i = 0; _i < num_levels + 1; _i++) {
				hashes_to_check.add(new HashSet<Integer>());
			}

			// add all hashes to the tree, comparing any duplicates
			for (Map.Entry<Integer, ByteArray> e : newHashes.entrySet()) {
				int i = e.getKey();
				ByteArray h = e.getValue();
				ByteArray eH = get(e.getKey());
				if (eH != null) {
					if (!h.equals(eH)) {
						throw new BadHashError(
								String.format(
										"new hash (%s) does not match existing hash %s at %s",
										h.getArrayinBase32(),
										eH.getArrayinBase32(), nameHash(i)));
					}
				} else {
					int level = depthOf(i);
					hashes_to_check.get(level).add(i);
					set(i, h);
					remove_upon_failure.add(i);
				}
			}

			for (int level = hashes_to_check.size() - 1; level >= 0; level--) {
				Set<Integer> thisLevel = hashes_to_check.get(level);
				Iterator<Integer> iter = thisLevel.iterator();
				Set<Integer> siblingsTobeRemoved = new HashSet<Integer>();

				while (iter.hasNext()) {
					int i = iter.next();
					iter.remove();
					if (i == 0) {
						// the root, we cannot check the root only
						continue;
					}

					if (siblingsTobeRemoved.contains(i)) {
						continue;
					}

					int siblingnum = sibling(i);
					if (get(siblingnum) == null) {
						// without a sibling, we can't compute a parent, and we
						// can't verify this node
						throw new NotEnoughHashesError(String.format(
								"unable to validate [%d]", i));
					}
					int parentnum = parent(i);
					int leftnum = Math.min(i, siblingnum);
					int rightnum = Math.max(i, siblingnum);
					ByteArray new_parent_hash = new ByteArray(Hash.pair_hash(
							get(leftnum).getBytes(), get(rightnum).getBytes()));
					ByteArray parent = get(parentnum);
					if (parent != null) {
						if (!parent.equals(new_parent_hash)) {
							throw new BadHashError(String.format(
									"h([%d]+[%d]) != h[%d]", leftnum, rightnum,
									parentnum));
						}
					} else {
						set(parentnum, new_parent_hash);
						remove_upon_failure.add(parentnum);
						int parent_level = depthOf(parentnum);
						hashes_to_check.get(parent_level).add(parentnum);
					}
					// our sibling is now as valid as this node
					// thisLevel.remove(siblingnum);
					siblingsTobeRemoved.add(siblingnum);
				}
			}
		} catch (BadHashError e) {
			for (int i : remove_upon_failure) {
				set(i, null);
			}
			throw e;
		} catch (NotEnoughHashesError e) {
			for (int i : remove_upon_failure) {
				set(i, null);
			}
			throw e;
		}

	}

	public int getLeafIndex(int leafNum) {
		return first_leaf_num + leafNum;
	}

	public boolean isFull() {
		for (ByteArray e : this) {
			if (e == null)
				return false;
		}
		return true;
	}

	private int depthOf(int l) {
		return MathUtils.logFloor(l + 1, 2);
	}

	private String nameHash(int i) {
		String name = String.format("[%d of %d]", i, size());
		if (i >= first_leaf_num) {
			int leafnum = i - first_leaf_num;
			int numleaves = size() - first_leaf_num;
			name += String.format("(leaf [%d] of %d)", leafnum, numleaves);
		}
		return name;
	}

	@Override
	public String toString() {
		return "IncompleteHashTree [first_leaf_num=" + first_leaf_num + ", Ht="
				+ dumptoString() + "]";
	}

	private String dumptoString() {
		StringBuilder sb = new StringBuilder();
		for (ByteArray e : this) {
			if (e != null) {
				sb.append(e.getArrayinBase32());
			} else {
				sb.append("null");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static void main(String[] args) throws BadHashError,
			NotEnoughHashesError {

		// TODO: wirte unit tests for HashTrees
		String[] hashes = new String[] {
				"s3b5rulaof2g6r7hjnpb3rpjmxqqy63khn45pjiczch6xvsybnmq",
				"cgyldvb2sg6gxwhn7fvdlkwdrlqv5agymwhbkb7lto4wxqmd57jq",
				"kafr5i4uxy2jgtarnd6emzi5zxl65dcs3bsf52fxslvc42jbxkdq",
				"opatchzrsmgu46hlqdjxj5fwzd32pvanikwidv5bvjgyn3dknqcq",
				"pep66nezv3auc72pmoa46sfhhgfhtah2qexjzqxnb7kpl5khvr5a",
				"aa6emahl63duivhbzatsdrlruaahizd2napsnp5g5migtmteostq",
				"fjv5pzmiyzxrgbh2qnrtssze5svo6643lykqcw6sqvqodanujyua",
				"2hw3cpqqpxd6hf7ehzhotaqnrud3hvywoyetllnqx32ep3x3uoza",
				"ovauu3n52vxrzukfdovefeobnh3sjirp6eeoppyn2vdo2oz7unna",
				"a27myezycjtapv46n26xwy5tf4dkrl7o7qatpwayykzms3itbupq",
				"vkwswucaq53kxpg55g2xqq2ulbusfe5pbbvzonfbmco2xael5ruq",
				"56attapgk4m3ijbisrgckwvcsust5fzf5rou2ppwrep3vwfci2wq",
				"7o2botruntt3ncjappkuan2dnx5qvt3kffohngb43xceiligjqiq",
				"dtqnjupyvbwecbicnkfvzmnttoaiazvdvocl3ifkefk4hj7k2lna",
				"hyofu6is2z2b6ywrovbz5fcdxpw52nhrn2i24ul6dincs2vwniwa" };

		List<ByteArray> l = new ArrayList<ByteArray>();
		for (String hash : hashes) {
			l.add(new ByteArray(Base32.decode(hash)));
		}

		HashTree ht = new HashTree(l);

		Map<Integer, ByteArray> root = new HashMap<Integer, ByteArray>();
		root.put(0, ht.get(0));

		IncompleteHashTree inht = new IncompleteHashTree(hashes.length);
		inht.setHashes(root);

		root.clear();

		root.put(7, ht.get(7));
		root.put(8, ht.get(8));
		root.put(4, ht.get(4));
		root.put(1, ht.get(1));
		root.put(2, ht.get(2));

		inht.setHashes(root);

	}
}

package eg.nileu.cis.nilestore.utils.hashtree;

import java.util.ArrayList;
import java.util.List;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

@SuppressWarnings("serial")
public class HashTree extends CompleteBinaryTree<ByteArray> {

	private final int first_leaf_num;

	public HashTree(List<ByteArray> L) {
		int start = L.size();
		int end = MathUtils.round_pow2(start);

		first_leaf_num = end - 1;
		List<ByteArray> LL = new ArrayList<ByteArray>(L);
		for (int i = start; i < end; i++) {
			byte[] empty_leaf_hash = Hash.empty_leaf_hash(i);
			LL.add(new ByteArray(empty_leaf_hash));
		}
		List<List<ByteArray>> rows = new ArrayList<List<ByteArray>>();
		rows.add(LL);

		while (rows.get(rows.size() - 1).size() != 1) {
			List<ByteArray> last = rows.get(rows.size() - 1);
			rows.add(new ArrayList<ByteArray>());
			for (int i = 0; i < (last.size() / 2); i++) {
				byte[] e = Hash.pair_hash(last.get(2 * i).getBytes(),
						last.get((2 * i) + 1).getBytes());
				rows.get(rows.size() - 1).add(new ByteArray(e));
			}
		}

		for (int i = rows.size() - 1; i >= 0; i--) {
			this.addAll(rows.get(i));
		}
	}

	public List<Integer> neededHashes(int leaf_num, boolean include_leaf) {
		List<Integer> needed = neededFor(first_leaf_num + leaf_num);
		if (include_leaf) {
			needed.add(first_leaf_num + leaf_num);
		}

		return needed;
	}

	public static void main(String[] args) {

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
		String i = "[";
		for (ByteArray e : ht) {
			i += "\"" + Base32.encode(e.getBytes()) + "\",";
		}
		i += "]";

		System.out.println(i);

	}
}

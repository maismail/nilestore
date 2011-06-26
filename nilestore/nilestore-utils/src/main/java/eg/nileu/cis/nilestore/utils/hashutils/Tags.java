package eg.nileu.cis.nilestore.utils.hashutils;

public class Tags {

	public final static int keylen = 16;

	/*
	 * Immutable File tags
	 */

	public final static String STORAGE_INDEX_TAG = "allmydata_immutable_key_to_storage_index_v1";
	public final static String BLOCK_TAG = "allmydata_encoded_subshare_v1";
	public final static String UEB_TAG = "allmydata_uri_extension_v1";
	public final static String PLAINTEXT_TAG = "allmydata_plaintext_v1";
	public final static String CIPHERTEXT_TAG = "allmydata_crypttext_v1";
	public final static String CIPHERTEXT_SEGMENT_TAG = "allmydata_crypttext_segment_v1";
	public final static String PLAINTEXT_SEGMENT_TAG = "allmydata_plaintext_segment_v1";
	public final static String CONVERGENT_ENCRYPTION_TAG = "allmydata_immutable_content_to_key_with_added_secret_v1+";

	public final static String CLIENT_RENEWAL_TAG = "allmydata_client_renewal_secret_v1";
	public final static String CLIENT_CANCEL_TAG = "allmydata_client_cancel_secret_v1";
	public final static String FILE_RENEWAL_TAG = "allmydata_file_renewal_secret_v1";
	public final static String FILE_CANCEL_TAG = "allmydata_file_cancel_secret_v1";
	public final static String BUCKET_RENEWAL_TAG = "allmydata_bucket_renewal_secret_v1";
	public final static String BUCKET_CANCEL_TAG = "allmydata_bucket_cancel_secret_v1";

	// mutable
	public final static String MUTABLE_WRITEKEY_TAG = "allmydata_mutable_privkey_to_writekey_v1";
	public final static String MUTABLE_WRITE_ENABLER_MASTER_TAG = "allmydata_mutable_writekey_to_write_enabler_master_v1";
	public final static String MUTABLE_WRITE_ENABLER_TAG = "allmydata_mutable_write_enabler_master_and_nodeid_to_write_enabler_v1";
	public final static String MUTABLE_PUBKEY_TAG = "allmydata_mutable_pubkey_to_fingerprint_v1";
	public final static String MUTABLE_READKEY_TAG = "allmydata_mutable_writekey_to_readkey_v1";
	public final static String MUTABLE_DATAKEY_TAG = "allmydata_mutable_readkey_to_datakey_v1";
	public final static String MUTABLE_STORAGEINDEX_TAG = "allmydata_mutable_readkey_to_storage_index_v1";

	// dirnodes
	public final static String DIRNODE_CHILD_WRITECAP_TAG = "allmydata_mutable_writekey_and_salt_to_dirnode_child_capkey_v1";
	public final static String DIRNODE_CHILD_SALT_TAG = "allmydata_dirnode_child_rwcap_to_salt_v1";

	public final static String EMPTY_LEAF_HASH = "Merkle tree empty leaf";
	public final static String PAIR_HASH_TAG = "Merkle tree internal node";
}

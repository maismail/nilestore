/**
 * 
 */
package eg.nileu.cis.nilestore.main.progress;

/**
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 *
 */
public interface ProgressListener {
	public void transfered(long bytes,float rate);
}

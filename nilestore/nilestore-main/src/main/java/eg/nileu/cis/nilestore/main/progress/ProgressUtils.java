/**
 * 
 */
package eg.nileu.cis.nilestore.main.progress;


/**
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 *
 */
public class ProgressUtils {

	public static String progressBar(String prefix,int percent, float rate){
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("[");
		
		for(int i=0;i<50;i++){
			int c = percent/2;
			if(i<c){
				sb.append("=");
			}
			else if (i==c){
				sb.append(">");
			}else{
				sb.append(" ");
			}
		}
		sb.append("]");
		sb.append("\t" + percent + "%\t");
		sb.append(String.format("%.2f KB/sec\t",rate));
		return sb.toString();
	}
}

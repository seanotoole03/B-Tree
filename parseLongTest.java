
public class parseLongTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bin1 = "1000000000000000000000000110000000110101100";
		long l1 = Long.parseLong(bin1,2);
		System.out.println(bin1 + " => " + l1);
		
		String bin2 = "1100";
		long l2 = Long.parseLong(bin2, 2);
		System.out.println(bin2 + " => " + l2);
	}

}

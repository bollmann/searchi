package pagerank.phase2;

public class test {

	public static void main(String[] args) {
		
		String abc = "1.0# linkB linkC linkD ";
		String [] t = abc.split("#");
		System.out.println("Pr " + t[0]);
		System.out.println("-- " + t[1].trim());
		
		System.out.println("Len " + t[1].trim().split(" ").length);
		

	}

}

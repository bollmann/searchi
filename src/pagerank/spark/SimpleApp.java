package pagerank.spark;

/* SimpleApp.java */
import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;

public class SimpleApp {
  public static void main(String[] args) {
    String logFile = "/usr/local/spark/spark-1.2.0-bin-hadoop2.4/README.md"; // Should be some file on your system
    SparkConf conf = new SparkConf().setAppName("SimpleApp");
    conf.setSparkHome("/usr/local/spark/spark-1.2.0-bin-hadoop2.4");
    
    String [] jars = new String[1];
    jars[0] = "/home/ishan/Git/Repos/searchi/simpleapp.jar";
    
    conf.setJars(jars);
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaRDD<String> logData = sc.textFile(logFile).cache();

    long numAs = logData.filter(new Function<String, Boolean>() {
      public Boolean call(String s) { return s.contains("a"); }
    }).count();

    long numBs = logData.filter(new Function<String, Boolean>() {
      public Boolean call(String s) { return s.contains("b"); }
    }).count();

    System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
  }
}

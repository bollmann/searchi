package mapreduce;

public interface Job {

  void map(String key, String value, Context context);
  
  void reduce(String key, String values[], Context context);
  
}

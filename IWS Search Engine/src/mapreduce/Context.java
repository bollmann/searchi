package mapreduce;

public interface Context {

  void write(String key, String value);
  
}

package mapreduce.worker;

import java.util.Arrays;

public class ReduceInput {
	private String key;
	private String[] values;
	
	public ReduceInput(String key, String[] values) {
		this.key = key;
		this.values = values;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("key:" + key + " values:" + Arrays.toString(values));
		return sb.toString();
	}
}

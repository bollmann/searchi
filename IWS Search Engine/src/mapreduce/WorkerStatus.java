package mapreduce;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class WorkerStatus {
	private final Logger logger = Logger.getLogger(getClass());
	private String ipAddress;
	private int port;
	private String name;
	private Date lastStatusReceived;
	private String jobClass;
	private int keysRead;
	private int keysWritten;
	private String status;
	private boolean active = true;

	public boolean isActive() {
		Date now = Calendar.getInstance().getTime();
		if (lastStatusReceived != null) {
			if ((now.getTime() - lastStatusReceived.getTime()) / 1000 > 30) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		logger.info("Worker " + getName() + " changing status from "
				+ this.status + " to " + status);
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKeysRead() {
		return keysRead;
	}

	public void setKeysRead(int keysRead) {
		this.keysRead = keysRead;
	}

	public int getKeysWritten() {
		return keysWritten;
	}

	public void setKeysWritten(int keysWritten) {
		this.keysWritten = keysWritten;
	}

	public Date getLastStatusReceived() {
		return lastStatusReceived;
	}

	public void setLastStatusReceived(Date lastStatusReceived) {
		this.lastStatusReceived = lastStatusReceived;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public WorkerStatus() {
		keysRead = 0;
		keysWritten = 0;
	}

	public String toParameters() {
		StringBuilder sb = new StringBuilder();
		sb.append("job=" + jobClass);
		sb.append("&keysWritten=" + keysWritten);
		sb.append("&keysRead=" + keysRead);
		sb.append("&port=" + port);
		sb.append("&status=" + status);
		return sb.toString();
	}

	public String encodeForMap() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName() + "=" + getIpAddress() + ":" + getPort());
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Worker=> name:" + name + " job:" + jobClass + " ipAddress:"
				+ ipAddress + " port:" + port + " keysRead:" + keysRead
				+ " keysWritten:" + keysWritten + " status:" + status
				+ " active:" + active);
		return sb.toString();
	}
}

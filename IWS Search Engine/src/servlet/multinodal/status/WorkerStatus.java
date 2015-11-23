package servlet.multinodal.status;

import java.util.Date;

import org.apache.log4j.Logger;

public class WorkerStatus {
	private final Logger logger = Logger.getLogger(getClass());
	private String ipAddress;
	private int port;
	private Date lastStatusReceived;
	private int urlsProcessed;
	private String status;
	private boolean active = true;
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
	public Date getLastStatusReceived() {
		return lastStatusReceived;
	}
	public void setLastStatusReceived(Date lastStatusReceived) {
		this.lastStatusReceived = lastStatusReceived;
	}
	public int getUrlProcessed() {
		return urlsProcessed;
	}
	public void setUrlProcessed(int urlProcessed) {
		this.urlsProcessed = urlProcessed;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Logger getLogger() {
		return logger;
	}

	public String toParameters() {
		StringBuilder sb = new StringBuilder();
		sb.append("urlsProcessed=" + urlsProcessed);
		sb.append("&port=" + port);
		sb.append("&status=" + status);
		return sb.toString();
	}
}

package org.openhab.binding.loxone.integration;

import org.apache.commons.lang.StringUtils;

public class LoxoneHost {
	private final String name;
	private String host;
	private int port = 80;
	private boolean ssl;
	private String username = "admin";
	private String password = "admin";

	public LoxoneHost(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isValid() {
		return !StringUtils.isEmpty(host) && port > 0 && port < 65000
				&& !StringUtils.isEmpty(username)
				&& !StringUtils.isEmpty(password);
	}

	@Override
	public String toString() {
		return "LoxoneHost [name=" + name + ", host=" + host + ", port=" + port
				+ ", ssl=" + ssl + ", username=" + username + ", password=******"
			    + "]";
	}
	
	

}

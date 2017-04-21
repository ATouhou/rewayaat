package com.rewayaat.web.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:localdev.properties")
public class ClientProvider implements EnvironmentAware {

	public static String INDEX = "rewayaat";
	public static String TYPE = "rewayaat";
	private static ClientProvider instance = null;
	private static Object lock = new Object();
	private Client client;
	public static String host;
	public static int port;

	public static ClientProvider instance() {

		if (instance == null) {
			synchronized (lock) {
				if (null == instance) {
					instance = new ClientProvider();
				}
			}
		}
		return instance;
	}

	public void prepareClient() throws UnknownHostException {
		Settings settings = Settings.builder()
		        .put("client.transport.sniff", true).build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		this.client = client;
	}

	public void closeNode() {
		client.close();
	}

	public Client getClient() throws UnknownHostException {
		if (client == null) {
			prepareClient();
		}
		return client;
	}

	public void printThis() {
		System.out.println(this);
	}

	@Override
	public void setEnvironment(final Environment environment) {
		port = Integer.parseInt(environment.getProperty("spring.data.elasticsearch.properties.port"));
		host = environment.getProperty("spring.data.elasticsearch.properties.host");
	}

}

package slimer.utils.ES;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author kelier
 *redis configuration class
 *use RedisProperties that defined by Spring boot ,read config properties in the application.yml
 */
@EnableAutoConfiguration
public class ElasticSearchConfiguration {
	ElasticsearchProperties elasticsearchProperties;
	Client client;
	

	@Value("${elasticsearch.clustername}")
	public String clusterName = "elasticsearch";

	@Value("${elasticsearch.clusterhosts}")
	public String clusterNodes;

	@Value("${elasticsearch.index}")
	public String index;
	
	@Value("${elasticsearch.type}")
	public String type;

	@Bean
	public Client initClient()throws IOException {
		Map<String, String> settingMap = new HashMap<String, String>();
		settingMap.put("cluster.name",elasticsearchProperties.getClusterName());
		Settings settings = ImmutableSettings.settingsBuilder().put(settingMap).build();
		this.client = new TransportClient(settings);
		
		String clusterHosts =elasticsearchProperties.getClusterNodes();
		String[] hostsSplit = clusterHosts.split(",");
		if (hostsSplit != null) {
			for (String hostInfo : hostsSplit) {
				int flgPos = hostInfo.indexOf(":");
				if (flgPos > -1) {
					String host = hostInfo.substring(0, flgPos).trim();
					int port = Integer.parseInt(hostInfo.substring(flgPos + 1).trim());
					((TransportClient) this.client).addTransportAddress(new InetSocketTransportAddress(host, port));
				}
			}
		}
		
		
		return client;
	}
	public String getClusterNodes() {
		return this.clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}
}

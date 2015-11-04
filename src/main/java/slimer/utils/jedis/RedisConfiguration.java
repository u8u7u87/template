package slimer.utils.jedis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPoolConfig;

@EnableAutoConfiguration
public class RedisConfiguration {

	@Value("${spring.redis.sentinel.master}")
	private String sentinelMasterName = null;

	@Value("${spring.redis.sentinel.nodes}")
	private String hostAndPorts = null;

	@Value("${jedis.pool.maxidl}")
	private Integer maxIdle = null;

	@Value("${jedis.pool.minidl}")
	private Integer minIdl = null;

	@Value("${jedis.pool.maxtotal}")
	private Integer maxTotal = null;

	@Bean
	public SlimerJedisConnectionFactory connectionFactory() {
		Set<String> sentinelHostAndPorts = StringUtils.commaDelimitedListToSet(this.getHostAndPorts());
		RedisSentinelConfiguration sc = new RedisSentinelConfiguration(this.getSentinelMasterName(), sentinelHostAndPorts);
		JedisPoolConfig pc = this.createPoolConfig();
		SlimerJedisConnectionFactory factory = new SlimerJedisConnectionFactory(sc, pc);

		return factory;
	}

	private JedisPoolConfig createPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(this.getMaxIdle());
		poolConfig.setMinIdle(this.getMinIdl());
		poolConfig.setMaxTotal(this.getMaxTotal());

		return poolConfig;
	}

	private String getSentinelMasterName() {
		return this.sentinelMasterName;
	}

	private String getHostAndPorts() {
		return this.hostAndPorts;
	}

	public Integer getMaxIdle() {
		return this.maxIdle;
	}

	public Integer getMinIdl() {
		return this.minIdl;
	}

	public Integer getMaxTotal() {
		return this.maxTotal;
	}

}
package slimer.utils.jedis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
public class RedisConfiguration {
	RedisProperties redisProperties;
	

	@Value("${jedis.pool.maxidl}")
	private Integer maxIdle = null;

	@Value("${jedis.pool.minidl}")
	private Integer minIdl = null;

	@Value("${jedis.pool.maxtotal}")
	private Integer maxTotal = null;

	@Bean
	public SlimerJedisConnectionFactory connectionFactory() {
		Set<String> sentinelHostAndPorts = StringUtils.commaDelimitedListToSet(redisProperties.getSentinel().getNodes());
		RedisSentinelConfiguration sc = new RedisSentinelConfiguration(redisProperties.getSentinel().getMaster(), sentinelHostAndPorts);
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

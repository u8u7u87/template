package slimer.utils.jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import slimer.utils.SerializeUtil;

/**
 * CacheManager
 *
 *
 */
@SuppressWarnings({ "cast" })
@Service
public class SlimerCacheManager {
	private static final Logger logger = LoggerFactory.getLogger(SlimerCacheManager.class);

	@Resource
	private volatile CacheManager cacheManager;

	@Autowired
	private SlimerJedisConnectionFactory jedisConnectionFactory;

	private Jedis jedis;

	/**
	 * constructor
	 */
	public SlimerCacheManager() {
	}

	/**
	 * get RedisCacheManager
	 *
	 * @return
	 */
	public CacheManager getCacheManager() {
		if (this.cacheManager == null) {
			SlimerCacheManager.logger.error("未注入RedisCacheManager,请检查spring配置.");
		}
		return this.cacheManager;
	}

	/**
	 * 获取指定名称、指定key的缓存值
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 * @param key
	 *            参数：缓存key
	 * @return
	 */
	public Object get(String cacheName, Object key) {
		try {
			ValueWrapper value = this.cacheManager.getCache(cacheName).get(key);
			return value == null ? null : value.get();
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager get method error:\n" + e.getMessage());
			return null;
		}
	}

	/**
	 * 往RedisCache中put指定名称、指定key的缓存value
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 * @param key
	 *            参数：缓存key
	 * @param value
	 *            参数：缓存值
	 */
	public void put(String cacheName, Object key, Object value) {
		try {
			/*if (value.getClass().getSuperclass().equals(BizModel.class)) {
				BizModel obj = (BizModel) value;
				if (!cacheName.endsWith(obj.getTable().getName())) {
					throw MyErrorEnum.customError.getMyException("不能乱放啊，这个value的tableName和cacheName不匹配");
				}
			}*/
			this.cacheManager.getCache(cacheName).put(key, value);
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager put method error:\n" + e.getMessage());
		}
	}

	/**
	 * 往RedisCache中put指定名称、指定key的缓存value
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 * @param key
	 *            参数：缓存key
	 * @param value
	 *            参数：缓存值
	 */
	public void putIfAbsent(String cacheName, Object key, Object value) {
		try {
			this.cacheManager.getCache(cacheName).putIfAbsent(key, value);
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager put method error:\n" + e.getMessage());
		}
	}

	/**
	 * 指定缓存的过期时间
	 *
	 * @param cacheName
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void setExpires(String cacheName, String key, String value, int seconds) {
		// //long time = new Date().getTime();
		Jedis jedis = this.getNewJedis();
		try {
			String expiresKey = cacheName.concat("~").concat(key);
			jedis.setex(expiresKey, seconds, value);
			// //jedis.set(expiresKey, value);
			// //jedis.expireAt(expiresKey, time + expires);
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager setExpires method error:\n" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 *
	 * @param cacheName
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void setExpires(String cacheName, String key, Object value, int seconds) {
		Jedis jedis = this.getNewJedis();
		try {
			String expiresKey = cacheName.concat("~").concat(key);
			jedis.setex(expiresKey.getBytes(), seconds, SerializeUtil.serialize(value));
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager setExpires method error:\n" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 *
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public Object getExpiresObject(String cacheName, String key) {

		Object result = null;
		Jedis jedis = this.getNewJedis();
		try {
			String expiresKey = cacheName.concat("~").concat(key);
			byte[] objBytes = jedis.get(expiresKey.getBytes());
			result = SerializeUtil.unserialize(objBytes);
		} catch (Exception e) {
			result = null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}

		}

		return result;

	}

	/**
	 *
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public Object getExpires(String cacheName, String key) {
		String result = "";
		Jedis jedis = this.getNewJedis();
		try {
			String expiresKey = cacheName.concat("~").concat(key);
			result = jedis.get(expiresKey);
		} catch (Exception e) {
			result = "";
		} finally {
			if (jedis != null) {
				jedis.close();
			}

		}
		return result;
	}

	// public List<Object> mGet(String cacheName, List<Object> keys){
	// String expiresKey = cacheName.concat("~").concat(key);
	// result = this.getJedis().g.get(expiresKey);
	// }

	/**
	 * 删除指定名称缓存下指定key的值
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 * @param key
	 *            参数：key
	 */
	public void remove(String cacheName, String key) {
		Jedis jedis = this.getNewJedis();
		try {
			key = cacheName.concat("~").concat(key);
			Set<String> set = jedis.keys(key);
			String[] kk = new String[set.size()];
			jedis.del(set.toArray(kk));

		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager remove method error:\n" + e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void del(String key) throws Exception {
		Jedis jedis = this.getNewJedis();
		try {
			Set<String> set = jedis.keys(key);
			String[] kk = new String[set.size()];
			jedis.del(set.toArray(kk));
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 删除指定名称的缓存
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 */
	public void removeAll(String cacheName) {
		try {
			this.cacheManager.getCache(cacheName).clear();
		} catch (Exception e) {
			SlimerCacheManager.logger.error("OtsCacheManager removeAll method error:\n" + e.getMessage());
		}
	}

	// lpush
	public void lpush(String queneName, String str) {
		Jedis jedis = this.getNewJedis();
		try {
			jedis.lpush(queneName, str);
		} catch (Exception e) {
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	// rpop
	public String rpop(String queneName) {
		Jedis jedis = this.getNewJedis();
		String value = null;
		try {
			value = jedis.rpop(queneName);
		} catch (Exception e) {
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	public boolean isExistKey(String key) {
		Jedis jedis = this.getNewJedis();
		try {
			if (jedis.exists(key)) {
				return true;
			}
		} catch (Exception e) {
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	public String hmset(String key, Map<String, String> map) throws Exception {
		Jedis jedis = this.getNewJedis();
		try {
			jedis.hmset(key, map);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return "OK";
	}

	public Map<String, String> hgetAll(String key) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		Jedis jedis = this.getNewJedis();
		try {
			map = jedis.hgetAll(key);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return map;
	}
	
	public String set(String key, String value) throws Exception {
		Jedis jedis = this.getNewJedis();
		try {
			jedis.set(key, value);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return "OK";
	}
	
	public String getbykey(String key) throws Exception{
		String s = null;
		Jedis jedis = this.getNewJedis();
		try {
			s = jedis.get(key);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return s;
	}

	/**
	 * 得到指定名称的所有缓存信息
	 *
	 * @param cacheName
	 *            参数：缓存名称
	 * @return Cache 返回值
	 */
	public Cache readAll(String cacheName) {
		return this.cacheManager.getCache(cacheName);
	}

	public Jedis getNewJedis() {
		return this.jedis = this.jedisConnectionFactory.getJedis();
	}

}

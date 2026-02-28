package com.onlinejudge.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import jakarta.annotation.PreDestroy;

@Component
public class RedisRateLimiter {

    private final JedisPool jedisPool;
    private final int maxRequests;
    private final int windowSeconds;

    /**
     * Constructor Injection cho RedisRateLimiter.
     * Spring sẽ tự động inject các giá trị từ application.properties/yml vào các tham số này.
     */
    public RedisRateLimiter(
            @Value("${spring.data.redis.host}") String redisHost,
            @Value("${spring.data.redis.port}") int redisPort,
            @Value("${rate.limit.max-requests}") int maxRequests,
            @Value("${rate.limit.window-seconds}") int windowSeconds) {
        
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
        
        // Khởi tạo JedisPool ngay tại constructor
        // Lưu ý: Trong thực tế production, bạn có thể muốn inject JedisPool bean thay vì tự new như thế này
        // để tận dụng cấu hình connection pool chi tiết hơn.
        this.jedisPool = new JedisPool(redisHost, redisPort);
    }

    /**
     * Phương thức kiểm tra xem request có được phép hay không.
     * Sử dụng cơ chế sliding window đơn giản qua INCR và EXPIRE.
     *
     * @param identifier Định danh người dùng/IP
     * @return true nếu được phép, false nếu vượt quá giới hạn
     */
    public boolean isAllowed(String identifier) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limit:stream:" + identifier;
            
            // Tăng giá trị counter lên 1
            long currentCount = jedis.incr(key);

            // Nếu đây là request đầu tiên trong cửa sổ thời gian, đặt thời gian hết hạn
            if (currentCount == 1) {
                jedis.expire(key, windowSeconds);
            }

            return currentCount <= maxRequests;
        } catch (Exception e) {
            // Fail-open: Nếu Redis lỗi, cho phép request đi qua để tránh làm gián đoạn dịch vụ
            System.err.println("Error checking rate limit: " + e.getMessage());
            return true;
        }
    }

    /**
     * Lấy số lượng request còn lại trong cửa sổ thời gian hiện tại.
     */
    public long getRemainingRequests(String identifier) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limit:stream:" + identifier;
            String countStr = jedis.get(key);
            
            if (countStr == null) {
                return maxRequests;
            }
            
            long currentCount = Long.parseLong(countStr);
            return Math.max(0, maxRequests - currentCount);
        } catch (Exception e) {
            // Nếu lỗi, trả về max để frontend không bị block sai
            return maxRequests;
        }
    }

    /**
     * Lấy thời gian (giây) còn lại trước khi counter được reset.
     */
    public long getResetTime(String identifier) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limit:stream:" + identifier;
            Long ttl = jedis.ttl(key);
            
            // TTL trả về -1 nếu key tồn tại nhưng không có expire, -2 nếu key không tồn tại
            if (ttl == null || ttl < 0) {
                return 0;
            }
            return ttl;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Đóng JedisPool khi Spring context bị shutdown để giải phóng tài nguyên.
     */
    @PreDestroy
    public void cleanup() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}
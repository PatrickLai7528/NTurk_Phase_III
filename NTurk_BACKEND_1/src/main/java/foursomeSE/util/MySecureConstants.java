package foursomeSE.util;

// 在Restfulcontroller里面都用了
public interface MySecureConstants {
    String HEADER = "Authorization";
    String SECRET = "par amour";
    String TOKEN_PREFIX = "Bearer ";
    long EXPIRATION_TIME = 864_000_000;
}

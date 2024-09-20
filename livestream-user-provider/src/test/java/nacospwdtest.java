import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author dream
 * @data 2024/9/12 下午8:57
 * @descripation
 */
public class nacospwdtest {
        public static void main(String[] args) {
            System.out.println(new BCryptPasswordEncoder().encode("live"));
        }
}

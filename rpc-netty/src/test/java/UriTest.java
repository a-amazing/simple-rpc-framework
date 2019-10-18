import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author:wangyi
 * @Date:2019/10/18
 */
public class UriTest {

    @Test
    public void testUri() throws URISyntaxException {
        URI uri = new URI("jdbc:mysql://localhost:3306/rpcdemo?user=root&password=root");
        System.out.println(uri.getScheme());
//        System.out.println(uri.getAuthority());
//        System.out.println(uri.getUserInfo());
//        System.out.println(uri.getPath());
//        System.out.println(uri.getRawPath());
//        System.out.println(uri.getRawFragment());
//        System.out.println(uri.getFragment());
        System.out.println(uri.getSchemeSpecificPart());
//        System.out.println(uri.getQuery());
    }

}

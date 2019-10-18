import com.github.liyue2008.rpc.nameservice.JDNCNameService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author:wangyi
 * @Date:2019/10/18
 */
public class JDBCNameServiceTest {
    private JDNCNameService nameService;

    @Before
    public void before() throws URISyntaxException {
        nameService = new JDNCNameService();
        nameService.connect(new URI("jdbc:mysql://127.0.0.1:3306"));
    }

    @Test
    public void testInsert() throws URISyntaxException, IOException {
        URI uri = new URI("rpc://localhost:9999");
        nameService.registerService("com.wangyi.rpcdemo.DemoService",uri);
    }

}

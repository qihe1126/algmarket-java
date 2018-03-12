import com.algmarket.AlgMarket;
import com.algmarket.AlgMarketClient;
import com.algmarket.algm.Algm;
import com.algmarket.algm.AlgmResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class AlgmTest {

    private String key;

    @Before
    public void setup() {
        key = System.getenv("ALGMARKET_KEY");
        Assume.assumeNotNull(key);
    }

    @Test
    public void algorithmPipeJson() throws Exception {
        String input = "我是测试人员";
        AlgMarketClient client = AlgMarket.client(key);
        Algm algm = client.algm("qihe/TestJava1/0.0.4");
        AlgmResponse result = algm.call(input);
        System.out.println(result.asJsonString());
    }



}

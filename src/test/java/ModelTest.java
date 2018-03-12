import com.algmarket.AlgMarket;
import com.algmarket.AlgMarketClient;
import com.algmarket.model.ModelDirectory;
import com.algmarket.model.ModelFile;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ModelTest {

    private String key;

    @Before
    public void setup() {
        key = System.getenv("ALGMARKET_KEY");
        Assume.assumeNotNull(key);
    }

    @Test
    public void createDir() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory robots = client.dirModel("model://.my/model_set");
        robots.create();

    }

    @Test
    public void dirIsExists() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory modelDirectory = client.dirModel("model://.my/model_set");
        System.out.println(modelDirectory.exists());
    }

    @Test
    public void deleteFileSet() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        client.dirModel("model://.my/model_set").delete();

    }

    @Test
    public void deleteFileSetForce() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        // 强制删除文件夹集下有文件情况
        client.dirModel("model://.my/model_set").delete(true);
    }

    @Test
    public void uploadFile() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory robots = client.dirModel("model://qihe/model_set");
        File file = new File("D:/1234.jpg");
        robots.putFile(file);
    }

    @Test
    public void uploadFileContent() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory robots = client.dirModel("model://qihe/model_set");
        robots.file("Optimus_Prime.txt").put("Leader of the Autobots");
    }

    @Test
    public void uploadFileByte() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory robots = client.dirModel("model://.my/model_set");
        robots.file("zhang111.txt").put(new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20});
    }

    @Test
    public void deleteFile() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        client.modelfile("model://.my/model_set/1234.jpg").delete();
    }

    @Test
    public void fileDownload() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        ModelFile algo = client.modelfile("model://.my/model_set/1234.jpg");
        File file = algo.getFile();
        System.out.println(file.getPath());
    }

    @Test
    public void listModelSet() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory myRoot = client.dirModel("model://.my");
        for (ModelDirectory dir : myRoot.dirs()) {
            System.out.println("Directory " + dir.toString());
        }
    }

    @Test
    public void listModelFile() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        ModelDirectory myRoot = client.dirModel("model://.my/model_set");
        for (ModelFile file : myRoot.files()) {
            System.out.println("File " + file.toString() + " at URL: " + file.path);
        }
    }

}

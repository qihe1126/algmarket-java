import com.algmarket.AlgMarket;
import com.algmarket.AlgMarketClient;
import com.algmarket.data.DataDirectory;
import com.algmarket.data.DataFile;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DataTest {

    private String key;

    @Before
    public void setup() {
        key = System.getenv("ALGMARKET_KEY");
        Assume.assumeNotNull(key);
    }

    @Test
    public void createDir() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory robots = client.dir("data://.my/dataSetOne");
        robots.create();
    }
    @Test
    public void dirIsExists() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory dataDirectory = client.dir("data://.my/dataSetOne2");
        System.out.println(dataDirectory.exists());
    }
    @Test
    public void deleteFileSet() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        client.dir("data://.my/dataSetOne").delete();
    }

    @Test
    public void deleteFileSetForce() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        // 强制删除文件夹集下有文件情况
        client.dir("data://.my/dataSetOne").delete(true);
    }

    @Test
    public void uploadFile() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory robots = client.dir("data://.my/dataSetOne");
        File file = new File("D:/1234.jpg");
        robots.putFile(file);
    }

    @Test
    public void uploadFileContent() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory robots = client.dir("data://.my/dataSetOne");
        robots.file("Optimus_Prime.txt").put("Leader of the Autobots");
    }

    @Test
    public void uploadFileByte() throws Exception {

        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory robots = client.dir("data://.my/dataSetOne");
        robots.file("zhang111.txt").put(new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20});
    }

    @Test
    public void fileDownload() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        DataFile algo = client.file("data://.my/dataSetOne/1234.jpg");
        File file = algo.getFile();
        System.out.println(file.getPath());
    }

    @Test
    public void deleteFile() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        client.file("data://.my/dataSetOne/1234.jpg").delete();
    }

    @Test
    public void listDataSet() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory myRoot = client.dir("data://.my");
        for (DataDirectory dir : myRoot.dirs()) {
            System.out.println("Directory " + dir.toString() + " at URL ,fileName:" + dir.getName());
        }
    }

    @Test
    public void listDataFile() throws Exception {
        AlgMarketClient client = AlgMarket.client(key);
        DataDirectory myRoot = client.dir("data://.my/dataSetOne");

        myRoot.files().
        for (DataFile file : myRoot.files()) {
            System.out.println("File " + file.toString() + " at URL: " + file.path);
        }
    }


}

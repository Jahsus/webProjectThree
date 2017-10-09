package promotest;

import org.junit.*;


public class PromoTest {
    @BeforeClass
    public static void setUpClass() throws Exception {

        Core.setup();
    }

    @AfterClass
        public static void tearDownClass () throws Exception {
            Core.quit();
        }

//
//    @After
//    public void tearDown() throws Exception {
//        Core.quit();
//    }

    @Test
    public void main() throws Exception {
        Core.loadMainPage();
        Core.slidePromo(5);
        Core.clickWatchLatter();
        Core.checkWatchLatterButton();
        Core.checkWatchLatterBlock();

    }

}







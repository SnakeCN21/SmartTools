import com.snake.smarttools.SmarttoolsApplication;
import com.snake.smarttools.controller.PorntoolsController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SmarttoolsApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class PorntoolsTest {
    @Autowired
    private PorntoolsController porntoolsController;

    @Test
    public void test() {
        porntoolsController.taskStart();
    }
}

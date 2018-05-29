package foursomeSE;

import foursomeSE.entity.user.Requester;
import foursomeSE.entity.user.Worker;
import foursomeSE.jpa.user.RequesterJPA;
import foursomeSE.jpa.user.WorkerJPA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static foursomeSE.service.user.UserUtils.userByUsername;
import static foursomeSE.util.DataSupplier.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DBTest {
    @Autowired
    private WorkerJPA workerJPA;

    @Autowired
    private RequesterJPA requesterJPA;

    @Before
    public void doBeforeClass() {
    }

    @After
    public void doAfterClass() {
    }


    // 其实这两个一点也不像是测试
    @Test
    public void testInsertWorkers() {
//        List<Worker> workers = mockWorkers();

//        workerJPA.saveAll(workers);

        System.out.println("pre print");
        System.out.println(workerJPA.findByEmailAddress("worker1@ex.com"));
    }

    @Test
    public void testRequesterJPA() {
//        List<Requester> requesters = mockRequesters();

//        requesterJPA.saveAll(requesters);
        String username = "requester1@ex.com";
        Requester r1 = userByUsername(requesterJPA, username);
        r1.setNickname(r1.getNickname() + "1");
        requesterJPA.save(r1);
    }
}

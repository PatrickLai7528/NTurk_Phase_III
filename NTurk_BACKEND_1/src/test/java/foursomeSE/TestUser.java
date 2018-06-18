/**
 * 这个是很久以前的测试类，不一定能跑得起来。
 * */

//package foursomeSE;
//
//import foursomeSE.controller.TaskController;
//import foursomeSE.entity.communicate.jwt.JwtAuthenticationRequest;
//import foursomeSE.entity.communicate.jwt.JwtAuthenticationResponse;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@SpringBootTest
//public class TestUser {
//
//    @Autowired
//    private TaskController taskController;
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @BeforeClass
//    public static void su(){
//        System.out.println("beforeClass111");
//    }
//
//    @Before
//    public void b() {
//        System.out.println("before111");
//    }
//
//    @Test
//    public void greetingShouldReturnDefaultMessage() throws Exception {
////        (homeController.greeting().contains("hello world"));
//        assertThat(new Integer(1)).isEqualTo(1);
//
////        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/helloWorld",
////                String.class)).contains("Hello World");
//    }
//
//    @Test
//    public void test2() throws Exception {
////        (homeController.greeting().contains("hello world"));
//        assertThat(new Integer(2)).isEqualTo(2);
//
////        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/helloWorld",
////                String.class)).contains("Hello World");
//    }
//
////    @Test
////    public void integrationTest() {
////        JwtAuthenticationRequest signin = new JwtAuthenticationRequest("test1", "test1");
////        JwtAuthenticationResponse response = restTemplate.postForObject("http://localhost:" + port + "/auth", signin, JwtAuthenticationResponse.class);
////
////        System.out.println("integration111:  " + response.getUserId());
//////            assertThat(x).isEqualTo(false);
////
////    }
//
//    @Test
//    public void add() {
//
//    }
//}

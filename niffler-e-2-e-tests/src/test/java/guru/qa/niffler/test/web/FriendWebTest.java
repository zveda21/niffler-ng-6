package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import org.hibernate.usertype.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
public class FriendWebTest {

    @Test
    void testWithEmptyUser0(@UsersQueueExtension.UserType (empty = true)UsersQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("user -- " + user);
    }

    @Test
    void testWithEmptyUser1(@UsersQueueExtension.UserType (empty = true)UsersQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("user -- " + user);
    }
    @Test
    void testWithEmptyUser2(@UsersQueueExtension.UserType (empty = false)UsersQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("user -- " + user);
    }

    @Test
    void testWithEmptyUser3(@UsersQueueExtension.UserType (empty = false)UsersQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("user -- " + user);
    }

}

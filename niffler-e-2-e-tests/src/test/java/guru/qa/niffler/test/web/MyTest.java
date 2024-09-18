package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ListInject;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.ListInjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
@ExtendWith(ListInjectionExtension.class)

public class MyTest {

    @Test
    public void test1(@ListInject()List<String> list){
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        System.out.println("hello");

    }
}

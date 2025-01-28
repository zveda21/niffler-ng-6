package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Sql(scripts = "/currentUserShouldBeReturned.sql")
  @Test
  void currentUserShouldBeReturned() throws Exception {
    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"))
        .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
        .andExpect(jsonPath("$.currency").value("RUB"))
        .andExpect(jsonPath("$.photo").isNotEmpty())
        .andExpect(jsonPath("$.photoSmall").isNotEmpty());
  }

  @Test
  void allUsersEndpoint() throws Exception {
    UserEntity firstUser = new UserEntity();
    firstUser.setUsername("dima");
    firstUser.setCurrency(CurrencyValues.RUB);
    usersRepository.save(firstUser);

    UserEntity secondUser = new UserEntity();
    secondUser.setUsername("anna");
    secondUser.setCurrency(CurrencyValues.USD);
    usersRepository.save(secondUser);

    mockMvc.perform(get("/internal/users/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("anna"))
            .andExpect(jsonPath("$[1].username").value("dima"));
  }

  @Test
  void updateEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("testUser");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(userDataEntity);

    mockMvc.perform(post("/internal/users/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"userNameRng\", \"currency\":\"EUR\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currency").value("EUR"));
  }
}
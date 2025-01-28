package guru.qa.niffler.service;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.model.FriendState.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private UserService testedObject;

  private final UUID mainTestUserUuid = UUID.randomUUID();
  private final String mainTestUserName = "dima";
  private UserEntity mainTestUser;

  private final UUID secondTestUserUuid = UUID.randomUUID();
  private final String secondTestUserName = "barsik";
  private UserEntity secondTestUser;

  private final UUID thirdTestUserUuid = UUID.randomUUID();
  private final String thirdTestUserName = "emma";
  private UserEntity thirdTestUser;


  private final String notExistingUser = "not_existing_user";


  @BeforeEach
  void init() {
    mainTestUser = new UserEntity();
    mainTestUser.setId(mainTestUserUuid);
    mainTestUser.setUsername(mainTestUserName);
    mainTestUser.setCurrency(CurrencyValues.RUB);

    secondTestUser = new UserEntity();
    secondTestUser.setId(secondTestUserUuid);
    secondTestUser.setUsername(secondTestUserName);
    secondTestUser.setCurrency(CurrencyValues.RUB);
    secondTestUser.setPhoto("large.jpg".getBytes(StandardCharsets.UTF_8));
    secondTestUser.setPhotoSmall("small.jpg".getBytes(StandardCharsets.UTF_8));

    thirdTestUser = new UserEntity();
    thirdTestUser.setId(thirdTestUserUuid);
    thirdTestUser.setUsername(thirdTestUserName);
    thirdTestUser.setCurrency(CurrencyValues.RUB);
    thirdTestUser.setPhoto("large1.jpg".getBytes(StandardCharsets.UTF_8));
    thirdTestUser.setPhotoSmall("small1.jpg".getBytes(StandardCharsets.UTF_8));
  }


  @ValueSource(strings = {
      "data:image/png;base64,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",
      ""
  })
  @ParameterizedTest
  void userShouldBeUpdated(String photo, @Mock UserRepository userRepository) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));

    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    testedObject = new UserService(userRepository);

    final String photoForTest = photo.isEmpty() ? null : photo;

    final UserJson toBeUpdated = new UserJson(
        null,
        mainTestUserName,
        "Test",
        "TestSurname",
        "Test TestSurname",
        CurrencyValues.USD,
        photoForTest,
        null,
        null
    );
    final UserJson result = testedObject.update(toBeUpdated);
    assertEquals(mainTestUserUuid, result.id());
    assertEquals("Test TestSurname", result.fullname());
    assertEquals(CurrencyValues.USD, result.currency());
    assertEquals(photoForTest, result.photo());

    verify(userRepository, times(1)).save(any(UserEntity.class));
  }

  @Test
  void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound(@Mock UserRepository userRepository) {
    when(userRepository.findByUsername(eq(notExistingUser)))
        .thenReturn(Optional.empty());

    testedObject = new UserService(userRepository);

    final NotFoundException exception = assertThrows(NotFoundException.class,
        () -> testedObject.getRequiredUser(notExistingUser));
    assertEquals(
        "Can`t find user by username: '" + notExistingUser + "'",
        exception.getMessage()
    );
  }

  @Test
  void allUsersShouldReturnCorrectUsersList(@Mock UserRepository userRepository) {
    when(userRepository.findByUsernameNot(eq(mainTestUserName)))
        .thenReturn(getMockUsersMappingFromDb());

    testedObject = new UserService(userRepository);

    final List<UserJsonBulk> users = testedObject.allUsers(mainTestUserName, null);
    assertEquals(2, users.size());
    final UserJsonBulk invitation = users.stream()
        .filter(u -> u.friendState() == INVITE_SENT)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));

    final UserJsonBulk friend = users.stream()
        .filter(u -> u.friendState() == null)
        .findFirst()
        .orElseThrow(() -> new AssertionError("user without status not found"));


    assertEquals(secondTestUserName, invitation.username());
    assertEquals(thirdTestUserName, friend.username());
  }

  @Test
  void testAllUsersReturnsSmallPhotoOnly(@Mock UserRepository userRepository) {
    when(userRepository.findByUsernameNot(eq(mainTestUserName)))
            .thenReturn(getMockUsersMappingFromDb());

    testedObject = new UserService(userRepository);

    final List<UserJsonBulk> users = testedObject.allUsers(mainTestUserName, null);

    // Asserts
    assertNotNull(users);
    assertEquals(2, users.size());
    assertEquals("small.jpg", users.get(0).photoSmall()); // Small photo should be used
    assertEquals("small1.jpg", users.get(1).photoSmall()); // Small photo should be used
  }

    @Test
    void testGetCurrentUserWhenUserNotFoundInDB(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(Optional.empty());

        testedObject = new UserService(userRepository);

        UserJson userJson = testedObject.getCurrentUser(mainTestUserName);

        // Asserts
        assertNotNull(userJson);
        assertNull(userJson.id());
        assertEquals(mainTestUser.getUsername(), userJson.username());
    }

    @Test
    void testSearchUsersCallsRepositoryWithCorrectQuery(@Mock UserRepository userRepository) {
        String searchQuery = "john";
        UserWithStatus mockUser = new UserWithStatus(
                UUID.randomUUID(),
                "mockUser",
                CurrencyValues.USD,
                "search query for mock user",
                "small3.jpg".getBytes(StandardCharsets.UTF_8),
                FriendshipStatus.PENDING
        );

        when(userRepository.findByUsernameNot(eq(mainTestUserName), eq(searchQuery)))
                .thenReturn(List.of(mockUser));

      testedObject = new UserService(userRepository);

      final List<UserJsonBulk> users = testedObject.allUsers(mainTestUserName, searchQuery);

      // Asserts
      assertNotNull(users, "The list of users should not be null.");
      assertEquals(1, users.size(), "The returned list should contain exactly 1 user.");
      assertEquals(mockUser.username(), users.get(0).username(), "The first user should match the mock user's username.");
      verify(userRepository, times(1)).findByUsernameNot(eq(mainTestUserName), eq(searchQuery));
    }

  @Test
  void testAddFriend(@Mock UserRepository userRepository) {
    when(userRepository.findByUsername(eq(mainTestUserName))).thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName))).thenReturn(Optional.of(secondTestUser));

    testedObject = new UserService(userRepository);

    UserJson result = testedObject.createFriendshipRequest(mainTestUserName, secondTestUserName);

    // Asserts
    assertEquals(secondTestUserName, result.username());
    assertEquals(INVITE_SENT, result.friendState());
    verify(userRepository, times(1)).save(mainTestUser);
  }

  private List<UserWithStatus> getMockUsersMappingFromDb() {
    return List.of(
        new UserWithStatus(
            secondTestUser.getId(),
            secondTestUser.getUsername(),
            secondTestUser.getCurrency(),
            secondTestUser.getFullname(),
            secondTestUser.getPhotoSmall(),
            FriendshipStatus.PENDING
        ),
        new UserWithStatus(
            thirdTestUser.getId(),
            thirdTestUser.getUsername(),
            thirdTestUser.getCurrency(),
            thirdTestUser.getFullname(),
            thirdTestUser.getPhotoSmall(),
            FriendshipStatus.ACCEPTED
        )
    );
  }
}
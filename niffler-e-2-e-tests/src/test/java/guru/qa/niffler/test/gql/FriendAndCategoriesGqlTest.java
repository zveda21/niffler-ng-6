package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FriendAndCategoriesGqlTest extends BaseGraphQlTest {

    @User()
    @Test
    @ApiLogin
    void shouldNotAllowMoreThanTwoFriendSubQueries(@Token String bearerToken) {
        final ApolloCall<FriendsCategoriesQuery.Data> friendAndCategory = apolloClient.query(FriendsCategoriesQuery.builder()
                        .page(0)
                        .size(10)
                        .build())
                .addHttpHeader("Authorization", bearerToken);
        final ApolloResponse<FriendsCategoriesQuery.Data> response = Rx2Apollo.single(friendAndCategory).blockingGet();

        List<Error> errors = Optional.ofNullable(response.errors)
                .orElse(Collections.emptyList());

        assertNotNull(errors);
        assertEquals("Can`t fetch over 2 friends sub-queries", response.errors.getFirst().getMessage());
    }
}

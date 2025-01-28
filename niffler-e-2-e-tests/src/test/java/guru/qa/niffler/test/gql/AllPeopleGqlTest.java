package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.AllPeopleQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AllPeopleGqlTest extends BaseGraphQlTest{

    @User(friends = 1)
    @Test
    @ApiLogin
    void shouldNotAllowAccessToCategoriesOfOtherUsers(@Token String bearerToken){
        final ApolloCall<AllPeopleQuery.Data> allPeople = apolloClient.query(AllPeopleQuery.builder()
                        .page(0)
                        .size(10)
                .build())
                .addHttpHeader("Authorization", bearerToken);
        final ApolloResponse<AllPeopleQuery.Data> response = Rx2Apollo.single(allPeople).blockingGet();
        List<Error> errors = Optional.ofNullable(response.errors)
                .orElse(Collections.emptyList());

        assertNotNull(errors);
        assertEquals("Can`t query categories for another user", response.errors.getFirst().getMessage());
    }
}

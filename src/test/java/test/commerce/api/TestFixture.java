package test.commerce.api;

import commerce.command.CreateShopperCommand;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;

public record TestFixture(TestRestTemplate client) {

    public void createShopper(String email, String username, String password) {
        var command = new CreateShopperCommand(email, username, password);
        client.postForEntity("/shopper/signUp", command, Void.class);
    }

    public String issueShopperToken(String email, String password) {
        AccessTokenCarrier carrier = client.postForObject(
            "/shopper/issueToken",
            new IssueShopperToken(email, password),
            AccessTokenCarrier.class
        );

        return carrier.accessToken();
    }

    public String createShopperThenIssueToken() {
        String email1 = EmailGenerator.generate();
        String password1 = PasswordGenerator.generate();
        createShopper(email1, UsernameGenerator.generate(), password1);
        return issueShopperToken(email1, password1);
    }

    public void setShopperAsDefaultUser(String email, String password) {
        String token = issueShopperToken(email, password);
        RestTemplate restTemplate = client.getRestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            if (!request.getHeaders().containsKey("Authorization")) {
                request.getHeaders().setBearerAuth(token);
            }
            return execution.execute(request, body);
        });
    }
}

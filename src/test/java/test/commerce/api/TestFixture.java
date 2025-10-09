package test.commerce.api;

import commerce.command.CreateShopperCommand;
import commerce.query.IssueSellerToken;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;

import java.net.URI;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

public record TestFixture(TestRestTemplate client) {

    public static TestFixture create(Environment environment) {
        var client = new TestRestTemplate();
        var handler = new LocalHostUriTemplateHandler(environment);
        client.setUriTemplateHandler(handler);
        return new TestFixture(client);
    }

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
        setDefaultAuthorization(token);
    }

    private void setDefaultAuthorization(String token) {
        RestTemplate restTemplate = client.getRestTemplate();
        restTemplate.getInterceptors().addFirst((request, body, execution) -> {
            if (!request.getHeaders().containsKey("Authorization")) {
                request.getHeaders().setBearerAuth(token);
            }
            return execution.execute(request, body);
        });
    }

    public void createSellerThenSetAsDefaultUser() {
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        String username = UsernameGenerator.generate();
        createSeller(email, username, password);
        setSellerAsDefaultUser(email, password);
    }

    private void createSeller(String email, String username, String password) {
        CreateShopperCommand command = new CreateShopperCommand(email, username, password);
        client.postForEntity("/seller/signUp", command, Void.class);
    }

    private void setSellerAsDefaultUser(String email, String password) {
        String token = issueSellerToken(email, password);
        setDefaultAuthorization(token);
    }

    private String issueSellerToken(String email, String password) {
        AccessTokenCarrier carrier = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );

        return carrier.accessToken();
    }

    public void createShopperThenAsDefaultUser() {
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        String username = UsernameGenerator.generate();
        createShopper(email, username, password);
        setShopperAsDefaultUser(email, password);
    }

    public UUID registerProduct() {
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/products",
            generateRegisterProductCommand(),
            Void.class
        );

        URI location = response.getHeaders().getLocation();
        String path = requireNonNull(location).getPath();
        String id = path.substring("/seller/products/".length());
        return UUID.fromString(id);
    }
}

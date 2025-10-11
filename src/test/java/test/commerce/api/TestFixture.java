package test.commerce.api;

import commerce.ProductRepository;
import commerce.command.CreateShopperCommand;
import commerce.command.RegisterProductCommand;
import commerce.query.IssueSellerToken;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import commerce.result.PageCarrier;
import commerce.view.ProductView;
import commerce.view.SellerMeView;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import test.commerce.EmailGenerator;
import test.commerce.PasswordGenerator;
import test.commerce.UsernameGenerator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

public record TestFixture(
    TestRestTemplate client,
    ProductRepository productRepository
) {

    public static TestFixture create(Environment environment, ProductRepository productRepository) {
        var client = new TestRestTemplate();
        var handler = new LocalHostUriTemplateHandler(environment);
        client.setUriTemplateHandler(handler);
        return new TestFixture(client, productRepository);
    }

    public void createShopper(String email, String username, String password) {
        var command = new CreateShopperCommand(email, username, password);
        ensureSuccessful(
        client.postForEntity("/shopper/signUp", command, Void.class)
            , command);
    }

    private void ensureSuccessful(ResponseEntity<Void> response, CreateShopperCommand command) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to create shopper: " + command);
        }
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

    public void createShopperThenSetAsDefaultUser() {
        String email = EmailGenerator.generate();
        String password = PasswordGenerator.generate();
        String username = UsernameGenerator.generate();
        createShopper(email, username, password);
        setShopperAsDefaultUser(email, password);
    }

    public UUID registerProduct() {
        return registerProduct(generateRegisterProductCommand());
    }

    public UUID registerProduct(RegisterProductCommand command) {
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/products",
            command,
            Void.class
        );
        URI location = response.getHeaders().getLocation();
        String path = requireNonNull(location).getPath();
        String id = path.substring("/seller/products/".length());
        return UUID.fromString(id);
    }

    public List<UUID> registerProducts() {
        return List.of(registerProduct(), registerProduct(), registerProduct());
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public List<UUID> registerProducts(int count) {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(registerProduct());
        }
        return ids;
    }

    public SellerMeView getSeller() {
        return client.getForEntity("/seller/me", SellerMeView.class).getBody();
    }

    public String consumeProductPage() {
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            RequestEntity.get("/shopper/products").build(),
            new ParameterizedTypeReference<>() { }
        );

        return requireNonNull(response.getBody()).continuationToken();
    }

    public String consumeTwoProductPages() {
        String token = consumeProductPage();
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            RequestEntity.get("/shopper/products?continuationToken=" + token).build(),
            new ParameterizedTypeReference<>() { }
        );
        return requireNonNull(response.getBody()).continuationToken();
    }
}

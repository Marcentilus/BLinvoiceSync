import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Configuration {

    private String url;
    private String login;
    private String password;
    private String token;
}

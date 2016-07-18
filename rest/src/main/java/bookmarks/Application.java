package bookmarks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    @Bean
    public CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
        return (result) -> Arrays.asList("jhoeller", "dsyer", "pwebb", "ogierke", "rwinch", "mfisher", "mpollack").forEach(each -> {
            Account account = accountRepository.save(new Account(each, "111"));
            bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + each, "A description"));
            bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + each, "A description"));
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}



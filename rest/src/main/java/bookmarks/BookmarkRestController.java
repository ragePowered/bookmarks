package bookmarks;

import bookmarks.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{userName}/bookmarks")
public class BookmarkRestController {
    private final BookmarkRepository bookmarkRepository;
    private final AccountRepository accountRepository;

    @Autowired
    BookmarkRestController(BookmarkRepository bookmarkRepository, AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    private void validateUser(String userName) {
        this.accountRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(userName));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userName, @RequestBody Bookmark input) {
        this.validateUser(userName);
        return this.accountRepository.findByUserName(userName)
                .map(account -> {
                            Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.getUri(), input.getDescription()));

                            HttpHeaders httpHeaders = new HttpHeaders();
                            Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
                            httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

                            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                        }
                ).get();

    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    BookmarkResource readBookmark(@PathVariable String userName, @PathVariable Long bookmarkId) {
        this.validateUser(userName);
        return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<BookmarkResource> readBookmarks(@PathVariable String userName) {
        this.validateUser(userName);
        return bookmarkRepository.findByAccountUserName(userName).stream().map(BookmarkResource::new).collect(Collectors.toList());
    }
}

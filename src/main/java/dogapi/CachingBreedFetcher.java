package dogapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Cache wrapper for a BreedFetcher. Successful lookups are cached
 * (including "no sub-breeds" as an empty list). Failures are never cached.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher upstream;
    private final Map<String, List<String>> cache = new ConcurrentHashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.upstream = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        final String k = normalize(breed);
        final List<String> cached = cache.get(k);
        if (cached != null) {
            return new ArrayList<>(cached);
        }
        callsMade++;
        final List<String> fetched;
        try {
            fetched = upstream.getSubBreeds(breed);
        } catch (BreedNotFoundException e) {
            throw e;
        }
        final List<String> s =
                (fetched == null || fetched.isEmpty())
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(new ArrayList<>(fetched));

        cache.put(k, s);
        return new ArrayList<>(s);
    }

    public int getCallsMade() {
        return callsMade;
    }

    private static String normalize(String breed) throws BreedNotFoundException {
        if (breed == null) throw new BreedNotFoundException("null");
        final String trimmed = breed.trim();
        if (trimmed.isEmpty()) throw new BreedNotFoundException(breed);
        return trimmed.toLowerCase();
    }
}

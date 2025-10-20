package dogapi;

import java.util.List;
// Option A: qualify inline (no extra import)
// Option B: uncomment this import and then you can use just `BreedNotFoundException`
// import dogapi.BreedFetcher.BreedNotFoundException;

/**
 * A minimal implementation of the BreedFetcher interface for testing purposes.
 * To avoid excessive calls to the real API, we can primarily test with a local
 * implementation that demonstrates the basic functionality of the interface
 */
public class BreedFetcherForLocalTesting implements BreedFetcher {
    private int callCount = 0;

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        callCount++;
        if ("hound".equalsIgnoreCase(breed)) {
            return List.of("afghan", "basset");
        }
        throw new BreedFetcher.BreedNotFoundException(breed);
    }

    public int getCallCount() {
        return callCount;
    }
}

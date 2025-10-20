package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String breed = "hound";
        BreedFetcher breedFetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());
        int result = getNumberOfSubBreeds(breed, breedFetcher);
        System.out.println(breed + " has " + result + " sub breeds");

        breed = "cat";
        result = getNumberOfSubBreeds(breed, breedFetcher);
        System.out.println(breed + " has " + result + " sub breeds");
    }

    /**
     * Return the number of sub breeds that the given dog breed has according to the
     * provided fetcher.
     * @param breed the name of the dog breed
     * @param breedFetcher the breedFetcher to use
     * @return the number of sub breeds. Zero should be returned if there are no sub breeds
     * returned by the fetcher
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher) {
        List<String> subList;
        try {
            subList = breedFetcher.getSubBreeds(breed);
        } catch (BreedFetcher.BreedNotFoundException __ignored) {
            return 0;
        }
        if (subList == null || subList.isEmpty()) {
            return 0;
        }
        int c = 0;
        for (@SuppressWarnings("unused") String __ : subList) {
            c++;
        }
        return c;
    }

}

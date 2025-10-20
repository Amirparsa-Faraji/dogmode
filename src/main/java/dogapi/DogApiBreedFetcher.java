package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BreedFetcher backed by the public dog.ceo API.
 * Any error (network/parse/not-found) is surfaced as BreedNotFoundException.
 */
public class DogApiBreedFetcher implements BreedFetcher {

    private final OkHttpClient http = new OkHttpClient();

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        final String normalized = (breed == null) ? "" : breed.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }
        final String endpoint = "https://dog.ceo/api/breed/" + normalized + "/list";
        final Request req = new Request.Builder()
                .url(endpoint)
                .get()
                .build();

        try (Response rsp = http.newCall(req).execute()) {
            if (!rsp.isSuccessful() || rsp.body() == null) {
                throw new BreedNotFoundException(breed);
            }
            final String p = rsp.body().string();
            final JSONArray arr = getObjects(breed, p);
            if (arr.isEmpty()) {
                return Collections.emptyList();
            }

            final List<String> out = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                out.add(arr.getString(i));
            }
            return out;
        } catch (IOException | RuntimeException ex) {
            throw new BreedNotFoundException(breed);
        }
    }

    @NotNull
    private static JSONArray getObjects(String breed, String payload) throws BreedNotFoundException {
        final JSONObject root = new JSONObject(payload);
        if (!"success".equalsIgnoreCase(root.optString("status"))) {
            throw new BreedNotFoundException(breed);
        }

        final Object msg = root.opt("message");
        if (!(msg instanceof JSONArray)) {
            throw new BreedNotFoundException(breed);
        }

        final JSONArray arr = (JSONArray) msg;
        return arr;
    }
}

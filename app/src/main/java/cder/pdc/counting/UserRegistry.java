package cder.pdc.counting;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class UserRegistry 
{
    private final Map<String, UUID> userToId = new HashMap<>();

    @PostConstruct
    public void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/usernames.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String username = line.trim().toLowerCase();
                userToId.putIfAbsent(username, UUID.nameUUIDFromBytes(username.getBytes()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load usernames.txt", e);
        }
    }

    public boolean isValid(String username) {
        return userToId.containsKey(username.toLowerCase());
    }

    public UUID getId(String username) {
        return userToId.get(username.toLowerCase());
    }

    public Set<String> getAllUsernames() {
        return userToId.keySet();
    }
}


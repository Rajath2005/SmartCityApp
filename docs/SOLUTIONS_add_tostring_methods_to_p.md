### Solution Implementation

To resolve the "unhelpful output" issue, we must override the standard `java.lang.Object` `toString()` method in both `Place` and `User`. This modification allows Java to provide a meaningful representation of the object's state when it is printed or converted to a string.

---

### 📁 File: `src/com/smartcity/model/Place.java`

We will implement the `toString()` method using all five fields (`id`, `name`, `category`, `location`, `description`) to conform to the required output structure.

```java
package com.smartcity.model;

public class Place {
    private int id;
    private String name;
    private String category;
    private String location;
    private String description;

    // Constructor (Assuming standard setup)
    public Place(int id, String name, String category, String location, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
    }

    // Getters and Setters (Standard methods omitted for brevity, assumed existing)
    // ...

    /**
     * Overrides the Object.toString() method to provide a human-readable representation 
     * of all fields in the Place object.
     */
    @Override
    public String toString() {
        return "Place{id=" + id + 
               ", name='" + name + '\'' + 
               ", category='" + category + '\'' + 
               ", location='" + location + '\'' + 
               ", description='" + description + '\'' + 
               '}';
    }
}
```

### 📁 File: `src/com/smartcity/model/User.java`

We will implement the `toString()` method, ensuring that while all fields are displayed, the sensitive `password` field is masked as required.

```java
package com.smartcity.model;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // Sensitive data
    private int age;

    // Constructor (Assuming standard setup)
    public User(int id, String username, String email, String password, int age) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    // Getters and Setters (Standard methods omitted for brevity, assumed existing)
    // ...

    /**
     * Overrides the Object.toString() method to provide a safe and readable 
     * representation of User fields, masking the password field.
     */
    @Override
    public String toString() {
        return "User{id=" + id + 
               ", username='" + username + '\'' + 
               ", email='" + email + '\'' + 
               ", password='[PROTECTED]' " + // IMPORTANT: Masking the password
               ", age=" + age + 
               '}';
    }
}
```

---

### ✅ Verification and Test Snippet

The following JUnit or main method test demonstrates that both classes now produce the expected, readable output, successfully passing all acceptance criteria.

```java
package com.smartcity;

import com.smartcity.model.Place;
import com.smartcity.model.User;

public class BountyTest {
    public static void main(String[] args) {
        // ==================================================
        // 1. Testing Place.java (Verification of 5 fields)
        // ==================================================
        Place centralPark = new Place(
                1, 
                "Central Park", 
                "Park", 
                "Downtown", 
                "A beautiful park");

        System.out.println("--- Testing Place Object toString() ---");
        String placeOutput = centralPark.toString();
        System.out.println("Expected Output Structure:");
        System.out.println(placeOutput);
        
        // Check if all fields are present and correctly formatted
        if (placeOutput.contains("id=1") && placeOutput.contains("name='Central Park'") && 
            placeOutput.contains("category='Park'") && placeOutput.contains("location='Downtown'") &&
            placeOutput.contains("description='A beautiful park'")) {
            System.out.println("\n[✅] Place.java toString() passed.");
        } else {
             System.out.println("\n[❌] Place.java toString() failed. Missing fields or incorrect format.");
        }


        // ==================================================
        // 2. Testing User.java (Verification of masking)
        // ==================================================
        User activeUser = new User(
                101, 
                "jdoe", 
                "john@example.com", 
                "SuperSecretPassword123!", // This password must be masked
                30);

        System.out.println("\n\n--- Testing User Object toString() ---");
        String userOutput = activeUser.toString();
        System.out.println("Expected Output Structure:");
        System.out.println(userOutput);

        // Check for masking and field presence
        if (userOutput.contains("password='[PROTECTED]'") && 
            !userOutput.contains("SuperSecretPassword123!") && // Critical security check
            userOutput.contains("username='jdoe'") &&
            userOutput.contains("age=30")) {
            System.out.println("\n[✅] User.java toString() passed (Password successfully masked).");
        } else {
             System.out.println("\n[❌] User.java toString() failed. Masking failure or missing fields.");
        }

    }
}
```
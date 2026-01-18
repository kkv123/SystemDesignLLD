package BuilderDesignPattern;

// Step 1: Define a Builder interface
interface Builder<T> {
    T build();
}

// Step 2: Single class implementing both Product and Builder
public class SingleClassBuilderDemo {

    // Product class
    static class User {
        private final String name;
        private final int age;
        private final String address;

        // Private constructor
        private User(UserBuilder builder) {
            this.name = builder.name;
            this.age = builder.age;
            this.address = builder.address;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + ", address='" + address + "'}";
        }

        // --- Inner Builder implementing Builder interface ---
        static class UserBuilder implements Builder<User> {
            private String name;
            private int age;
            private String address;

            public UserBuilder setName(String name) {
                this.name = name;
                return this;
            }

            public UserBuilder setAge(int age) {
                this.age = age;
                return this;
            }

            public UserBuilder setAddress(String address) {
                this.address = address;
                return this;
            }

            @Override
            public User build() {
                return new User(this);
            }
        }
    }

    // --- Main method ---
    public static void main(String[] args) {
        // Build User object using Builder interface
        Builder<User> builder1 = new User.UserBuilder()
                .setName("Kuldeep")
                .setAge(28)
                .setAddress("Pune");

        User user1 = builder1.build();

        Builder<User> builder2 = new User.UserBuilder()
                .setName("Alice")
                .setAge(25); // address optional

        User user2 = builder2.build();

        System.out.println(user1);
        System.out.println(user2);
    }
}
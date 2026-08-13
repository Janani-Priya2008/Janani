package com.janani.fruitmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class JananiFruitMartApplication {

    private final List<User> users = new ArrayList<>();
    private final List<Fruit> fruits = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final List<WishlistItem> wishlist = new ArrayList<>();

    private final AtomicInteger userId = new AtomicInteger(1);
    private final AtomicInteger fruitId = new AtomicInteger(1);
    private final AtomicInteger orderId = new AtomicInteger(1001);
    private final AtomicInteger reviewId = new AtomicInteger(1);

    public JananiFruitMartApplication() {

        // Sample Users
        users.add(new User(
                userId.getAndIncrement(),
                "Janani",
                "janani@gmail.com",
                "123456",
                "BUYER"
        ));

        users.add(new User(
                userId.getAndIncrement(),
                "Fruit Seller",
                "seller@janani.com",
                "123456",
                "SELLER"
        ));

        // Sample Fruits
        addSampleFruit(
                "Apple",
                "Fresh red apples",
                "Fruits",
                120,
                50,
                "India"
        );

        addSampleFruit(
                "Banana",
                "Fresh yellow bananas",
                "Fruits",
                60,
                100,
                "Tamil Nadu"
        );

        addSampleFruit(
                "Orange",
                "Fresh juicy oranges",
                "Fruits",
                90,
                70,
                "Nagpur"
        );

        addSampleFruit(
                "Mango",
                "Sweet Alphonso mangoes",
                "Fruits",
                150,
                40,
                "Maharashtra"
        );

        addSampleFruit(
                "Grapes",
                "Fresh green grapes",
                "Fruits",
                100,
                60,
                "Nashik"
        );

        addSampleFruit(
                "Watermelon",
                "Fresh and juicy watermelon",
                "Fruits",
                80,
                30,
                "Tamil Nadu"
        );
    }

    private void addSampleFruit(
            String name,
            String description,
            String category,
            double price,
            int stock,
            String origin) {

        fruits.add(
                new Fruit(
                        fruitId.getAndIncrement(),
                        name,
                        description,
                        category,
                        price,
                        stock,
                        origin
                )
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(
                JananiFruitMartApplication.class,
                args
        );
    }

    // =====================================================
    // HOME
    // =====================================================

    @GetMapping("/")
    public String home() {
        return "Welcome to Janani FruitMart";
    }

    // =====================================================
    // USER REGISTRATION
    // =====================================================

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.name == null ||
                request.email == null ||
                request.password == null) {

            return ResponseEntity.badRequest()
                    .body("All fields are required");
        }

        for (User user : users) {

            if (user.email.equalsIgnoreCase(request.email)) {

                return ResponseEntity.badRequest()
                        .body("Email already registered");
            }
        }

        User user = new User(
                userId.getAndIncrement(),
                request.name,
                request.email,
                request.password,
                "BUYER"
        );

        users.add(user);

        return ResponseEntity.ok(
                "Registration successful"
        );
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        for (User user : users) {

            if (user.email.equalsIgnoreCase(request.email)
                    && user.password.equals(request.password)) {

                Map<String, Object> response =
                        new HashMap<>();

                response.put("message",
                        "Login successful");

                response.put("userId",
                        user.id);

                response.put("name",
                        user.name);

                response.put("role",
                        user.role);

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    // =====================================================
    // GET ALL FRUITS
    // =====================================================

    @GetMapping("/fruits")
    public List<Fruit> getAllFruits() {
        return fruits;
    }

    // =====================================================
    // GET FRUIT BY ID
    // =====================================================

    @GetMapping("/fruits/{id}")
    public ResponseEntity<?> getFruit(
            @PathVariable int id) {

        for (Fruit fruit : fruits) {

            if (fruit.id == id) {
                return ResponseEntity.ok(fruit);
            }
        }

        return ResponseEntity.notFound().build();
    }

    // =====================================================
    // SEARCH FRUITS
    // =====================================================

    @GetMapping("/fruits/search")
    public List<Fruit> searchFruit(
            @RequestParam String keyword) {

        List<Fruit> result =
                new ArrayList<>();

        for (Fruit fruit : fruits) {

            if (fruit.name
                    .toLowerCase()
                    .contains(keyword.toLowerCase())
                    ||
                    fruit.description
                            .toLowerCase()
                            .contains(keyword.toLowerCase())) {

                result.add(fruit);
            }
        }

        return result;
    }

    // =====================================================
    // CATEGORY FILTER
    // =====================================================

    @GetMapping("/fruits/category/{category}")
    public List<Fruit> categoryFilter(
            @PathVariable String category) {

        List<Fruit> result =
                new ArrayList<>();

        for (Fruit fruit : fruits) {

            if (fruit.category
                    .equalsIgnoreCase(category)) {

                result.add(fruit);
            }
        }

        return result;
    }

    // =====================================================
    // SELLER - ADD FRUIT
    // =====================================================

    @PostMapping("/seller/fruits")
    public ResponseEntity<?> addFruit(
            @RequestBody Fruit fruit) {

        Fruit newFruit =
                new Fruit(
                        fruitId.getAndIncrement(),
                        fruit.name,
                        fruit.description,
                        fruit.category,
                        fruit.price,
                        fruit.stock,
                        fruit.origin
                );

        fruits.add(newFruit);

        return ResponseEntity.ok(
                "Fruit added successfully"
        );
    }

    // =====================================================
    // SELLER - UPDATE FRUIT
    // =====================================================

    @PutMapping("/seller/fruits/{id}")
    public ResponseEntity<?> updateFruit(
            @PathVariable int id,
            @RequestBody Fruit updatedFruit) {

        for (int i = 0; i < fruits.size(); i++) {

            if (fruits.get(i).id == id) {

                Fruit fruit =
                        new Fruit(
                                id,
                                updatedFruit.name,
                                updatedFruit.description,
                                updatedFruit.category,
                                updatedFruit.price,
                                updatedFruit.stock,
                                updatedFruit.origin
                        );

                fruits.set(i, fruit);

                return ResponseEntity.ok(
                        "Fruit updated successfully"
                );
            }
        }

        return ResponseEntity.notFound().build();
    }

    // =====================================================
    // SELLER - DELETE FRUIT
    // =====================================================

    @DeleteMapping("/seller/fruits/{id}")
    public ResponseEntity<?> deleteFruit(
            @PathVariable int id) {

        boolean removed =
                fruits.removeIf(
                        fruit -> fruit.id == id
                );

        if (removed) {

            return ResponseEntity.ok(
                    "Fruit deleted successfully"
            );
        }

        return ResponseEntity.notFound().build();
    }

    // =====================================================
    // ADD TO CART
    // =====================================================

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(
            @RequestBody CartRequest request) {

        Fruit fruit = findFruit(
                request.fruitId
        );

        if (fruit == null) {

            return ResponseEntity.badRequest()
                    .body("Fruit not found");
        }

        if (request.quantity <= 0) {

            return ResponseEntity.badRequest()
                    .body("Invalid quantity");
        }

        if (request.quantity > fruit.stock) {

            return ResponseEntity.badRequest()
                    .body("Not enough stock");
        }

        for (CartItem item : cart) {

            if (item.userId == request.userId
                    && item.fruitId == request.fruitId) {

                item.quantity += request.quantity;

                return ResponseEntity.ok(
                        "Cart quantity updated"
                );
            }
        }

        cart.add(
                new CartItem(
                        request.userId,
                        request.fruitId,
                        request.quantity
                )
        );

        return ResponseEntity.ok(
                "Fruit added to cart"
        );
    }

    // =====================================================
    // VIEW CART
    // =====================================================

    @GetMapping("/cart/{userId}")
    public List<Map<String, Object>> viewCart(
            @PathVariable int userId) {

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (CartItem item : cart) {

            if (item.userId == userId) {

                Fruit fruit =
                        findFruit(item.fruitId);

                if (fruit != null) {

                    Map<String, Object> data =
                            new HashMap<>();

                    data.put("fruitId",
                            fruit.id);

                    data.put("name",
                            fruit.name);

                    data.put("price",
                            fruit.price);

                    data.put("quantity",
                            item.quantity);

                    data.put(
                            "subtotal",
                            fruit.price *
                                    item.quantity
                    );

                    result.add(data);
                }
            }
        }

        return result;
    }

    // =====================================================
    // UPDATE CART
    // =====================================================

    @PutMapping("/cart/update")
    public ResponseEntity<?> updateCart(
            @RequestBody CartRequest request) {

        for (CartItem item : cart) {

            if (item.userId == request.userId
                    && item.fruitId == request.fruitId) {

                Fruit fruit =
                        findFruit(request.fruitId);

                if (fruit == null) {
                    return ResponseEntity.badRequest()
                            .body("Fruit not found");
                }

                if (request.quantity > fruit.stock) {

                    return ResponseEntity.badRequest()
                            .body("Not enough stock");
                }

                item.quantity =
                        request.quantity;

                return ResponseEntity.ok(
                        "Cart updated"
                );
            }
        }

        return ResponseEntity.notFound()
                .build();
    }

    // =====================================================
    // REMOVE FROM CART
    // =====================================================

    @DeleteMapping("/cart/remove")
    public ResponseEntity<?> removeCart(
            @RequestParam int userId,
            @RequestParam int fruitId) {

        boolean removed =
                cart.removeIf(
                        item ->
                                item.userId == userId
                                        &&
                                item.fruitId == fruitId
                );

        if (removed) {

            return ResponseEntity.ok(
                    "Fruit removed from cart"
            );
        }

        return ResponseEntity.notFound()
                .build();
    }

    // =====================================================
    // CHECKOUT
    // =====================================================

    @PostMapping("/orders/checkout/{userId}")
    public ResponseEntity<?> checkout(
            @PathVariable int userId) {

        List<CartItem> userCart =
                new ArrayList<>();

        for (CartItem item : cart) {

            if (item.userId == userId) {
                userCart.add(item);
            }
        }

        if (userCart.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Cart is empty");
        }

        double total = 0;

        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem item : userCart) {

            Fruit fruit =
                    findFruit(item.fruitId);

            if (fruit == null) {
                continue;
            }

            if (item.quantity > fruit.stock) {

                return ResponseEntity.badRequest()
                        .body(
                                "Not enough stock for "
                                        + fruit.name
                        );
            }

            total +=
                    fruit.price *
                            item.quantity;

            orderItems.add(
                    new OrderItem(
                            fruit.id,
                            fruit.name,
                            item.quantity,
                            fruit.price
                    )
            );
        }

        for (CartItem item : userCart) {

            Fruit fruit =
                    findFruit(item.fruitId);

            if (fruit != null) {

                fruit.stock -=
                        item.quantity;
            }
        }

        Order order =
                new Order(
                        orderId.getAndIncrement(),
                        userId,
                        orderItems,
                        total,
                        "PENDING",
                        LocalDateTime.now()
                );

        orders.add(order);

        cart.removeIf(
                item -> item.userId == userId
        );

        return ResponseEntity.ok(order);
    }

    // =====================================================
    // ORDER HISTORY
    // =====================================================

    @GetMapping("/orders/{userId}")
    public List<Order> orderHistory(
            @PathVariable int userId) {

        List<Order> result =
                new ArrayList<>();

        for (Order order : orders) {

            if (order.userId == userId) {
                result.add(order);
            }
        }

        return result;
    }

    // =====================================================
    // ORDER TRACKING
    // =====================================================

    @GetMapping("/orders/{userId}/{orderId}")
    public ResponseEntity<?> trackOrder(
            @PathVariable int userId,
            @PathVariable int orderId) {

        for (Order order : orders) {

            if (order.id == orderId
                    && order.userId == userId) {

                return ResponseEntity.ok(
                        order
                );
            }
        }

        return ResponseEntity.notFound()
                .build();
    }

    // =====================================================
    // ADD REVIEW
    // =====================================================

    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(
            @RequestBody Review review) {

        Fruit fruit =
                findFruit(review.fruitId);

        if (fruit == null) {

            return ResponseEntity.badRequest()
                    .body("Fruit not found");
        }

        if (review.rating < 1 ||
                review.rating > 5) {

            return ResponseEntity.badRequest()
                    .body(
                            "Rating must be between 1 and 5"
                    );
        }

        Review newReview =
                new Review(
                        reviewId.getAndIncrement(),
                        review.userId,
                        review.fruitId,
                        review.rating,
                        review.comment
                );

        reviews.add(newReview);

        return ResponseEntity.ok(
                "Review submitted successfully"
        );
    }

    // =====================================================
    // VIEW REVIEWS
    // =====================================================

    @GetMapping("/reviews/{fruitId}")
    public List<Review> getReviews(
            @PathVariable int fruitId) {

        List<Review> result =
                new ArrayList<>();

        for (Review review : reviews) {

            if (review.fruitId == fruitId) {
                result.add(review);
            }
        }

        return result;
    }

    // =====================================================
    // WISHLIST
    // =====================================================

    @PostMapping("/wishlist/add")
    public String addWishlist(
            @RequestBody WishlistItem item) {

        

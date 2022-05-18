# Spring-Undo  [![Java CI with Gradle](https://github.com/michaelfmnk/spring-undo/actions/workflows/gradle.yml/badge.svg)](https://github.com/michaelfmnk/spring-undo/actions/workflows/gradle.yml)

#### Spring Boot starter that provides a way to easily implement undo functionality in your Spring Boot application.


---
### Features
 - **Drastically simplifies undo implementation**. Reduces boilerplate that otherwise you need to manage.
 - **Supports application scaling.** Persist-module can use shared storage between all instances of your application. So you can call undo from any instance.
 - **Spring-Undo is a Spring Boot Starter**. You don't need to write any additional configuration for setting up Spring-Undo.

### Usage

1. Create an event dto that will represent that can be undone.
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String email;
}
```

2. Register an event listener that will be called when either undo timeout is reached or undo is called.

```java
@Component
public static class UserRegisteredUndoEventListener extends UndoEventListener<UserRegisteredEvent> {
    @Override
    public void onUndo(UserRegisteredEvent action) {
        // handle here undo logic
    }

    @Override
    public void onPersist(UserRegisteredEvent action) {
        // handle here what to do when event is persisted
        // For example, you can send an email to the user here
    }
}
```

3. Autowire `Undo` bean and publish any object that represents an undoable action.

```java
@RestController
@RequiredArgsConstructor
class UserController {
    private final Undo undo;

    @PostMapping("/register")
    public String register(RegistrationDto registration) {
        // main application logic
        var event = new UserRegisteredEvent("newUser@gmail.com");
        
        // publish method returns event id that can be used to undo the action
        return undo.publish(undoable);
    }
}
```

4. Create a controller that will undo the action.

```java
@RestController
public class UndoController {
    private final Undo undo;

    @GetMapping("/undo/{id}")
    public String undo(@PathVariable String id) {
        undo.undo(id);
        return "Undo successful";
    }
}
```
5. You're done!

### Contacts
Message me if you have any questions or suggestions: [michael@fomenko.dev](mailto:michael@fomenko.dev)


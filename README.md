# Spring-Undo

#### Spring Boot starter provides a way to easily implement undo functionality in your Spring Boot application.

---

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
class UndoController {
    private final Undo undo;

    @GetMapping("/undo/{id}")
    public String undo(@PathVariable String id) {
        undo.undo(id);
        return "Undo successful";
    }
}
```

5. You're done!


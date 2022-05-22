package dev.fomenko.springundocore;

import lombok.Getter;

import java.util.Collection;

public class UndoListenerInvocationException extends RuntimeException {
    @Getter
    private final Collection<Throwable> causes;

    public UndoListenerInvocationException(String msg, Collection<Throwable> causes) {
        super(msg, causes.iterator().next());
        this.causes = causes;
    }
}

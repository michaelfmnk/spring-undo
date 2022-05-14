package dev.fomenko.springundocore;

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.ParameterizedType;

@CommonsLog
public abstract class UndoEventListener<T> {
    @Getter
    private final Class<T> actionClass;

    @SuppressWarnings("unchecked")
    public UndoEventListener() {
        this.actionClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public abstract void onUndo(T action);

    public void onPersist(T action) {
        log.warn("onPersist is not implemented for UndoEventListener of " + actionClass.getName());
    }
}

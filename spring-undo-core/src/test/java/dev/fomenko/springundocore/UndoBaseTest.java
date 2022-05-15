package dev.fomenko.springundocore;

import dev.fomenko.springundocore.config.UndoTestConfiguration;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.dto.TestDtoA;
import dev.fomenko.springundocore.service.ActionIdGenerator;
import dev.fomenko.springundocore.service.EventRecorder;
import dev.fomenko.springundocore.service.UndoService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UndoTestConfiguration.class})
class UndoBaseTest {

    @Autowired
    private UndoService recordsService;
    @Autowired
    private Undo undo;

    @MockBean
    private EventRecorder eventRecorder;

    @SpyBean
    private ActionIdGenerator idGenerator;
    @SpyBean
    private UndoTestConfiguration.FirstListenerA firstListenerA;
    @SpyBean
    private UndoTestConfiguration.SecondListenerA secondListenerA;
    @SpyBean
    private UndoTestConfiguration.FirstListenerB firstListenerB;
    @SpyBean
    private UndoTestConfiguration.SecondListenerB secondListenerB;

    @Test
    void shouldInvokeListeners() {
        Mockito.when(eventRecorder.deleteRecordById(Mockito.eq("1"))).thenReturn(true);
        TestDtoA dtoA = new TestDtoA("a");
        Mockito.when(eventRecorder.getRecordById(Mockito.eq("1"))).thenReturn(
                Optional.ofNullable(
                        ActionRecord.<TestDtoA>builder()
                                .expiresAt(LocalDateTime.now())
                                .action(dtoA)
                                .build()));
        recordsService.invokeListenerByRecordId("1");

        Mockito.verify(firstListenerB, Mockito.never()).onUndo(Mockito.any());
        Mockito.verify(secondListenerB, Mockito.never()).onUndo(Mockito.any());
        Mockito.verify(firstListenerA, Mockito.times(1)).onUndo(Mockito.eq(dtoA));
        Mockito.verify(secondListenerA, Mockito.times(1)).onUndo(Mockito.eq(dtoA));
    }

    @Test
    void shouldSaveEventWithRecorder() {
        TestDtoA action = new TestDtoA("testA");
        Mockito.when(idGenerator.generateId()).thenReturn("eventId");
        undo.publish(action, Duration.ofSeconds(1));

        Mockito.verify(eventRecorder, Mockito.times(1))
                .saveRecord(Mockito.eq(new ActionRecord<>("eventId", action, LocalDateTime.parse("2019-02-24T09:33:13"))));
    }

    @Test
    @Disabled
    void example() {
        /// user will use only Undo component

        // after performing needed action, user should publish RecoverableEvent
        String eventId = undo.publish(new TestDtoA("test"), Duration.ofSeconds(8));


        // then if user needs no undo the action, he needs to invoke undo(tokenId);
        undo.undo(eventId);

        // then all the listeners being invoked
    }


}

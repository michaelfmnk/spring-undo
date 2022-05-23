package dev.fomenko.springundocore;

import dev.fomenko.springundocore.config.UndoTestConfiguration;
import dev.fomenko.springundocore.config.UndoTestConfiguration.FaultyListenerC;
import dev.fomenko.springundocore.config.UndoTestConfiguration.FirstListenerA;
import dev.fomenko.springundocore.config.UndoTestConfiguration.FirstListenerB;
import dev.fomenko.springundocore.config.UndoTestConfiguration.SecondListenerA;
import dev.fomenko.springundocore.config.UndoTestConfiguration.SecondListenerB;
import dev.fomenko.springundocore.config.UndoTestConfiguration.SecondListenerC;
import dev.fomenko.springundocore.config.UndoTestConfiguration.ThirdListenerC;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.dto.TestDtoA;
import dev.fomenko.springundocore.dto.TestDtoC;
import dev.fomenko.springundocore.service.ActionIdGenerator;
import dev.fomenko.springundocore.service.EventRecorder;
import dev.fomenko.springundocore.service.UndoService;
import org.junit.jupiter.api.Assertions;
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
    private FirstListenerA firstListenerA;
    @SpyBean
    private SecondListenerA secondListenerA;
    @SpyBean
    private FirstListenerB firstListenerB;
    @SpyBean
    private SecondListenerB secondListenerB;
    @SpyBean
    private FaultyListenerC faultyListenerC;
    @SpyBean
    private SecondListenerC secondListenerC;
    @SpyBean
    private ThirdListenerC thirdListenerB;

    @Test
    void shouldInvokeListeners() {
        // given
        TestDtoA dtoA = new TestDtoA("a");
        Mockito.when(eventRecorder.deleteRecordById(Mockito.eq("1"))).thenReturn(true);
        Mockito.when(eventRecorder.getRecordById(Mockito.eq("1"))).thenReturn(
                Optional.ofNullable(
                        ActionRecord.<TestDtoA>builder()
                                .expiresAt(LocalDateTime.now())
                                .action(dtoA)
                                .build()));
        // when
        undo.undo("1");

        // then
        Mockito.verify(firstListenerB, Mockito.never()).onUndo(Mockito.any());
        Mockito.verify(secondListenerB, Mockito.never()).onUndo(Mockito.any());
        Mockito.verify(firstListenerA).onUndo(Mockito.eq(dtoA));
        Mockito.verify(secondListenerA).onUndo(Mockito.eq(dtoA));
    }

    @Test
    void shouldSaveEventWithRecorder() {
        // given
        TestDtoA action = new TestDtoA("testA");
        Mockito.when(idGenerator.generateId()).thenReturn("eventId");

        // when
        undo.publish(action, Duration.ofSeconds(1));

        // then
        var expectedRecord = new ActionRecord<>("eventId", action, LocalDateTime.parse("2019-02-24T09:33:13"));
        Mockito.verify(eventRecorder).saveRecord(Mockito.eq(expectedRecord));
    }

    @Test
    void shouldInvokeAllUndoListenersEvenIfOneFails() {
        // given

        var testDto = new TestDtoC("testA");
        String eventId = "eventId";

        Mockito.when(idGenerator.generateId()).thenReturn(eventId);
        Mockito.when(eventRecorder.deleteRecordById(Mockito.eq(eventId))).thenReturn(true);
        Mockito.when(eventRecorder.getRecordById(Mockito.eq(eventId))).thenReturn(
                Optional.ofNullable(
                        ActionRecord.<TestDtoC>builder()
                                .expiresAt(LocalDateTime.now())
                                .action(testDto)
                                .build()));

        // when
        undo.publish(testDto, Duration.ofSeconds(1));
        var exception = Assertions.assertThrows(UndoListenerInvocationException.class,
                () -> undo.undo(eventId));

        // then
        Assertions.assertEquals(1, exception.getCauses().size());

        var inOrder = Mockito.inOrder(faultyListenerC, secondListenerC, thirdListenerB);

        inOrder.verify(faultyListenerC).onUndo(Mockito.eq(testDto));
        inOrder.verify(secondListenerC).onUndo(Mockito.eq(testDto));
        inOrder.verify(thirdListenerB).onUndo(Mockito.eq(testDto));
    }

    @Test
    @Disabled("for demo purposes")
    void example() {
        /// user will use only Undo component

        // after performing needed action, user should publish RecoverableEvent
        String eventId = undo.publish(new TestDtoA("test"), Duration.ofSeconds(8));


        // then if user needs no undo the action, he needs to invoke undo(tokenId);
        undo.undo(eventId);

        // then all the listeners being invoked
    }

}

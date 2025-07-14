package ru.otus.processor;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

@DisplayName("EvenSecondExceptionProcessor")
class EvenSecondExceptionProcessorTest {

    @Test
    @DisplayName("Должен выбрасывать исключение при чётной секунде")
    void shouldThrowExceptionOnEvenSecond() {
        // given: 12:00:02 — четная секунда
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2023, 1, 1, 12, 0, 2)
                        .atZone(ZoneId.systemDefault())
                        .toInstant(),
                ZoneId.systemDefault());
        Processor processor = new EvenSecondExceptionProcessor(fixedClock);
        Message message = new Message.Builder(1L).build();

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> processor.process(message));

        assertTrue(exception.getMessage().contains("even"));
    }

    @Test
    @DisplayName("Не должен выбрасывать исключение при нечётной секунде")
    void shouldNotThrowExceptionOnOddSecond() {
        // given: 12:00:03 — нечетная секунда
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2023, 1, 1, 12, 0, 3)
                        .atZone(ZoneId.systemDefault())
                        .toInstant(),
                ZoneId.systemDefault());
        Processor processor = new EvenSecondExceptionProcessor(fixedClock);
        Message input = new Message.Builder(2L).build();

        // when
        Message result = processor.process(input);

        // then
        assertSame(input, result);
    }
}

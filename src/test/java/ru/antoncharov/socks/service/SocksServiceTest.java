package ru.antoncharov.socks.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.antoncharov.socks.domain.Socks;
import ru.antoncharov.socks.dto.SocksDto;
import ru.antoncharov.socks.exception.NotEnoughSocksException;
import ru.antoncharov.socks.repository.SocksRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @Mock
    private SocksRepository sockRepository;

    @InjectMocks
    private SocksService sockService;

    @Test
    void shouldAddSocks() {
        SocksDto sockDto = new SocksDto("black", 100, 10);
        sockService.addSocks(sockDto);

        verify(sockRepository).save(any(Socks.class));
    }

    @Test
    void shouldRemoveSocksWhenEnoughInStock() throws NotEnoughSocksException {
        Socks existingSock = new Socks();
        existingSock.setQuantity(20);
        when(sockRepository.findFirstByColorAndCottonPercentage("black", 100)).thenReturn(Optional.of(existingSock));

        SocksDto sockDto = new SocksDto("black", 100, 10);
        sockService.removeSocks(sockDto);

        Assertions.assertThat(existingSock.getQuantity()).isEqualTo(10);
        verify(sockRepository).save(existingSock);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughSocks() {
        when(sockRepository.findFirstByColorAndCottonPercentage("black", 100)).thenReturn(Optional.empty());

        SocksDto sockDto = new SocksDto("black", 100, 10);

        Assertions.assertThatThrownBy(() -> sockService.removeSocks(sockDto))
                .isInstanceOf(NotEnoughSocksException.class)
                .hasMessage("No matching socks found");
    }
}
